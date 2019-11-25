package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander.Isaenco
 */
@Service
public class InsuranceClientAsync {

    private static final Logger log = LoggerFactory.getLogger(InsuranceClientAsync.class);


    /**
     * Deprecated async quote for all policy metas
     */
    @Async
    public ListenableFuture<QuoteResult> quoteAsync(QuoteRequest request, PolicyMeta policyMeta, InsuranceVendorApi api, CountDownLatch latch) throws InterruptedException {

        log.info("quoteAsync() with latch {} {} {} {}", request, policyMeta, api.getVendorCode(), latch);

        try {
            return new AsyncResult<>(api.quote(request, policyMeta));
        } finally {
            latch.countDown();
        }
    }

    /**
     * Current async quote for all policy metas
     */
    @Async
    public ListenableFuture<QuoteResult> quoteAsync(QuoteRequest request, PolicyMeta policyMeta, InsuranceVendorApi api) throws InterruptedException {

        log.info("quoteAsync() {} {} {}", request, policyMeta, api.getVendorCode());

        return new AsyncResult<>(api.quote(request, policyMeta));
    }

    /**
     * Async authentication
     */
    @Async
    public ListenableFuture<Boolean> authAsync(InsuranceVendorApi api) throws InterruptedException {

        log.info("authAsync() with vendor {}", api.getVendorCode());

        return new AsyncResult<>(api.auth());
    }
}