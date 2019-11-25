package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.cache.ProductCacheKey;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.repository.UserRepository;
import com.travelinsurancemaster.repository.VendorRepository;
import com.travelinsurancemaster.services.security.LoginService;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Vlad on 16.02.2015.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class InsuranceClientFacade {

    private static final Logger log = LoggerFactory.getLogger(InsuranceClientFacade.class);

    public static final String TIMEOUT_ERROR = "-999";

    @Value("${results.requestExpiration}")
    private int quoteRequestsTimeout;

    private final Map<UUID, QuoteRequestResult> processingQuoteRequests = Collections.synchronizedMap(new LinkedHashMap<UUID, QuoteRequestResult>(16, 0.75f, true));

    private Map<UUID, List<QuoteResult>> cachedQuoteRequestResults = new HashMap<>();

    private Map<UUID, List<QuoteResult>> estimatedQuoteRequestResults = new HashMap<>();

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private Map<String, InsuranceVendorApi> clients;

    @Autowired
    private InsuranceClientAsync insuranceClientAsync;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    CacheManager cacheManager;

    public static long startTime = 0;

    @PostConstruct
    private void postConstruct(){
        log.info("postConstruct()");

        AutowireCapableBeanFactory factory = appContext.getAutowireCapableBeanFactory();
        clients.values().forEach(factory::autowireBean);
    }

    public InsuranceVendorApi getClient(String code) {
        return clients.get(code);
    }

    /**
     * Performs purchase for current plan
     */
    public PurchaseResponse purchase(final PurchaseRequest request) {
        log.info("purchase() {}", request);
        try {
            InsuranceVendorApi client = clients.get(request.getProduct().getPolicyMeta().getVendor().getCode());
            return client != null ? client.purchase(request) : null;
        } catch (Exception e) {
            PurchaseResponse purchaseResponse = new PurchaseResponse();
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", e.getMessage()));
            return purchaseResponse;
        }
    }

    /**
     * Performs quote for policyMeta list, collects list of products
     */
    public List<Product> quote(List<PolicyMetaQuoteRequestPair> policyMetas, boolean withErrors) {
        log.info("quote() {}", policyMetas);
        CountDownLatch countDownLatch = new CountDownLatch(policyMetas.size());
        UUID requestId = startQuoteRequests(policyMetas, withErrors, countDownLatch);
        try {
            countDownLatch.await(apiProperties.getTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
        QuoteRequestResult quoteRequestResult = getQuoteRequestResult(requestId);
        List<Product> products = quoteRequestResult.getProducts();
        products.addAll(getQuoteRequestCachedProducts(requestId));
        String rid = requestId.toString();
        if(products.size() > 0) products.forEach(p -> p.setRequestId(rid));
        return products;
    }

    /**
     * Сервис-таск для очистки подвисших результатов.
     */
    @Scheduled(fixedRateString = "60000", initialDelay = 0)
    public void cleanUpProcessedQuoteRequests() {
        List<QuoteRequestResult> finishing = new ArrayList<>();
        int cnt = 0;
        for (Iterator<QuoteRequestResult> it = this.processingQuoteRequests.values().iterator(); it.hasNext(); ) {
            QuoteRequestResult pqr = it.next();
            if (System.currentTimeMillis() - pqr.accesstimestamp > quoteRequestsTimeout) {
                if(!pqr.isFinished()){
                    finishing.add(pqr);
                }
                it.remove();
                cnt++;
            } else {
                break;
            }
        }
        for (QuoteRequestResult pqr : finishing) {
            pqr.finish();
        }
    }

    public void startAuthRequests() {
        clients.values().forEach(client -> {
            Vendor vendor = vendorRepository.findByCode(client.getVendorCode());
            if(vendor != null && vendor.isActive()) {
                try {
                    ListenableFuture<Boolean> responseFuture  = insuranceClientAsync.authAsync(client);
                    responseFuture.addCallback(new ListenableFutureCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof CancellationException){
                                log.debug("Authentication request to {} was canceled", vendor.getName());
                            } else {
                                log.error(throwable.getMessage(), throwable);
                            }
                        }
                        @Override
                        public void onSuccess(Boolean authenticationResult) {}
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error(e.getMessage(), e);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Стартуем запросы в отдельных потоках и собираем future для дальнейшей обработки
     *
     * @param policyMetaQuoteRequestPairs
     * @return UUID - уникальный идентификатор запроса
     */
    public UUID startQuoteRequests(List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequestPairs, boolean withErrors, CountDownLatch countDownLatch) {
        Cache cache = cacheManager.getCache(CacheConfig.PRODUCTS_CACHE);
        if(startTime == 0) startTime = System.nanoTime();
        log.info("startQuoteRequests() {}", policyMetaQuoteRequestPairs);
        User user = loginService.getLoggedInUser();//current logged in user
        UUID rqid = UUID.randomUUID();
        List<PolicyMetaResult> results = new ArrayList<>();
        List<QuoteResult> cachedQuoteResults = new ArrayList<>();
        List<QuoteResult> estimatedQuoteResults = new ArrayList<>();
        for (PolicyMetaQuoteRequestPair policyMetaQuoteRequestPair : policyMetaQuoteRequestPairs) {
            PolicyMeta meta = policyMetaQuoteRequestPair.getPolicyMeta();
            PolicyMetaCode metaCode = policyMetaCodeService.getPolicyMetaCode(meta.getId(), policyMetaQuoteRequestPair.getQuoteRequest());
            Vendor vendor = meta.getVendor();
            //If the vendor is being tested
            if(vendor.isTest()) {
                //Skip adding quote if current's user roles don't include admin role and the current user isn't included to the list of allowed to see users
                if(user == null || !user.getRoles().contains(Role.ROLE_ADMIN) && !vendor.getLongTestUserIds().contains(user.getId())) {
                    continue;
                }
            }
            //If vendor is set to be shown to all users except pure consumers, check if the user is a pure consumer and skip if this is the case
            if(vendor.isShowPureConsumers() && (user == null || user.getRoles().size() == 0 || user.getRoles().size() == 1 && user.getRoles().contains(Role.ROLE_USER))) {
                continue;
            }
            QuoteRequest quoteRequest = policyMetaQuoteRequestPair.getQuoteRequest();
            InsuranceVendorApi api = clients.get(meta.getVendor().getCode());
            try {
                QuoteRequest quoteRequestWithCancellation = quoteRequestService.getQuoteRequestWithCancellation(quoteRequest, meta);
                if(quoteRequest.hasNoCategroriesOrOnlyTripCancellation()) {
                    //Make a quote estimate
                    List<Result.Error> errors = api.validateQuote(quoteRequestWithCancellation, meta, metaCode);
                    if (CollectionUtils.isEmpty(errors)) {
                        QuoteResult estimatedQuoteResult = api.quoteEstimate(quoteRequestWithCancellation, meta, metaCode);
                        if (estimatedQuoteResult != null) {
                            estimatedQuoteResults.add(estimatedQuoteResult);
                        }
                    }
                }
                //Check if the request is cached
                ProductCacheKey key = new ProductCacheKey(quoteRequest, meta);
                Cache.ValueWrapper valueWrapper = cache.get(key);
                QuoteResult cachedQuoteResult = valueWrapper != null ? (QuoteResult) valueWrapper.get() : null;
                //If cached quote result contains no products, evict it
                if(cachedQuoteResult != null && cachedQuoteResult.products != null && cachedQuoteResult.products.size() == 0) {
                    cache.evict(key);
                    //We don't need cached quote result without products
                    cachedQuoteResult = null;
                }
                if(cachedQuoteResult != null) {
                    //If the request is cached use the cached value
                    cachedQuoteResults.add(cachedQuoteResult);
                    if(countDownLatch != null) countDownLatch.countDown();
                } else {
                    //If the request is not cached, send the quote request
                    ListenableFuture<QuoteResult> future;
                    if(countDownLatch == null) {
                        future = this.insuranceClientAsync.quoteAsync(quoteRequestWithCancellation, meta, api);
                    } else {
                        future = this.insuranceClientAsync.quoteAsync(quoteRequestWithCancellation, meta, api, countDownLatch);
                    }
                    results.add(new PolicyMetaResult(future, meta, metaCode, vendor, quoteRequest, countDownLatch));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        cachedQuoteRequestResults.put(rqid, cachedQuoteResults);

        estimatedQuoteRequestResults.put(rqid, estimatedQuoteResults);

        QuoteRequestResult quoteRequestResult = new QuoteRequestResult(rqid, results, withErrors);
        this.processingQuoteRequests.put(rqid, quoteRequestResult);

        return rqid;
    }

    public UUID startQuoteRequests(List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequestPairs) {
        return startQuoteRequests(policyMetaQuoteRequestPairs, false, null);
    }

    @Autowired
    QuoteInfoSession quoteInfoSession;

    /**
     * Выбирает из словаря сущность {@link QuoteRequestResult} по {@code requestId}.
     * Если у QuoteRequestResult истек таймаут, его выполнение принудительно завершается.
     * @param requestId идентификатор запроса
     * @return {@link QuoteRequestResult} или {@code null}
     */
    public QuoteRequestResult getQuoteRequestResult(UUID requestId) {
        log.info("getQuoteRequestResult() {}", requestId);
        QuoteRequestResult quoteRequestResult = this.processingQuoteRequests.get(requestId);
        if (quoteRequestResult != null) {
            quoteRequestResult.quoteQueries.forEach(qq -> quoteInfoSession.updateQuoteInfoMapFromDatabase(qq.quoteRequest.getQuoteId()));
            quoteRequestResult.setAccessTimestamp();
            if(!quoteRequestResult.isFinished() && System.currentTimeMillis() - quoteRequestResult.prcessingtimestamp > 120000){
				log.debug("Cancel quote for " + quoteRequestResult.getProducts().stream().map(p -> p.getPolicyMeta().getDisplayName()).collect(Collectors.joining(", ")));
                quoteRequestResult.finish();
            }
        }
        return quoteRequestResult;
    }

    public List<QuoteResult> getCachedQuoteRequestResult(UUID requestId) {
        return cachedQuoteRequestResults.get(requestId);
    }

    public List<QuoteResult> getEstimatedQuoteRequestResult(UUID requestId) {
        return estimatedQuoteRequestResults.get(requestId);
    }

    public List<Product> getQuoteRequestCachedProducts(UUID requestId) {
        List<Product> products = new ArrayList<>();
        List<QuoteResult> cachedQuoteRequestResults = getCachedQuoteRequestResult(requestId);
        for(QuoteResult quoteResult : cachedQuoteRequestResults) {
            products.addAll(quoteResult.products);
        }
        return products;
    }

    /**
     * Возвращает {@link QuoteResult} с ошибкой.
     * @param quoteRequest запрос
     * @param policyMetaUniqueCode уникальный код {@link PolicyMeta}
     * @param e ошибка
     * @return {@link QuoteResult}
     */
    private QuoteResult getErrorQuoteResult(QuoteRequest quoteRequest, String policyMetaUniqueCode, Result.Error e) {
        log.info("getErrorQuoteResult() {} {} {}", quoteRequest, policyMetaUniqueCode, e);

        QuoteResult quoteResult = new QuoteResult();
        quoteResult.quoteRequest = quoteRequest;
        quoteResult.policyMetaUniqueCode = policyMetaUniqueCode;
        quoteResult.setStatus(Result.Status.ERROR);
        quoteResult.getErrors().add(e);
        return quoteResult;
    }

    /**
     * Останавливает все текущие таски определенного {@link UUID}
     * @param requestId - идентификатор запроса
     */
    public void stopRequest(UUID requestId) {
        log.info("stopRequest() {}", requestId);

        QuoteRequestResult quoteRequestResult = getQuoteRequestResult(requestId);
        if (quoteRequestResult != null) {
            quoteRequestResult.finish();
            log.debug("Finished QuoteRequest: {}", requestId.toString());
        }
    }

    public class QuoteRequestResult
    {
        private final UUID requestId;
        private final List<PolicyMetaResult> quoteQueries;
        private final List<Product> resultProducts = Collections.synchronizedList(new ArrayList<>());
        private long accesstimestamp;
        private final long prcessingtimestamp = System.currentTimeMillis();

        private QuoteRequestResult(UUID requestId, List<PolicyMetaResult> quoteQueries, boolean withErrors) {
            this.requestId = requestId;
            this.quoteQueries = Collections.synchronizedList(new ArrayList<PolicyMetaResult>(quoteQueries));

            for (PolicyMetaResult pmr : quoteQueries) {
                pmr.future.addCallback(new ListenableFutureCallback<QuoteResult>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable instanceof CancellationException){
                            log.debug("Request {} was canceled", pmr.policyMeta != null ? pmr.policyMeta.getUniqueCode() : "");
                        } else {
                            log.error(throwable.getMessage(), throwable);
                        }
                        QuoteRequestResult.this.quoteQueries.remove(pmr);
                    }

                    @Override
                    public void onSuccess(QuoteResult quoteResult) {
                        if(!withErrors) {
                            resultProducts.addAll(quoteResult.products);
                        } else {
                            if(quoteResult.getStatus() != Result.Status.ERROR && !CollectionUtils.isEmpty(quoteResult.products)) {
                                resultProducts.addAll(quoteResult.products);
                            } else if(!CollectionUtils.isEmpty(quoteResult.getErrors())) {
                                Product product = new Product();
                                product.getErrors().addAll(quoteResult.getErrors());
                                resultProducts.add(product);
                            }
                        }
                        long duration = (System.nanoTime() - pmr.startTime) / 1000000;
                        String durationStr = PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
                        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@End vendor=" + pmr.vendor.getName() + " policy=" + pmr.policyMeta.getDisplayName() + " time=" + durationStr);
                        QuoteRequestResult.this.quoteQueries.remove(pmr);
                        if(pmr.countDownLatch != null) pmr.countDownLatch.countDown();
                    }
                });
            }

            this.setAccessTimestamp();
        }

        /**
         * Завершает все таски.
         */
        public synchronized void finish() {
            List<PolicyMetaResult> copyQuoteQueries = new ArrayList<>(this.quoteQueries);
            for (PolicyMetaResult pmr : copyQuoteQueries) {
                pmr.future.cancel(true);
            }

            this.quoteQueries.clear();
        }

        public List<Product> getProducts() {
            return new ArrayList<>(this.resultProducts);
        }

        public boolean isFinished() {
            return this.quoteQueries.size() == 0;
        }

        private void setAccessTimestamp(){
            this.accesstimestamp = System.currentTimeMillis();
        }

    }


    public static class PolicyMetaResult {

        ListenableFuture<QuoteResult> future;
        PolicyMeta policyMeta;
        PolicyMetaCode policyMetaCode;
        Vendor vendor;
        QuoteRequest quoteRequest;
        CountDownLatch countDownLatch;
        final long startTime;


        public PolicyMetaResult(ListenableFuture<QuoteResult> future, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, Vendor vendor, QuoteRequest quoteRequest, CountDownLatch countDownLatch) {
            this.future = future;
            this.policyMeta = policyMeta;
            this.policyMetaCode = policyMetaCode;
            this.vendor = vendor;
            this.quoteRequest = quoteRequest;
            this.countDownLatch = countDownLatch;
            startTime = System.nanoTime();
        }
    }
}