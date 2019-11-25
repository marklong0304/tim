package com.travelinsurancemaster.web.controllers.report;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.datatable.Order;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportDataTableJsonResponse;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.query.IAffiliateCompanyBalance;
import com.travelinsurancemaster.model.dto.query.IAffiliatePaymentData;
import com.travelinsurancemaster.model.dto.query.IAffiliateUserBalance;
import com.travelinsurancemaster.model.dto.report.SalaryReportFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.datatable.report.SalaryReportService;
import com.travelinsurancemaster.services.export.report.ExcelSalaryReportView;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.*;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
 * Created by Chernov Artur on 29.09.15.
 */
@Controller
@RequestMapping(value = "reports")
public class SalaryReportController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(SalaryReportController.class);

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SalaryReportService salaryReportService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AffiliatePaymentService affiliatePaymentService;

    private Map<String, String> salaryParamMapper = new ConcurrentHashMap<>();

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
        salaryParamMapper.put("affiliate", "affiliateCommission.affiliate.name");
        salaryParamMapper.put("vendor", "policyMeta.vendor.name");
        salaryParamMapper.put("policy", "policyMeta.displayName");
        salaryParamMapper.put("policyNumber", "policyNumber");
        salaryParamMapper.put("totalPrice", "totalPrice");
        salaryParamMapper.put("purchaseDate", "purchaseDate");
        salaryParamMapper.put("expectedSalary", "affiliateCommission.salary");
        salaryParamMapper.put("salary", "affiliateCommission.salaryToPay");
        salaryParamMapper.put("payDate", "affiliateCommission.paid");
        salaryParamMapper.put("note", "note");
        salaryParamMapper.put("company", "affiliateCommission.affiliate.company.name");
        salaryParamMapper.put("id", "purchaseUuid");
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    @RequestMapping(value = "/salary", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("purchase", new Purchase());
        return "report/salaryReport";
    }

    @RequestMapping(value = "/purchase/MySalary/{purchaseUuid}", method = RequestMethod.GET)
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
        model.addAttribute("backUrl", "/reports/salary");
        return "admin/purchase/view";
    }

    @RequestMapping(value = "/salary/get.json", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public SalaryReportDataTableJsonResponse getSalaryJsonData(@RequestBody String payload) {
        SalaryReportDataTableJsonRequest request = JsonUtils.getObject(payload, SalaryReportDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE)) {
            request.setAffiliates(Collections.singletonList(currentUser.getId()));
        }
        SalaryReportFilter salaryReportFilter = salaryReportService.getSalaryReportFilter(request);
        Map<Long, AffiliatePayment> affiliateUserPayments = new HashMap<>();
        Map<Long, AffiliatePayment> affiliateCompanyPayments = new HashMap<>();
        Map<String, SalaryCorrection> balanceSalaryCorrections = new HashMap<>();

        List<Sort.Order> orders = new ArrayList<>();
        for (Order responseOrder : request.getOrder()) {
            Sort.Order order = new Sort.Order(Sort.Direction.fromString(responseOrder.getDir().toUpperCase()),
                    salaryParamMapper.get(request.getColumns().get(responseOrder.getColumn()).getData()));
            orders.add(order);
        }

        Sort sortSpec = new Sort(orders);
        List<Purchase> purchases = purchaseService.getAllSuccessBySalaryReportFilter(salaryReportFilter, sortSpec);
        List<SalaryReportJson> salaryReportJsonList = new ArrayList<>();
        Set<User> affiliateUsers = new HashSet<>();
        Set<Company> affiliateCompanies = new HashSet<>();
        int elements = 0;
        for (Purchase purchase : purchases) {
            if(purchase.getAffiliateCommission() != null && purchase.getVendorCommission() != null && purchase.getVendorCommission().isConfirm()) {
                elements++;
                SalaryReportJson salaryReportJson = new SalaryReportJson(purchase);
                salaryReportService.fillEditable(salaryReportJson);
                salaryReportJsonList.add(salaryReportJson);
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

        List<SalaryCorrection> salaryCorrection = salaryReportService.getSalaryCorrection(salaryReportFilter);
        for (SalaryCorrection correction : salaryCorrection) {
            elements++;
            SalaryReportJson salaryReportJson = new SalaryReportJson(correction);
            salaryReportService.fillEditable(salaryReportJson);
            salaryReportJsonList.add(salaryReportJson);
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
            if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE) && !currentUser.hasRole(Role.ROLE_ADMIN) && affiliateUser.getId().equals(currentUser.getId())){
                SalaryReportJson salaryReportJson = new SalaryReportJson(balanceId--, affiliateUser, affiliateUserBalance.getBalance().toString());
                salaryReportService.fillEditable(salaryReportJson);
                salaryReportJsonList.add(salaryReportJson);
            }
        }

        //Compute affiliate company balances
        List<IAffiliateCompanyBalance> affiliateCompanyBalances = affiliatePaymentService.getAffiliateCompanyBalances();
        for(IAffiliateCompanyBalance affiliateCompanyBalance : affiliateCompanyBalances) {
            Company affiliateCompany = companyService.getById(affiliateCompanyBalance.getAffiliateCompanyId());
            if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE) && !currentUser.hasRole(Role.ROLE_ADMIN)
                    && affiliateCompany.getId().equals(currentUser.getCompany().getId())) {
                SalaryReportJson salaryReportJson = new SalaryReportJson(balanceId--, affiliateCompany, affiliateCompanyBalance.getBalance().toString());
                salaryReportService.fillEditable(salaryReportJson);
                salaryReportJsonList.add(salaryReportJson);
            }
        }

        Collections.sort(salaryReportJsonList, new SalaryReportJsonComparator());

        SalaryReportDataTableJsonResponse salaryReportDataTableJsonResponse = new SalaryReportDataTableJsonResponse();
        salaryReportDataTableJsonResponse.setRecordsTotal(elements);
        salaryReportDataTableJsonResponse.setRecordsFiltered(elements);
        salaryReportDataTableJsonResponse.setData(salaryReportJsonList);
        salaryReportDataTableJsonResponse.sendFilterOptionsJson(salaryReportJsonList);

        return salaryReportDataTableJsonResponse;
    }

    @RequestMapping(value = "/salary/getSalaryInfo/{purchaseUid}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public AffiliateCommission getSalaryInfo(@PathVariable("purchaseUid") String purchaseUid) {
        Purchase purchase = purchaseService.getPurchase(purchaseUid);
        if (purchase == null) {
            return null;
        }
        return purchase.getAffiliateCommission();
    }

    @RequestMapping(value = "/salary/export", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public ModelAndView downloadExcel(@RequestParam("filterData") String filterData, Model model) {
        SalaryReportDataTableJsonRequest request = JsonUtils.getObject(filterData, SalaryReportDataTableJsonRequest.class);
        if (request == null) {
            return null;
        }
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.hasRole(Role.ROLE_AFFILIATE) && !currentUser.hasRole(Role.ROLE_ADMIN)) {
            request.setAffiliates(Collections.singletonList(currentUser.getId()));
        }
        SalaryReportFilter salaryReportFilter = salaryReportService.getSalaryReportFilter(request);
        List<Purchase> purchases = purchaseService.getAllSuccessBySalaryReportFilter(salaryReportFilter);
        return new ModelAndView(new ExcelSalaryReportView(), "purchases", purchases);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
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
        model.addAttribute("backUrl", "/reports/salary");
        model.addAttribute("report", true);
        model.addAttribute("affiliatePaymentDataList", affiliatePaymentDataList);
        model.addAttribute("totalSalaryToPay", totalSalaryToPay);
        model.addAttribute("totalSalaryPaid", totalSalaryPaid);
        model.addAttribute("totalBalance", totalBalance);
        return "admin/salary/balance";
    }

}
