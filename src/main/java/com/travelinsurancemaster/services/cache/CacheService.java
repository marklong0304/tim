package com.travelinsurancemaster.services.cache;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.webservice.common.PolicyMetaQuoteRequestPair;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chernov Artur on 14.08.15.
 */

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    @Value("${cache.prepare.parallel}")
    private boolean parallelPrepareCache;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private InsuranceClientFacade insuranceClientFacade;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyMetaCacheService policyMetaCacheService;

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE}, allEntries = true)
    public void evictProducts() {
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void evictPolicyMetas() {
    }

    @CacheEvict(value = {CacheConfig.CATEGORIES_CACHE}, allEntries = true)
    public void evictCategories() {
    }

    /**
     * Cache preparing performs in @Async for a faster displaying of result page
     */
    @Async
    @Transactional(readOnly = true)
    public void prepareCache(QuoteRequest quoteRequest) {
        log.debug("Start preparing Cache for quoteRequest={}", quoteRequest);
        log.debug("Create new QuoteRequest with clear categories");
        QuoteRequest clearCategoriesQuoteRequest = QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest);
        // enable trip cancellation
        Category tripCancellation = categoryCacheService.getCategory(CategoryCodes.TRIP_CANCELLATION);
        QuoteRequest quoteRequestWithTripCancellation = QuoteRequest.newInstance(clearCategoriesQuoteRequest);
        quoteRequestWithTripCancellation.getCategories().put(tripCancellation.getCode(), tripCancellation.getName());
        // enable trip cancellation and primary medical
        //TODO: Depricated? new design without primary medical and pre-ex weaver
        //Category primaryMedical = categoryService.getByCode(CategoryCodes.PRIMARY_MEDICAL);
        //QuoteRequest quoteRequestWithTripCancellationAndPrimaryMedical = QuoteRequest.newInstance(quoteRequestWithTripCancellation);
        //quoteRequestWithTripCancellationAndPrimaryMedical.getCategories().put(primaryMedical.getCode(), primaryMedical.getName());
        // enable trip cancellation, primary medical and pre-ex waiver
        //Category preExWaiver = categoryService.getByCode(CategoryCodes.PRE_EX_WAIVER);
        //QuoteRequest quoteRequestWithTripCancellationAndPrimaryMedicalAndPreExWaiver = QuoteRequest.newInstance(quoteRequestWithTripCancellationAndPrimaryMedical);
        //quoteRequestWithTripCancellationAndPrimaryMedicalAndPreExWaiver.getCategories().put(preExWaiver.getCode(), preExWaiver.getName());
        if (parallelPrepareCache) {
            log.debug("Staring prepare cache with parallel mode");
            List<QuoteRequest> quoteRequests = new ArrayList<>();
            quoteRequests.add(clearCategoriesQuoteRequest);
            quoteRequests.add(quoteRequestWithTripCancellation);
            //quoteRequests.add(quoteRequestWithTripCancellationAndPrimaryMedical);
            //quoteRequests.add(quoteRequestWithTripCancellationAndPrimaryMedicalAndPreExWaiver);
            warmUpCache(quoteRequests);
        } else {
            log.debug("Staring prepare cache without parallel mode");
            warmUpCache(Collections.singletonList(clearCategoriesQuoteRequest));
            warmUpCache(Collections.singletonList(quoteRequestWithTripCancellation));
            //warmUpCache(Collections.singletonList(quoteRequestWithTripCancellationAndPrimaryMedical));
            //warmUpCache(Collections.singletonList(quoteRequestWithTripCancellationAndPrimaryMedicalAndPreExWaiver));
        }
    }

    private void warmUpCache(List<QuoteRequest> quoteRequests) {
        List<PolicyMetaQuoteRequestPair> policyMetaQuoteRequests = new ArrayList<>();
        for (QuoteRequest quoteRequest : quoteRequests) {
            policyMetaQuoteRequests.addAll(policyMetaService.getPolicyMetaQuoteRequestList(quoteRequest));
        }
        //insuranceClientFacade.quote(policyMetaQuoteRequests);
        this.insuranceClientFacade.startQuoteRequests(policyMetaQuoteRequests);
    }

    @Transactional(readOnly = true)
    public void fillPolicyMetaCache() {
        policyMetaCacheService.getPolicyMetas();
    }

    @Transactional(readOnly = true)
    public void fillCategoryCache() {
        categoryCacheService.getCategoryMap();
    }

    @Transactional(readOnly = true)
    public void invalidateCategoriesAndMetas(){
        evictPolicyMetas();
        evictCategories();
        groupService.invalidateCache();
    }
}
