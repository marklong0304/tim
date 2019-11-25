package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.GroupNamesProvidersPlanPage;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.dto.cms.page.*;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.CategoryCacheService;
import com.travelinsurancemaster.services.PolicyMetaCategoryService;
import com.travelinsurancemaster.services.QuoteRequestService;
import com.travelinsurancemaster.services.QuoteStorageService;
import com.travelinsurancemaster.services.cms.*;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.TextUtils;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Chernov Artur on 24.07.15.
 */

@Controller
public class ViewPageController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ViewPageController.class);

    @Autowired
    private PageService pageService;

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private CategoryContentService categoryContentService;

    @Autowired
    private FilingClaimPageService filingClaimPageService;

    @Autowired
    private FilingClaimContactService filingClaimContactService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @RequestMapping(value = "/page/{pageName}", method = RequestMethod.GET)
    public String getPages(@PathVariable("pageName") String name, Model model) {
        log.debug("Getting page by name: {}", name);
        User currentUser = SecurityHelper.getCurrentUser();

        if ("providers".equals(name)) {
            return getProvidersPage(model, currentUser);
        }
        if ("glossary".equals(name)) {
            return getGlossaryPage(model);
        }
        if ("filingClaim".equals(name)) {
            return getFilingClaimPage(model, currentUser);
        }

        if (isDebug()
                && Arrays.asList("glossary_page", "provider_page", "provider_plan", "providers_list", "filingClaim", "filing_claim_provider", "affiliate_page", "affiliate_form").contains(name)) {
            return PageService.TEMPLATE_RAW_PATH + name;
        }

        Page page = pageService.getPage(name);
        if (page == null || (!Objects.equals(page.getStatus(), PageStatus.PUBLISHED) && (currentUser == null || (
                !currentUser.hasRole(Role.ROLE_ADMIN) && !currentUser.hasRole(Role.ROLE_CONTENT_MANAGER))))) {
            return "redirect:/404";
        }
        model.addAttribute("page", page);
        return pageService.getPageTemplatePath(page);
    }

    @RequestMapping(value = "/page/filingClaim/{pageName}", method = RequestMethod.GET)
    public String getFilingClaimDetail(@PathVariable("pageName") String name, Model model) {
        FilingClaimPage page = filingClaimPageService.getFilingClaimPage(name);
        if (page == null) {
            return "redirect:/404";
        }

        List<FilingClaimContact> contacts = filingClaimContactService.getFilingClaimContactsByPage(page);

        for(FilingClaimContact contact : contacts) {
            List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(page.getVendorPage(), contact);
            contact.setPolicyMetaPages(policyMetaPages);
        }

//        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(page.getVendorPage(), contact);
//        model.addAttribute("policyMetaPages", policyMetaPages);

        model.addAttribute("filingClaimPage", page);
        model.addAttribute("contacts", contacts);
        model.addAttribute("policyMetaPagesCount", policyMetaPageService.countPolicyMetaPages(page.getVendorPage()));
        return PageService.TEMPLATE_PATH + "filingClaimProvider";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("q") String searchStr, Model model) {
        log.debug("Search {}", searchStr);
        List<BasePage> pages = pageService.search(searchStr, 20);
        List<BasePage> vendorPages = vendorPageService.search(searchStr, 20);
        List<BasePage> policyMetaPages = policyMetaPageService.search(searchStr, 20);
        Map<BasePage, String> pagesWithUrl = new HashMap<>();
        for (BasePage page : pages) {
            pagesWithUrl.put(page, page.getName());
        }
        for (BasePage page : vendorPages) {
            pagesWithUrl.put(page, "provider/" + page.getName());
        }
        for (BasePage page : policyMetaPages) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(page.getId());
            pagesWithUrl.put(page, "provider/" + policyMetaPage.getVendorPage().getName() + "/" + page.getName());
        }
        model.addAttribute("searchStr", searchStr);
        model.addAttribute("pages", pagesWithUrl);
        return "cms/page/search";
    }

    private String getProvidersPage(Model model, User currentUser) {
        model.addAttribute("vendorPages", vendorPageService.getVendorPagesMap(currentUser));
        model.addAttribute("categoryContentList", categoryContentService.getCategoryContentList());
        model.addAttribute("providersPage", pageService.getProvidersPage());
        return PageService.TEMPLATE_PATH + "providers_list";
    }

    private String getFilingClaimPage(Model model, User currentUser) {
        model.addAttribute("filingClaimPages", filingClaimPageService.findAllPublishedSortedByName(currentUser));
        return PageService.TEMPLATE_PATH + "filingClaim";
    }

    private String getGlossaryPage(Model model) {
        model.addAttribute("categoryContentList", categoryContentService.getCategoryContentListWithContent());
        return PageService.TEMPLATE_PATH + "glossary";
    }

    @RequestMapping(value = "/page/provider/{vendorPageName}", method = RequestMethod.GET)
    public String getProviderPage(@PathVariable("vendorPageName") String vendorPageName, Model model) {
        User currentUser = SecurityHelper.getCurrentUser();

        VendorPage currentVendorPage = vendorPageService.getVendorPageWithContent(vendorPageName);
        model.addAttribute("vendorPage", currentVendorPage);
        model.addAttribute("policyMetaPages", policyMetaPageService.getPolicyMetaPagesByVendorPageSorted(currentVendorPage));
        model.addAttribute("vendorPages", vendorPageService.getVendorPagesMap(currentUser));
        model.addAttribute("categoryContentList", categoryContentService.getCategoryContentList());
        return PageService.TEMPLATE_PATH + "provider_page";
    }

    @RequestMapping(value = "/page/provider/{vendorPageName}/{policyMetaPageName}", method = RequestMethod.GET)
    public String getProviderPage(@PathVariable("vendorPageName") String vendorPageName,
                                  @PathVariable("policyMetaPageName") String policyMetaPageName,
                                  HttpServletRequest httpServletRequest, Model model) {
        PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPageWithContent(policyMetaPageName);
        if (policyMetaPage == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaPage.getPolicyMeta();
        Certificate certificate = null;
        QuoteRequest quoteRequest = quoteStorageService.getCurrentQuoteRequest(httpServletRequest);
        //find not default certificate
        certificate = certificateService.getCertificate(quoteRequest, policyMeta, httpServletRequest, true);
        User currentUser = SecurityHelper.getCurrentUser();

        model.addAttribute("certificate", certificate);
        model.addAttribute("policyMetaPage", policyMetaPage);
        model.addAttribute("vendorPage", policyMetaPage.getVendorPage());
        model.addAttribute("vendorPages", vendorPageService.getVendorPagesMap(currentUser));
        model.addAttribute("policyMetaPages", policyMetaPageService.getPolicyMetaPagesByVendorPageSorted(policyMetaPage.getVendorPage()));
        model.addAttribute("categoryContentList", categoryContentService.getCategoryContentList());
        model.addAttribute("restrictionCMS", !policyMetaPage.getPolicyMetaRestrictionsList().isEmpty()? policyMetaPage.getPolicyMetaRestrictionsList().get(0) : null);
        model.addAttribute("restrictionText", TextUtils.createTextFromRestrictions(policyMeta.getPolicyMetaRestrictions()));
        Map<String, Map<String, PolicyMetaCategoryContent>> groups = new LinkedHashMap<>();
        Map<String, PolicyMetaCategoryContent> policyMetaCategoryHash = new HashMap<>();
        // if there is no valid quoteRequest, then try to get category value by Residence (from ip)
        if (quoteRequest == null) {
            quoteRequest = quoteRequestService.createFakeQuoteRequestByIP(httpServletRequest);
        }
        for (PolicyMetaCategoryContent policyMetaCategoryContent : policyMetaPage.getPolicyMetaCategoryList()) {
            PolicyMetaCategory policyMetaCategory = policyMetaCategoryService.getPolicyMetaCategory(policyMeta.getId(), policyMetaCategoryContent.getCategory().getCode(), quoteRequest);
            policyMetaCategoryContent.setValuesFromPolicyMetaApi(
                    policyMetaCategoryService.getCategoryValuesFilteredByValueRestrictions(policyMetaCategory, quoteRequest));
            if (CollectionUtils.isEmpty(policyMetaCategoryContent.getValuesFromPolicyMetaApi())) {
                continue;
            }
            policyMetaCategoryContent.setUpsaleCategory(policyMetaCategory != null && policyMetaCategory.getType() != null && policyMetaCategory.getType().equals(PolicyMetaCategory.MetaParamType.UP_SALE));
            policyMetaCategoryHash.put(policyMetaCategoryContent.getCategory().getName(), policyMetaCategoryContent);
        }
        Map<String, Category> categoryMap = categoryCacheService.getCategoryMap();
        populateGroups(groups, policyMetaCategoryHash, categoryMap, policyMetaPage);
        model.addAttribute("groups", groups);
        return PageService.TEMPLATE_PATH + "provider_plan";
    }

    private void populateGroups(Map<String, Map<String, PolicyMetaCategoryContent>> groups,
                                Map<String, PolicyMetaCategoryContent> policyMetaCategoryHash,
                                Map<String, Category> categoryMap,
                                PolicyMetaPage policyMetaPage) {
        for (GroupNamesProvidersPlanPage groupNamesProvidersPlanPage : GroupNamesProvidersPlanPage.values()) {
            switch (groupNamesProvidersPlanPage) {
                case GENERAL_PLAN_INFO:
                    Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMapPlanInfo = new LinkedHashMap<>();
                    addSimpleKey(policyMetaCategoryContentMapPlanInfo, "Certificate");
                    addCategoryContent(policyMetaCategoryContentMapPlanInfo, policyMetaCategoryHash, categoryMap,
                            Arrays.asList(CategoryCodes.MONEY_BACK_GUARANTEE, CategoryCodes.RENEWABLE_POLICY));
                    addSimpleKey(policyMetaCategoryContentMapPlanInfo, "Restrictions");
                    policyMetaPage.getPolicyMetaPlanInfoList().forEach(policyMetaCategoryContent -> {
                        if (!policyMetaCategoryContent.getName().equals("Package")) {
                            policyMetaCategoryContentMapPlanInfo.put(policyMetaCategoryContent.getName(), policyMetaCategoryContent);
                        }
                    });
                    groups.put(groupNamesProvidersPlanPage.getCode(), policyMetaCategoryContentMapPlanInfo);
                    break;
                case TRIP_CANCELLATION:
                    Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMapCancellation = new LinkedHashMap<>();
                    addCategoryContent(policyMetaCategoryContentMapCancellation, policyMetaCategoryHash, categoryMap,
                            Arrays.asList(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_INTERRUPTION,
                                    CategoryCodes.CANCEL_FOR_MEDICAL, CategoryCodes.HURRICANE_AND_WEATHER,
                                    CategoryCodes.TERRORISM, CategoryCodes.FINANCIAL_DEFAULT,
                                    CategoryCodes.EMPLOYMENT_LAYOFF, CategoryCodes.CANCEL_FOR_WORK_REASONS,
                                    CategoryCodes.CANCEL_FOR_ANY_REASON, CategoryCodes.PRE_EX_WAIVER,
                                    CategoryCodes.LOOK_BACK_PERIOD));
                    groups.put(groupNamesProvidersPlanPage.getCode(), policyMetaCategoryContentMapCancellation);
                    break;
                case TRAVEL_MEDICAL:
                    Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMapMedical = new LinkedHashMap<>();
                    addCategoryContent(policyMetaCategoryContentMapMedical, policyMetaCategoryHash, categoryMap,
                            Arrays.asList(CategoryCodes.PRIMARY_MEDICAL, CategoryCodes.EMERGENCY_MEDICAL,
                                    CategoryCodes.MEDICAL_DEDUCTIBLE, CategoryCodes.PRE_EX_WAIVER_ON_TRIP,
                                    CategoryCodes.ACUTE_ONSET,
                                    CategoryCodes.LOOK_BACK_PERIOD, CategoryCodes.MEDICAL_EVACUATION));
                    groups.put(groupNamesProvidersPlanPage.getCode(), policyMetaCategoryContentMapMedical);
                    break;
                case LOSS_OR_DELAY:
                    Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMapLoss = new LinkedHashMap<>();
                    addCategoryContent(policyMetaCategoryContentMapLoss, policyMetaCategoryHash, categoryMap,
                            Arrays.asList(CategoryCodes.MISSED_CONNECTION, CategoryCodes.TRAVEL_DELAY,
                                    CategoryCodes.BAGGAGE_LOSS, CategoryCodes.BAGGAGE_DELAY,
                                    CategoryCodes.NON_MEDICAL_EVACUATION));
                    groups.put(groupNamesProvidersPlanPage.getCode(), policyMetaCategoryContentMapLoss);
                    break;
                case MORE_BENEFITS:
                    Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMapOther = new LinkedHashMap<>();
                    addCategoryContent(policyMetaCategoryContentMapOther, policyMetaCategoryHash, categoryMap,
                            Arrays.asList(CategoryCodes.HAZARDOUS_SPORTS, CategoryCodes.AMATEUR_SPORTS,
                                    CategoryCodes.TWENTY_FOUR_HOUR_AD_AND_D, CategoryCodes.COMMON_CARRIER_AD_AND_D,
                                    CategoryCodes.FLIGHT_ONLY_AD_AND_D, CategoryCodes.RENTAL_CAR,
                                    CategoryCodes.IDENTITY_THEFT, CategoryCodes.TWENTY_FOUR_HOUR_ASSISTANCE_SERVICE,
                                    CategoryCodes.SHENGEN_VISA));
                    addSimpleKey(policyMetaCategoryContentMapOther, "Package");
                    policyMetaPage.getPolicyMetaCustomCategoryList().forEach(policyMetaCategoryContent -> {
                        policyMetaCategoryContentMapOther.put(policyMetaCategoryContent.getName(), policyMetaCategoryContent);
                    });
                    groups.put(groupNamesProvidersPlanPage.getCode(), policyMetaCategoryContentMapOther);
                    break;
            }
        }
    }

    private void addSimpleKey(Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMap, String key) {
        policyMetaCategoryContentMap.put(key, null);
    }

    private void addCategoryContent(
            Map<String, PolicyMetaCategoryContent> policyMetaCategoryContentMap,
            Map<String, PolicyMetaCategoryContent> policyMetaCategoryHash,
            Map<String, Category> categoryMap,
            List<String> categoryCodes) {
        for (String categoryCode : categoryCodes) {
            Category category = categoryMap.get(categoryCode);
            if (category == null) {
                continue;
            }
            policyMetaCategoryContentMap.put(category.getName(), policyMetaCategoryHash.getOrDefault(category.getName(), null));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/page/provider/{vendorPageName}/{policyMetaPageName}/{policyMetaCategoryContentId}", method = RequestMethod.POST)
    public PolicyMetaCategoryContentResult getPolicyMetaCategoryContentCertificate(@PathVariable("policyMetaCategoryContentId") Long policyMetaCategoryContentId, Model model) {
        if (policyMetaCategoryContentId == null) {
            return null;
        }
        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentService.getPolicyMetaCategoryContentCertificate(policyMetaCategoryContentId);
        if (policyMetaCategoryContent == null) {
                return null;
        }
        PolicyMeta policyMeta= null;
        if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null && policyMetaCategoryContent.getPolicyMetaPageForCategory().getPolicyMeta()!= null){
            policyMeta = policyMetaCategoryContent.getPolicyMetaPageForCategory().getPolicyMeta();
        } else if (policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null && policyMetaCategoryContent.getPolicyMetaPageForRestrictions().getPolicyMeta() != null){
            policyMeta = policyMetaCategoryContent.getPolicyMetaPageForRestrictions().getPolicyMeta();
        } else if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null && policyMetaCategoryContent.getPolicyMetaPageForPlanInfo().getPolicyMeta() != null){
            policyMeta = policyMetaCategoryContent.getPolicyMetaPageForPlanInfo().getPolicyMeta();
        } else if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null && policyMetaCategoryContent.getPolicyMetaPageForCustomCategory().getPolicyMeta() != null){
            policyMeta = policyMetaCategoryContent.getPolicyMetaPageForCustomCategory().getPolicyMeta();
        } else if (policyMetaCategoryContent.getPolicyMetaPackage() != null && policyMetaCategoryContent.getPolicyMetaPackage().getPolicyMeta() != null) {
            policyMeta = policyMetaCategoryContent.getPolicyMetaPackage().getPolicyMeta();
        }
        if (policyMeta == null){
            log.debug("Policy meta category not found");
            throw new RuntimeException();
        }

        return new PolicyMetaCategoryContentResult(policyMetaCategoryContent.getName(), policyMetaCategoryContent.getCertificateText(), policyMeta.getVendor().getCode(), policyMeta.getDisplayName());
    }

    @ResponseBody
    @RequestMapping(value = "/page/provider/{vendorPageName}/{policyMetaPageName}/packages/{policyMetaPackageId}", method = RequestMethod.POST)
    public PolicyMetaCategoryContentResult getPolicyMetaCategoryContentCertificateFromPackage(@PathVariable("policyMetaPackageId") Long policyMetaPackageId, Model model) {
        if (policyMetaPackageId == null) {
            return null;
        }

        PolicyMetaCategoryContent content = policyMetaCategoryContentService.getPolicyMetaPackageById(policyMetaPackageId);
        if (content == null) {
            return null;
        }

        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentService.getPolicyMetaCategoryContentCertificate(content.getId());
        PolicyMeta policyMeta = policyMetaCategoryContent.getPolicyMetaPackage().getPolicyMeta();
        if (policyMeta == null) {
            log.debug("Policy meta category not found");
            throw new RuntimeException();
        }
        return new PolicyMetaCategoryContentResult(policyMetaCategoryContent.getName(), policyMetaCategoryContent.getCertificateText(), policyMeta.getVendor().getCode(), policyMeta.getDisplayName());
    }
}
