package com.travelinsurancemaster;

/**
 * Created by Chernov Artur on 08.05.15.
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${token.emailRegistration.timeToLive.inMinutes}")
    private String tokenEmailRegistrationDuration;

    @Value("${token.emailVerification.timeToLive.inMinutes}")
    private String tokenEmailVerificationDuration;

    @Value("${token.lostPassword.timeToLive.inMinutes}")
    private String tokenLostPasswordDuration;

    @Value("${token.updateEmail.timeToLive.inMinutes}")
    private Integer tokenUpdateEmailDuration;

    @Value("${token.emailRegistration.verification.url}")
    private String tokenVerificationUrl;

    @Value("${token.restorePassword.url}")
    private String tokenRestorePasswordUrl;

    @Value("${token.updateEmail.url}")
    private String tokenUpdateEmailUrl;

    @Deprecated
    @Value("${application.hostname}")
    private String host;

    @Value("${mail.site.address}") // Should be used instead of host, when all usages of host will be retired.
    private String siteAddress;

    @Value("${mail.from}")
    private String from;

    @Bean
    public JavaMailSender mailSender(@Value("${mail.protocol}")
                                     String protocol,
                                     @Value("${mail.host}")
                                     String host,
                                     @Value("${mail.port}")
                                     int port,
                                     @Value("${mail.smtp.auth}")
                                     String auth,
                                     @Value("${mail.smtp.starttls.enable}")
                                     String starttls,
                                     @Value("${mail.username}")
                                     String username,
                                     @Value("${mail.password}")
                                     String password,
                                     @Value("${mail.smtp.ssl.trust}")
                                     String sslTrust) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.socketFactory.ssl.enable", false);
        mailProperties.put("mail.smtp.starttls.enable", starttls);
        mailProperties.put("mail.smtp.ssl.trust", sslTrust);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }

    public int getEmailRegistrationTokenExpiryTimeInMinutes() {
        return Integer.parseInt(tokenEmailRegistrationDuration);
    }

    public int getEmailVerificationTokenExpiryTimeInMinutes() {
        return Integer.parseInt(tokenEmailVerificationDuration);
    }

    public int getLostPasswordTokenExpiryTimeInMinutes() {
        return Integer.parseInt(tokenLostPasswordDuration);
    }

    public String getTokenVerificationUrl() {
        return tokenVerificationUrl;
    }

    public String getTokenRestorePasswordUrl() {
        return tokenRestorePasswordUrl;
    }

    public String getTokenUpdateEmailUrl() {
        return tokenUpdateEmailUrl;
    }

    public Integer getTokenUpdateEmailDuration() {
        return tokenUpdateEmailDuration;
    }

    @Deprecated
    public String getHost() {
        return host;
    }

    public String getFrom() {
        return from;
    }

    public String getSiteAddress() {
        return siteAddress;
    }
}