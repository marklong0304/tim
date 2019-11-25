package com.travelinsurancemaster.web;

import com.travelinsurancemaster.ProductSortService;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.CategoryReduceResult;
import com.travelinsurancemaster.model.dto.FilteredProducts;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.QuoteStorage;
import com.travelinsurancemaster.model.dto.json.JsonCategory;
import com.travelinsurancemaster.model.dto.json.JsonCompareResult;
import com.travelinsurancemaster.model.dto.json.JsonSearchProduct;
import com.travelinsurancemaster.model.dto.json.JsonSearchResult;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.cache.CacheService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Alexander.Isaenco
 */

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QuoteInfoSession implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(QuoteInfoSession.class);

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ProductService productService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ProductSortService productSortService;

    @Value("${results.showCountAfterEnabledFilter}")
    private boolean showCountAfterEnabledFilter;

    private Map<String, QuoteInfo> quoteInfoMap = new ConcurrentHashMap<>();

    public QuoteRequest getQuoteRequest(String quoteId) {
        if (quoteInfoMap.containsKey(quoteId)) {
            return quoteInfoMap.get(quoteId).getQuoteRequest();
        }
        QuoteStorage quoteStorage = quoteStorageService.findOne(quoteId);
        if (quoteStorage == null) {
            return null;
        }
        return JsonUtils.getObject(quoteStorage.getQuoteRequestJson(), QuoteRequest.class);
    }

    public QuoteInfo getQuoteInfo(String quoteId) {
        return getQuoteInfo(quoteId, true, RestoreType.NAN, true);
    }

    public QuoteInfo getQuoteInfo(boolean prepareCache, String quoteId) {
        return getQuoteInfo(quoteId, true, RestoreType.NAN, prepareCache);
    }

    public QuoteInfo getQuoteInfo(String quoteId, boolean makeNew) {
        return getQuoteInfo(quoteId, makeNew, RestoreType.NAN, true);
    }

    public QuoteInfo getQuoteInfo(String quoteId, RestoreType restoreType) {
        return getQuoteInfo(quoteId, true, restoreType, true);
    }

    public QuoteInfo getQuoteInfo(String quoteId, boolean makeNew, RestoreType restoreType) {
        return getQuoteInfo(quoteId, makeNew, restoreType, true);
    }

    public QuoteInfo getQuoteInfo(String quoteId, boolean makeNew, RestoreType restoreType, boolean prepareCache) {
        log.debug("Get Quote Info by quoteId={}, makeNew={}, restoreType={}, prepareCache={}", quoteId, makeNew, restoreType, prepareCache);
        if (StringUtils.isEmpty(quoteId)) {
            log.debug("quoteId is empty");
            return null;
        }
        QuoteStorage quoteStorage;
        // if quoteId already used in this session get from session map
        if (quoteInfoMap.containsKey(quoteId)) {
            QuoteInfo quoteInfo = quoteInfoMap.get(quoteId);
            if (restoreType.getId() == RestoreType.STORAGE.getId()) {
                quoteStorage = quoteStorageService.findOne(quoteId);
                QuoteRequest quoteRequest = JsonUtils.getObject(quoteStorage.getQuoteRequestJson(), QuoteRequest.class);
                quoteInfo.setQuoteRequest(quoteRequest);
                quoteInfo.setOriginal(quoteStorage.isOriginal());
            } else if (restoreType.getId() == RestoreType.ORIGINAL.getId()) {
                quoteInfo.setQuoteRequest(quoteRequestService.getOriginalQuoteRequest(quoteInfo.getQuoteRequest()));
            }
            return quoteInfo;
        }
        if (!makeNew) {
            return null;
        }
        log.debug("Get QuoteRequest from QuoteStorage by quoteId={}", quoteId);
        quoteStorage = quoteStorageService.findOne(quoteId);
        if (quoteStorage == null) {
            return null;
        }
        QuoteInfo quoteInfo = new QuoteInfo(quoteId);
        log.debug("Put QuoteInfo to session map by quoteId={}", quoteId);
        quoteInfoMap.put(quoteId, quoteInfo);
        QuoteRequest quoteRequest = JsonUtils.getObject(quoteStorage.getQuoteRequestJson(), QuoteRequest.class);
        if (restoreType.getId() == RestoreType.ORIGINAL.getId()) {
            quoteRequest = quoteRequestService.getOriginalQuoteRequest(quoteRequest);
        }
        quoteInfo.setQuoteRequest(quoteRequest);
        quoteInfo.setOriginal(quoteStorage.isOriginal());
        return quoteInfo;
    }

    public QuoteStorage saveNewQuoteStorage(QuoteRequest quoteRequest, HttpServletRequest request, HttpServletResponse response,
                                            User affiliate, PolicyMeta policyMeta, boolean saved, boolean original) {
        return saveNewQuoteStorage(quoteRequest, request, response, affiliate, policyMeta, saved, original, false);
    }


    public QuoteStorage saveNewQuoteStorage(QuoteRequest quoteRequest, HttpServletRequest request, HttpServletResponse response,
                                            User affiliate, PolicyMeta policyMeta, boolean saved, boolean original, boolean systemSaved) {
        String uid = cookieService.saveUidToCookie(request, response);
        QuoteStorage quoteStorage = new QuoteStorage();
        quoteStorage.setUid(uid);
        quoteStorage.setCreateDate(new Date());
        quoteStorage.setSaved(saved);
        quoteStorage.setOriginal(original);
        User user = SecurityHelper.getCurrentUser();
        quoteStorage.setUser(user);
        quoteStorage.setAffiliate(affiliate);
        if (policyMeta != null) {
            quoteStorage.setType(QuoteStorage.QuoteType.DETAILS);
            quoteStorage.setPolicyUniqueCode(policyMeta.getUniqueCode());
        }
        quoteStorage.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
        quoteStorage = quoteStorageService.save(quoteStorage);
        quoteRequest.setQuoteId(quoteStorage.getId());
        quoteStorage.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
        return quoteStorageService.save(quoteStorage);
    }

    public QuoteStorage updateQuoteStorage(QuoteStorage quoteStorage, QuoteRequest quoteRequest) {
        quoteRequest.setQuoteId(quoteStorage.getId());
        quoteStorage.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
        User user = SecurityHelper.getCurrentUser();
        quoteStorage.setUser(user);
        return quoteStorageService.save(quoteStorage);
    }

    public void updateQuoteInfoMapFromDatabase(String quoteId) {
        QuoteInfo quoteInfo = quoteInfoMap.get(quoteId);
        if(quoteInfo != null) {
            QuoteStorage quoteStorage = quoteStorageService.findOne(quoteId);
            quoteInfo.setQuoteRequest(quoteStorage.getQuoteRequestObj());
            quoteInfoMap.put(quoteId, quoteInfo);
        }
    }

    //private final Map<String, FilteredProducts> baseResults = new ConcurrentHashMap<>();

    public JsonSearchResult startBaseProductsRequest(QuoteInfo quoteInfo, QuoteRequest quoteRequest) {
        QuoteRequest clearCategoriesQuoteRequest = QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest);
        UUID requestId = this.productService.startQuoteRequests(clearCategoriesQuoteRequest);
//        synchronized (this.baseResults){
//            this.baseResults.put(quoteInfo.getQuoteUuid(), new FilteredProducts().setAsyncRequestId(requestId));
//        }
        return new JsonSearchResult(quoteRequest, requestId.toString());
    }

    public JsonSearchResult startFilterProductsRequest(QuoteInfo quoteInfo, QuoteRequest quoteRequest) {
//        synchronized (this.baseResults){
//            if(!this.baseResults.containsKey(quoteInfo.getQuoteUuid())){
//                QuoteRequest clearCategoriesQuoteRequest = QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest);
//                UUID baseRequestId = this.productService.startQuoteRequests(clearCategoriesQuoteRequest);
//                this.baseResults.put(quoteInfo.getQuoteUuid(), new FilteredProducts().setAsyncRequestId(baseRequestId));
//            }
//        }
        /*
        synchronized (quoteInfo) {
            if (!quoteInfo.hasBaseProducts()) {
                QuoteRequest clearCategoriesQuoteRequest = QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest);
                UUID baseRequestId = this.productService.startQuoteRequests(clearCategoriesQuoteRequest);
                quoteInfo.setBaseProducts(new FilteredProducts().setAsyncRequestId(baseRequestId));
            }
            if (!quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
                QuoteRequest quoteRequestWithCancellation = QuoteRequest.newInstance(quoteRequest);
                quoteRequestWithCancellation.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
                UUID uuid = this.productService.startQuoteRequests(quoteRequestWithCancellation);
                quoteInfo.setProductsWithCancellation(new FilteredProducts().setAsyncRequestId(uuid));
            }
        }
        */
        UUID requestId = this.productService.startQuoteRequests(quoteRequest);
        String rid = requestId.toString();
        return new JsonSearchResult(quoteRequest, rid);
    }

    public JsonSearchResult getJsonSearchResult(QuoteInfo quoteInfo, QuoteRequest quoteRequest, UUID requestId) {
        FilteredProducts baseProducts = quoteInfo.getBaseProducts(); //this.baseResults.get(quoteInfo.getQuoteUuid());
        if (baseProducts == null) {
            baseProducts = this.productService.getFilteredProductsAsync(QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest), requestId);
        } else if (!baseProducts.isComplete() && baseProducts.getAsyncRequestId() != null) {
            baseProducts = this.productService.getFilteredProductsAsync(QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest), baseProducts.getAsyncRequestId());
            synchronized (quoteInfo) {
                FilteredProducts bp = quoteInfo.getBaseProducts(); //this.baseResults.get(quoteInfo.getQuoteUuid());
                if (bp == null || !bp.isComplete()) {
                    quoteInfo.setBaseProducts(baseProducts);
                }
            }
        }

        FilteredProducts filteredProducts = this.productService.getFilteredProductsAsync(quoteRequest, requestId);
        FilteredProducts filteredProductsWithCancellation = filteredProducts;

        if (!quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            synchronized (quoteInfo) {
                if (quoteInfo.hasProductsWithCancellation()) {
                    FilteredProducts products = quoteInfo.getProductsWithCancellation();
                    if (products.isComplete()) {
                        filteredProductsWithCancellation = products;
                    } else {
                        QuoteRequest quoteRequestWithCancellation = QuoteRequest.newInstance(quoteRequest);
                        quoteRequestWithCancellation.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
                        filteredProductsWithCancellation = this.productService.getFilteredProductsAsync(quoteRequestWithCancellation, products.getAsyncRequestId());
                        if (filteredProductsWithCancellation.isComplete()) {
                            quoteInfo.setProductsWithCancellation(filteredProductsWithCancellation);
                        }
                    }
                }
            }
        }
        // added plan types filter
        baseProducts = productService.getFilteredProducts(baseProducts, quoteRequest.getPlanType());
        filteredProducts = productService.getFilteredProducts(filteredProducts, quoteRequest.getPlanType());
        filteredProductsWithCancellation = productService.getFilteredProducts(filteredProductsWithCancellation, quoteRequest.getPlanType());

        return this.getJsonSearchResult(filteredProducts, filteredProductsWithCancellation, quoteInfo, quoteRequest, filteredProducts.isComplete() && baseProducts.isComplete(), baseProducts);
    }


    @Deprecated
    public JsonSearchResult getJsonSearchResult(QuoteInfo quoteInfo, QuoteRequest quoteRequest, JsonSearchResult.JsonSearchResultType type) {
        if (quoteInfo.isOriginal()) {
            type = JsonSearchResult.JsonSearchResultType.ORIGINAL;
        }
        if (type == null) {
            type = JsonSearchResult.JsonSearchResultType.NAN;
        }

        final QuoteRequest clearCategoriesQuoteRequest = QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest);
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Future<List<Product>> baseProductsFuture = Executors.newSingleThreadExecutor().submit(() -> {
            SecurityContextHolder.setContext(securityContext);
            RequestContextHolder.setRequestAttributes(requestAttributes, true);
            return productService.getFilteredProducts(clearCategoriesQuoteRequest).getIncludedProducts();
        });

        FilteredProducts filteredProducts = productService.getFilteredProducts(quoteRequest);
        CategoryReduceResult categoryReduceResult = new CategoryReduceResult(quoteRequest);
        if (type == JsonSearchResult.JsonSearchResultType.ORIGINAL) {
            int c = 0;
            while (CollectionUtils.isEmpty(filteredProducts.getIncludedProducts()) && categoryReduceResult.isReduced()) {
                categoryReduceResult = searchService.updateCategoryReduceResult(categoryReduceResult);
                quoteRequest = categoryReduceResult.getQuoteRequest();
                filteredProducts = productService.getFilteredProducts(quoteRequest);
                if (c++ > 100) {
                    //TODO fix
                    log.error("Unlimited for in reduce ORIGINAL request");
                    break;
                }
            }
            quoteInfo.setOriginalCategoryReduceMessage(categoryReduceResult.getMessage());
        }
        productSortService.sort(filteredProducts, quoteRequest, quoteInfo.getSortOrder());

        List<Product> baseProducts;
        try {
            baseProducts = baseProductsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        FilteredProducts filteredProductsWithCancellation = filteredProducts;
        if (!quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            QuoteRequest quoteRequestWithCancellation = QuoteRequest.newInstance(quoteRequest);
            quoteRequestWithCancellation.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
            filteredProductsWithCancellation = productService.getFilteredProducts(quoteRequestWithCancellation);
        }
        Map<Long, JsonCategory> baseCatalogValues = searchService.getCatalogCategoriesValuesFromProducts(filteredProducts.getIncludedProducts(), baseProducts, filteredProductsWithCancellation.getIncludedProducts(), quoteRequest);
        List<JsonCategory> jsonCategories = searchService.getCategoriesFromProducts(filteredProducts.getIncludedProducts(), filteredProductsWithCancellation.getIncludedProducts(), quoteRequest, baseCatalogValues);
        Product bestPlan = productSortService.getBestPlan(!CollectionUtils.isEmpty(filteredProducts.getIncludedProducts()) ? filteredProducts.getIncludedProducts() : filteredProducts.getExcludedProducts(), quoteRequest);
        boolean emptyIncludedProducts = !CollectionUtils.isEmpty(filteredProducts.getIncludedProducts());
        filteredProducts.getIncludedProducts().remove(bestPlan);
        List<JsonSearchProduct> jsonSearchProducts = searchService.getJsonResponses(filteredProducts.getIncludedProducts(), Collections.<Product>emptyList(), bestPlan, quoteRequest, emptyIncludedProducts);
        return new JsonSearchResult(jsonSearchProducts, jsonCategories, type, quoteRequest, quoteInfo.getOriginalCategoryReduceMessage(),
                showCountAfterEnabledFilter);
    }

    private JsonSearchResult getJsonSearchResult(FilteredProducts filteredProducts, FilteredProducts filteredProductsWithCancellation, QuoteInfo quoteInfo, QuoteRequest quoteRequest, boolean finished, FilteredProducts baseProducts) {
        productSortService.sort(filteredProducts, quoteRequest, quoteInfo.getSortOrder());

        Map<Long, JsonCategory> baseCatalogValues = searchService.getCatalogCategoriesValuesFromProducts(filteredProducts.getIncludedProducts(), baseProducts.getIncludedProducts(), filteredProductsWithCancellation.getIncludedProducts(), quoteRequest);
        List<JsonCategory> jsonCategories = searchService.getCategoriesFromProducts(filteredProducts.getIncludedProducts(), filteredProductsWithCancellation.getIncludedProducts(), quoteRequest, baseCatalogValues);
        boolean emptyIncludedProducts = !CollectionUtils.isEmpty(filteredProducts.getIncludedProducts());
        Product bestPlan = productSortService.getBestPlan(!CollectionUtils.isEmpty(filteredProducts.getIncludedProducts()) ? filteredProducts.getIncludedProducts() : filteredProducts.getExcludedProducts(), quoteRequest);
        filteredProducts.getIncludedProducts().remove(bestPlan);
        List<JsonSearchProduct> jsonSearchProducts = this.searchService.getJsonResponses(filteredProducts.getIncludedProducts(), Collections.<Product>emptyList(), bestPlan, quoteRequest, emptyIncludedProducts);

        return new JsonSearchResult(jsonSearchProducts, jsonCategories, JsonSearchResult.JsonSearchResultType.NAN, quoteRequest, quoteInfo.getOriginalCategoryReduceMessage(), this.showCountAfterEnabledFilter, finished);
    }

    /* FOR COMPARE PAGE */

    public JsonCompareResult startFilterProductsRequest(QuoteInfo quoteInfo, QuoteRequest quoteRequest, List<String> includedPolicies) {
        UUID requestId;
        if(CollectionUtils.isEmpty(includedPolicies) || includedPolicies.size() == 1 && StringUtils.isBlank(includedPolicies.get(0))) {
            requestId = this.productService.startQuoteRequests(quoteRequest);
        } else {
            requestId = this.productService.startQuoteRequests(quoteRequest, includedPolicies);
        }
        String rid = requestId.toString();
        boolean d = quoteRequest.isDepositAndPaymentDates();
        return new JsonCompareResult(d, rid, includedPolicies);
    }

    public CompareResult getCompareResult(QuoteInfo quoteInfo, QuoteRequest quoteRequest, UUID requestId, Set<String> includedPolicies) {

        FilteredProducts baseProducts = quoteInfo.getBaseProducts();
        if (baseProducts == null) {
            baseProducts = this.productService.getFilteredProductsAsync(QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest), requestId);
        } else if (!baseProducts.isComplete() && baseProducts.getAsyncRequestId() != null) {
            baseProducts = this.productService.getFilteredProductsAsync(QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest), baseProducts.getAsyncRequestId());
            synchronized (quoteInfo) {
                FilteredProducts bp = quoteInfo.getBaseProducts();
                if (bp == null || !bp.isComplete()) {
                    quoteInfo.setBaseProducts(baseProducts);
                }
            }
        }

        FilteredProducts filteredProducts = this.productService.getFilteredProductsAsync(quoteRequest, requestId);
        FilteredProducts filteredProductsWithCancellation = filteredProducts;
        if (!quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            synchronized (quoteInfo) {
                if (quoteInfo.hasProductsWithCancellation()) {
                    FilteredProducts products = quoteInfo.getProductsWithCancellation();
                    if (products.isComplete()) {
                        filteredProductsWithCancellation = products;
                    } else {
                        QuoteRequest quoteRequestWithCancellation = QuoteRequest.newInstance(quoteRequest);
                        quoteRequestWithCancellation.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
                        filteredProductsWithCancellation = this.productService.getFilteredProductsAsync(quoteRequestWithCancellation, products.getAsyncRequestId());
                        if (filteredProductsWithCancellation.isComplete()) {
                            quoteInfo.setProductsWithCancellation(filteredProductsWithCancellation);
                        }
                    }
                }
            }
        }

        if(!CollectionUtils.isEmpty(includedPolicies)) {
            //Compare included policies
            baseProducts = productService.getFilteredProducts(baseProducts, includedPolicies, quoteRequest.getPlanType());
            filteredProducts = productService.getFilteredProducts(filteredProducts, includedPolicies, quoteRequest.getPlanType());
            filteredProductsWithCancellation = productService.getFilteredProducts(filteredProductsWithCancellation, includedPolicies, quoteRequest.getPlanType());
        } else {
            //Compare all policies
            baseProducts = productService.getFilteredProducts(baseProducts, quoteRequest.getPlanType());
            filteredProducts = productService.getFilteredProducts(filteredProducts, quoteRequest.getPlanType());
            filteredProductsWithCancellation = productService.getFilteredProducts(filteredProductsWithCancellation, quoteRequest.getPlanType());
        }

        productSortService.sort(filteredProducts, quoteRequest, quoteInfo.getSortOrder());

        return getCompareResult(filteredProducts, filteredProductsWithCancellation, quoteRequest, filteredProducts.isComplete() && baseProducts.isComplete(), baseProducts);
    }

    private CompareResult getCompareResult(FilteredProducts filteredProducts, FilteredProducts filteredProductsWithCancellation,
                                           QuoteRequest quoteRequest, boolean finished, FilteredProducts baseProducts) {
        Map<Long, JsonCategory> baseCatalogValues = searchService.getCatalogCategoriesValuesFromProducts(filteredProducts.getIncludedProducts(), baseProducts.getIncludedProducts(), filteredProductsWithCancellation.getIncludedProducts(), quoteRequest);
        List<JsonCategory> jsonCategories = searchService.getCategoriesFromProducts(filteredProducts.getIncludedProducts(), filteredProductsWithCancellation.getIncludedProducts(), quoteRequest, baseCatalogValues);

        return new CompareResult(filteredProducts.getIncludedProducts(), jsonCategories, finished, this.showCountAfterEnabledFilter);
    }
}