package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.AffiliateLink;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.repository.AffiliateLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Service
@Transactional
public class AffiliateLinkService {

    private static final Logger log = LoggerFactory.getLogger(AffiliateLinkService.class);

    public static final String AFFILIATE_LINK_COOKIE = "affiliateLinkCode";
    public static final String AFFILIATE_LINK_URL = "/a";
    public static final String AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_URL = "create-affiliate-account";
    public static final String AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_TEMPLATE= "affiliate/create_affiliate_account";
    public static final String AFFILIATE_CREATE_INDIVIDUAL = "create-affiliate-individual";
    public static final String AFFILIATE_CREATE_COMPANY = "create-affiliate-company";

    @Autowired
    private AffiliateLinkRepository affiliateLinkRepository;

    @Autowired
    private CookieService cookieService;

    public AffiliateLink get(Long id) {
        return affiliateLinkRepository.findOne(id);
    }

    @Transactional
    public AffiliateLink get(String code) {
        return affiliateLinkRepository.findByCode(code);
    }

    public AffiliateLink save(AffiliateLink affiliateLink) {
        return affiliateLinkRepository.save(affiliateLink);
    }

    public AffiliateLink get(HttpServletRequest request) {
        Optional<String> affiliateLinkCode = Optional.ofNullable(cookieService.getFromCookie(AFFILIATE_LINK_COOKIE, request));
        if (affiliateLinkCode.isPresent()) {
            return get(affiliateLinkCode.get());
        }
        return null;
    }

    public void deleteFromCookie(HttpServletRequest request, HttpServletResponse response){
        cookieService.removeCookie(AFFILIATE_LINK_COOKIE, request, response);
    }


    public boolean isUserInAffiliateLinkPurchasers(User user, AffiliateLink affiliateLink) {
        return affiliateLink.getPurchasers().stream().filter(p -> p.getUser().getId().equals(user.getId())).findFirst().isPresent();
    }

    public boolean isAffiliateLinkEligible(User user, AffiliateLink affiliateLink) {
        return !affiliateLink.hasExpired() && !isUserInAffiliateLinkPurchasers(user, affiliateLink);
    }
}