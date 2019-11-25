package com.travelinsurancemaster;

import com.travelinsurancemaster.util.ViewLastLog;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.apache.commons.lang3.StringUtils;
import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.resourceresolver.SpringResourceResourceResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;


/**
 * Created by Vlad on 10.02.2015.
 */
@Configuration
@EnableConfigurationProperties
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("${session.maxInactiveInterval}")
    private Integer maxInactiveInterval;

    @Value("${mail.templates.path}")
    private String templatesPath;

    @Bean
    public FilterRegistrationBean encodingFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        registrationBean.setFilter(encodingFilter);
        return registrationBean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /* robots.txt */
        registry.addResourceHandler("/robots.txt")
                .addResourceLocations("classpath:/robots.txt");

        /*sitemap.xml*/
        registry.addResourceHandler("/sitemap.xml")
                .addResourceLocations("classpath:/sitemap.xml");
    }

    @Bean
    public FileTemplateResolver fileTemplateResolver() {
        FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix(!StringUtils.isBlank(templatesPath) ? templatesPath + "/" : "");
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setTemplateMode("HTML5");
        fileTemplateResolver.setCharacterEncoding("UTF-8");
        fileTemplateResolver.setOrder(1);
        return fileTemplateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setPrefix("mails/");
        classLoaderTemplateResolver.setSuffix(".html");
        classLoaderTemplateResolver.setTemplateMode("LEGACYHTML5");
        classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
        classLoaderTemplateResolver.setOrder(2);
        return classLoaderTemplateResolver;
    }

    @Bean
    public TemplateResolver thymeleafTemplateResolver() {
        TemplateResolver resolver = new TemplateResolver();
        resolver.setResourceResolver(thymeleafResourceResolver());
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        resolver.setOrder(3);
        return resolver;
    }

    @Bean
    public SpringResourceResourceResolver thymeleafResourceResolver() {
        return new SpringResourceResourceResolver();
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addTemplateResolver(thymeleafTemplateResolver());
        engine.addTemplateResolver(classLoaderTemplateResolver());
        engine.addTemplateResolver(fileTemplateResolver());
        engine.addDialect(new SpringSecurityDialect());
        engine.addDialect(new LayoutDialect());
        engine.addDialect(new Java8TimeDialect());
        return engine;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html");
        resolver.setRedirectHttp10Compatible(false);
        resolver.setOrder(3);
        return resolver;
    }

    @Bean
    public ServletRegistrationBean logServlet() {
        return new ServletRegistrationBean(new ViewLastLog(), "/admin/log/*");
    }

    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet(), "/admin/db/*");
        registrationBean.setInitParameters(new HashMap<String, String>() {{
            put("webAllowOthers", "true");
        }});
        return registrationBean;
    }

    @Bean
    public DefaultErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }

    @Bean
    public HttpSessionListener sessionTimeoutListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                se.getSession().setMaxInactiveInterval(maxInactiveInterval);
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
            }
        };
    }
}
