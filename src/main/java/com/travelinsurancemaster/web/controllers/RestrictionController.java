package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.JsonResponse;
import com.travelinsurancemaster.model.dto.validator.RestrictionValidator;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.validator.QuoteRequestValidator;
import com.travelinsurancemaster.repository.PolicyMetaCategoryRestrictionRepository;
import com.travelinsurancemaster.repository.PolicyMetaCodeRestrictionRepository;
import com.travelinsurancemaster.repository.PolicyMetaPackageRestrictionRepository;
import com.travelinsurancemaster.repository.PolicyMetaRestrictionRepository;
import com.travelinsurancemaster.services.CalculatedRestrictionService;
import com.travelinsurancemaster.services.RestrictionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ritchie on 4/12/16.
 */
@Controller
@RequestMapping(value = "/vendors/policy")
public class RestrictionController {

    @Autowired
    private RestrictionValidator restrictionValidator;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private CalculatedRestrictionService calculatedRestrictionService;

    @Autowired
    private PolicyMetaRestrictionRepository policyMetaRestrictionRepository;

    @Autowired
    private PolicyMetaCategoryRestrictionRepository policyMetaCategoryRestrictionRepository;

    @Autowired
    private PolicyMetaCodeRestrictionRepository policyMetaCodeRestrictionRepository;

    @Autowired
    private PolicyMetaPackageRestrictionRepository policyMetaPackageRestrictionRepository;

    @Autowired
    private QuoteRequestValidator quoteRequestValidator;

    @RequestMapping(value = "/policyMetaRestriction/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public JsonResponse createPolicyMetaRestriction(@Valid @ModelAttribute("restriction") PolicyMetaRestriction policyMetaRestriction, BindingResult bindingResult) {
        restrictionValidator.validate(policyMetaRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        restrictionService.save(policyMetaRestriction);
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/policyMetaCategoryRestriction/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public JsonResponse createPolicyMetaCategoryRestriction(@Valid @ModelAttribute("restriction") PolicyMetaCategoryRestriction policyMetaCategoryRestriction,
                                                            BindingResult bindingResult) {
        restrictionValidator.validate(policyMetaCategoryRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        restrictionService.save(policyMetaCategoryRestriction);
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/policyMetaCodeRestriction/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public JsonResponse createPolicyMetaCodeRestriction(@Valid @ModelAttribute("restriction") PolicyMetaCodeRestriction policyMetaCodeRestriction,
                                                        BindingResult bindingResult) {
        restrictionValidator.validate(policyMetaCodeRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        restrictionService.save(policyMetaCodeRestriction);
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/policyMetaPackageRestriction/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public JsonResponse createPolicyMetaPackageRestriction(@Valid @ModelAttribute("restriction") PolicyMetaPackageRestriction policyMetaPackageRestriction,
                                                        BindingResult bindingResult) {
        restrictionValidator.validate(policyMetaPackageRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        restrictionService.save(policyMetaPackageRestriction);
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/policyQuoteParamRestriction/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public JsonResponse createPolicyMetaPackageRestriction(
            @Valid @ModelAttribute("restriction") PolicyQuoteParamRestriction policyQuoteParamRestriction,
            BindingResult bindingResult) {
        restrictionValidator.validate(policyQuoteParamRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        restrictionService.save(policyQuoteParamRestriction);
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/policyMetaRestriction/edit/{metaRestrictionId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public PolicyMetaRestriction editPolicyMetaRestriction(@PathVariable("metaRestrictionId") Long policyMetaRestrictionId) {
        return policyMetaRestrictionRepository.findOne(policyMetaRestrictionId);
    }

    @RequestMapping(value = "/policyMetaCategoryRestriction/edit/{metaRestrictionId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public PolicyMetaCategoryRestriction editPolicyMetaCategoryRestriction(@PathVariable("metaRestrictionId") Long policyMetaRestrictionId) {
        return policyMetaCategoryRestrictionRepository.findOne(policyMetaRestrictionId);
    }

    @RequestMapping(value = "/policyMetaCodeRestriction/edit/{metaRestrictionId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public PolicyMetaCodeRestriction editPolicyMetaCodeRestriction(@PathVariable("metaRestrictionId") Long policyMetaCodeId) {
        return policyMetaCodeRestrictionRepository.findOne(policyMetaCodeId);
    }

    @RequestMapping(value = "/policyMetaPackageRestriction/edit/{metaRestrictionId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public PolicyMetaPackageRestriction editPolicyMetaPackageRestriction(@PathVariable("metaRestrictionId") Long policyMetaPackageId) {
        return policyMetaPackageRestrictionRepository.findOne(policyMetaPackageId);
    }

    @RequestMapping(value = "/policyQuoteParamRestriction/edit/{quoteParamRestrictionId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public PolicyQuoteParamRestriction editPolicyQuoteParamRestriction(@PathVariable("quoteParamRestrictionId") Long quoteParamRestrictionId) {
        return restrictionService.getPolicyQuoteParamRestrictionById(quoteParamRestrictionId);
    }

    @RequestMapping(value = "/policyMetaRestriction/delete/{policyMetaRestrictionId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaRestriction(@PathVariable("policyMetaRestrictionId") Long policyMetaRestrictionId) {
        PolicyMetaRestriction policyMetaRestriction = restrictionService.getPolicyMetaRestrictionById(policyMetaRestrictionId);
        if (policyMetaRestriction == null) {
            return "redirect:/404";
        }
        restrictionService.remove(policyMetaRestriction);
        return "success";
    }

    @RequestMapping(value = "/policyMetaCategoryRestriction/delete/{policyMetaRestrictionId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaCategoryRestriction(@PathVariable("policyMetaRestrictionId") Long policyMetaCategoryRestrictionId) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction = restrictionService.getPolicyMetaCategoryRestrictionById(policyMetaCategoryRestrictionId);
        if (policyMetaCategoryRestriction == null) {
            return "redirect:/404";
        }
        restrictionService.remove(policyMetaCategoryRestrictionId);
        return "success";
    }

    @RequestMapping(value = "/policyMetaCodeRestriction/delete/{restrictionId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaCodeRestriction(@PathVariable("restrictionId") Long policyMetaCodeRestrictionId) {
        PolicyMetaCodeRestriction policyMetaCodeRestriction = restrictionService.getPolicyMetaCodeRestrictionById(policyMetaCodeRestrictionId);
        if (policyMetaCodeRestriction == null) {
            return "redirect:/404";
        }
        restrictionService.remove(policyMetaCodeRestriction);
        return "success";
    }

    @RequestMapping(value = "/policyMetaPackageRestriction/delete/{restrictionId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaPackageRestriction(@PathVariable("restrictionId") Long policyMetaPackageRestrictionId) {
        PolicyMetaPackageRestriction policyMetaPackageRestriction = restrictionService.getPolicyMetaPackageRestrictionById(policyMetaPackageRestrictionId);
        if (policyMetaPackageRestriction == null) {
            return "redirect:/404";
        }
        restrictionService.remove(policyMetaPackageRestriction);
        return "success";
    }

    @RequestMapping(value = "/policyQuoteParamRestriction/delete/{restrictionId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyQuoteParamRestriction(@PathVariable("restrictionId") Long policyQuoteParamRestrictionId) {
        PolicyQuoteParamRestriction policyQuoteParamRestriction1 = restrictionService.getPolicyQuoteParamRestrictionById(policyQuoteParamRestrictionId);
        if (policyQuoteParamRestriction1 == null) {
            return "redirect:/404";
        }
        restrictionService.remove(policyQuoteParamRestriction1);
        return "success";
    }

    @RequestMapping(value = "/testCalculatedRestriction", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse testCalculatedRestriction(@ModelAttribute("restrictionValidateQuoteRequest") QuoteRequest quoteRequest,
                                                  BindingResult bindingResult,
                                                  @RequestParam Map<String, String> requestParams) {
        String calculatedRestrictions = requestParams.get("validationFormCalculatedRestrictions");
        if (StringUtils.isEmpty(calculatedRestrictions)) {
            List<ObjectError> objectErrorsList = new ArrayList<>();
            ObjectError fieldError = new FieldError("restriction", "validationFormCalculatedRestrictions", "Calculated restriction is empty!");
            objectErrorsList.add(fieldError);
            return new JsonResponse(false, objectErrorsList);
        }
        String travelersCount = requestParams.get("travelersCount");
        String tripLength = requestParams.get("tripLength");
        String travelerAge = requestParams.get("travelers[0].age");
        int travelersCountInt = Integer.parseInt(travelersCount);
        for (int i = 1; i < travelersCountInt; i++) {
            quoteRequest.getTravelers().add(new GenericTraveler(travelerAge));
        }
        PolicyMetaRestriction policyMetaRestriction = new PolicyMetaRestriction();
        BindException bindException = new BindException(policyMetaRestriction, "validationFormCalculatedRestrictions");
        restrictionValidator.validateCalculatedRestrictions(calculatedRestrictions, quoteRequest, bindException);
        if (bindException.hasErrors()) {
            List<ObjectError> objectErrorsList = new ArrayList<>();
            ObjectError fieldError = new FieldError("restriction", "validationFormCalculatedRestrictions", "Groovy script is invalid!");
            objectErrorsList.add(fieldError);
            return new JsonResponse(false, objectErrorsList);
        }
        quoteRequestValidator.validate(quoteRequest, bindingResult);
        List<ObjectError> allErrors = new ArrayList<>();
        if (bindingResult.hasErrors() || StringUtils.isEmpty(travelersCount) || StringUtils.isEmpty(tripLength) || StringUtils.isEmpty(travelerAge)) {
            allErrors.addAll(bindingResult.getAllErrors());
            if (StringUtils.isEmpty(travelersCount)) {
                allErrors.add(new FieldError("quoteRequest", "travelersCount", "No travelers!"));
            }
            if (StringUtils.isEmpty(tripLength)) {
                allErrors.add(new FieldError("quoteRequest", "tripLength", "Trip length is not defined!"));
            }
            if (StringUtils.isEmpty(travelerAge)) {
                allErrors.add(new FieldError("quoteRequest", "age", "Age not may be null!"));
            }
            return new JsonResponse(false, allErrors);
        }
        return new JsonResponse(calculatedRestrictionService.isValid(quoteRequest, calculatedRestrictions, false));
    }
}