package com.travelinsurancemaster;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author Alexander.Isaenco
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.travelinsurancemaster.model",
        "com.travelinsurancemaster.repository",
        "com.travelinsurancemaster.services",
        "com.travelinsurancemaster.util",
        "com.travelinsurancemaster.web"
})
@Import({WsConfig.class, WebMvcConfig.class, JacksonConfig.class, MailConfig.class, SecurityConfig.class, ServiceConfig.class, CacheConfig.class})
public class TestConfig {
    //TODO fix all client tests, user interface to access
}
