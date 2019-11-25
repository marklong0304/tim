package com.travelinsurancemaster.web.controllers.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.Order;
import com.travelinsurancemaster.model.dto.json.datatable.commission.report.CommissionReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.commission.report.CommissionReportDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.commission.report.CommissionReportJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.report.CommissionReportFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.report.CommissionReportTableService;
import com.travelinsurancemaster.services.export.report.ExcelCommissionReportView;
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

/**
 * Created by Chernov Artur on 30.09.15.
 */
@Controller
@RequestMapping(value = "reports")
public class CommissionReportController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(CommissionReportController.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommissionReportTableService commissionReportTableService;

    @Autowired
    private PolicyMetaService policyMetaService;

    private Map<String, String> commissionsParamMapper = new ConcurrentHashMap<>();

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
        commissionsParamMapper.put("affiliate", "affiliateCommission.affiliate.name");
        commissionsParamMapper.put("userName", "user.name");
        commissionsParamMapper.put("vendor", "policyMeta.vendor.name");
        commissionsParamMapper.put("policy", "policyMeta.displayName");
        commissionsParamMapper.put("policyNumber", "policyNumber");
        commissionsParamMapper.put("traveler", "purchaseQuoteRequest.primaryTraveler.firstName");
        commissionsParamMapper.put("totalPrice", "totalPrice");
        commissionsParamMapper.put("purchaseDate", "purchaseDate");
        commissionsParamMapper.put("expectedCommission", "vendorCommission.expectedCommission");
        commissionsParamMapper.put("confirm", "vendorCommission.confirm");
        commissionsParamMapper.put("checkNumber", "vendorCommission.checkNumber");
        commissionsParamMapper.put("receivedCommission", "vendorCommission.receivedCommission");
        commissionsParamMapper.put("receivedDate", "vendorCommission.receivedDate");
        commissionsParamMapper.put("note", "note");
        commissionsParamMapper.put("id", "purchaseUuid");
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public String list() {
        return "report/commissionReport";
    }

    @RequestMapping(value = "/purchase/MyCommission/{purchaseUuid}", method = RequestMethod.GET)
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
        model.addAttribute("backUrl", "/reports/commission");
        return "admin/purchase/view";
    }

    @RequestMapping(value = "/commission/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public CommissionReportDataTableJsonResponse getCommissionJsonData(@RequestBody String payload) {
        CommissionReportDataTableJsonRequest request = JsonUtils.getObject(payload, CommissionReportDataTableJsonRequest.class);
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
                    commissionsParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);
        PageRequest pageRequest = new PageRequest(request.getStart() / request.getLength(), request.getLength(), sortSpec);
        CommissionReportFilter commissionReportFilter = commissionReportTableService.getCommissionReportFilter(request);
        List<Purchase> purchasesList = purchaseService.getAllSuccessByCommissionReportFilter(commissionReportFilter, sortSpec);
        Page<Purchase> purchases = purchaseService.getAllSuccessByCommissionReportFilter(commissionReportFilter, pageRequest);

        List<CommissionReportJson> commissionReportJsonList = new ArrayList<>();
        for (Purchase purchase : purchases) {
            CommissionReportJson commissionReportJson = new CommissionReportJson(purchase);
            commissionReportTableService.fillEditable(commissionReportJson, purchase);
            commissionReportJsonList.add(commissionReportJson);
        }
        CommissionReportDataTableJsonResponse commissionReportDataTableJsonResponse = new CommissionReportDataTableJsonResponse();
        commissionReportDataTableJsonResponse.setRecordsTotal((int) purchases.getTotalElements());
        commissionReportDataTableJsonResponse.setRecordsFiltered((int) purchases.getTotalElements());
        commissionReportDataTableJsonResponse.setData(commissionReportJsonList);
        commissionReportDataTableJsonResponse.sendFilterOptions(purchasesList);
        return commissionReportDataTableJsonResponse;
    }

    @RequestMapping(value = "/commission/export", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public ModelAndView downloadExcel(@RequestParam("filterData") String filterData, Model model) {
        CommissionReportDataTableJsonRequest request = JsonUtils.getObject(filterData, CommissionReportDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE) && !currentUser.hasRole(Role.ROLE_ADMIN)) {
            request.setAffiliates(Collections.singletonList(currentUser.getId()));
        }
        CommissionReportFilter commissionReportFilter = commissionReportTableService.getCommissionReportFilter(request);
        List<Purchase> purchases = purchaseService.getAllSuccessByCommissionReportFilter(commissionReportFilter);
        return new ModelAndView(new ExcelCommissionReportView(), "purchases", purchases);
    }
}

