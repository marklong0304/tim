package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.PercentInfo;
import com.travelinsurancemaster.model.security.validator.CompanyValidator;
import com.travelinsurancemaster.services.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/companies")
public class CompanyController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyValidator companyValidator;

    private void setNavigation(Company company, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/companies", "Companies");
        if (company != null && company.getId() != null) {
            map.put("/company/edit/" + String.valueOf(company.getId()), company.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/companies");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String getCompanies(Model model) {
        log.debug("Getting companies");
        model.addAttribute("companies", companyService.getAll());
        return "admin/companies/companies";
    }

    @RequestMapping(value = "/delete/{companyId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String deleteCompany(@PathVariable("companyId") Long companyId) {
        log.debug("Delete company by id={} action", companyId);
        companyService.delete(companyId);
        return "redirect:/companies";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createCompany(Model model) {
        log.debug("Getting create company page");
        Company newCompany = new Company();
        newCompany.setPaymentOption(PaymentOption.CHECK);
        model.addAttribute("company", newCompany);
        setNavigation(newCompany, model);
        return "admin/companies/company_create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String handleCompanyCreateForm(@ModelAttribute("company") Company company, BindingResult bindingResult, Model model) {
        log.debug("Processing company create company={}, bindingResult={}", company, bindingResult);
        companyValidator.validate(company, bindingResult);
        //User user = userService.prepareUser(company);
        if (bindingResult.hasErrors()) {
            setNavigation(new Company(), model);
            return "admin/companies/company_create";
        }
        try {
            companyService.save(company);
        } catch (Exception e) {
            log.warn("Exception occurred when trying to create the company", e);
            setNavigation(new Company(), model);
            return "admin/companies/company_create";
        }
        return "redirect:/companies";
    }

    @RequestMapping(value = "/edit/{companyId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String editCompany(@PathVariable("companyId") Long companyId, Model model) {
        log.debug("Getting edit company page");
        Company company = companyService.getById(companyId);
        if (company == null) {
            return "redirect:/404";
        }

        List<PercentInfo> percentInfos = company.getPercentInfo();
        //Add entry field if compensation type is set and there are no compensation type entries in the database
        if(company.getPercentType() != PercentType.NONE && percentInfos.size() == 0) {
            percentInfos.add(new PercentInfo());
        }
        model.addAttribute("company", company);
        setNavigation(company, model);
        return "admin/companies/company_edit";
    }

    @RequestMapping(value = "/edit/{companyId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String handleCompanyEditForm(@ModelAttribute("company") Company company, @RequestParam Map<String, String> params, BindingResult bindingResult, Model model) {
        log.debug("Processing company update company={}, bindingResult={}", company, bindingResult);
        companyValidator.validate(company, bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(company, model);
            return "admin/companies/company_edit";
        }
        try {
            List<PercentInfo> percentInfos = company.getPercentInfo();
            //Remove empty compensation types
            percentInfos.removeIf(pi -> pi.getValue() == null && pi.getValueFrom() == null && pi.getValueTo() == null && pi.getTextValue() == null);
            //Update company
            company = companyService.save(company);
            //Add entry field if compensation type is set
            percentInfos = company.getPercentInfo();
            if(company.getPercentType() != PercentType.NONE && percentInfos.size() == 0) {
                percentInfos.add(new PercentInfo());
            }
            model.addAttribute("company", company);
        } catch (Exception e) {
            log.warn("Exception occurred when trying to update the company: {}", company.toString(), e);
            setNavigation(company, model);
            return "admin/companies/company_edit";
        }
        setNavigation(company, model);
        addMessageSuccess("Saved", model);
        return "admin/companies/company_edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST, params = {"changePercentType"})
    public String changePercentType(@ModelAttribute("company") Company company, Model model) {
        List<PercentInfo> percentInfos = company.getPercentInfo();
        percentInfos.clear();
        if(company.getPercentType() != PercentType.NONE) {
            percentInfos.add(new PercentInfo());
        }
        setNavigation(company, model);
        return "admin/companies/company_edit";
    }

    @RequestMapping(value = "/edit/{companyId}", method = RequestMethod.POST, params = {"addRange"})
    public String addQuoteRange(@ModelAttribute("company") Company company, Model model) {
        company.getPercentInfo().add(new PercentInfo());
        setNavigation(company, model);
        return "admin/companies/company_edit";
    }

    @RequestMapping(value = "/edit/{companyId}", method = RequestMethod.POST, params = {"removeRange"})
    public String removeQuoteRange(@RequestParam("removeRange") int rangeInd, @ModelAttribute("company") Company company, Model model) {
        company.getPercentInfo().remove(rangeInd);
        setNavigation(company, model);
        return "admin/companies/company_edit";
    }
}