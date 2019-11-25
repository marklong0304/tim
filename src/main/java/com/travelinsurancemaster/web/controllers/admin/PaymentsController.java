package com.travelinsurancemaster.web.controllers.admin;

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
import com.travelinsurancemaster.model.dto.purchase.AffiliatePaymentFilter;
import com.travelinsurancemaster.model.dto.purchase.PaymentFilter;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.AffiliatePaymentService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.datatable.PaymentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Alexander.Isaenco
 */

@Controller
@RequestMapping(value = "commissions")
public class PaymentsController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(PaymentsController.class);
    private static final String MY_SALARY = "mysalary";

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AffiliatePaymentService affiliatePaymentService;

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

    private Map<String, String> paymentsParamMapper = new ConcurrentHashMap<>();

    private Map<String, String> affiliatePaymentsParamMapper = new ConcurrentHashMap<>();

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

        affiliatePaymentsParamMapper.put("affiliateUser", "affiliateUser.name");
        affiliatePaymentsParamMapper.put("affiliateCompany", "affiliateCompany.name");
        affiliatePaymentsParamMapper.put("paymentOption", "paymentOption");
        affiliatePaymentsParamMapper.put("paymentDate", "paymentDate");
        affiliatePaymentsParamMapper.put("total", "total");
        affiliatePaymentsParamMapper.put("id", "id");
    }

    @RequestMapping(value = "/payments", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String list(@RequestParam(name = "id", required = false) String id, Model model) {
        if (id != null){
            User user = userService.get(Long.parseLong(id));
            model.addAttribute("userId", id);
            model.addAttribute("fullName", user.getName() + " " + user.getUserInfo().getLastName());
            model.addAttribute("email", user.getEmail());
        }
        User currentUser = SecurityHelper.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        return "admin/payments/list";
    }

    @RequestMapping(value = "/affiliatePayments", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String affiliatePaymentList() {
        return "admin/affiliatePayments/list";
    }

    @RequestMapping(value = "/payments/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public PaymentDataTableJsonResponse getPaymentsJsonData(@RequestBody String payload) {
        PaymentDataTableJsonRequest request = JsonUtils.getObject(payload, PaymentDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(
                    Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    paymentsParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);
        PageRequest pageRequest = new PageRequest(request.getStart() / request.getLength(), request.getLength(), sortSpec);
        PaymentFilter paymentFilter = paymentService.getPaymentFilter(request);
        Page<Purchase> purchases = purchaseService.getAllSuccessByPaymentFilter(paymentFilter, pageRequest);
        List<PaymentJson> paymentsJson = purchases.getContent().stream().map(p -> new PaymentJson(p)).collect(Collectors.toList());
        PaymentDataTableJsonResponse paymentDataTableJsonResponse = new PaymentDataTableJsonResponse();
        paymentDataTableJsonResponse.setRecordsTotal((int) purchases.getTotalElements());
        paymentDataTableJsonResponse.setRecordsFiltered((int) purchases.getTotalElements());
        paymentDataTableJsonResponse.setData(paymentsJson);
        paymentDataTableJsonResponse.sendFilterOptions(purchases.getContent());
        return paymentDataTableJsonResponse;
    }

    @RequestMapping(value = "/affiliatePayments/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public AffiliatePaymentDataTableJsonResponse getAffiliatePaymentsJsonData(@RequestBody String payload) {
        AffiliatePaymentDataTableJsonRequest request = JsonUtils.getObject(payload, AffiliatePaymentDataTableJsonRequest.class);
        if (request == null) {
            return null;
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

    @RequestMapping(value = "/purchase/{type}/{purchaseUuid}", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String getPurchasePage(Model model,
                                  @PathVariable("type") String type,
                                  @PathVariable("purchaseUuid") String purchaseUuid,
                                  @RequestParam(value = "upgraded", required = false, defaultValue = "false") Boolean upgraded
    ) {
        if (type == null || purchaseUuid == null) {
            return "redirect:/404";
        }
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        if(purchase == null) {
            return "redirect:/404";
        }
        purchaseService.fillTravelersFromJson(purchase);
        model.addAttribute("purchase", purchase);
        model.addAttribute("type", type);
        String backUrl;
        if (type.equals(MY_SALARY)) {
            backUrl = "/reports/salary";
        } else {
            backUrl = "/commissions/" + type;
        }
        User currentUser = SecurityHelper.getCurrentUser();
        Boolean disabled = currentUser != null && !currentUser.hasRole(Role.ROLE_ADMIN);
        model.addAttribute("disabledField", disabled);
        model.addAttribute("backUrl", backUrl);
        if(upgraded != null && upgraded) addMessageSuccess("Created an upgraded purchase", model);

        model.addAttribute("resultPage", purchase.getResultPage());
        model.addAttribute("vendors", vendorService.findAllSortedByName());
        model.addAttribute("policies", policyMetaService.getAll());
        return "admin/purchase/view";
    }

    @RequestMapping(value = "/purchase/commission/{purchaseUuid}", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String updatePurchase(
            @Valid @ModelAttribute("purchase") Purchase purchase,
            @RequestParam(value = "primaryTraveler") Integer primaryTravelerIndex,
            @PathVariable("purchaseUuid") String purchaseUuid,
            Model model
    ) {
        if (purchaseUuid == null) return "redirect:/404";

        Purchase savedPurchase = purchaseService.getPurchase(purchaseUuid);
        savedPurchase = purchaseService.updatePurchaseFromForm(savedPurchase, purchase, primaryTravelerIndex, true);
        savedPurchase = purchaseService.save(savedPurchase);

        User currentUser = SecurityHelper.getCurrentUser();
        Boolean disabled = currentUser != null && !currentUser.hasRole(Role.ROLE_ADMIN);
        model.addAttribute("disabledField", disabled);
        model.addAttribute("purchase", savedPurchase);
        model.addAttribute("backUrl", "/commissions/commission");
        addMessageSuccess("Saved", model);

        model.addAttribute("vendors", vendorService.findAllSortedByName());
        model.addAttribute("policies", policyMetaService.getAll());
        return "admin/purchase/view";
    }

    @RequestMapping(value = "/purchase/commission/{purchaseUuid}", method = RequestMethod.POST, params = {"createUpgradePolicy"})
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String createUpgradePurchase(
            @Valid @ModelAttribute("purchase") Purchase purchase,
            @RequestParam(value = "primaryTraveler") Integer primaryTravelerIndex,
            @PathVariable("purchaseUuid") String purchaseUuid
    ) {
        if (purchaseUuid == null) return "redirect:/404";

        Purchase newPurchase = purchaseService.createNewPurchase(purchaseUuid);
        newPurchase = purchaseService.updatePurchaseFromForm(newPurchase, purchase, primaryTravelerIndex, false);
        newPurchase.setPolicyNumber("+" + newPurchase.getPolicyNumber());
        newPurchase = purchaseService.save(newPurchase);
        return "redirect:/commissions/purchase/commission/" + newPurchase.getPurchaseUuid() + "?upgraded=true";
    }

    @RequestMapping(value = "/payments/export", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public ModelAndView downloadExcel(@RequestParam("filterData") String filterData, Model model) {
        PaymentDataTableJsonRequest request = JsonUtils.getObject(filterData, PaymentDataTableJsonRequest.class);
        PaymentFilter paymentFilter = paymentService.getPaymentFilter(request);
        List<Purchase> purchases = purchaseService.getAllSuccessByPaymentFilter(paymentFilter);
        return new ModelAndView(new ExcelPaymentsView(), "purchases", purchases);
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String cancelPurchase(@RequestParam("note") String note, @RequestParam("purchaseUuid") String purchaseUuid, @RequestParam("type") String type, Model model) {
        if (purchaseUuid == null || type == null) {
            return "redirect:/404";
        }
        User currentUser = SecurityHelper.getCurrentUser();
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        purchase.setNote(note);
        purchase.setCancelled(LocalDate.now());
        purchase.setCancelledBy(currentUser.getEmail());
        purchaseService.save(purchase);
        model.addAttribute("purchase", purchase);
        return "redirect:/commissions/purchase/" + type + "/" + purchaseUuid;
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String restorePurchase(@RequestParam("purchaseUuid") String purchaseUuid, @RequestParam("type") String type, Model model) {
        if (purchaseUuid == null || type == null) {
            return "redirect:/404";
        }
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        purchase.setNote(null);
        purchase.setCancelled(null);
        purchase.setCancelledBy(null);
        purchaseService.save(purchase);
        model.addAttribute("purchase", purchase);
        return "redirect:/commissions/purchase/" + type + "/" + purchaseUuid;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public ResponseEntity deletePurchase(@RequestParam("purchaseUuid") String purchaseUuid) {
        if (purchaseUuid == null) {
            ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        purchaseService.delete(purchase.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(value = "/affiliatePayment/{id}", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String getAffiliatePaymentPage(Model model, @PathVariable("id") Long id) {
        if (id == null) {
            return "redirect:/404";
        }
        AffiliatePayment affiliatePayment = affiliatePaymentService.get(id);
        if(affiliatePayment.getPaymentOption() == null) affiliatePayment.setPaymentOption(PaymentOption.CHECK);
        User user = loginService.getLoggedInUser();
        model.addAttribute("report", false);
        model.addAttribute("backUrl", "/commissions/affiliatePayments");
        model.addAttribute("allowStatusPaidChange", user.hasRole(Role.ROLE_ADMIN) || user.hasRole(Role.ROLE_ACCOUNTANT));
        model.addAttribute("affiliatePayment", affiliatePayment);
        return "admin/affiliatePayments/edit";
    }

    @RequestMapping(value = "/affiliatePayment/{id}", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public String updateAffiliatePayment(@Valid @ModelAttribute("affiliatePayment") AffiliatePayment affiliatePayment, @PathVariable("id") Long id, Model model) {
        if (id == null) {
            return "redirect:/404";
        }
        AffiliatePayment savedAffiliatePayment = affiliatePaymentService.get(id);
        if(affiliatePayment.getPaymentOption() == null) affiliatePayment.setPaymentOption(PaymentOption.CHECK);
        User user = loginService.getLoggedInUser();
        model.addAttribute("allowStatusPaidChange", user.hasRole(Role.ROLE_ADMIN) || user.hasRole(Role.ROLE_ACCOUNTANT));
        savedAffiliatePayment = affiliatePaymentService.updateAffiliatePaymentFromForm(savedAffiliatePayment, affiliatePayment);
        savedAffiliatePayment = affiliatePaymentService.saveWithPayments(savedAffiliatePayment);
        model.addAttribute("affiliatePayment", savedAffiliatePayment);
        if(savedAffiliatePayment.getStatusPaid() != null) {
            addMessageSuccess("Saved", model);
        }
        model.addAttribute("report", false);
        model.addAttribute("backUrl", "/commissions/affiliatePayments");
        return "admin/affiliatePayments/edit";
    }
}