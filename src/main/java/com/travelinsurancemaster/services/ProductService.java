package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.FilteredProducts;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.webservice.common.PolicyMetaQuoteRequestPair;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteResult;
import com.travelinsurancemaster.util.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 27.05.15.
 */
@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private InsuranceClientFacade insuranceClientFacade;

    @Autowired
    private EmailService emailService;

    @Value("${mail.quoteMismatch:null}")
    private String quoteMismatchEmail;

    @Value("${mail.quoteMismatch.disabled:false}")
    private boolean quoteMismatchEmailDisabled;

    private static final DecimalFormat moneyFormat = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.US));

    @Deprecated
    @Transactional(readOnly = true)
    public FilteredProducts getFilteredProducts(QuoteRequest quoteRequest) {
        log.debug("getFilteredProducts for {}", quoteRequest);
        List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequestList = policyMetaService.getPolicyMetaQuoteRequestList(quoteRequest);
        List<Product> products = insuranceClientFacade.quote(policyMetaQuoteRequestList, false);
        FilteredProducts filteredProducts = getFilteredProducts(products, quoteRequest);
        log.debug("getFilteredProducts end");
        return filteredProducts;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Product> getQuoteRequestProducts(UUID requestId, InsuranceClientFacade.QuoteRequestResult quoteRequestResult) {
        List<Product> products = quoteRequestResult.getProducts();
        products.addAll(insuranceClientFacade.getQuoteRequestCachedProducts(requestId));
        //Add estimated products
        List<QuoteResult> estimatedQuoteRequestResults = insuranceClientFacade.getEstimatedQuoteRequestResult(requestId);
        for(QuoteResult quoteResult : estimatedQuoteRequestResults) {
            for(Product estimatedProduct : quoteResult.products) {
                //Find estimated product in the list of received from request products
                long policyMetaId = estimatedProduct.getPolicyMeta().getId();
                Optional<Product> optionalMatchedProduct = products.stream().filter(p -> p.getPolicyMeta().getId() == policyMetaId).findFirst();
                if(!optionalMatchedProduct.isPresent()) {
                    //If the estimated product not found add it to the list
                    products.add(estimatedProduct);
                } else {
                    //If the estimated product found check if their prices coincide
                    Product matchedProduct = optionalMatchedProduct.get();
                    if(matchedProduct.getTotalPrice().doubleValue() != estimatedProduct.getTotalPrice().doubleValue()) {
                        //If the prices do not coincide, send email to the admin
                        sendQuoteMismatchEmail(matchedProduct.getPolicyMeta(), matchedProduct.getPolicyMeta().getVendor(),
                                matchedProduct.getTotalPrice(), estimatedProduct.getTotalPrice(), quoteResult.quoteRequest);
                    }
                }
            }
        }
        //Set request id in products
        String rid = requestId.toString();
        if(products.size() > 0) products.forEach(p -> p.setRequestId(rid));
        return products;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Product> getQuoteRequestProducts(UUID requestId) {
        //Get requested products or return null if requestId doesn't exist
        List<Product> products = null;
        InsuranceClientFacade.QuoteRequestResult quoteRequestResult = insuranceClientFacade.getQuoteRequestResult(requestId);
        if(quoteRequestResult != null) {
            products = getQuoteRequestProducts(requestId, quoteRequestResult);
        }
        return products;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public FilteredProducts getFilteredProductsAsync(QuoteRequest quoteRequest, UUID requestId) {
        //Get products for the request if request exists
        InsuranceClientFacade.QuoteRequestResult quoteRequestResult = this.insuranceClientFacade.getQuoteRequestResult(requestId);
        if (quoteRequestResult == null) return new FilteredProducts();
        boolean isFinished = quoteRequestResult.isFinished();
        List<Product> products = getQuoteRequestProducts(requestId, quoteRequestResult);
        //Filter products
        FilteredProducts filteredProducts = getFilteredProducts(products, quoteRequest);
        filteredProducts.setAsyncRequestId(requestId);
        filteredProducts.setComplete(isFinished);
        return filteredProducts;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Product getProductAsync(String policyUniqueCode, String requestId) {
        Product product = null;
        UUID requestUuid = null;
        try{
            requestUuid = requestId != null ? UUID.fromString(requestId) : null;
        } catch (IllegalArgumentException exception) { }
        if(policyUniqueCode != null && requestUuid != null) {
            List<Product> products = getQuoteRequestProducts(requestUuid);
            if(products != null && products.size() > 0) {
                Optional<Product> productWithCode = products.stream().filter(p -> policyUniqueCode.equals(p.getPolicyMeta().getUniqueCode())).findFirst();
                if (productWithCode.isPresent()) {
                    product = productWithCode.get();
                }
            }
        }
        return product;
    }

    public UUID startQuoteRequests(QuoteRequest quoteRequest) {
        List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequestList = policyMetaService.getPolicyMetaQuoteRequestList(quoteRequest);
        return this.insuranceClientFacade.startQuoteRequests(policyMetaQuoteRequestList);
    }

    public UUID startQuoteRequests(QuoteRequest quoteRequest, List<String> includedPolicies) {
        List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequestList = policyMetaService.getPolicyMetaQuoteRequestList(quoteRequest, includedPolicies);
        return this.insuranceClientFacade.startQuoteRequests(policyMetaQuoteRequestList);
    }

    /**
     * Фильтрация списка продуктов по quoteRequest
     */
    private FilteredProducts getFilteredProducts(List<Product> products, QuoteRequest quoteRequest) {
        FilteredProducts filteredProducts = new FilteredProducts();
        products.parallelStream().forEach(product -> {
            PolicyMeta policyMeta = product.getPolicyMeta();
            if (!policyMetaService.isPolicyContainsCategory(policyMeta, quoteRequest)) {
                // add product to excludedProducts
                filteredProducts.addExcludedProduct(product);
            } else {
                //add product to included product
                filteredProducts.addIncludedProduct(product);
            }
        });
        return filteredProducts;
    }

    /**
     * Получение продукта по коду policy и quoteRequest
     */
    @Transactional(readOnly = true)
    public Product getProduct(String uniqueCode, QuoteRequest quoteRequest) {
        return getProduct(uniqueCode, quoteRequest, false);
    }

    @Transactional(readOnly = true)
    public Product getProduct(String uniqueCode, QuoteRequest quoteRequest, boolean withErrors) {
        return getProduct(uniqueCode, quoteRequest, withErrors, false);
    }

    @Transactional(readOnly = true)
    public Product getProduct(String uniqueCode, QuoteRequest quoteRequest, boolean withErrors, boolean ignoreConditionals) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(uniqueCode);
        if (policyMeta == null) {
            return null;
        }
        List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequestList = new ArrayList<>();
        QuoteRequest quoteRequestWithUpsaleForPolicy = policyMetaService.getQuoteRequestWithUpsaleForPolicy(policyMeta,
                quoteRequest, false, ignoreConditionals);
        if (quoteRequestWithUpsaleForPolicy != null) {
            policyMetaQuoteRequestList.add(new PolicyMetaQuoteRequestPair(policyMeta, quoteRequestWithUpsaleForPolicy));
        }
        List<Product> products;
        products = insuranceClientFacade.quote(policyMetaQuoteRequestList, withErrors);
        if (CollectionUtils.isEmpty(products)) {
            return null;
        }
        return products.get(0);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public FilteredProducts getFilteredProducts(FilteredProducts filteredProducts, Set<String> includedPolicies, PlanType planType) {
        FilteredProducts newFilteredProducts = FilteredProducts.newInstance(filteredProducts);
        List<Product> productsAfterFiltering = filteredProducts.getIncludedProducts().stream()
                .filter(product -> policyMetaService.isPolicySupportsPlanType(product.getPolicyMeta().getId(), planType) && includedPolicies.contains(product.getPolicyMeta().getUniqueCode()))
                .collect(Collectors.toList());
        newFilteredProducts.setIncludedProducts(productsAfterFiltering);
        return newFilteredProducts;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public FilteredProducts getFilteredProducts(FilteredProducts filteredProducts, PlanType planType) {
        FilteredProducts newFilteredProducts = FilteredProducts.newInstance(filteredProducts);
        List<Product> productsAfterFiltering = filteredProducts.getIncludedProducts().stream()
                .filter(product -> policyMetaService.isPolicySupportsPlanType(product.getPolicyMeta().getId(), planType))
                .collect(Collectors.toList());
        newFilteredProducts.setIncludedProducts(productsAfterFiltering);
        return newFilteredProducts;
    }

    private void sendQuoteMismatchEmail(PolicyMeta policyMeta, Vendor vendor, BigDecimal returnedQuote, BigDecimal localQuote, QuoteRequest quoteRequest) {

        Context ctx = new Context();
        ctx.setVariable("policyMeta", policyMeta);
        ctx.setVariable("vendor", vendor);
        ctx.setVariable("returnedQuote", moneyFormat.format(returnedQuote));
        ctx.setVariable("localQuote", moneyFormat.format(localQuote));
        ctx.setVariable("quoteRequest", JsonUtils.writeValueAsString(quoteRequest).replace("\n", "<br>"));

        String htmlContent = emailService.getHtmlContent(EmailService.QUOTE_MISMATCH_MAIL, ctx);

        if(!quoteMismatchEmailDisabled && quoteMismatchEmail != null) {
            Integer htmlContentHash = htmlContent.hashCode();
            if (!sentEmailHashes.contains(htmlContentHash)) {
                String subject = "Quote mismatch for " + policyMeta.getDisplayName() + " of " + vendor.getName();
                emailService.sendMail(quoteMismatchEmail, subject, htmlContent);
                sentEmailHashes.add(htmlContentHash);
            }
        }
    }

    @Scheduled(cron = "${mail.cron.schedule}")
    public void clearEmailHistory() {
        sentEmailHashes = new HashSet<>();
    }

    private static Set<Integer> sentEmailHashes = new HashSet<>();
}