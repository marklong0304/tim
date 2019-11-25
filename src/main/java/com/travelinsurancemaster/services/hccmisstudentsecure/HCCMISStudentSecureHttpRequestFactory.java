package com.travelinsurancemaster.services.hccmisstudentsecure;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by ritchie on 3/17/15.
 */
public class HCCMISStudentSecureHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private final HostnameVerifier hostnameVerifier;

    public HCCMISStudentSecureHttpRequestFactory(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
        }
        super.prepareConnection(connection, httpMethod);
    }
}
