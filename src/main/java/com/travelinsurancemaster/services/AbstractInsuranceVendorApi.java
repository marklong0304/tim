package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public abstract class AbstractInsuranceVendorApi implements InsuranceVendorApi {

    private static final Logger log = LoggerFactory.getLogger(AbstractInsuranceVendorApi.class);

    @Autowired
    protected InsuranceMasterApiProperties apiProperties;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    /**
     * @return QuoteResult for each policyMeta
     */
    @Cacheable(value = CacheConfig.PRODUCTS_CACHE)
    public QuoteResult quote(QuoteRequest request, PolicyMeta policyMeta) {
        final long startTime = System.nanoTime();
        final String productCode = policyMeta != null ? policyMeta.getUniqueCode() : "";
        log.debug("Start processing quote request {}-{}", getVendorCode(), productCode);
        try {
            QuoteResult quoteResult;
            PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), request);
            if (policyMetaCode == null) {
                quoteResult = new QuoteResult();
                quoteResult.quoteRequest = request;
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.getErrors().add(new Result.Error("-1", "PolicyMetaCode is null!"));
            } else {
                List<Result.Error> errors = validateQuote(request, policyMeta, policyMetaCode);
                if (CollectionUtils.isNotEmpty(errors)) {
                    String s = StringUtils.join(errors, ", ");
                    log.warn("Invalid params for quote request {}-{}: {}", getVendorCode(), productCode, s);
                    quoteResult = new QuoteResult();
                    quoteResult.quoteRequest = request;
                    quoteResult.getErrors().addAll(errors);
                } else {
                    quoteResult = quoteInternal(request, policyMeta, policyMetaCode);
                }
            }
            if (quoteResult == null) {
                quoteResult = new QuoteResult();
                quoteResult.quoteRequest = request;
                quoteResult.setStatus(Result.Status.ERROR);
                quoteResult.error("-1", "quoteResult is NULL!");
            }
            log.debug("processing quote request {}-{}: {}, {}", getVendorCode(), productCode, quoteResult.getStatus(), quoteResult.getErrorMsg());
            return quoteResult;
        } catch (Exception e) {
            QuoteResult quoteResult = new QuoteResult();
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.error("-1", ExceptionUtils.getRootCauseMessage(e));
            log.error(e.getMessage(), e);
            return quoteResult;
        } finally {
            long duration = (System.nanoTime() - startTime) / 1000000;
            String durationStr = PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
            log.debug("End processing quote request {}-{}: {}", getVendorCode(), productCode, durationStr);
        }
    }

    public QuoteResult quoteEstimate(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) { return null; };

    protected abstract QuoteResult quoteInternal(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode);

    /**
     * @return PurchaseResponse for Product
     */
    final public PurchaseResponse purchase(PurchaseRequest request) {
        Long policyMetaId = request.getProduct().getPolicyMeta().getId();
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMetaId, request.getQuoteRequest());
        if (policyMetaCode == null) {
            log.error("policyMetaCode is null for {} - {}", policyMetaId, request.getQuoteRequest());
            throw new NullPointerException("policyMetaCode is null for " + policyMetaId.toString());
        }
        request.getProduct().setPolicyMetaCode(policyMetaCode);
        log.debug("Start processing purchase {}-{}", getVendorCode(), policyMetaCode.getCode());
        final long startTime = System.nanoTime();
        PurchaseResponse response = null;
        try {
            response = purchaseInternal(request);
            return response;
        } finally {
            long duration = (System.nanoTime() - startTime) / 1000000;
            String durationStr = PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
            log.debug("End processing purchase {}-{}: {}, {}: {}", getVendorCode(), policyMetaCode.getCode(),
                    response != null ? response.getStatus() : "ERROR", response != null ? response.getErrors() : "null response", durationStr);
        }
    }

    protected abstract PurchaseResponse purchaseInternal(PurchaseRequest request);

    public abstract String getVendorCode();

    /**
     * High-level validation of QuoteRequest.
     */
    public final List<Result.Error> validateQuote(QuoteRequest request, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        LocalDate today = DateUtil.getLocalDateNow(request.getTimezoneOffset());
        if (request.getDepartDate().compareTo(today) <= 0) {
            return Collections.singletonList(new Result.Error("-1", "Departure date must be tomorrow or later"));
        }
        PolicyMeta cached = policyMetaService.getCached(policyMeta.getId());
        if (!restrictionService.isValid(request, cached.getPolicyMetaRestrictions())) {
            return Collections.singletonList(new Result.Error("-1", "Invalid request due to policy restrictions"));
        }
        if (!policyMetaService.isPolicyContainsUpsales(cached, request)) {
            return Collections.singletonList(new Result.Error("-1", "Invalid request due to upsale restrictions"));
        }
        return validateQuoteRequest(request, policyMetaCode);
    }

    protected abstract List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode);

    public void filterPolicyUpsaleCategories(List<PolicyMetaCategory> upsaleCategories, Product product, QuoteRequest quoteRequest) {}

    public boolean auth() { return true; }
}