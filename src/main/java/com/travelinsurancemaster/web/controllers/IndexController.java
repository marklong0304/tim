package com.travelinsurancemaster.web.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.QuoteStorage;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.cms.page.FilingClaimPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.dto.email.ContactForm;
import com.travelinsurancemaster.model.dto.json.JsonIndexStepResult;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.common.forms.Step1QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step3QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.validator.Step1QuoteRequestFormValidator;
import com.travelinsurancemaster.model.webservice.common.validator.Step2QuoteRequestFormValidator;
import com.travelinsurancemaster.model.webservice.common.validator.Step3QuoteRequestFormValidator;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.cms.FilingClaimContactService;
import com.travelinsurancemaster.services.cms.FilingClaimPageService;
import com.travelinsurancemaster.services.cms.PageService;
import com.travelinsurancemaster.services.cms.VendorPageService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.*;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping(value = {"/", "/index"})
@SessionAttributes({"step1Form", "step2Form", "step3Form"})
@Scope(value = "request")
public class IndexController extends AbstractController {

    private static final String PRE_EXISTING_MEDICAL_AND_CANCELLATION = "preExistingMedicalAndCancellation";

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private FilingClaimPageService filingClaimPageService;

    @Autowired
    private GeoIpDatabaseService geoIpDatabaseComponent;

    @Autowired
    private QuoteInfoSession quoteInfoSession;

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private PageService pageService;

    @Autowired
    private InsuranceClientFacade insuranceClientFacade;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Value("${com.travelinsurancemaster.debug}")
    private boolean isDebug;

    private Map<String, String> vendorsPageUrl = new LinkedHashMap<>();
    private CountryCode[] destinationCountries;

    @PostConstruct
    public void init() {
        List<Vendor> vendors = vendorService.findAllActiveSortedByName();
        vendors.forEach(vendor -> {
            VendorPage vendorPage = vendorPageService.getPublishedVendorPageByVendor(vendor);
            vendorsPageUrl.put(vendor.getCode(), vendorPage != null ? vendorPage.getName() : null);
        });
        destinationCountries = CountryCodesUtils.getPublicVisibleCountryCodesSorted();

    }

    @ModelAttribute("index")
    public Boolean isIndex() {
        return true;
    }

    @ModelAttribute("vendors")
    public Map<String, String> getVendors() {
        return vendorsPageUrl;
    }

    @ModelAttribute("destinationCountries")
    public CountryCode[] getDestinationCountries() {
        return destinationCountries;
    }

    @GetMapping
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Request GET index page");
        cookieService.saveUidToCookie(request, response);

        if (!model.containsAttribute("quoteRequest")) {
            Step2QuoteRequestForm step2QuoteRequestForm = new Step2QuoteRequestForm();
            step2QuoteRequestForm.getTravelers().add(new GenericTraveler());
            User user = SecurityHelper.getCurrentUser();
            if (user != null) {
                user = userService.get(user.getId());
            }
            if (!geoIpDatabaseComponent.fillUserQuoteInfoByIP(step2QuoteRequestForm, request)
                    && user != null
                    && user.getUserInfo() != null
                    && user.getUserInfo().getCountry() != null) {
                step2QuoteRequestForm.setResidentCountry(user.getUserInfo().getCountry());
                step2QuoteRequestForm.setResidentState(user.getUserInfo().getStateOrProvince());
                step2QuoteRequestForm.setResidentCountryStatePair(CountryCodesUtils.getCountryStatePair(step2QuoteRequestForm.getResidentCountry(), step2QuoteRequestForm.getResidentState()));
            }

            if (!model.containsAttribute("step1Form")) {
                Step1QuoteRequestForm step1QuoteRequestForm = new Step1QuoteRequestForm();
                if (isDebug) {
                    step1QuoteRequestForm.setDestinationCountry(CountryCode.AL);
                    step1QuoteRequestForm.setDepartDate(LocalDate.now().plusDays(20));
                    step1QuoteRequestForm.setReturnDate(LocalDate.now().plusDays(30));
                    int val = (new Random().nextInt(4000) + 1000) / 100;
                    step1QuoteRequestForm.setTripCost(new BigDecimal(val * 100));
                }
                model.addAttribute("step1Form", step1QuoteRequestForm);
            }

            if (isDebug) {
                step2QuoteRequestForm.setTravelers(Lists.newArrayList(new GenericTraveler(22)));
                step2QuoteRequestForm.setResidentCountryStatePair(CountryCodesUtils.getCountryStatePair(CountryCode.US, StateCode.MN));
            }

            Step2QuoteRequestForm step2QuoteRequestFormOld = (Step2QuoteRequestForm) model.asMap().get("step2Form");
            if (step2QuoteRequestFormOld == null
                    || step2QuoteRequestFormOld.getResidentState() == null
                    && step2QuoteRequestFormOld.getResidentCountry() == null) {
                model.addAttribute("step2Form", step2QuoteRequestForm);
            }
            if (!model.containsAttribute("step3Form")) {
                Step3QuoteRequestForm form3 = new Step3QuoteRequestForm();

                if (isDebug) {
                    form3.setDepositDate(LocalDate.now().minusDays(10));
                    form3.setPaymentDate(LocalDate.now().minusDays(8));
                }
                form3.setPreExistingMedicalAndCancellation(true);
                model.addAttribute("step3Form", form3);
            }
            model.addAttribute("base", "true");
        } else {
            QuoteRequest quoteRequest = (QuoteRequest) model.asMap().get("quoteRequest");
            model.addAttribute("step1Form", quoteRequestService.fillStep1Form(quoteRequest));
            model.addAttribute("step2Form", quoteRequestService.fillStep2Form(quoteRequest));
            model.addAttribute("step3Form", quoteRequestService.fillStep3Form(quoteRequest));
            model.addAttribute("base", "false");
        }
        model.addAttribute("indexPage", pageService.getIndexPage());
        return "index";
    }

    @ResponseBody
    @PostMapping(value = "/api/step1.json")
    public JsonIndexStepResult postStep1FormAjax(Model model, @RequestParam Map<String, String> params) {
        log.debug("REST Request POST Step 1 form");
        Step1QuoteRequestForm form1 = (Step1QuoteRequestForm) model.asMap().get("step1Form");
        if (form1 == null) {
            return new JsonIndexStepResult("sessionExpired");
        }
        // Validate step 1 form
        Errors bindingResult = new BeanPropertyBindingResult(form1, "step1QuoteRequestForm");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, QuoteRequestConstants.DESTINATION_COUNTRY, "destinationCountry", "Destination country is required");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, QuoteRequestConstants.DEPART_DATE, "Depart date is required");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, QuoteRequestConstants.RETURN_DATE, "Return date is required");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, "tripCostTotal", "Trip cost total is required");

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());
            return new JsonIndexStepResult("error", errors);
        }
        CountryCode destinationCountry = CountryCode.valueOf(params.get(QuoteRequestConstants.DESTINATION_COUNTRY));

        Long timezoneOffset = NumberUtils.parseLong(params.get(QuoteRequestConstants.TIMEZONE_OFFSET), (long) 0);
        LocalDate departDate = DateUtil.getLocalDate(params.get(QuoteRequestConstants.DEPART_DATE), DateUtil.DEFAULT_DATE_FORMAT);
        LocalDate returnDate = DateUtil.getLocalDate(params.get(QuoteRequestConstants.RETURN_DATE), DateUtil.DEFAULT_DATE_FORMAT);
        BigDecimal tripCost = new BigDecimal(StringUtils.defaultString(params.get("tripCost").isEmpty() ? "0" : params.get("tripCost"), "0").replaceAll(",", ""));
        boolean tripCostTotal = Boolean.parseBoolean(params.get("tripCostTotal"));

        form1.setTimezoneOffset(timezoneOffset);
        form1.setDestinationCountry(destinationCountry);
        form1.setDepartDate(departDate);
        form1.setReturnDate(returnDate);
        form1.setTripCost(tripCost.setScale(0, BigDecimal.ROUND_CEILING));
        form1.setTripCostTotal(tripCostTotal);
        if (form1.getDestinationCountry() != CountryCode.US && form1.getDestinationCountry() != CountryCode.CA) {
            form1.setDestinationState(null);
        }

        Step1QuoteRequestFormValidator validator = new Step1QuoteRequestFormValidator();
        validator.validate(form1, bindingResult);
        if (bindingResult.hasErrors()) {
            log.debug("Step 1 Form has errors");
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());
            return new JsonIndexStepResult("error", errors);
        } else {
            log.debug("Step 1 Form validated success");
            return new JsonIndexStepResult("success");
        }
    }

    @ResponseBody
    @PostMapping(value = "/api/step2.json")
    public JsonIndexStepResult postStep2FormAjax(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.debug("REST Request POST Step 2 Form");
        Step2QuoteRequestForm form2 = (Step2QuoteRequestForm) model.asMap().get("step2Form");
        form2.setIncludesUS("true".equals(params.get("includesUS")));
        if (form2 == null) {
            return new JsonIndexStepResult("sessionExpired");
        }
        // Validate step2 form
        Errors bindingResult = new BeanPropertyBindingResult(form2, "step2QuoteRequestForm");
        ValidationUtils.rejectIfEmptyJsonArrayParam(params, bindingResult, "travelersString", "Travelers ages are required");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, "residentCountryStatePair", QuoteRequestConstants.RESIDENT_COUNTRY, "Resident country is required");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, QuoteRequestConstants.CITIZEN_COUNTRY, "Citizen country is required");
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());
            return new JsonIndexStepResult("error", errors);
        }
        String residentCountryStatePair = params.get("residentCountryStatePair");
        CountryCode citizenCountry = CountryCode.valueOf(params.get(QuoteRequestConstants.CITIZEN_COUNTRY));

        List<GenericTraveler> travelersList = new ArrayList<>();
        List<String> travelersAgesList = new ArrayList<>();
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        try {
            travelersAgesList = mapper.readValue(params.get("travelersString"), new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            log.debug("Error making object from json string");
        }
        for (String ageOrBirthday : travelersAgesList) {
            GenericTraveler traveler = new GenericTraveler(ageOrBirthday);
            travelersList.add(traveler);
        }
        form2.setResidentCountryStatePair(residentCountryStatePair);
        form2.setResidentCountry(CountryCodesUtils.getCountryCode(residentCountryStatePair));
        form2.setResidentState(CountryCodesUtils.getStateCode(residentCountryStatePair));
        form2.setCitizenCountry(citizenCountry);
        form2.setTravelers(travelersList);

        if (form2.getResidentCountry() != CountryCode.US && form2.getResidentCountry() != CountryCode.CA) {
            form2.setResidentState(null);
        }
        Step2QuoteRequestFormValidator validator = new Step2QuoteRequestFormValidator();
        validator.validate(form2, bindingResult);

        if (bindingResult.hasErrors()) {
            log.debug("Step 2 Form has errors");
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());
            return new JsonIndexStepResult("error", errors);
        } else {
            // create QuoteRequest Form step 2
            QuoteRequest quoteRequest = quoteRequestService.prepareQuoteRequestAfterStep2(model);
            // skip Form 3 for zero cost request
            if (quoteRequest.isZeroCost()) {
                quoteRequest.setPreExistingMedicalAndCancellation(false);
                quoteRequest.setPaymentDate(null);
                quoteRequest.setDepositDate(null);
            }
            // Store quote request
            QuoteStorage quoteStorage = quoteInfoSession.saveNewQuoteStorage(quoteRequest, request, response, null, null, false, true);
            log.debug("Step 2 Form validated success. New Quote Request stored with uuid={}, zero cost request={}", quoteStorage.getId(), quoteRequest.isZeroCost());
            return new JsonIndexStepResult("success", quoteStorage.getId(), quoteRequest.isZeroCost());
        }
    }

    @ResponseBody
    @PostMapping(value = "/api/step3.json")
    public JsonIndexStepResult postStep3FormAjax(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.debug("REST Request POST Step 3 Form");
        Step3QuoteRequestForm form3 = (Step3QuoteRequestForm) model.asMap().get("step3Form");
        if (form3 == null) {
            return new JsonIndexStepResult("sessionExpired");
        }
        // Validate Form 3
        Errors bindingResult = new BeanPropertyBindingResult(form3, "step3QuoteRequestForm");
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, PRE_EXISTING_MEDICAL_AND_CANCELLATION, "Trip cancellation is required");
        boolean preExistingMedicalAndCancellation = BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBoolean(params.get(PRE_EXISTING_MEDICAL_AND_CANCELLATION)), false);
        ValidationUtils.rejectIfEmptyParam(params, bindingResult, QuoteRequestConstants.DEPOSIT_DATE, "Deposit date is required");
        if (preExistingMedicalAndCancellation) {
            ValidationUtils.rejectIfEmptyParam(params, bindingResult, QuoteRequestConstants.PAYMENT_DATE, "Payment date is required");
        }
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());
            return new JsonIndexStepResult("error", errors);
        }
        form3.setPreExistingMedicalAndCancellation(preExistingMedicalAndCancellation);
        Date depositDate = DateUtil.getDate(params.get(QuoteRequestConstants.DEPOSIT_DATE));
        if (preExistingMedicalAndCancellation) {
            Date paymentDate = DateUtil.getDate(params.get(QuoteRequestConstants.PAYMENT_DATE));
            form3.setDepositDate(DateUtil.toLocalDate(depositDate));
            form3.setPaymentDate(DateUtil.toLocalDate(paymentDate));
        } else {
            form3.setDepositDate(DateUtil.toLocalDate(depositDate));
            form3.setPaymentDate(null);
        }
        Step3QuoteRequestFormValidator validator = new Step3QuoteRequestFormValidator();
        validator.validate(form3, bindingResult);
        if (bindingResult.hasErrors()) {
            log.debug("Step 3 Form has errors");
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());
            return new JsonIndexStepResult("error", errors);
        }
        postResultFormInternal(model, params.get("quoteStorageId"), request, response);
        log.debug("Step 3 Form validated success");
        return new JsonIndexStepResult("success");
    }

    //This function asks for auth only
    @ResponseBody
    @PostMapping(value = "/api/updateBaseCache")
    public JsonIndexStepResult updateBaseCache(@RequestParam("quoteId") String quoteId) {
        log.debug("Request POST update base cache (after form 2) for quoteId={}", quoteId);
        if (quoteId == null) {
            return new JsonIndexStepResult("error");
        }
        log.debug("Async update cache started");
        //QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, true, RestoreType.NAN);
        //return new JsonIndexStepResult("success", quoteInfo.getRequestId());
        insuranceClientFacade.startAuthRequests();
        return new JsonIndexStepResult("success");
    }

    //This function should not be called after step 3 as we don't need to ask for base products anymore
    @ResponseBody
    @PostMapping(value = "/api/updateFilteredCache")
    public JsonIndexStepResult updateFilteredCache(@RequestParam("quoteId") String quoteId, Model model) {
        log.debug("Request POST update filtered cache (after form 3) for quoteId={}", quoteId);
        if (quoteId == null) {
            return new JsonIndexStepResult("error");
        }
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return new JsonIndexStepResult("error");
        }
        log.debug("Async update cache started");
        this.quoteInfoSession.startBaseProductsRequest(quoteInfo, quoteRequestService.getQuoteRequestAfterStep3(model));
        return new JsonIndexStepResult("success");
    }

    @ResponseBody
    @PostMapping(value = "/api/removeFromCache")
    public JsonIndexStepResult removeFromCache(@RequestParam("quoteId") String quoteId) {
        log.debug("Request POST remove base cache (back from Form 3 to Form 2) for quoteId={}", quoteId);
        if (quoteId == null) {
            return new JsonIndexStepResult("error");
        }
        log.debug("Async remove from cache started");
        quoteStorageService.deleteQuoteStorage(quoteId);
        return new JsonIndexStepResult("success");
    }

    @PostMapping(value = "/api/searchQuote")
    public String postResultForm(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response,
                                 Model model, RedirectAttributes redirectAttributes) {
        log.debug("Request POST go to Result Page from Index page with quoteStorageId={}", params.get("quoteStorageId"));
        Step3QuoteRequestForm form3 = (Step3QuoteRequestForm) model.asMap().get("step3Form");
        if (form3 == null) {
            redirectAttributes.addFlashAttribute("dialogCaption", "Session notification");
            redirectAttributes.addFlashAttribute("message", "Session has expired, please repeat your request!");
            return "redirect:/";
        }
        form3.setPreExistingMedicalAndCancellation(false); //always false if called from here. is set from - /api/step3.json
        QuoteStorage quoteStorage = postResultFormInternal(model, params.get("quoteStorageId"), request, response);
        log.debug("Redirect form Index Page to Results Page with quoteId={}", quoteStorage.getId());
        String redirectUrl = "/comparePlans/?quoteId=" + quoteStorage.getId();
        /*if("true".equals(params.get("isMobileDevice"))) {
            redirectUrl = "/results/" + quoteStorage.getId();
        } else {
            redirectUrl =  "/comparePlans/?quoteId=" + quoteStorage.getId();
        }*/
        return "redirect:" + redirectUrl;
    }

    private QuoteStorage postResultFormInternal(Model model, String quoteStorageId, HttpServletRequest request, HttpServletResponse response) {
        QuoteRequest quoteRequest = quoteRequestService.getQuoteRequestAfterStep3(model);
        QuoteStorage quoteStorage = quoteStorageService.findOne(quoteStorageId);
        if (quoteStorage != null) {
            quoteStorage = quoteInfoSession.updateQuoteStorage(quoteStorage, quoteRequest);
            quoteInfoSession.getQuoteInfo(false, quoteStorage.getId()).setQuoteRequest(quoteRequest);
        } else {
            quoteStorage = quoteInfoSession.saveNewQuoteStorage(quoteRequest, request, response, null, null, false, true);
        }
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteStorage.getId(), false, RestoreType.NAN, false);
        if (quoteInfo != null) {
            quoteInfo.setOriginal(true);
        }
        return quoteStorage;
    }

    @GetMapping(value = "/newQuote")
    public String getNewQuote(Model model, HttpSession session) {
        log.debug("Request GET new quote");
        model.asMap().remove("step1Form");
        model.asMap().remove("step2Form");
        model.asMap().remove("step3Form");
        try {
            session.removeAttribute("step1Form");
            session.removeAttribute("step2Form");
            session.removeAttribute("step3Form");
        } catch (IllegalStateException e) {
            // nothing to do, session was already invalidated.
        }
        log.debug("Redirect to Index Page");
        return "redirect:/";
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String getContactPage(Model model) {

        model.addAttribute("vendors", vendorService.findAllActiveSortedByName());
        model.addAttribute("policies", policyMetaService.getAllActivePolicyMeta());
        model.addAttribute("contacts", filingClaimPageService.findAllSortedByName());
        model.addAttribute("contactForm", new ContactForm());
        return "contact";
    }
}
