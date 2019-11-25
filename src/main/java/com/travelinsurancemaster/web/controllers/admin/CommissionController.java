package com.travelinsurancemaster.web.controllers.admin;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.VendorCommission;
import com.travelinsurancemaster.model.dto.commission.VendorCommissionUpload;
import com.travelinsurancemaster.model.dto.commission.VendorCommissionUploadForm;
import com.travelinsurancemaster.model.dto.json.datatable.Order;
import com.travelinsurancemaster.model.dto.json.datatable.commission.CommissionDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.commission.CommissionDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.commission.CommissionJson;
import com.travelinsurancemaster.model.dto.purchase.CommissionFilter;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.UploadCommissionService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.CommissionTableService;
import com.travelinsurancemaster.services.export.ExcelCommissionView;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.NumberUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chernov Artur on 20.08.15.
 */
@Controller
@RequestMapping(value = "commissions")
public class CommissionController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(CommissionController.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommissionTableService commissionTableService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private UploadCommissionService uploadCommissionService;

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

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public String list(Model model) {
        User currentUser = SecurityHelper.getCurrentUser();
        Boolean disabled = currentUser != null && !currentUser.hasRole(Role.ROLE_ADMIN);
        model.addAttribute("disabledField", disabled);
        return "admin/commissions/list";
    }

    @RequestMapping(value = "/commission/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public CommissionDataTableJsonResponse getCommissionJsonData(@RequestBody String payload) {
        CommissionDataTableJsonRequest request = JsonUtils.getObject(payload, CommissionDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        request.getUpdatedFields().forEach(commissionTableService::updateCommission);
        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    commissionsParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);
        PageRequest pageRequest = new PageRequest(request.getStart() / request.getLength(), request.getLength(), sortSpec);
        CommissionFilter commissionFilter = commissionTableService.getCommissionFilter(request);
        List<Purchase> purchasesList = purchaseService.getAllSuccessByCommissionFilter(commissionFilter, sortSpec);
        Page<Purchase> purchases = purchaseService.getAllSuccessByCommissionFilter(commissionFilter, pageRequest);
        List<CommissionJson> commissionJsonList = new ArrayList<>();
        for (Purchase purchase : purchases) {
            CommissionJson commissionJson = new CommissionJson(purchase);
            commissionTableService.fillEditable(commissionJson, purchase);
            commissionJsonList.add(commissionJson);
        }
        CommissionDataTableJsonResponse commissionDataTableJsonResponse = new CommissionDataTableJsonResponse();
        commissionDataTableJsonResponse.setRecordsTotal((int) purchases.getTotalElements());
        commissionDataTableJsonResponse.setRecordsFiltered((int) purchases.getTotalElements());
        commissionDataTableJsonResponse.setData(commissionJsonList);
        commissionDataTableJsonResponse.sendFilterOptions(purchasesList);
        return commissionDataTableJsonResponse;
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/commission/upload", method = RequestMethod.GET)
    public String getUploadPage(Model model) {
        model.addAttribute("form", new VendorCommissionUploadForm());
        return "admin/commissions/upload";
    }

    public boolean containsVendor(final List<VendorCommissionUpload> list, final String vendor){
        return list.stream().anyMatch(o -> o.getVendor().equals(vendor));
    }

    public boolean containsPolicyNumber(final List<VendorCommissionUpload> list, final String policyNumber){
        return list.stream().anyMatch(o -> o.getPolicyNumber().equals(policyNumber));
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/commission/upload", method = RequestMethod.POST)
    public String upload(@ModelAttribute("form") VendorCommissionUploadForm form, Model model) {
        form.setData(form.getData() != null ? form.getData().trim() : StringUtils.EMPTY);
        List<VendorCommissionUpload> vendorCommissionUploads = uploadCommissionService.create(form.getData());
        List<VendorCommissionUpload> distinctUploads = new ArrayList<>();
        vendorCommissionUploads.forEach(item -> {
            if (containsVendor(distinctUploads, item.getVendor()) && containsPolicyNumber(distinctUploads, item.getPolicyNumber())){
                VendorCommissionUpload vcu = distinctUploads.stream()
                        .filter(o -> o.getVendor().equals(item.getVendor()) && o.getPolicyNumber().equals(item.getPolicyNumber()))
                        .findFirst().get();
                int sum = Integer.parseInt(vcu.getReceivedCommission()) + Integer.parseInt(item.getReceivedCommission());
                vcu.setReceivedCommission(String.valueOf(sum));
            } else {
                distinctUploads.add(item);
            }
        });
        boolean isOk = uploadCommissionService.check(distinctUploads);
        form.setUploadList(distinctUploads);
        model.addAttribute("isOk", isOk);
        return "admin/commissions/upload";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/commission/upload", method = RequestMethod.POST, params = {"save"})
    public String saveUpload(@ModelAttribute("form") VendorCommissionUploadForm form, Model model) {
        for (VendorCommissionUpload upload : form.getUploadList()) {
            if (upload.isConfirm()) {
                List<Purchase> purchases = purchaseService.getByPolicyNumberAndVendorName(upload.getPolicyNumber(), upload.getVendor());
                if (purchases.size() > 1) {
                    log.debug("Multiply purchases by unique vendor and code");
                }
                Purchase purchase = purchases.get(0);
                VendorCommission vendorCommission = purchase.getVendorCommission();
                vendorCommission.setReceivedCommission(NumberUtils.parseBigDecimal(upload.getReceivedCommission()));
                vendorCommission.setConfirm(true);
                vendorCommission.setCheckNumber(upload.getCheckNumber());
                vendorCommission.setReceivedDate(new Date());
                purchaseService.save(purchase);
            }
        }
        return "redirect:/commissions/commission";
    }

    @RequestMapping(value = "/commission/export", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public ModelAndView downloadExcel(@RequestParam("filterData") String filterData, Model model) {
        CommissionDataTableJsonRequest request = JsonUtils.getObject(filterData, CommissionDataTableJsonRequest.class);
        CommissionFilter commissionFilter = commissionTableService.getCommissionFilter(request);
        List<Purchase> purchases = purchaseService.getAllSuccessByCommissionFilter(commissionFilter);
        return new ModelAndView(new ExcelCommissionView(), "purchases", purchases);
    }
}

