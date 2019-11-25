package com.travelinsurancemaster.services;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Alexander.Isaenco
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    public static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        log(request, body, response);
        return response;
    }

    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        if (log.isTraceEnabled()) {
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(response.getBody(), stringWriter);
            log.trace("Request {} on url {}. Body: \r\n {} \r\n Status {} - {}. Body: \r\n{}", request.getMethod().name(), request.getURI().toString(),
                    new String(body),
                    response.getStatusCode(), response.getStatusText(), stringWriter.toString());
        }
    }
}
