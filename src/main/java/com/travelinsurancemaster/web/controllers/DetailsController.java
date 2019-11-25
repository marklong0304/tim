package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContentResult;
import com.travelinsurancemaster.model.dto.json.GroupDTO;
import com.travelinsurancemaster.model.dto.json.JsonSearchProduct;
import com.travelinsurancemaster.model.dto.json.UpsaleResponseDTO;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by ritchie on 5/12/15.
 */
@Controller
@RequestMapping(value = "details")
public class DetailsController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(DetailsController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private QuoteInfoSession quoteInfoSession;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaPackageService policyMetaPackageService;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private UpsaleService upsaleService;

    @ModelAttribute("groups")
    public Map<PlanType, List<GroupDTO>> getGroups() {
        return groupService.getGroupByPlanTypeMap();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String showDetailsPage(@RequestParam Map<String, String> params, Model model, HttpServletRequest httpServletRequest) {
        QuoteRequest quoteRequestParam = null;
        String quoteId = null;
        if (params.containsKey("quoteRequestJson")) {
            quoteRequestParam = JsonUtils.getObject(params.get("quoteRequestJson"), QuoteRequest.class);
        } else {
            quoteId = params.get("quoteId");
            if (quoteId == null) {
                return "redirect:/";
            }
            QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, params.containsKey("restore") ? RestoreType.STORAGE : RestoreType.NAN);
            if (quoteInfo == null) {
                return "redirect:/";
            }
            quoteRequestParam = quoteInfo.getQuoteRequest();
        }
        if (quoteRequestParam == null) {
            return "redirect:/";
        }
        String requestId = params.get("requestId");
        String planCode = params.get("plan");
        QuoteRequest quoteRequest = QuoteRequest.newInstance(quoteRequestParam);
        upsaleService.enabledUpsalesUsingPackagesFromFilters(policyMetaService.getPolicyMetaByUniqueCode(planCode), quoteRequest);
        log.info(" planCode {} quoteRequest {}", planCode, quoteRequest);
        Product selectedProduct = productService.getProduct(planCode, quoteRequest);
        log.info(" selectedProduct {}", selectedProduct);
        if (selectedProduct == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(selectedProduct.getPolicyMeta().getId(), false, true, true, false, true, false, false);
        log.info(" policyMeta {}", policyMeta);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        List<PolicyMetaCategory> policyMetaCategoriesByRestrictions = policyMetaCategoryService.getPolicyMetaCategories(policyMeta.getId(), quoteRequest);
        selectedProduct.getPolicyMeta().setPolicyMetaCategories(policyMetaCategoriesByRestrictions);
        selectedProduct.getPolicyMeta().setPolicyMetaPage(policyMeta.getPolicyMetaPage());
        selectedProduct.getPolicyMeta().setDisplayName(policyMeta.getDisplayName());
        JsonSearchProduct jsonSearchProduct = searchService.getJsonSearchResponse(selectedProduct, quoteRequest, true, true);
        Certificate certificate = certificateService.getCertificate(quoteRequest, policyMeta, httpServletRequest);
        model.addAttribute("certificate", certificate);
        model.addAttribute("product", selectedProduct);

        List<JsonSearchProduct.InnerCategory> left = jsonSearchProduct.getCategories().subList(0, 3);
        List<JsonSearchProduct.InnerCategory> right = jsonSearchProduct.getCategories().subList(3, 6);

        model.addAttribute("productCardLeft", left);
        model.addAttribute("productCardRight", right);

        model.addAttribute("quoteRequest", quoteRequest);
        model.addAttribute("quoteRequestJson", JsonUtils.getJsonString(quoteRequest));
        model.addAttribute("upsaleValues", policyMetaService.getCategoryUpsaleValuesFromProduct(policyMeta, selectedProduct, quoteRequest));
        model.addAttribute("packages", policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequest));
        model.addAttribute("quoteId", quoteId);
        model.addAttribute("requestId", requestId);
        model.addAttribute("settings", systemSettingsService.getDefault());
        model.addAttribute("zeroCost", quoteRequest.isZeroCost());
        model.addAttribute("depositAndPayment", quoteRequest.isDepositAndPaymentDates());
        QuoteRequest requestToApi = quoteRequestService.getQuoteRequestWithCancellation(quoteRequest, policyMeta);
        model.addAttribute("minTripCost", quoteRequestService.checkMinTripCost(quoteRequest, policyMeta));
        model.addAttribute("zeroToApi", requestToApi.isZeroCost());
        model.addAttribute("overrideCancellation", requestToApi.isZeroCost() || quoteRequestService.checkMinTripCost(quoteRequest, policyMeta));
        model.addAttribute("minCost", policyMeta.getMinimalTripCost());
        //Check if there are not empty manual restrictions
        boolean manualRestrictionsExist = false;
        for(PolicyMetaCategoryContent content : policyMeta.getPolicyMetaPage().getPolicyMetaRestrictionsList()) {
            if(content.getCertificateText() != null && content.getCertificateText().length() > 0) {
                manualRestrictionsExist = true;
                break;
            }
        }
        model.addAttribute("manualRestrictionsExist", manualRestrictionsExist);
        if(!manualRestrictionsExist) {
            model.addAttribute("restriction", com.travelinsurancemaster.util.TextUtils.createTextFromRestrictions(policyMeta.getPolicyMetaRestrictions()));
        }
        model.addAttribute("contentCategoriesWithCertificateText", policyMetaService.getContentCategoriesWithCertificateText(policyMeta, quoteRequest));
        return "details/detailsPage";
    }

    @RequestMapping(value = "detailsUpsale", method = RequestMethod.POST)
    @ResponseBody
    public String detailsUpsale(
            @RequestParam String policyUniqueCode,
            @RequestParam(value = "rentalCarStartDate", required = false) @DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_FORMAT) LocalDate rentalCarStartDate,
            @RequestParam(value = "rentalCarEndDate", required = false) @DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_FORMAT) LocalDate rentalCarEndDate,
            @RequestParam Map<String, String> params) {

        QuoteRequest quoteRequestFromParams;
        String quoteId = params.get("quoteId");
        if (params.containsKey("quoteRequestJson")) {
            quoteRequestFromParams = JsonUtils.getObject(params.get("quoteRequestJson"), QuoteRequest.class);
        } else {
            if (quoteId == null) {
                return "error making quote request with new params";
            }
            QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
            if (quoteInfo == null) {
                return "error making quote request with new params";
            }
            quoteRequestFromParams = quoteInfo.getQuoteRequest();
        }
        if(rentalCarStartDate != null) quoteRequestFromParams.setRentalCarStartDate(rentalCarStartDate);
        if(rentalCarEndDate != null) quoteRequestFromParams.setRentalCarEndDate(rentalCarEndDate);
        if (quoteRequestFromParams == null) {
            throw new RuntimeException("no quote request");
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(policyUniqueCode);
        if (policyMeta == null) {
            throw new RuntimeException("no policy meta");
        }
        QuoteRequest quoteRequest = QuoteRequest.newInstance(quoteRequestFromParams);
        quoteRequest = quoteRequestService.fillBaseTripCancellation(quoteRequest);
        boolean hasCancellationGroup = false;

        Map<String, String> values = new HashMap<>();
        Set<Long> activatedPackages = new HashSet<>();

        String changed = params.get("changed");

        for (String paramKey : params.keySet()) {
            if(paramKey.startsWith("p-val.")) {
                continue;
            }
            Category category = categoryService.getByCode(paramKey);
            if (category == null) {
                continue;
            }

            String val = params.get(category.getCode());

            List<PolicyMetaCategoryValue> categoryValues = policyMetaCategoryValueService.getSortedCategoryValues(policyMeta.getId(), category.getCode(), quoteRequest);
            if (CollectionUtils.isEmpty(categoryValues)) {
                return "Error. No values for category found";
            }

            if(!values.containsKey(category.getCode())){
                values.put(category.getCode(), val);
            }

            if (changed != null && category.getCode().equals(changed)) {
                PolicyMetaCategory policyMetaCategory = this.policyMetaCategoryService.getPolicyMetaCategory(policyMeta.getId(), category.getCode(), quoteRequest);
                PolicyMetaPackage policyMetaPackage = this.policyMetaPackageService.findByPolicyMetaPackageValue(policyMeta, policyMetaCategory, val);
                if (policyMetaPackage != null && !activatedPackages.contains(policyMetaPackage.getId()) && this.restrictionService.isValid(quoteRequest, policyMetaPackage.getPolicyMetaPackageRestrictions())) {
                    activatedPackages.add(policyMetaPackage.getId());
                    for (PolicyMetaPackageValue policyMetaPackageValue : policyMetaPackage.getPolicyMetaPackageValues()) {
                        String code = policyMetaPackageValue.getPolicyMetaCategory().getCategory().getCode();
                        values.put(code, policyMetaPackageValue.getValue());
                    }
                }
            }
        }

        UpsaleResponseDTO upsaleResponse = new UpsaleResponseDTO();
        for (String categoryCode : values.keySet()) {
            Category category = categoryService.getByCode(categoryCode);
            if (category == null) {
                continue;
            }
            String val = values.get(categoryCode);
            List<PolicyMetaCategoryValue> categoryValues = policyMetaCategoryValueService.getSortedCategoryValues(policyMeta.getId(), categoryCode, quoteRequest);
            if (CollectionUtils.isEmpty(categoryValues)) {
                return "Error. No values for category found";
            }

            upsaleService.setPlanDescriptionCategoriesCaption(categoryCode, categoryValues, val, upsaleResponse);
            if (PolicyMetaCategoryValue.isEmptyValue(val)) {
                if (quoteRequest.getCategories().containsKey(categoryCode)) {
                    quoteRequest.getCategories().remove(categoryCode);
                }
                continue;
            }
            quoteRequest.getCategories().put(categoryCode, val);
            hasCancellationGroup = GroupService.isCancellationGroup(category);
        }

        if (hasCancellationGroup && !quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        }

        if (!hasCancellationGroup && quoteRequest.getBaseTripCancellation().getId() == BaseTripCancellation.FALSE.getId()) {
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_CANCELLATION);
        }
        upsaleService.updateUpsalesConsideringPackages(policyMeta, quoteRequest, changed);
        Product product = productService.getProduct(policyUniqueCode, quoteRequest, true, true);
        if (product == null) {
            upsaleResponse.setError("No product based on your request");
        } else if (CollectionUtils.isNotEmpty(product.getErrors())) {
            String error = StringUtils.EMPTY;
            for (Result.Error e : product.getErrors()) {
                error += e.getErrorMsg();
            }
            upsaleResponse.setError(error);
        } else {
            upsaleResponse.setPrice(product.getTotalPrice());
            upsaleResponse.setQuoteRequest(quoteRequest);
        }
        upsaleService.fillPackageChangeNotifications(quoteRequestFromParams, quoteRequest, policyMeta, upsaleResponse);
        upsaleService.fillTooltips(quoteRequest, policyMeta, upsaleResponse);
        return JsonUtils.getJsonString(upsaleResponse);
    }


    @ResponseBody
    @RequestMapping(value = "/{policyMetaId}/{categoryCode}", method = RequestMethod.POST)
    public PolicyMetaCategoryContentResult getPolicyMetaCategoryContentCertificate(@PathVariable("policyMetaId") Long policyMetaId,
                                                                                   @PathVariable("categoryCode") String categoryCode) {
        if (categoryCode == null || policyMetaId == null) {
            return null;
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        Category category = categoryService.getByCode(categoryCode);
        if (policyMeta == null || category == null) {
            return null;
        }
        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentService.getPolicyMetaCategoryContent(policyMeta, category);
        if (policyMetaCategoryContent == null) {
            return null;
        }
        return new PolicyMetaCategoryContentResult(policyMetaCategoryContent.getName(), policyMetaCategoryContent.getCertificateText(), policyMeta.getVendor().getCode(), policyMeta.getDisplayName());
    }
}
