package com.travelinsurancemaster.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

/**
 * Created by Vlad on 25.02.2015.
 */
public abstract class AbstractSoapClient extends AbstractInsuranceVendorApi {

    private static final Logger log = LoggerFactory.getLogger(AbstractSoapClient.class);

    private final WebServiceGatewaySupport support;

    public AbstractSoapClient() {
        this.support = new WebServiceGatewaySupport() {
        };
    }

    protected WebServiceTemplate getWebServiceTemplate() {
        return support.getWebServiceTemplate();
    }

    public void setDefaultUri(String uri) {
        support.setDefaultUri(uri);
    }

    public void setMarshaller(Marshaller marshaller) {
        support.setMarshaller(marshaller);
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        support.setUnmarshaller(unmarshaller);
    }

    public void setInterceptors(ClientInterceptor[] interceptors) {
        support.setInterceptors(interceptors);
    }
}
