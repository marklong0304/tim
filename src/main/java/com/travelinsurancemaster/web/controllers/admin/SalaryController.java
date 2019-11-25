package com.travelinsurancemaster.web.controllers.admin;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.JsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.Order;
import com.travelinsurancemaster.model.dto.json.datatable.salary.*;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryCorrectionJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.purchase.SalaryFilter;
import com.travelinsurancemaster.model.dto.query.IAffiliateCompanyBalance;
import com.travelinsurancemaster.model.dto.query.IAffiliatePaymentData;
import com.travelinsurancemaster.model.dto.query.IAffiliateUserBalance;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.datatable.DataTableField;
import com.travelinsurancemaster.model.webservice.common.validator.PurchaseQuoteRequestValidator;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.datatable.SalaryTableService;
import com.travelinsurancemaster.services.datatable.report.SalaryReportService;
import com.travelinsurancemaster.services.export.ExcelSalaryView;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.NumberUtils;
import com.travelinsurancemaster.util.SalaryJsonComparator;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chernov Artur on 20.08.15.
 */
@Controller
@RequestMapping(value = "commissions")
public class SalaryController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(SalaryController.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SalaryTableService salaryTableService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PurchaseQuoteRequestValidator purchaseQuoteRequestValidator;

    @Autowired
    private SalaryReportService salaryReportService;

    @Autowired
    private AffiliatePaymentService affiliatePaymentService;

    private Map<String, String> salaryParamMapper = new ConcurrentHashMap<>();

    private List<User> users;

    private List<Vendor> vendors;

    private List<PolicyMeta> policies;

    @PostConstruct
    public void init() {
        salaryParamMapper.put("affiliate", "affiliateCommission.affiliate.name");
        salaryParamMapper.put("vendor", "policyMeta.vendor.name");
        salaryParamMapper.put("policy", "policyMeta.displayName");
        salaryParamMapper.put("policyNumber", "policyNumber");
        salaryParamMapper.put("totalPrice", "totalPrice");
        salaryParamMapper.put("purchaseDate", "purchaseDate");
        salaryParamMapper.put("expectedSalary", "affiliateCommission.salary");
        salaryParamMapper.put("receivedCommission", "vendorCommission.receivedCommission");
        salaryParamMapper.put("salary", "affiliateCommission.salaryToPay");
        salaryParamMapper.put("payDate", "affiliateCommission.paid");
        salaryParamMapper.put("note", "note");
        salaryParamMapper.put("company", "affiliateCommission.affiliate.company.name");
        salaryParamMapper.put("id", "purchaseUuid");
    }

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

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/salary", method = RequestMethod.GET)
    public String list(Model model) {
        User currentUser = SecurityHelper.getCurrentUser();
        Boolean disabled = currentUser != null && !currentUser.hasRole(Role.ROLE_ADMIN);
        model.addAttribute("disabledField", disabled);
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("salaryCorrectionJson", new SalaryCorrectionJson());
        return "admin/salary/list";
    }

    @RequestMapping(value = "/salary/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public SalaryDataTableJsonResponse getSalaryJsonData(@RequestBody String payload) {
        SalaryDataTableJsonRequest request = JsonUtils.getObject(payload, SalaryDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        SalaryFilter salaryFilter = salaryTableService.getSalaryFilter(request);
        Map<Long, AffiliatePayment> affiliateUserPayments = new HashMap<>();
        Map<Long, AffiliatePayment> affiliateCompanyPayments = new HashMap<>();
        Map<String, SalaryCorrection> balanceSalaryCorrections = new HashMap<>();
        request.getUpdatedFields().sort(Comparator.comparing(DataTableField::getName));
        request.getUpdatedFields().forEach(field -> salaryTableService.updateSalary(field, salaryFilter, affiliateUserPayments, affiliateCompanyPayments, balanceSalaryCorrections));
        //affiliateUserPayments.forEach((userId, ap) -> affiliatePaymentService.updateTotal(ap.getId()));
        //affiliateCompanyPayments.forEach((companyId, ap) -> affiliatePaymentService.updateTotal(ap.getId()));

        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    salaryParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);
        List<Purchase> purchases = purchaseService.getAllSuccessBySalaryFilter(salaryFilter, sortSpec);
        List<SalaryJson> salaryJsonList = new ArrayList<>();
        Set<User> affiliateUsers = new HashSet<>();
        Set<Company> affiliateCompanies = new HashSet<>();
        int elements = 0;
        for (Purchase purchase : purchases) {
            if(purchase.getAffiliateCommission() != null && purchase.getVendorCommission() != null && purchase.getVendorCommission().isConfirm()) {
                elements++;
                SalaryJson salaryJson = new SalaryJson(purchase);
                salaryTableService.fillEditable(salaryJson);
                salaryJsonList.add(salaryJson);
                User affiliateUser = purchase.getAffiliateCommission().getAffiliate();
                if(affiliateUser != null) {
                    Company affiliateCompany = affiliateUser.getCompany();
                    if(affiliateCompany == null) {
                        affiliateUsers.add(affiliateUser);
                    } else {
                        affiliateCompanies.add(affiliateCompany);
                    }
                }
            }
        }

        List<SalaryCorrection> salaryCorrection = salaryReportService.getSalaryCorrection(salaryFilter);
        for (SalaryCorrection correction : salaryCorrection) {
            elements++;
            SalaryJson salaryJson = new SalaryJson(correction);
            salaryTableService.fillEditable(salaryJson);
            salaryJsonList.add(salaryJson);
            User affiliateUser = correction.getAffiliate();
            if(affiliateUser != null) {
                Company affiliateCompany = affiliateUser.getCompany();
                if(affiliateCompany == null) {
                    affiliateUsers.add(affiliateUser);
                } else {
                    affiliateCompanies.add(affiliateCompany);
                }
            }
        }

        //Let balance salary corrections have negative ids starting from -1 to distinguish them from existing in the db salary corrections
        Long balanceId = new Long(-1);

        //Compute affiliate user balances
        List<IAffiliateUserBalance> affiliateUserBalances = affiliatePaymentService.getAffiliateUserBalances();
        for(IAffiliateUserBalance affiliateUserBalance : affiliateUserBalances) {
            User affiliateUser = userService.get(affiliateUserBalance.getAffiliateUserId());
            SalaryJson salaryJson = new SalaryJson(balanceId--, affiliateUser, affiliateUserBalance.getBalance().toString());
            salaryTableService.fillEditable(salaryJson);
            salaryJsonList.add(salaryJson);
        }

        //Compute affiliate company balances
        List<IAffiliateCompanyBalance> affiliateCompanyBalances = affiliatePaymentService.getAffiliateCompanyBalances();
        for(IAffiliateCompanyBalance affiliateCompanyBalance : affiliateCompanyBalances) {
            Company affiliateCompany = companyService.getById(affiliateCompanyBalance.getAffiliateCompanyId());
            SalaryJson salaryJson = new SalaryJson(balanceId--, affiliateCompany, affiliateCompanyBalance.getBalance().toString());
            salaryTableService.fillEditable(salaryJson);
            salaryJsonList.add(salaryJson);
        }

        Collections.sort(salaryJsonList, new SalaryJsonComparator());

        SalaryDataTableJsonResponse salaryDataTableJsonResponse = new SalaryDataTableJsonResponse();
        salaryDataTableJsonResponse.setRecordsTotal(elements);
        salaryDataTableJsonResponse.setRecordsFiltered(elements);
        salaryDataTableJsonResponse.setData(salaryJsonList);
        salaryDataTableJsonResponse.sendFilterOptionsJson(salaryJsonList);

        return salaryDataTableJsonResponse;
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/salary/search", method = RequestMethod.GET)
    public String listSalarySearch() {
        return "admin/salary/search";
    }

    @RequestMapping(value = "/salary/search/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public SalarySearchDataTableJsonResponse getSalarySearchJsonData(@RequestBody String payload) {
        SalarySearchDataTableJsonRequest request = JsonUtils.getObject(payload, SalarySearchDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        SalaryFilter salaryFilter = salaryTableService.getSalaryFilter(request);

        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    salaryParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }
        Sort sortSpec = new Sort(orders);

        PageRequest pageRequest = new PageRequest(request.getStart() / request.getLength(), request.getLength(), sortSpec);
        Page<Purchase> purchases = purchaseService.getAllSuccessBySalarySearchFilter(salaryFilter, pageRequest);
        List<SalarySearchJson> salarySearchJsonList = new ArrayList<>();
        int elements = 0;
        for (Purchase purchase : purchases.getContent()) {
            elements++;
            SalarySearchJson salarySearchJson = new SalarySearchJson(purchase);
            salarySearchJsonList.add(salarySearchJson);
        }

        SalarySearchDataTableJsonResponse salarySearchDataTableJsonResponse = new SalarySearchDataTableJsonResponse();
        salarySearchDataTableJsonResponse.setRecordsTotal(elements);
        salarySearchDataTableJsonResponse.setRecordsFiltered(elements);
        salarySearchDataTableJsonResponse.setData(salarySearchJsonList);
        return salarySearchDataTableJsonResponse;
    }

    @RequestMapping(value = "/salary/getSalaryInfo/{purchaseUid}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public AffiliateCommission getSalaryInfo(@PathVariable("purchaseUid") String purchaseUid) {
        Purchase purchase = purchaseService.getPurchase(purchaseUid);
        if (purchase == null) {
            return null;
        }
        return purchase.getAffiliateCommission();
    }

    @RequestMapping(value = "/salary/export", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public ModelAndView downloadExcel(@RequestParam("filterData") String filterData, Model model) {
        SalaryDataTableJsonRequest request = JsonUtils.getObject(filterData, SalaryDataTableJsonRequest.class);
        SalaryFilter salaryFilter = salaryTableService.getSalaryFilter(request);
        List<Purchase> purchases = purchaseService.getAllSuccessBySalaryFilter(salaryFilter);
        return new ModelAndView(new ExcelSalaryView(), "purchases", purchases);
    }


    @RequestMapping(value = "/salary", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public JsonResponse correctSalary(@RequestBody SalaryCorrectionJson salaryCorrectionJson, Model model) {
        salaryReportService.addSalaryCorrection(salaryCorrectionJson);
        return new JsonResponse(true);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    @RequestMapping(value = "/salary/balance/{affiliateId}", method = RequestMethod.GET)
    public String balanceDetails(@PathVariable("affiliateId") Long affiliateId, Model model) {
        User affiliateUser = userService.getFullUserById(affiliateId);
        model.addAttribute("affiliateUser", affiliateUser);
        Company affiliateCompany = affiliateUser.getCompany();
        List<IAffiliatePaymentData> affiliatePaymentDataList;
        if(affiliateCompany == null) {
            affiliatePaymentDataList = affiliatePaymentService.getAffiliateUserPayments(affiliateUser.getId());
        } else {
            model.addAttribute("affiliateCompany", affiliateCompany);
            affiliatePaymentDataList = affiliatePaymentService.getAffiliateCompanyPayments(affiliateCompany.getId());
        }
        BigDecimal totalSalaryToPay = BigDecimal.ZERO,
                totalSalaryPaid = BigDecimal.ZERO,
                totalBalance = BigDecimal.ZERO;
        for(IAffiliatePaymentData affiliatePaymentData : affiliatePaymentDataList) {
            BigDecimal salaryToPay = !affiliatePaymentData.getCancelled() ? NumberUtils.getNotNullValue(affiliatePaymentData.getSalaryToPay()) : BigDecimal.ZERO;
            totalSalaryToPay = totalSalaryToPay.add(salaryToPay);
            totalSalaryPaid = totalSalaryPaid.add(affiliatePaymentData.getSalaryPaid());
            totalBalance = totalBalance.add(salaryToPay.subtract(affiliatePaymentData.getSalaryPaid()));
        }
        model.addAttribute("backUrl", "/commissions/salary");
        model.addAttribute("report", false);
        model.addAttribute("affiliatePaymentDataList", affiliatePaymentDataList);
        model.addAttribute("totalSalaryToPay", totalSalaryToPay);
        model.addAttribute("totalSalaryPaid", totalSalaryPaid);
        model.addAttribute("totalBalance", totalBalance);
        return "admin/salary/balance";
    }

    @RequestMapping(value = "/salary/delete", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
    public ResponseEntity deleteSalaryCorrection(@RequestParam("id") Long id) {
        if (id == null) {
            ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
        salaryReportService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}