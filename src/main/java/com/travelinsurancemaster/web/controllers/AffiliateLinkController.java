package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.dto.AffiliateLink;
import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.security.validator.CompanyValidator;
import com.travelinsurancemaster.model.security.validator.UserCreateValidator;
import com.travelinsurancemaster.services.AffiliateLinkService;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.CookieService;
import com.travelinsurancemaster.services.cms.PageService;
import com.travelinsurancemaster.services.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Controller
public class AffiliateLinkController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(AffiliateLinkController.class);

    private static enum AffiliateAccountType {INDIVIDUAL, COMPANY}

    private static final String PAGE_CMS_AFFILIATE_PROGRAM_AGREEMENT = "Affiliate-Program-Agreement";

    @Autowired
    private PageService pageService;

    @Autowired
    private AffiliateLinkService affiliateLinkService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserCreateValidator userCreateValidator;

    @Autowired
    private CompanyValidator companyValidator;

    @RequestMapping(value = AffiliateLinkService.AFFILIATE_LINK_URL, method = RequestMethod.GET)
    public String setReferral(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
        if (params.keySet().isEmpty()) {
            return "redirect:/";
        }
        Optional<String> referralCode = Optional.ofNullable(params.keySet().iterator().next());
        if (referralCode.isPresent()) {
            AffiliateLink affiliateLink = affiliateLinkService.get(referralCode.get());
            if (affiliateLink != null) {
                int maxAge = -1;
                if(affiliateLink.getExpiryDate() != null) {
                    maxAge = (int) (affiliateLink.getExpiryDate().getTime() - new Date().getTime()) / 1000;
                }
                cookieService.saveToCookie(AffiliateLinkService.AFFILIATE_LINK_COOKIE, affiliateLink.getCode(), maxAge, request, response);
            }
        }
        return "redirect:/";
    }

    @RequestMapping(value = AffiliateLinkService.AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_URL, method = RequestMethod.GET)
    public String createAffiliateAccountPage(Model model) {
        model.addAttribute("affiliateProgramAgreement",
                pageService.getPage(PAGE_CMS_AFFILIATE_PROGRAM_AGREEMENT).getContent());
        model.addAttribute("affiliateIndividualForm", newUser());
        model.addAttribute("affiliateCompanyForm", newCompanyUser());
        model.addAttribute("defaultAccount", AffiliateAccountType.INDIVIDUAL);
        return "affiliate/create_affiliate_account";
    }

    @RequestMapping(value = AffiliateLinkService.AFFILIATE_CREATE_INDIVIDUAL, method = RequestMethod.POST)
    public String handleCreateAffiliateIndividualAccount(@ModelAttribute("affiliateIndividualForm") User user,
                                                         BindingResult bindingResult,
                                                         Model model) {
        log.debug("Processing create user={}, bindingResult={}", user, bindingResult);

        model.addAttribute("affiliateCompanyForm", newCompanyUser());
        model.addAttribute("affiliateProgramAgreement", pageService.getPage(PAGE_CMS_AFFILIATE_PROGRAM_AGREEMENT).getContent());
        userCreateValidator.validateWithoutEmail(user, bindingResult);
        if(bindingResult.hasErrors()) {
            model.addAttribute("defaultAccount", AffiliateAccountType.INDIVIDUAL);
            return AffiliateLinkService.AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_TEMPLATE;
        }
        try {
            if(userService.getUserByEmail(user.getEmail()) == null) {
                user = userService.registration(user);
            }
            userService.requestAffiliation(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("Exception occurred when trying to create the user", e);
            model.addAttribute("defaultAccount", AffiliateAccountType.INDIVIDUAL);
            return AffiliateLinkService.AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_TEMPLATE;
        }
        return "redirect:/accountInfo";
    }

    @RequestMapping(value = AffiliateLinkService.AFFILIATE_CREATE_COMPANY, method = RequestMethod.POST)
    public String handleCreateAffiliateCompanyAccount(@ModelAttribute("affiliateCompanyForm") User user,
                                                      BindingResult bindingResult, Model model) {
        log.debug("Processing company create company={}, bindingResult={}", user.getCompany(), bindingResult);
        model.addAttribute("affiliateIndividualForm", newUser());
        model.addAttribute("affiliateProgramAgreement", pageService.getPage(PAGE_CMS_AFFILIATE_PROGRAM_AGREEMENT).getContent());
        companyValidator.validate(user.getCompany(), bindingResult);
        userCreateValidator.validateCompanyUser(user, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("defaultAccount", AffiliateAccountType.COMPANY);
            return AffiliateLinkService.AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_TEMPLATE;
        }
        try {
            Company company = companyService.save(user.getCompany());
            user.setCompany(company);
            if(userService.getUserByEmail(user.getEmail()) == null) {
                userService.registration(user);
            } else {
                userService.updateSimpleUser(user);
            }
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ROLE_AFFILIATE);
            roles.add(Role.ROLE_COMPANY_MANAGER);
            userService.addRoles(user, roles);
        } catch (Exception e) {
            log.warn("Exception occurred when trying to create the company", e);
            model.addAttribute("defaultAccount", AffiliateAccountType.COMPANY);
            return AffiliateLinkService.AFFILIATE_CREATE_INDIVIDUAL_OR_COMPANY_ACCOUNT_TEMPLATE;
        }
        return "redirect:/accountInfo";
    }

    private User newCompanyUser(){
        User newUser = newUser();
        newUser.getUserInfo().setPaymentOption(PaymentOption.CHECK);
        newUser.setCompany(newCompany());
        return newUser;
    }

    private User newUser(){
        User newUser = new User();
        newUser.getUserInfo().setPaymentOption(PaymentOption.CHECK);
        return newUser;
    }

    private Company newCompany(){
        Company newCompany = new Company();
        newCompany.setPaymentOption(PaymentOption.CHECK);
        return newCompany;
    }
}
