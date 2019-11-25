package com.travelinsurancemaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class InsuranceMasterApp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.listeners(new ApplicationListener<ContextRefreshedEvent>() {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                System.out.println("!!!!!!!applicationListener");
            }
        });
        return application.sources(InsuranceMasterApp.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(InsuranceMasterApp.class, args);
    }

    @PostConstruct
    public static void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}