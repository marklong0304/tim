package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.dto.json.AjaxResponse;
import com.travelinsurancemaster.model.dto.json.FailureAjaxResponse;
import com.travelinsurancemaster.model.dto.json.JsonSearchProduct;
import com.travelinsurancemaster.model.dto.json.SuccessAjaxResponse;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.common.validator.PurchaseRequestValidator;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.clients.TravelexInsuranceClient;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ValidationUtils;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Chernov Artur on 13.05.15.
 */


@Controller
@RequestMapping(value = "results")
public class PurchaseController extends AbstractController {

    private static final String INVALID_PARAMS_MSG = "Invalid params";
    private static final String EMPTY_PRODUCT_MSG = "Invalid request due to policy restrictions";
    private static final String CURRENT_PAGE = "ComparePlans";

    private static final Logger log = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private InsuranceClientFacade insuranceClientFacade;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Autowired
    private PurchaseRequestValidator purchaseRequestValidator;

    @Autowired
    private ProductService productService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private AffiliateLinkService affiliateLinkService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuoteInfoSession quoteInfoSession;

    @Autowired
    private SearchService searchService;

    @Autowired
    private UpsaleService upsaleService;

    @GetMapping(value = "purchasePage/{quoteId}")
    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true, beforeInvocation = true)
    public String purchasePage(
            @PathVariable("quoteId") String quoteId,
            @RequestParam(value = "uniqueCode", required = false) String uniqueCode,
            @RequestParam(value = "purchaseQuoteId", required = false) String purchaseQuoteId,
            @RequestParam(value = "purchaseUUID", required = false) String purchaseUUID,
            @RequestParam(value = "purchaseRequestId", required = false) String requestId,
            @RequestParam(value = "comparePlans", required = false) String comparePlans,
            Model model,
            HttpServletRequest httpServletRequest) {
        log.debug("Get purchase page with params: quoteId={}, uniqueCode={}, purchaseQuoteId={}, purchaseUUID={}", quoteId, uniqueCode, purchaseQuoteId, purchaseUUID);
        /* restore if back from error purchase */
        PurchaseRequest purchaseRequest = null;
        QuoteRequest quoteRequestParam;
        Purchase purchase = purchaseService.getPurchase(purchaseUUID);
        if (StringUtils.isNotBlank(purchaseUUID)) {
            log.debug("Restore purchase from UUID={}", purchaseUUID);
            uniqueCode = purchase.getPolicyMeta().getUniqueCode();
            String quoteRequestJson = purchase.getQuoteRequestJson();
            quoteRequestParam = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
            purchaseRequest = purchase.getPurchaseRequest();
            purchaseRequest.setQuoteRequest(purchase.getQuoteRequest());
        } else {
            log.debug("Restore purchase from purchaseQuoteId={}", purchaseQuoteId);
            quoteRequestParam = quoteInfoSession.getQuoteRequest(purchaseQuoteId);
        }
        if (quoteRequestParam == null || StringUtils.isBlank(uniqueCode)) {
            log.warn("Could't find quoteRequestParam and uniqueCode. Redirected to Home page");
            return "redirect:/";
        }
        QuoteRequest quoteRequest = QuoteRequest.newInstance(quoteRequestParam);
        upsaleService.enabledUpsalesUsingPackagesFromFilters(policyMetaService.getPolicyMetaByUniqueCode(uniqueCode), quoteRequest);
        //Get product from the search result
        Product product = productService.getProductAsync(uniqueCode, requestId);
        //If not found, request it
        if(product == null) {
            log.warn("##############################Product with unique code={} was not not found in search results. Requesting a new quote from the provider.", uniqueCode);
            product = productService.getProduct(uniqueCode, quoteRequest);
        }
        if(product == null) {
            log.warn("Product with unique code={} not found. Redirecting to Home page.", uniqueCode);
            return "redirect:/";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(product.getPolicyMeta().getId());
        product.setPolicyMeta(policyMeta);
        Certificate certificate = certificateService.getCertificate(quoteRequest, policyMeta, httpServletRequest);
        upsaleService.updateUpsalesConsideringPackages(policyMeta, quoteRequest, null);
        JsonSearchProduct jsonSearchProduct = searchService.getJsonSearchResponse(product, quoteRequest, true, true);
        PurchaseRequest pr = (purchaseRequest == null ? purchaseService.fillPurchaseRequest(quoteRequest, product, httpServletRequest) : purchaseRequest);
        String backUrl;
        if("true".equals(comparePlans)) {
            backUrl = "/comparePlans/?quoteId=" + quoteId;
        } else {
            backUrl = "/results/" + quoteId;
        }
        model.addAttribute("certificate", certificate);
        model.addAttribute("quoteRequest", quoteRequest);
        model.addAttribute("quoteRequestJson", JsonUtils.getJsonString(quoteRequest));
        model.addAttribute("purchasePlan", product);
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("purchaseRequest", pr);
        model.addAttribute("supportedCreditCards", vendorService.getSupportedCreditCards(product.getPolicyMeta().getVendor().getId()));
        List<PolicyMetaCategory> upsaleCategories = policyMetaService.getUpsalesFromPolicy(policyMeta, quoteRequest);
        insuranceClientFacade.getClient(policyMeta.getVendor().getCode()).filterPolicyUpsaleCategories(upsaleCategories, product, quoteRequest);
        model.addAttribute("upsaleCategories", upsaleCategories);
        model.addAttribute("upsaleValues", policyMetaService.getCategoryUpsaleValuesFromProduct(policyMeta, product, quoteRequest));
        model.addAttribute("packages", policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequest));
        model.addAttribute("productCardLeft", jsonSearchProduct.getCategories().subList(0, 3));
        model.addAttribute("productCardRight", jsonSearchProduct.getCategories().subList(3, 6));
        model.addAttribute("quoteId", quoteId);
        model.addAttribute("purchaseQuoteId", purchaseQuoteId);
        model.addAttribute("requestId", requestId);
        model.addAttribute("showRentalCarDates", vendorService.hasRentalCarDates(policyMeta.getVendor()));
        model.addAttribute("backUrl", backUrl);
        return "purchase/purchasePage";
    }

    @GetMapping(value = "purchase/{quoteId}")
    public String purchase(@PathVariable("quoteId") String quoteId, Model model) {
        log.debug("Get purchase page with quoteId={}", quoteId);
        if (model.asMap().get("purchaseResponse") != null) {
            return "purchase/purchasePage";
        } else {
            return "redirect:/404";
        }
    }

    @ResponseBody
    @PostMapping(value = "purchasePage/updateQuoteRequest")
    public UpdatePriceResponse updateQuoteRequest(@RequestParam("uniqueCode") String uniqueCode,
                                                  @RequestParam("quoteRequestJson") String quoteRequestJson) {
        log.debug("REST request to update quoteRequest: uniqueCode={}, quoteRequestJson={}", uniqueCode, quoteRequestJson);
        QuoteRequest quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        if (quoteRequest == null || uniqueCode == null) {
            log.warn("Invalid parameters: quoteRequest={}, uniqueCode={}", quoteRequest, uniqueCode);
            return new UpdatePriceResponse(false, INVALID_PARAMS_MSG, null);
        }
        Product product = productService.getProduct(uniqueCode, quoteRequest, true);
        if (product == null) {
            log.warn("Product not found: uniqueCode={}", uniqueCode);
            return new UpdatePriceResponse(false, EMPTY_PRODUCT_MSG, null);
        }
        if (CollectionUtils.isNotEmpty(product.getErrors())) {
            Result.Error error = product.getErrors().get(0);
            log.debug("Return response with error={}", error);
            return new UpdatePriceResponse(false, error.getErrorMsg(), null);
        }
        log.debug("Return success response with new price={}", product.getTotalPrice().toString());
        return new UpdatePriceResponse(true, null, product.getTotalPrice().toString());
    }

    @PostMapping(value = "validatePurchase/{quoteId}")
    public ResponseEntity<AjaxResponse> validatePurchase(@ModelAttribute("purchaseRequest") PurchaseRequest purchaseRequest,
                                                         @RequestParam("quoteRequestJson") String quoteRequestJson,
                                                         @RequestParam(value = "agreeTerms", required = false, defaultValue = "false") boolean agreeTerms,
                                                         BindingResult bindingResult) {
        log.debug("Validate purchase request with params: purchaseRequest={}, quoteRequestJson={}, agreeTerms={}", purchaseRequest, quoteRequestJson, agreeTerms);
        QuoteRequest quoteRequestParam = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        PolicyMeta policyMeta = purchaseRequest.getProduct().getPolicyMeta();
        Long policyMetaId = policyMeta.getId();
        policyMeta = policyMetaService.getCached(policyMetaId);
        if (policyMeta == null) {
            log.warn("Policy meta not found: policyMetaId: {}", policyMetaId);
            return new ResponseEntity<>(new FailureAjaxResponse<>(Collections.singletonList("Null policy meta")), HttpStatus.OK);
        }
        purchaseRequest.getProduct().getPolicyMeta().getPolicyMetaCodes().addAll(policyMeta.getPolicyMetaCodes());
        QuoteRequest quoteRequestWithUpsaleForPolicy = policyMetaService.getQuoteRequestWithUpsaleForPolicy(policyMeta,
                quoteRequestParam, false);
        QuoteRequest quoteRequestWithCancellation = quoteRequestService.getQuoteRequestWithCancellation(quoteRequestWithUpsaleForPolicy, policyMeta);
        if (quoteRequestWithCancellation == null) {
            log.warn("QuoteRequest is null");
            return new ResponseEntity<>(new FailureAjaxResponse<>(Collections.singletonList("Null quoteRequest")), HttpStatus.OK);
        }
        purchaseRequest.setQuoteRequest(quoteRequestWithCancellation);

        purchaseRequest.copyBillingFromHome();
        String vendorCode = purchaseRequest.getProduct().getPolicyMeta().getVendor().getCode();
        Vendor vendor = vendorService.getByCode(vendorCode);
        if (vendor == null) {
            log.warn("null vendor {}", vendorCode);
            return new ResponseEntity<>(new FailureAjaxResponse<>(Collections.singletonList("Null vendor")), HttpStatus.OK);
        }
        purchaseRequest.getProduct().getPolicyMeta().getVendor().getUnsupportedCardTypes().addAll(vendor.getUnsupportedCardTypes());
        purchaseRequestValidator.validate(purchaseRequest, bindingResult);
        purchaseRequestValidator.validateCreditCardType(bindingResult, purchaseRequest, vendor.getUnsupportedCardTypes());
        purchaseRequestValidator.validateEmail(bindingResult, userService.getUserByEmail(purchaseRequest.getEmail()));
        ValidationUtils.validatePhone(bindingResult, purchaseRequest.getPhone(), "phone", vendor.getCode());
        purchaseRequestValidator.validateProduct(bindingResult, purchaseRequest, productService.getProduct(policyMeta.getUniqueCode(), quoteRequestWithUpsaleForPolicy));
        purchaseRequestValidator.validateDepositDate(bindingResult, quoteRequestWithCancellation.getDepositDate());
        if (!agreeTerms) {
            bindingResult.rejectValue(null, null, "You must agree with Terms and Conditions!");
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FailureAjaxResponse<>(bindingResult.getAllErrors()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new SuccessAjaxResponse(true), HttpStatus.OK);
    }

    @PostMapping(value = "purchase/{quoteId}")
    public String purchase(
            @PathVariable("quoteId") String quoteId,
            @Valid @ModelAttribute("purchaseRequest") PurchaseRequest purchaseRequest,
            @RequestParam(value = "primaryTraveler", required = true) Integer primaryTravelerIndex,
            @RequestParam("quoteRequestJson") String quoteRequestJson,
            @RequestParam(value = "purchaseRentalCarStartDate", required = false) @DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_FORMAT) LocalDate purchaseRentalCarStartDate,
            @RequestParam(value = "purchaseRentalCarEndDate", required = false) @DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_FORMAT) LocalDate purchaseRentalCarEndDate,
            @RequestParam(value = "agreeTerms", required = false, defaultValue = "false") boolean agreeTerms,
            @RequestParam(value = "purchaseQuoteId", required = false) String purchaseQuoteId,
            @RequestParam(value = "timezoneOffset", required = false, defaultValue = "0") Long timezoneOffset,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            RedirectAttributes redirectAttributes) {
        log.debug("Make a purchase with params: quoteId={}, purchaseRequest={}, primaryTraveler={}, quoteRequestJson={}, agreeTerms={}, purchaseQuoteId={}",
                quoteId, purchaseRequest, primaryTravelerIndex, quoteRequestJson, agreeTerms, purchaseQuoteId);
        //Update quote request with params from purchase form
        QuoteRequest quoteRequestParam = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        quoteRequestParam.setRentalCarStartDate(purchaseRentalCarStartDate);
        quoteRequestParam.setRentalCarEndDate(purchaseRentalCarEndDate);
        quoteRequestJson = JsonUtils.getJsonString(quoteRequestParam);
        PolicyMeta policyMeta = purchaseRequest.getProduct().getPolicyMeta();
        Long policyMetaId = policyMeta.getId();
        policyMeta = policyMetaService.getCached(policyMetaId);
        if (policyMeta == null) {
            log.warn("null policy meta {}", policyMetaId);
            return "redirect:/404";
        }
        purchaseRequest.getProduct().getPolicyMeta().getPolicyMetaCodes().addAll(policyMeta.getPolicyMetaCodes());
        QuoteRequest quoteRequestWithUpsaleForPolicy = policyMetaService.getQuoteRequestWithUpsaleForPolicy(policyMeta, quoteRequestParam, false);
        QuoteRequest quoteRequestWithCancellation = quoteRequestService.getQuoteRequestWithCancellation(quoteRequestWithUpsaleForPolicy, policyMeta);
        if (quoteRequestWithCancellation == null) {
            log.warn("null quoteRequest");
            return "redirect:/404";
        }
        purchaseRequest.setQuoteRequest(quoteRequestWithCancellation);

        GenericTraveler primaryTraveler = setPrimaryTraveler(purchaseRequest, primaryTravelerIndex);
        String primaryTravelerName = primaryTraveler.getFirstName();
        String primaryTravelerLastName = primaryTraveler.getLastName();

        purchaseRequest.copyBillingFromHome();
        String vendorCode = purchaseRequest.getProduct().getPolicyMeta().getVendor().getCode();
        Vendor vendor = vendorService.getByCode(vendorCode);
        if (vendor == null) {
            log.warn("null vendor {}", vendorCode);
            return "redirect:/404";
        }
        model.addAttribute("supportedCreditCards", vendorService.getSupportedCreditCards(vendor.getId()));
        purchaseRequest.getProduct().getPolicyMeta().getVendor().getUnsupportedCardTypes().addAll(vendor.getUnsupportedCardTypes());
        purchaseRequestValidator.validate(purchaseRequest, bindingResult);
        purchaseRequestValidator.validateCreditCardType(bindingResult, purchaseRequest, vendor.getUnsupportedCardTypes());
        purchaseRequestValidator.validateEmail(bindingResult, userService.getUserByEmail(purchaseRequest.getEmail()));
        ValidationUtils.validatePhone(bindingResult, purchaseRequest.getPhone(), "phone", vendor.getCode());
        purchaseRequestValidator.validateProduct(bindingResult, purchaseRequest, productService.getProduct(policyMeta.getUniqueCode(), quoteRequestWithUpsaleForPolicy));
        purchaseRequestValidator.validateDepositDate(bindingResult, quoteRequestWithCancellation.getDepositDate());

        if (!agreeTerms) {
            bindingResult.rejectValue(null, null, "You must agree with Terms and Conditions!");
        }

        if (bindingResult.hasErrors()) {
            Certificate certificate = certificateService.getCertificate(quoteRequestParam, policyMeta, httpServletRequest);
            Product product = productService.getProduct(policyMeta.getUniqueCode(), quoteRequestWithUpsaleForPolicy);
            product.setPolicyMeta(policyMeta);
            upsaleService.updateUpsalesConsideringPackages(policyMeta, quoteRequestWithUpsaleForPolicy, null);
            JsonSearchProduct jsonSearchProduct = searchService.getJsonSearchResponse(product, quoteRequestParam, true, true);
            redirectAttributes.addFlashAttribute("purchaseRequest", purchaseRequest);
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
            redirectAttributes.addFlashAttribute("certificate", certificate);
            redirectAttributes.addFlashAttribute("quoteRequest", quoteRequestWithUpsaleForPolicy);
            redirectAttributes.addFlashAttribute("purchasePlan", purchaseRequest.getProduct());
            redirectAttributes.addFlashAttribute("quoteRequestJson", quoteRequestJson);
            redirectAttributes.addFlashAttribute("purchaseRequest", purchaseRequest);
            redirectAttributes.addFlashAttribute("policyMeta", policyMeta);
            redirectAttributes.addFlashAttribute("supportedCreditCards", vendorService.getSupportedCreditCards(policyMeta.getVendor().getId()));
            redirectAttributes.addFlashAttribute("upsaleCategories", policyMetaService.getUpsalesFromPolicy(policyMeta, quoteRequestWithUpsaleForPolicy));
            redirectAttributes.addFlashAttribute("upsaleValues", policyMetaService.getCategoryUpsaleValuesFromProduct(policyMeta, product, quoteRequestWithUpsaleForPolicy));
            redirectAttributes.addFlashAttribute("packages", policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequestWithUpsaleForPolicy));
            redirectAttributes.addFlashAttribute("productCardLeft", jsonSearchProduct.getCategories().subList(0, 3));
            redirectAttributes.addFlashAttribute("productCardRight", jsonSearchProduct.getCategories().subList(3, 6));
            redirectAttributes.addFlashAttribute("quoteId", quoteId);
            redirectAttributes.addAttribute("uniqueCode", policyMeta.getUniqueCode());
            redirectAttributes.addAttribute("purchaseQuoteId", purchaseQuoteId);
            redirectAttributes.addAttribute("timezoneOffset", timezoneOffset);
            return "redirect:/results/purchasePage/" + quoteId;
        }
        purchaseRequest = purchaseService.preparePurchaseRequest(purchaseRequest);

        PurchaseResponse response = null;
        User user = SecurityHelper.getCurrentUser();
        if (user == null){
            user = userService.getUserByEmail(purchaseRequest.getEmail());
        }
        if (user == null) {
            user = userService.autoCreateUser(purchaseRequest.getEmail(), primaryTravelerName, primaryTravelerLastName,
                    purchaseRequest.getAddress(),
                    quoteRequestWithCancellation.getResidentCountry(),
                    quoteRequestWithCancellation.getResidentState(),
                    purchaseRequest.getCity(),
                    purchaseRequest.getPostalCode(),
                    purchaseRequest.getPhone(),
                    httpServletRequest, httpServletResponse);
        }
        Purchase purchase;
        QuoteStorage quoteStorage = quoteStorageService.saveNewQuote(quoteId, quoteRequestJson, user);
        Optional<AffiliateLink> affiliateLink = Optional.ofNullable(affiliateLinkService.get(httpServletRequest));
        try {
            response = insuranceClientFacade.purchase(purchaseRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            purchase = purchaseService.savePurchase(purchaseRequest, response, user, quoteStorage.getId(), affiliateLink, null, httpServletRequest, timezoneOffset);
        }
        System.out.println("purchaseResponseStatus=" + response.getStatus());
        redirectAttributes.addFlashAttribute("quoteRequest", quoteRequestWithCancellation);
        redirectAttributes.addFlashAttribute("purchaseResponse", response);
        redirectAttributes.addFlashAttribute("purchaseRequest", purchaseRequest);
        redirectAttributes.addFlashAttribute("purchaseUuid", purchase.getPurchaseUuid());
        redirectAttributes.addFlashAttribute("totalPrice", purchase.getTotalPrice());
        redirectAttributes.addFlashAttribute("uniqueCode", policyMeta.getUniqueCode());
        redirectAttributes.addFlashAttribute("purchaseQuoteId", purchaseQuoteId);
        redirectAttributes.addAttribute("timezoneOffset", timezoneOffset);
        redirectAttributes.addFlashAttribute("policyMeta", policyMeta);
        return "redirect:/results/purchase/" + quoteStorage.getId();
    }

    private GenericTraveler setPrimaryTraveler(PurchaseRequest purchaseRequest, Integer primaryTravelerIndex) {
        GenericTraveler primaryTraveller = null;
        int i = 0;
        for (GenericTraveler traveler : purchaseRequest.getTravelers()) {
            if (i++ == primaryTravelerIndex) {
                traveler.setPrimary(true);
                primaryTraveller = traveler;
            } else {
                traveler.setPrimary(false);
            }
        }
        if (primaryTraveller == null) {
            primaryTraveller = purchaseRequest.getTravelers().get(0);
            primaryTraveller.setPrimary(true);
        }
        return primaryTraveller;
    }

    @GetMapping(value = "purchase/print/{purchaseUuid}")
    public String printPurchase(@PathVariable("purchaseUuid") String purchaseUuid, HttpServletRequest httpServletRequest, Model model) {
        log.debug("Get request for print purchase with uuid={}", purchaseUuid);
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        User currentUser = SecurityHelper.getCurrentUser();
        if (purchase == null || currentUser == null || !Objects.equals(purchase.getUser().getId(), currentUser.getId())
                || !purchase.isSuccess()) {
            return "redirect:/404";
        }
        model.addAttribute("quoteRequest", purchase.getQuoteRequest());
        model.addAttribute("purchaseRequest", purchase.getPurchaseRequest());
        model.addAttribute("policyNumber", purchase.getPolicyNumber());
        model.addAttribute("totalPrice", purchase.getTotalPrice());

        model.addAttribute("userName", purchase.getPurchaseQuoteRequest().getPrimaryTraveler().getFirstName());
        model.addAttribute("quoteRequest", purchase.getQuoteRequest());
        model.addAttribute("purchase", purchase);

        StringBuilder address = new StringBuilder();
        address.append(purchase.getPurchaseRequest().getAddress()).append(", ");
        if (StringUtils.isNotBlank(purchase.getPurchaseRequest().getCity())) {
            address.append(purchase.getPurchaseRequest().getCity()).append(", ");
        }
        if (purchase.getQuoteRequest().getResidentState() != null
                && StringUtils.isNotBlank(purchase.getQuoteRequest().getResidentState().getCaption())) {
            address.append(purchase.getQuoteRequest().getResidentState().getCaption()).append(", ");
        }
        if (purchase.getQuoteRequest().getResidentCountry() != null
                && StringUtils.isNotBlank(purchase.getQuoteRequest().getResidentCountry().getCaption())) {
            address.append(purchase.getQuoteRequest().getResidentCountry().getCaption()).append(" ");
        }
        address.append(purchase.getPurchaseRequest().getPostalCode());
        model.addAttribute("address", address);

        String certificateLink = certificateService.getCertificateUrl(purchase.getQuoteRequest(), purchase.getPolicyMeta(), httpServletRequest);
        model.addAttribute("certificateLink", StringUtils.isNotBlank(certificateLink) ? certificateLink : null);

        model.addAttribute("affiliateLink", userService.generateAffiliateLink(purchase.getUser()));
        return "/purchase/print";
    }

    private class UpdatePriceResponse {
        boolean success;
        String error;
        String price;

        public UpdatePriceResponse(boolean success, String error, String price) {
            this.success = success;
            this.error = error;
            this.price = price;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

    @RequestMapping(value = "ajaxTest", method = RequestMethod.POST)
    @ResponseBody
    public String ajaxTest() {
        return "";
    }
}
