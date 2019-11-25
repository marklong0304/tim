package com.travelinsurancemaster.web.controllers.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.report.sales.ReportInterval;
import com.travelinsurancemaster.model.dto.report.sales.SalesRequest;
import com.travelinsurancemaster.model.dto.report.sales.SalesResponse;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.report.SalesService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chernov Artur on 01.10.15.
 */
@Controller
@RequestMapping(value = "reports")
public class SalesReportController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(SalesReportController.class);

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private SalesService salesService;

    @ModelAttribute("users")
    public List<User> getUsers() {
        return userService.getAll();
    }

    @ModelAttribute("vendors")
    public List<Vendor> getVendors() {
        return vendorService.findAllSortedByName();
    }

    @ModelAttribute("policyMetas")
    public List<PolicyMeta> getPolicyMetas() {
        return policyMetaService.getAllSorted(false, false);
    }

    @PostConstruct
    public void init() {
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/sales", method = RequestMethod.GET)
    public String getSalesPage(Model model) {
        model.addAttribute("salesRequest", new SalesRequest());
        return "report/salesReport";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/sales", method = RequestMethod.POST)
    public String post(@ModelAttribute("salesRequest") SalesRequest salesRequest, BindingResult bindingResult, Model model) {
        if (CollectionUtils.isEmpty(salesRequest.getAffiliates())) {
            bindingResult.reject(null, "Affiliates cannot be empty");
        }
        if (CollectionUtils.isEmpty(salesRequest.getPolicyMetas())) {
            bindingResult.reject(null, "Plans cannot be empty");
        }
        if (salesRequest.getInterval() == ReportInterval.DAILY && TimeUnit.DAYS.convert(salesRequest.getTo().getTime() - salesRequest.getFrom().getTime(), TimeUnit.MILLISECONDS) > 30) {
            bindingResult.reject(null, "Date 'to' occurs no more than 30 days after the date 'from'");
        }
        if (bindingResult.hasErrors()) {
            return "report/salesReport";
        }
        SalesResponse salesResponse = salesService.getSalesResponse(salesRequest);
        model.addAttribute("response", salesResponse);
        return "report/salesReportTemplate";
    }
}