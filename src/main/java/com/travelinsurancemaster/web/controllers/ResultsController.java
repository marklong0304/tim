package com.travelinsurancemaster.web.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.ProductSortService;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.GroupNames;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.*;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.common.forms.EditQuoteRequestFormDTO;
import com.travelinsurancemaster.model.webservice.common.forms.Step1QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step3QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.validator.Step1QuoteRequestFormValidator;
import com.travelinsurancemaster.model.webservice.common.validator.Step2QuoteRequestFormValidator;
import com.travelinsurancemaster.model.webservice.common.validator.Step3QuoteRequestFormValidator;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.util.CountryCodesUtils;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ValidationUtils;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 27.04.15.
 */


@Controller
@RequestMapping(value = "results")
@Scope(value = "request")
public class ResultsController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(ResultsController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private QuoteInfoSession quoteInfoSession;

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Autowired
    private AffiliateLinkService affiliateLinkService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private InsuranceClientFacade insuranceClientFacade;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheConfig cacheConfig;

    private List<Group> groups;

    private List<Vendor> vendors;

    @PostConstruct
    public void init() {
        groups = groupService.getAllWithFilterCategoriesSortedByFilterOrder();
        vendors = vendorService.findAllActiveSortedByName();
    }

    @ModelAttribute("groups")
    public List<Group> getGroups() {
        return groups;
    }

    @ModelAttribute("vendors")
    public List<Vendor> getVendors() {
        return vendors;
    }

    @ModelAttribute("searchService")
    public SearchService getSearchService() {
        return searchService;
    }

    @GetMapping
    public String getEmptySearchResults() {
        return "redirect:/";
    }

    @RequestMapping(value = "/{quoteId}", method = RequestMethod.GET)
    public String getSearchResults(@PathVariable("quoteId") String quoteId, Model model, @RequestParam Map<String, String> params) {
        RestoreType restoreType = params.containsKey("restore") ? RestoreType.STORAGE : RestoreType.NAN;
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, false, restoreType);
        QuoteRequest quoteRequest;
        if (quoteInfo == null) {
            quoteRequest = quoteInfoSession.getQuoteRequest(quoteId);
        } else {
            quoteRequest = quoteInfo.getQuoteRequest();
        }
        if (quoteRequest == null) {
            return "redirect:/";
        }

        model.addAttribute("sortOrder", quoteInfo != null ? quoteInfo.getSortOrder() : ProductSortService.DEFAULT_ORDER);
        model.addAttribute("quoteRequest", quoteRequest);
        model.addAttribute("quoteRequestJson", JsonUtils.getJsonString(quoteRequest));
        return "results/resultsPage";
    }

    @ResponseBody
    @RequestMapping(value = "baseResults/{quoteId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true, beforeInvocation = true)
    public ResponseEntity<JsonSearchResult> baseResults(@PathVariable("quoteId") String quoteId) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.getUpsaleValues().clear();
        JsonSearchResult s = this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest);
        return ResponseEntity.ok(s);
    }

    @ResponseBody
    @RequestMapping(value = "preparedResults/{quoteId}/{requestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonSearchResult> getPreparedResults(@PathVariable(value = "quoteId") String quoteId, @PathVariable("requestId") String requestId) {
        RestoreType restoreType = RestoreType.NAN;
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, false, restoreType);
        QuoteRequest quoteRequest;
        if (quoteInfo == null) {
            quoteRequest = quoteInfoSession.getQuoteRequest(quoteId);
        } else {
            quoteRequest = quoteInfo.getQuoteRequest();
        }
        if (quoteRequest == null) {
            return null;
        }
        JsonSearchResult ans = this.quoteInfoSession.getJsonSearchResult(quoteInfo, quoteRequest, UUID.fromString(requestId));
        if(ans.isFinished()) {
            long duration = (System.nanoTime() - insuranceClientFacade.startTime) / 1000000;
            String durationStr = PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Quote processing time=" + durationStr);
            insuranceClientFacade.startTime = 0;
        }
        return ResponseEntity.ok(ans);
    }

    @ResponseBody
    @RequestMapping(value = "changePlanType/{quoteId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonSearchResult> changePlanType(@PathVariable("quoteId") String quoteId, @RequestParam("requestId") String requestId, @RequestParam("planType") String planType) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.setPlanType(PlanType.valueOf(planType));
        List<Category> cancellationCategories = groupService.getGroupByName(GroupNames.Cancellation).getCategoryList().stream()
                .filter(category -> quoteRequest.getCategories().containsKey(category.getCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cancellationCategories)) {
            cancellationCategories.forEach(category -> quoteRequest.getCategories().remove(category.getCode()));
        }
        if (quoteRequest.getPlanType().getId() == PlanType.COMPREHENSIVE.getId()) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_INTERRUPTION_RETURN_AIR_ONLY);
        } else {
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_CANCELLATION);
        }
        return ResponseEntity.ok(this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest));
    }

    @RequestMapping(value = "stopRequest/{requestId}", method = RequestMethod.POST)
    public ResponseEntity<String> stopRequest(@PathVariable("requestId") String requestId) {
        if (requestId.equals("null")) {
            return new ResponseEntity<>("OK!", HttpStatus.NO_CONTENT);
        }

        this.insuranceClientFacade.stopRequest(UUID.fromString(requestId));
        return new ResponseEntity<>("OK!", HttpStatus.NO_CONTENT);
    }

    @ResponseBody
    @RequestMapping(value = "updateCategories/{quoteId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonSearchResult> updateCategories(
            @PathVariable("quoteId") String quoteId,
            @RequestParam Map<String, String> params) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.getCategories().clear();
        boolean hasCancellationGroupParam = false;
        for (Group group : groups) {
            for (Category category : group.getCategoryList()) {
                if (params.containsKey(category.getCode())) {
                    if (category.getType() == Category.CategoryType.CATALOG
                            && NumberUtils.toInt(params.get(category.getCode()), -1) == -1) {
                        quoteRequest.getCategories().remove(category.getCode());
                    } else {
                        quoteRequest.getCategories().put(category.getCode(), params.get(category.getCode()));
                        if (GroupService.isCancellationGroup(category, quoteRequest.getPlanType())) {
                            hasCancellationGroupParam = true;
                        }
                    }
                } else {
                    quoteRequest.getCategories().remove(category.getCode());
                }
            }
        }
        if (hasCancellationGroupParam && !params.containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        }
        if (quoteRequest.getPlanType().getId() == PlanType.COMPREHENSIVE.getId()) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_INTERRUPTION_RETURN_AIR_ONLY);
        } else {
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_CANCELLATION);
        }
        quoteInfo.setOriginal(false);
        return ResponseEntity.ok(this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest));
    }

    @ResponseBody
    @RequestMapping(value = "updateSortOrder/{quoteId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonSearchResult> changeSortOrder(
            @PathVariable("quoteId") String quoteId,
            @RequestParam Map<String, String> params) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        String sortOrderParam = StringUtils.upperCase(params.get("sortOrder"));
        SortOrder sortOrder = EnumUtils.getEnum(SortOrder.class, sortOrderParam);
        if (sortOrder == null) {
            return ResponseEntity.badRequest().body(null);
        }
        quoteInfo.setSortOrder(sortOrder);
        return ResponseEntity.ok(this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest));
    }

    @ResponseBody
    @RequestMapping(value = "reset/{quoteId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonSearchResult> reset(
            @PathVariable("quoteId") String quoteId) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.getCategories().clear();
        quoteInfo.setOriginal(false);
        if (quoteRequest.getPlanType().getId() == PlanType.COMPREHENSIVE.getId()) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        }
        return ResponseEntity.ok(this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest));
    }

    @ResponseBody
    @RequestMapping(value = "original/{quoteId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonSearchResult> getOriginal(
            @PathVariable("quoteId") String quoteId) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, false, RestoreType.ORIGINAL);
        if (quoteInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteInfo.setOriginal(true);

        return ResponseEntity.ok(this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest));
    }

    @RequestMapping(value = "editQuote/{quoteId}")
    public String editQuote(@PathVariable("quoteId") String quoteId, RedirectAttributes redirectAttributes) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        redirectAttributes.addFlashAttribute("quoteRequest", quoteRequest);
        return "redirect:/";
    }

    @RequestMapping(value = "editQuoteStep3/{quoteId}")
    public String editQuoteStep3(@PathVariable("quoteId") String quoteId, RedirectAttributes redirectAttributes) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        redirectAttributes.addFlashAttribute("quoteRequest", quoteRequest);
        redirectAttributes.addFlashAttribute("step3", true);
        return "redirect:/";
    }

    @RequestMapping(value = "saveQuote/{quoteId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public String saveQuote(@PathVariable("quoteId") String quoteId, @RequestParam(value = "policyUniqueCode", required = false) String policyUniqueCode,
                            @RequestParam(value = "quoteRequestJson", required = false) String quoteRequestJson,
                            @RequestParam Map<String, String> requestParameters,
                            HttpServletRequest request, HttpServletResponse response) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        if (quoteRequestJson != null) {
            quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        }
        QuoteStorage quoteStorage = quoteStorageService.getByIdAndDeletedFalse(quoteId);
        User currentUser = SecurityHelper.getCurrentUser();
        if (quoteStorage == null || currentUser == null) {
            return "redirect:/results/" + quoteId;
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(policyUniqueCode);
        quoteStorage = quoteStorageService.saveQuote(quoteRequest, quoteStorage, currentUser, policyMeta, request, response, quoteInfo.isOriginal(), quoteInfoSession);
        if (requestParameters.containsKey("plans")) {
            /*response.setStatus(HttpServletResponse.SC_OK);
            return null;*/
            return "redirect:/comparePlans?quoteId=" + quoteStorage.getId();
        }
        if (policyMeta == null) {
            return "redirect:/results/" + quoteStorage.getId();
        } else {
            return "redirect:/details?restore&quoteId=" + quoteStorage.getId() + "&plan=" + policyMeta.getUniqueCode();
        }
    }

    @RequestMapping(value = "systemSaveQuote/{quoteId}")
    @ResponseBody
    public String systemSaveQuote(@PathVariable("quoteId") String quoteId, @RequestParam(value = "policyUniqueCode", required = false) String policyUniqueCode,
                                  @RequestParam(value = "quoteRequestJson", required = false) String quoteRequestJson,
                                  HttpServletRequest request, HttpServletResponse response) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "/";
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        if (quoteRequestJson != null) {
            quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        }
        QuoteStorage quoteStorage = quoteStorageService.getByIdAndDeletedFalse(quoteId);
        User currentUser = SecurityHelper.getCurrentUser();
        if (quoteStorage == null) {
            return "/results/" + quoteId;
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(policyUniqueCode);
        quoteStorage = quoteStorageService.saveQuote(quoteRequest, quoteStorage, currentUser, policyMeta, request,
                response, quoteInfo.isOriginal(), quoteInfoSession, true);
        if (policyMeta == null) {
            return "/results/" + quoteStorage.getId();
        } else {
            return "/details?quoteId=" + quoteStorage.getId() + "&plan=" + policyMeta.getUniqueCode();
        }
    }

    @RequestMapping(value = "/currentQuote", method = RequestMethod.GET)
    @ResponseBody
    public String getCurrentQuote(HttpServletRequest request) {
        return quoteStorageService.getCurrentQuote(request);
    }

    @Deprecated
    private String getJsonSearchResultString(QuoteInfo quoteInfo, QuoteRequest quoteRequest, JsonSearchResult.JsonSearchResultType type) {
        return JsonUtils.getJsonString(quoteInfoSession.getJsonSearchResult(quoteInfo, quoteRequest, type));
    }

    @ResponseBody
    @RequestMapping(value = "editQuoteRequest/{quoteId}")
    public ResponseEntity<EditQuoteRequestFormDTO> getEditQuoteRequestForm(
            @PathVariable("quoteId") String quoteId,
            @RequestParam(value = "policyUniqueCode", required = false) String policyUniqueCode,
            @RequestParam(value = "purchaseRequestId", required = false) String requestId,
            @RequestParam(value = "comparePlans", required = false) Boolean comparePlans,
            @RequestParam(value = "quoteRequestJson", required = false) String quoteRequestJson
    ) {
        QuoteRequest quoteRequest = null;
        if (quoteRequestJson != null) {
            quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        }
        if (quoteRequest == null) {
            QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
            if (quoteInfo == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            quoteRequest = quoteInfo.getQuoteRequest();
        }
        EditQuoteRequestFormDTO editQuoteRequestFormDTO = quoteRequestService.getEditQuoteRequestForm(quoteRequest, requestId, policyUniqueCode, comparePlans);
        return new ResponseEntity<>(editQuoteRequestFormDTO, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "editQuoteRequest/{quoteId}", method = RequestMethod.POST)
    public ResponseEntity<AjaxResponse> updateQuoteRequest(
            @PathVariable("quoteId") String quoteId,
            @ModelAttribute("editQuoteRequestForm") EditQuoteRequestFormDTO editQuoteRequestFormDTO,
            @RequestParam Map<String, String> params,
            HttpServletRequest request, HttpServletResponse response) {

        Step1QuoteRequestForm step1 = editQuoteRequestFormDTO.getStep1Form();
        Step2QuoteRequestForm step2 = editQuoteRequestFormDTO.getStep2Form();
        step2.setIncludesUS("true".equals(params.get("step2Form.includesUS")));
        Step3QuoteRequestForm step3 = editQuoteRequestFormDTO.getStep3Form();

        Errors step1bindingResult = new BeanPropertyBindingResult(step1, "step1QuoteRequestForm");
        Errors step2bindingResult = new BeanPropertyBindingResult(step2, "step2QuoteRequestForm");
        Errors step3bindingResult = new BeanPropertyBindingResult(step3, "step3QuoteRequestForm");

        Step1QuoteRequestFormValidator step1Validator = new Step1QuoteRequestFormValidator();
        Step2QuoteRequestFormValidator step2Validator = new Step2QuoteRequestFormValidator();
        Step3QuoteRequestFormValidator step3Validator = new Step3QuoteRequestFormValidator();
        // check step 1
        ValidationUtils.rejectIfEmptyParam(params, step1bindingResult, "step1Form." + QuoteRequestConstants.DESTINATION_COUNTRY, QuoteRequestConstants.DESTINATION_COUNTRY, "Destination country is required");
        ValidationUtils.rejectIfEmptyParam(params, step1bindingResult, "step1Form." + QuoteRequestConstants.DEPART_DATE, QuoteRequestConstants.DEPART_DATE, "Depart date is required");
        ValidationUtils.rejectIfEmptyParam(params, step1bindingResult, "step1Form." + QuoteRequestConstants.RETURN_DATE, QuoteRequestConstants.RETURN_DATE, "Return date is required");
        ValidationUtils.rejectIfEmptyParam(params, step1bindingResult, "step1Form.tripCostTotal", "tripCostTotal", "Trip cost total is required");

        BigDecimal tripCost = new BigDecimal(StringUtils.defaultString(
                params.get("tripCost").isEmpty() ? "0" : params.get("tripCost"), "0").replaceAll(",", ""));
        step1.setTripCost(tripCost);
        if (step1bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FailureAjaxResponse<>(step1bindingResult.getFieldErrors()), HttpStatus.OK);
        }
        // check step 2
        ValidationUtils.rejectIfEmptyJsonArrayParam(params, step2bindingResult, "step2Form.travelersString", "travelersString", "Travelers ages are required");
        ValidationUtils.rejectIfEmptyParam(params, step2bindingResult, "step2Form.residentCountryStatePair", QuoteRequestConstants.RESIDENT_COUNTRY, "Resident country is required");
        ValidationUtils.rejectIfEmptyParam(params, step2bindingResult, "step2Form." + QuoteRequestConstants.CITIZEN_COUNTRY, QuoteRequestConstants.CITIZEN_COUNTRY, "Citizen country is required");
        if (step2bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FailureAjaxResponse<>(step2bindingResult.getFieldErrors()), HttpStatus.OK);
        }
        String residentCountryStatePair = params.get("step2Form.residentCountryStatePair");
        CountryCode citizenCountry = CountryCode.valueOf(params.get("step2Form." + QuoteRequestConstants.CITIZEN_COUNTRY));
        step2.setResidentCountryStatePair(residentCountryStatePair);
        step2.setResidentCountry(CountryCodesUtils.getCountryCode(residentCountryStatePair));
        step2.setResidentState(CountryCodesUtils.getStateCode(residentCountryStatePair));
        step2.setCitizenCountry(citizenCountry);
        List<String> travelersAgesOrBirthdaysList = new ArrayList<>();
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        try {
            travelersAgesOrBirthdaysList = mapper.readValue(params.get("step2Form.travelersString"), new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            log.debug("error making object from json string");
        }
        step2.setTravelers(travelersAgesOrBirthdaysList.stream().map(GenericTraveler::new).collect(Collectors.toList()));

        // check step 3
        step3.setPreExistingMedicalAndCancellation(step3.getPaymentDate() != null);
        ValidationUtils.rejectIfEmptyParam(params, step3bindingResult, "step3Form.preExistingMedicalAndCancellation", "preExistingMedicalAndCancellation", "Trip cancellation is required");
        if (!tripCost.equals(BigDecimal.ZERO)) {
            ValidationUtils.rejectIfEmptyParam(params, step3bindingResult, "step3Form." + QuoteRequestConstants.DEPOSIT_DATE, QuoteRequestConstants.DEPOSIT_DATE, "Deposit date is required");
            if (step3.isPreExistingMedicalAndCancellation()) {
                ValidationUtils.rejectIfEmptyParam(params, step3bindingResult, "step3Form." + QuoteRequestConstants.PAYMENT_DATE, QuoteRequestConstants.PAYMENT_DATE, "Payment date is required");
            }
        }
        if (step3bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FailureAjaxResponse<>(step3bindingResult.getFieldErrors()), HttpStatus.OK);
        }

        step1Validator.validate(step1, step1bindingResult);
        step2Validator.validate(step2, step2bindingResult);
        step3Validator.validate(step3, step3bindingResult);
        List<FieldError> errors = new ArrayList<>();
        if (step1bindingResult.hasErrors()) {
            errors.addAll(step1bindingResult.getFieldErrors());
        }
        if (step2bindingResult.hasErrors()) {
            errors.addAll(step2bindingResult.getFieldErrors());
        }
        if (step3bindingResult.hasErrors()) {
            errors.addAll(step3bindingResult.getFieldErrors());
        }
        if (CollectionUtils.isNotEmpty(errors)) {
            return new ResponseEntity<>(new FailureAjaxResponse<>(errors), HttpStatus.OK);
        }
        QuoteRequest quoteRequest = quoteRequestService.getQuoteRequestFromEditForm(editQuoteRequestFormDTO);
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        QuoteRequest oldQuoteRequest = quoteInfo.getQuoteRequest();
        if (quoteRequestService.equalsQuoteRequests(quoteRequest, oldQuoteRequest)) {
            return new ResponseEntity<>(new SuccessAjaxResponse(true), HttpStatus.OK);
        }
        QuoteStorage quoteStorage = quoteInfoSession.saveNewQuoteStorage(quoteRequest, request, response, null, null, false, true);
        QuoteInfo newQuoteInfo = quoteInfoSession.getQuoteInfo(quoteStorage.getId(), false, RestoreType.NAN, false);
        if (newQuoteInfo != null) {
            newQuoteInfo.setOriginal(true);
        }
        if (StringUtils.equals(params.get("page"), "purchase") && StringUtils.isNotBlank(editQuoteRequestFormDTO.getPolicyUniqueCode())) {
            String policyUniqueCode = editQuoteRequestFormDTO.getPolicyUniqueCode();
            //Get product from the search result
            String requestId = editQuoteRequestFormDTO.getRequestId();
            Product product = productService.getProductAsync(policyUniqueCode, requestId);
            //If not found, request it
            if(product == null) {
                log.warn("##############################Product with unique code={} was not not found in search results. Requesting a new quote from the provider.", policyUniqueCode);
                product = productService.getProduct(policyUniqueCode, quoteRequest, true);
            }
            if (product != null && CollectionUtils.isEmpty(product.getErrors())) {
                BigDecimal totalPrice =  product.getTotalPrice();
                return new ResponseEntity<>(new PurchaseSuccessAjaxResponse(quoteStorage.getId(), product.getRequestId(), policyUniqueCode, totalPrice != null ? totalPrice.doubleValue() : 0), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new SuccessAjaxResponse(quoteStorage.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/cancellationGroup", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> getCancellationGroup() {
        return new ResponseEntity<>(groupService.getGroupByName(GroupNames.Cancellation).getCategoryList().stream().map(Category::getCode).collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/getCopyOfQuoteRequest/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getCopyOfQuoteRequest(@PathVariable("quoteId") String quoteId, @RequestParam(value = "quoteRequestJson", required = false) String quoteRequestJson, HttpServletRequest request, HttpServletResponse response) {
        QuoteStorage oldQuoteStorage = quoteStorageService.findOne(quoteId);
        QuoteRequest quoteRequest = null;
        if (quoteRequestJson != null) {
            quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        }
        if (quoteRequest == null) {
            QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
            if (quoteInfo != null) {
                quoteRequest = quoteInfo.getQuoteRequest();
            }
        }
        if (quoteRequest == null) {
            quoteRequest = oldQuoteStorage.getQuoteRequestObj();
        }
        QuoteStorage newQuoteStorage = quoteInfoSession.saveNewQuoteStorage(quoteRequest, request, response, oldQuoteStorage.getAffiliate(), null, oldQuoteStorage.isSaved(), oldQuoteStorage.isOriginal());
        return new ResponseEntity<>(newQuoteStorage.getId(), HttpStatus.OK);
    }
}
