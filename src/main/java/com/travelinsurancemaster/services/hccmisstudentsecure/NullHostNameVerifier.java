package com.travelinsurancemaster.services.hccmisstudentsecure;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by ritchie on 3/17/15.
 */
public class NullHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession sslSession) {
        // no checking, returns true for everything
        return true;
    }
}
