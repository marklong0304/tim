package com.travelinsurancemaster.services;

import org.springframework.web.client.RestTemplate;

/**
 * @author Alexander.Isaenco
 */
public abstract class AbstractRestClient extends AbstractInsuranceVendorApi {

    protected RestTemplate restTemplate;

    public AbstractRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
