package com.travelinsurancemaster.web.controllers.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.Order;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.payment.report.PaymentReportJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.report.PaymentReportFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.report.PaymentsReportTableService;
import com.travelinsurancemaster.services.export.ExcelPaymentsView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(value = "reports")
public class PaymentsReportController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(PaymentsReportController.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PaymentsReportTableService paymentsReportTableService;

    private Map<String, String> paymentsParamMapper = new ConcurrentHashMap<>();

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
        paymentsParamMapper.put("affiliate", "affiliateCommission.affiliate.name");
        paymentsParamMapper.put("userName", "user.name");
        paymentsParamMapper.put("vendor", "policyMeta.vendor.name");
        paymentsParamMapper.put("policy", "policyMeta.displayName");
        paymentsParamMapper.put("policyNumber", "policyNumber");
        paymentsParamMapper.put("purchaseDate", "purchaseDate");
        paymentsParamMapper.put("totalPrice", "totalPrice");
        paymentsParamMapper.put("traveler", "purchaseQuoteRequest.primaryTraveler.firstName");
        paymentsParamMapper.put("tripCost", "purchaseQuoteRequest.tripCost");
        paymentsParamMapper.put("departDate", "purchaseQuoteRequest.departDate");
        paymentsParamMapper.put("note", "note");
        paymentsParamMapper.put("id", "purchaseUuid");
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    @RequestMapping(value = "/payments", method = RequestMethod.GET)
    public String list() {
        return "report/paymentsReport";
    }

    @RequestMapping(value = "/purchase/MyBooking/{purchaseUuid}", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public String getPurchasePage(Model model, @PathVariable("purchaseUuid") String purchaseUuid) {
        if (purchaseUuid == null) {
            return "redirect:/404";
        }
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        User currentUser = SecurityHelper.getCurrentUser();
        Boolean disabled = currentUser != null && !currentUser.hasRole(Role.ROLE_ADMIN);
        model.addAttribute("disabledField", disabled);
        model.addAttribute("purchase", purchase);
        model.addAttribute("backUrl", "/reports/payments");
        return "admin/purchase/view";
    }

    @RequestMapping(value = "/payments/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public PaymentReportDataTableJsonResponse getPaymentsJsonData(@RequestBody String payload, Model model) {
        PaymentReportDataTableJsonRequest request = JsonUtils.getObject(payload, PaymentReportDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE)) {
            request.setAffiliates(Collections.singletonList(currentUser.getId()));
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    paymentsParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);
        PageRequest pageRequest = new PageRequest(request.getStart() / request.getLength(), request.getLength(), sortSpec);

        PaymentReportFilter paymentReportFilter = paymentsReportTableService.getPaymentReportFilter(request);
        List<Purchase> purchasesList = purchaseService.getAllSuccessByPaymentReportFilter(paymentReportFilter, sortSpec);
        Page<Purchase> purchases = purchaseService.getAllSuccessByPaymentReportFilter(paymentReportFilter, pageRequest);

        List<PaymentReportJson> paymentReportJsonList = new ArrayList<>();
        for (Purchase purchase : purchases) {
            paymentReportJsonList.add(new PaymentReportJson(purchase));
        }
        PaymentReportDataTableJsonResponse paymentReportDataTableJsonResponse = new PaymentReportDataTableJsonResponse();
        paymentReportDataTableJsonResponse.setRecordsTotal((int) purchases.getTotalElements());
        paymentReportDataTableJsonResponse.setRecordsFiltered((int) purchases.getTotalElements());
        paymentReportDataTableJsonResponse.setData(paymentReportJsonList);
        paymentReportDataTableJsonResponse.sendFilterOptions(purchasesList);
        return paymentReportDataTableJsonResponse;
    }

    @RequestMapping(value = "/payments/export", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public ModelAndView downloadExcel(@RequestParam("filterData") String filterData, Model model) {
        PaymentReportDataTableJsonRequest request = JsonUtils.getObject(filterData, PaymentReportDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }

        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE) && !currentUser.hasRole(Role.ROLE_ADMIN)) {
            request.setAffiliates(Collections.singletonList(currentUser.getId()));
        }

        PaymentReportFilter paymentReportFilter = paymentsReportTableService.getPaymentReportFilter(request);
        List<Purchase> purchases = purchaseService.getAllSuccessByPaymentReportFilter(paymentReportFilter);
        return new ModelAndView(new ExcelPaymentsView(), "purchases", purchases);
    }
}
