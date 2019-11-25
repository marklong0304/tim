package com.travelinsurancemaster.web.controllers.report;

import com.travelinsurancemaster.model.PaymentOption;
import com.travelinsurancemaster.model.dto.AffiliatePayment;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.Order;
import com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment.AffiliatePaymentDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment.AffiliatePaymentDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment.AffiliatePaymentJson;
import com.travelinsurancemaster.model.dto.json.datatable.payment.PaymentDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.payment.PaymentDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.payment.PaymentJson;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportJson;
import com.travelinsurancemaster.model.dto.purchase.AffiliatePaymentFilter;
import com.travelinsurancemaster.model.dto.purchase.PaymentFilter;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.report.PaymentReportFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.AffiliatePaymentService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.PaymentService;
import com.travelinsurancemaster.services.datatable.report.PaymentsReportTableService;
import com.travelinsurancemaster.services.export.ExcelPaymentsView;
import com.travelinsurancemaster.services.security.LoginService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "reports")
public class AffiliatePaymentsReportController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(PaymentsReportController.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PaymentsReportTableService paymentsReportTableService;

    @Autowired
    private AffiliatePaymentService affiliatePaymentService;

    private Map<String, String> affiliatePaymentsParamMapper = new ConcurrentHashMap<>();

    @ModelAttribute("users")
    public List<User> getUsers() {
        return userService.getAll();
    }

    @ModelAttribute("vendors")
    public List<Vendor> getVendors() {
        return vendorService.findAllSortedByName();
    }

    @ModelAttribute("policies")
    public List<PolicyMeta> getPolicies() {
        return policyMetaService.getAll();
    }

    @PostConstruct
    public void init() {
        affiliatePaymentsParamMapper.put("affiliateUser", "affiliateUser.name");
        affiliatePaymentsParamMapper.put("affiliateCompany", "affiliateCompany.name");
        affiliatePaymentsParamMapper.put("paymentOption", "paymentOption");
        affiliatePaymentsParamMapper.put("paymentDate", "paymentDate");
        affiliatePaymentsParamMapper.put("total", "total");
        affiliatePaymentsParamMapper.put("id", "id");
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    @RequestMapping(value = "/affiliatePayments", method = RequestMethod.GET)
    public String list() {
        return "report/affiliatePaymentsReport";
    }

    @RequestMapping(value = "/MyPayment/{id}", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public String getAffiliatePaymentPage(Model model, @PathVariable("id") Long id) {
        if (id == null) {
            return "redirect:/404";
        }
        AffiliatePayment affiliatePayment = affiliatePaymentService.get(id);
        if(affiliatePayment.getPaymentOption() == null) affiliatePayment.setPaymentOption(PaymentOption.CHECK);
        User user = loginService.getLoggedInUser();
        model.addAttribute("report", true);
        model.addAttribute("backUrl", "/reports/affiliatePayments");
        model.addAttribute("allowStatusPaidChange", user.hasRole(Role.ROLE_ADMIN) || user.hasRole(Role.ROLE_ACCOUNTANT));
        model.addAttribute("affiliatePayment", affiliatePayment);
        return "admin/affiliatePayments/edit";
    }

    @RequestMapping(value = "/affiliatePayments/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public AffiliatePaymentDataTableJsonResponse getAffiliatePaymentsJsonData(@RequestBody String payload) {
        AffiliatePaymentDataTableJsonRequest request = JsonUtils.getObject(payload, AffiliatePaymentDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }

        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE)) {
            request.setAffiliates(Collections.singletonList(currentUser.getId()));
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(
                    Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    affiliatePaymentsParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData())
            );
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);
        PageRequest pageRequest = new PageRequest(request.getStart() / request.getLength(), request.getLength(), sortSpec);
        AffiliatePaymentFilter affiliatePaymentFilter = affiliatePaymentService.getAffiliatePaymentFilter(request);
        Page<AffiliatePayment> affiliatePayments = affiliatePaymentService.getAllByAffiliatePaymentFilter(affiliatePaymentFilter, pageRequest);
        List<AffiliatePaymentJson> affiliatePaymentsJson = affiliatePayments.getContent().stream().map(ap -> new AffiliatePaymentJson(ap)).collect(Collectors.toList());
        AffiliatePaymentDataTableJsonResponse affiliatePaymentDataTableJsonResponse = new AffiliatePaymentDataTableJsonResponse();
        affiliatePaymentDataTableJsonResponse.setRecordsTotal((int) affiliatePayments.getTotalElements());
        affiliatePaymentDataTableJsonResponse.setRecordsFiltered((int) affiliatePayments.getTotalElements());
        affiliatePaymentDataTableJsonResponse.setData(affiliatePaymentsJson);
        affiliatePaymentDataTableJsonResponse.sendAffiliatePaymentFilterOptionsJson(affiliatePayments.getContent());
        return affiliatePaymentDataTableJsonResponse;
    }
}