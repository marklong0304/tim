package com.travelinsurancemaster;

import com.travelinsurancemaster.services.CookieService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by Chernov Artur on 17.04.15.
 */

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends GlobalAuthenticationConfigurerAdapter {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Value("${rest.auth.enabled}")
        private boolean enabledRestAuth;

        protected void configure(HttpSecurity http) throws Exception {
            if (enabledRestAuth) {
                http.antMatcher("/api/**").authorizeRequests().antMatchers("/api/certificate/create", "/api/certificate/update", "/api/certificate/remove"
                        , "/api/certificate/", "/api/certificate/list").hasAnyAuthority("ROLE_API")
                        .and()
                        .httpBasic()
                        .and().csrf().disable();
            } else {
                http.antMatcher("/api/**").authorizeRequests().antMatchers("/api/certificate/create", "/api/certificate/update", "/api/certificate/remove"
                        , "/api/certificate/", "/api/certificate/list").permitAll()
                        .and().csrf().disable();
            }
        }
    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Value("${token.key}")
        private String tokenKey;

        private final CookieService cookieService;
        private final UserDetailsService userDetailsService;
        private final SecurityProperties securityProperties;
        private final DataSource dataSource;


        public FormLoginWebSecurityConfigurerAdapter(CookieService cookieService, UserDetailsService userDetailsService, DataSource dataSource, SecurityProperties securityProperties) {
            this.cookieService = cookieService;
            this.userDetailsService = userDetailsService;
            this.dataSource = dataSource;
            this.securityProperties = securityProperties;
        }

        private String[] cookiesLogoutDelete = {"remember-me", "uid"};

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/purchasedPlans/**", "/accountInfo/**").authenticated()
                    .antMatchers("/", "/details/**", "/results/**", "/comparePlans/**", "/verification/**", "/registration", "/ajaxlogin", "/fonts/**").permitAll()
                    .antMatchers("/api/searchQuote", "/api/step1.json", "/api/step2.json", "/api/step3.json", "/api/removeFromCache",
                            "/api/updateBaseCache", "/api/updateFilteredCache", "/api/login.json", "/api/test.json").permitAll()
                    .antMatchers("/admin/**", "/vendors/**").hasAnyAuthority("ROLE_ADMIN")
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .successHandler(authenticationSuccessHandler())
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .deleteCookies(cookiesLogoutDelete)
                    .logoutSuccessUrl("/")
                    .and()
                    .exceptionHandling().accessDeniedPage("/error")
                    .and()
                    .rememberMe()
                    .rememberMeServices(rememberMeServices())
                    .key(tokenKey)
                    .and()
                    .csrf().requireCsrfProtectionMatcher(new CsrfMatcher("/admin/db/**", "/api/**")) //disable csrf for /db/**
                    .and()
                    .headers() //default headers + XFrame same origin for /db/**
                    .contentTypeOptions()
                    .and()
                    .xssProtection()
                    .and()
                    .cacheControl()
                    .and()
                    .httpStrictTransportSecurity()
                    .and()
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)); //.frameOptions() changed to SAMEORIGIN

            if (securityProperties.isRequireSsl()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

        @Bean
        public AuthenticationSuccessHandler authenticationSuccessHandler() {
            return new SavedRequestAwareAuthenticationSuccessHandler() {
                @Override
                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                    cookieService.removeCookieUid(request, response);
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            };
        }

        @Bean
        public RememberMeServices rememberMeServices() {
            AbstractRememberMeServices tokenRememberMeServices = new PersistentTokenBasedRememberMeServices(tokenKey, userDetailsService, tokenRepository());
            tokenRememberMeServices.setParameter("remember-me");
            tokenRememberMeServices.setCookieName("remember-me");
            tokenRememberMeServices.setTokenValiditySeconds(365 * 24 * 60 * 60);
            return tokenRememberMeServices;
        }

        @Bean
        public PersistentTokenRepository tokenRepository() {
            JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
            jdbcTokenRepository.setDataSource(dataSource);
            return jdbcTokenRepository;
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean(name = "tisAuthenticationManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        //HACK: add separate springSecurityFilterChain_error for DispatcherType.ERROR. Spring autoconfig doesn't support filter for error.
        @Bean
        @ConditionalOnBean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
        public FilterRegistrationBean securityFilterChainRegistrationError(
                @Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) Filter securityFilter,
                SecurityProperties securityProperties) {
            FilterRegistrationBean registration = new FilterRegistrationBean(securityFilter);
            registration.setOrder(securityProperties.getFilterOrder());
            registration.setDispatcherTypes(DispatcherType.ERROR);
            registration.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME + "_error");
            return registration;
        }
    }

    private static final class CsrfMatcher implements RequestMatcher {

        private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
        private List<RequestMatcher> allowedUrls = new ArrayList<>();

        public CsrfMatcher(String... allowedPatterns) {
            for (String allowedPattern : allowedPatterns) {
                allowedUrls.add(new AntPathRequestMatcher(allowedPattern));
            }
        }

        public boolean matches(HttpServletRequest request) {
            for (RequestMatcher allowedUrl : allowedUrls) {
                if (allowedUrl.matches(request) || allowedMethods.matcher(request.getMethod()).matches()) {
                    return false;
                }
            }
            return true;
        }
    }
}
