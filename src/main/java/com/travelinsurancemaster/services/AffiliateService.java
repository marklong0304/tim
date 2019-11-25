package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.thymeleaf.context.Context;

/**
 * @author Alexander.Isaenco
 */
@Service
public class AffiliateService {
    private static final Logger log = LoggerFactory.getLogger(AffiliateService.class);

    @Autowired
    private EmailService emailService;

    @Value("${mail.affiliate}")
    private String affiliateEmail;

    public void sendAffiliateMail(User user) {
        send(null);
    }

    public void sendAffiliateMail(Company company) {
        send(null);
    }

    private void send(MultiValueMap parameters) {
        final Context ctx = new Context();
        ctx.setVariable("params", parameters);
        final String htmlContent = emailService.getHtmlContent(EmailService.AFFILIATE_MAIL, ctx);
        emailService.sendMail(affiliateEmail, "Affiliate request", htmlContent);
    }


    private MultiValueMap toMap(User user) {
        MultiValueMap map = new LinkedMultiValueMap<>();
     //   map.put("User Affiliate Registration");
        return map;
    }


}
