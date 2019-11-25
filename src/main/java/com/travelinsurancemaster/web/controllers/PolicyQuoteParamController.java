package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyQuoteParam;
import com.travelinsurancemaster.model.dto.PolicyQuoteParamRestriction;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.validator.PolicyQuoteParamValidator;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PolicyQuoteParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by N.Kurennoy on 20.05.2016.
 */

@Controller
@Scope(value = "session")
@RequestMapping(value = "/vendors/policy/quoteParam")
@Secured("ROLE_ADMIN")
public class PolicyQuoteParamController extends AbstractController {
    @Autowired
    private PolicyQuoteParamService policyQuoteParamService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyQuoteParamValidator policyQuoteParamValidator;

    @RequestMapping(value = "/create/{policyMetaId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createPolicyQuoteParam(@PathVariable("policyMetaId") Long policyMetaId, Model model) {
        PolicyMeta policyMeta = this.policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        PolicyQuoteParam quoteParam = new PolicyQuoteParam();
        quoteParam.setPolicyMeta(policyMeta);
        model.addAttribute("quoteParam", quoteParam);
        model.addAttribute("restrictionValidateQuoteRequest", new QuoteRequest());
        model.addAttribute("listParams", quoteParam.getListParams());

        setNavigation(quoteParam,  model);
        return "admin/quoteParam/editQuoteParam";
    }

    @RequestMapping(value = "/create/{policyMetaId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String createPolicyQuoteParam(@Valid @ModelAttribute("quoteParam") PolicyQuoteParam quoteParam, BindingResult bindingResult, Model model) {
        policyQuoteParamValidator.validate(quoteParam, bindingResult);

        if (bindingResult.hasErrors()) {
            setNavigation(quoteParam,  model);
            model.addAttribute("quoteParam", quoteParam);
            model.addAttribute("restrictionValidateQuoteRequest", new QuoteRequest());
            model.addAttribute("listParams", quoteParam.getListParams());
            return "admin/quoteParam/editQuoteParam";
        }

        quoteParam = this.policyQuoteParamService.save(quoteParam);
        model.addAttribute("quoteParam", quoteParam);
        model.addAttribute("listParams", quoteParam.getListParams());

        setNavigation(quoteParam,  model);
        return "redirect:/vendors/policy/quoteParam/edit/" + quoteParam.getId();
    }

    @RequestMapping(value = "/edit/{quoteParamId}", method = RequestMethod.GET)
    public String editPolicyQuoteParam(@PathVariable("quoteParamId") Long quoteParamId,
                             Model model) {
        PolicyQuoteParam quoteParam = this.policyQuoteParamService.getById(quoteParamId);
        if (quoteParam == null) {
            return "redirect:/404";
        }
        model.addAttribute("quoteParam", quoteParam);
        model.addAttribute("listParams", quoteParam.getListParams());

        PolicyQuoteParamRestriction policyQuoteParamRestriction = new PolicyQuoteParamRestriction();
        policyQuoteParamRestriction.setPolicyQuoteParam(quoteParam);
        model.addAttribute("restriction", policyQuoteParamRestriction);
        model.addAttribute("restrictionValidateQuoteRequest", new QuoteRequest());

        setNavigation(quoteParam, model);
        return "admin/quoteParam/editQuoteParam";
    }

    @RequestMapping(value = "/delete/{quoteParamId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String deletePolicyMeta(@PathVariable("quoteParamId") Long quoteParamId) {
        PolicyQuoteParam quoteParam = this.policyQuoteParamService.getById(quoteParamId);
        if (quoteParam == null) {
            return "redirect:/404";
        }
        long policyMetaId = quoteParam.getPolicyMeta().getId();
        this.policyQuoteParamService.delete(quoteParam);
        return "redirect:/vendors/policy/edit/" + policyMetaId;
    }

    private void setNavigation(PolicyQuoteParam policyQuoteParam, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/vendors", "Vendors");
        PolicyMeta policyMeta = policyQuoteParam.getPolicyMeta();
        String backUrl = "/vendors";
        String currentPage = "";
        if (policyMeta != null) {
            Vendor vendor = policyMeta.getVendor();
            if (vendor != null) {
                String vendorUrl = "/vendors/edit/" + String.valueOf(vendor.getId());
                currentPage = "/vendors/policy/quoteParam/create/" + String.valueOf(policyMeta.getId());
                backUrl = "/vendors/policy/edit/" + String.valueOf(policyMeta.getId());
                map.put(vendorUrl, vendor.getName());
                map.put(backUrl, policyMeta.getDisplayName());
            }
        }

        if (policyQuoteParam.getId() != null) {
            map.put("/vendors/policy/quoteParam/edit/" + String.valueOf(policyQuoteParam.getId()), "Policy Quote Param");
        } else {
            map.put(currentPage, "New Policy Quote Param");

        }

        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", backUrl);
    }
}
