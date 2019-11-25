package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.JsonMatrixCategoryValue;
import com.travelinsurancemaster.model.dto.json.JsonResponse;
import com.travelinsurancemaster.model.dto.json.JsonRestrictionsResponse;
import com.travelinsurancemaster.model.dto.validator.PolicyMetaCategoryValueValidator;
import com.travelinsurancemaster.model.dto.validator.RestrictionValidator;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by ritchie on 4/25/16.
 */
@Controller
@Scope(value = "session")
@RequestMapping(value = "/vendors/policyMatrix")
public class PolicyMatrixController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(PolicyMatrixController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PolicyMatrixService policyMatrixService;

    @Autowired
    private PolicyMetaCategoryValueValidator policyMetaCategoryValueValidator;

    @Autowired
    private RestrictionValidator restrictionValidator;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private SubcategoryValueService subcategoryValueService;

    @RequestMapping(value = "/showMatrix/{policyMetaId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String showMatrix(@PathVariable("policyMetaId") Long policyMetaId,
                             Model model) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        model.addAttribute("policyMeta", policyMeta);
        List<Category> allCategories = categoryService.getAllCategoriesWithoutConditional();
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("categoryValue", new PolicyMetaCategoryValue());
        model.addAttribute("restriction", new PolicyMetaCategoryRestriction());
        model.addAttribute("restrictionValidateQuoteRequest", new QuoteRequest());
        Long sessionId = System.nanoTime();

        if (!model.containsAttribute("policyMetaCategoriesMap")) {
            // when page is reloaded - database category values are displayed on page, all unsaved changes is lost
            model.addAttribute("sessionId", sessionId);
            policyMatrixService.putInLocalStorageMap(sessionId,
                    policyMatrixService
                            .getPolicyMetaCategories(policyMetaId),
                    policyMeta.getPolicyMetaRestrictions());
            model.addAttribute("policyMetaCategoriesMap", policyMatrixService.getLocalStorage(sessionId, policyMetaId).getCategoriesMap());
        }

        setNavigation(policyMeta, model);
        return "admin/policies/policyMatrix";
    }

    private void setNavigation(PolicyMeta policyMeta, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/vendors", "Vendors");
        Vendor vendor = policyMeta.getVendor();
        if (vendor != null && vendor.getName() != null) {
            String backUrl = "/vendors/edit/" + String.valueOf(vendor.getId());
            map.put(backUrl, vendor.getName());
        }
        if (policyMeta.getId() != null) {
            String policyMetaId = String.valueOf(policyMeta.getId());
            String policyUrl = "/vendors/policy/edit/" + policyMetaId;
            map.put(policyUrl, policyMeta.getDisplayName());
            String policyMatrixUrl = "/vendors/policyMatrix/showMatrix/" + policyMetaId;
            map.put(policyMatrixUrl, "Matrix");
        }
        model.addAttribute("breadcrumb", map);
    }

    @RequestMapping(value = "/showMatrix/{policyMetaId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String showFilteredMatrix(@PathVariable("policyMetaId") Long policyMetaId,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam(value = "residenceSelectedArray[]", required = false) String[] residenceSelectedArray,
                                     @RequestParam(value = "citizenshipSelectedArray[]", required = false) String[] citizenshipSelectedArray,
                                     @RequestParam(value = "destinationSelectedArray[]", required = false) String[] destinationSelectedArray,
                                     @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        policyMatrixService.putInLocalStorageMap(sessionId,
                policyMatrixService
                        .getPolicyMetaCategoriesFilteredMap(policyMetaId,
                                residenceSelectedArray,
                                citizenshipSelectedArray,
                                destinationSelectedArray),
                        policyMeta.getPolicyMetaRestrictions());

        redirectAttributes.addFlashAttribute("policyMetaCategoriesMap", policyMatrixService.getLocalStorage(sessionId, policyMetaId).getCategoriesMap());
        redirectAttributes.addFlashAttribute("residenceSelectedArray", residenceSelectedArray);
        redirectAttributes.addFlashAttribute("citizenshipSelectedArray", citizenshipSelectedArray);
        redirectAttributes.addFlashAttribute("destinationSelectedArray", destinationSelectedArray);
        redirectAttributes.addFlashAttribute("sessionId", sessionId);
        return "redirect:/vendors/policyMatrix/showMatrix/" + policyMetaId;
    }

    @RequestMapping(value = "/removeCategoryValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse removeCategoryValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                            @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                            @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                            @RequestParam("selectedCategoryValueIndex") int selectedCategoryValueIndex,
                                            @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        if (policyMetaCategory == null) {
            return new JsonResponse(false);
        }
        if (policyMetaCategory.getValues().size() > 1) {
            if (policyMetaCategory.getValues().size() == 2) {
                policyMetaCategory.setType(PolicyMetaCategory.MetaParamType.SIMPLE);
            }
            policyMetaCategory.getValues().remove(selectedCategoryValueIndex);
            log.debug("Removed PolicyMetaCategoryValue from session map");
        } else {
            List<PolicyMetaCategory> policyMetaCategories = policyMatrixService.getLocalStorage(sessionId, policyMetaId).getCategoriesById(selectedPolicyMetaCategoryId);
            policyMetaCategories.remove(selectedPolicyMetaCategoryIndex);
            log.debug("Removed PolicyMetaCategoryValue from session map");
        }
        return new JsonResponse(true);
    }


    @RequestMapping(value = "/addCategoryValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String addCategoryValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                   @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                   @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                   @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
            if (policyMetaCategoryValue.getCaption().equals("")) {
                return "Empty or incorrect category value structure was already added!";
            }
        }
        if (policyMetaCategory.getValues().size() == 1) {
            policyMetaCategory.setType(PolicyMetaCategory.MetaParamType.UP_SALE);
        }
        PolicyMetaCategoryValue policyMetaCategoryValue = new PolicyMetaCategoryValue("", "", ValueType.NAN);
        policyMetaCategoryValue.setPolicyMetaCategory(policyMetaCategory);
        policyMetaCategory.getValues().add(policyMetaCategoryValue);
        log.debug("Added empty PolicyMetaCategoryValue to session map");
        return "success";
    }

    @RequestMapping(value = "/addCategoryBlock/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String addCategoryBlock(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                   @RequestParam("categoryId") Long categoryId,
                                   @RequestParam(value = "sessionId", required = true) Long sessionId) {
        Category category = categoryService.get(categoryId);
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        PolicyMetaCategory policyMetaCategoryNew = new PolicyMetaCategory("",
                PolicyMetaCategory.MetaParamType.SIMPLE, policyMeta, category);
        policyMetaCategoryNew.getValues().add(new PolicyMetaCategoryValue("", "", ValueType.NAN));
        policyMatrixService.getLocalStorage(sessionId, policyMetaId).getCategoriesMap().add(categoryId, policyMetaCategoryNew);
        log.debug("Added new category block to session map");
        return "success";
    }

    @RequestMapping(value = "/getCategoryRestrictions/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse getCategoryRestrictions(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                                @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                                @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        List<ObjectError> objectErrorsList = new ArrayList<>();
        List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictionsList = policyMetaCategory.getPolicyMetaCategoryRestrictions();
        int i = 0;
        for (PolicyMetaCategoryRestriction policyMetaCategoryRestriction : policyMetaCategoryRestrictionsList) {
            BindException bindException = new BindException(policyMetaCategoryRestriction, "policyMetaCategoryRestriction");
            restrictionValidator.validateRestrictionRange(
                    policyMetaCategoryRestriction, bindException);
            if (policyMetaCategoryRestriction.getRestrictionType() == Restriction.RestrictionType.CALCULATE) {
                restrictionValidator.validateCalculatedRestrictions(policyMetaCategoryRestriction.getCalculatedRestrictions(), bindException);
            }
            if (bindException.hasErrors()) {
                List<ObjectError> errors = bindException.getAllErrors();
                for (ObjectError objectError : errors) {
                    ObjectError fieldError = new FieldError("policyMetaCategoryRestriction", "restriction-" + i, objectError.getDefaultMessage());
                    objectErrorsList.add(fieldError);
                }
            }
            i++;
        }
        JsonRestrictionsResponse jsonRestrictionsResponse = new JsonRestrictionsResponse(true, objectErrorsList, policyMetaCategoryRestrictionsList);
        return jsonRestrictionsResponse;
    }

    @RequestMapping(value = "/getCategoryRestriction/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public PolicyMetaCategoryRestriction getCategoryRestriction(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                                @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                                                @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                                                @RequestParam("restrictionIndex") int restrictionIndex,
                                                                @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        return policyMetaCategory.getPolicyMetaCategoryRestrictions().get(restrictionIndex);
    }

    @RequestMapping(value = "/removeRestriction/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String removeRestriction(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                    @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                    @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                    @RequestParam("restrictionIndex") int restrictionIndex,
                                    @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        policyMetaCategory.getPolicyMetaCategoryRestrictions().remove(restrictionIndex);
        log.debug("Removed PolicyMetaCategoryRestriction from session map");
        return "success";
    }

    @RequestMapping(value = "/updateRestrictionsSelect/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updateRestrictionsSelect(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                           @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                           @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                           @RequestParam("restrictionIndex") int restrictionIndex,
                                           @RequestParam("selectValue") String selectValue,
                                           @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex).getPolicyMetaCategoryRestrictions().get(restrictionIndex);
        policyMetaCategoryRestriction.setRestrictionPermit(EnumUtils.getEnum(Restriction.RestrictionPermit.class, selectValue));
        return "success";
    }

    @RequestMapping(value = "/updateStatesCountries/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updateStatesCountries(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                        @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                        @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                        @RequestParam("restrictionIndex") int restrictionIndex,
                                        @RequestParam(value = "statesArray[]", required = false) StateCode[] statesArray,
                                        @RequestParam(value = "countriesArray[]", required = false) CountryCode[] countriesArray,
                                        @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex).getPolicyMetaCategoryRestrictions().get(restrictionIndex);
        Set<StateCode> statesOrigin = policyMetaCategoryRestriction.getStates();
        if (!ArrayUtils.isEmpty(statesArray)) {
            if (statesOrigin == null) {
                statesOrigin = new HashSet<>();
                policyMetaCategoryRestriction.setStates(statesOrigin);
            }
            for (StateCode stateCode : statesArray) {
                if (!statesOrigin.contains(stateCode)) {
                    statesOrigin.add(stateCode);
                }
            }
            List<StateCode> statesList = Arrays.asList(statesArray);
            for (Iterator<StateCode> iterator = statesOrigin.iterator(); iterator.hasNext();) {
                StateCode state = iterator.next();
                if (!statesList.contains(state)) {
                    iterator.remove();
                }
            }
        } else {
            if (statesOrigin == null) {
                statesOrigin = new HashSet<>();
            } else {
                statesOrigin.clear();
            }
        }
        Set<CountryCode> countriesOrigin = policyMetaCategoryRestriction.getCountries();
        if (!ArrayUtils.isEmpty(countriesArray)) {
            if (countriesOrigin == null) {
                countriesOrigin = new HashSet<>();
                policyMetaCategoryRestriction.setCountries(countriesOrigin);
            }
            for (CountryCode countryCode : countriesArray) {
                if (!countriesOrigin.contains(countryCode)) {
                    countriesOrigin.add(countryCode);
                }
            }
            List<CountryCode> countriesList = Arrays.asList(countriesArray);
            for (Iterator<CountryCode> iterator = countriesOrigin.iterator(); iterator.hasNext();) {
                CountryCode country = iterator.next();
                if (!countriesList.contains(country)) {
                    iterator.remove();
                }
            }
        } else {
            if (countriesOrigin == null) {
                countriesOrigin = new HashSet<>();
            } else {
                countriesOrigin.clear();
            }
        }
        return "success";
    }

    @RequestMapping(value = "/addRestriction/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse addRestriction(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                       @Valid @ModelAttribute("restriction") PolicyMetaCategoryRestriction policyMetaCategoryRestriction,
                                       BindingResult bindingResult,
                                       @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                       @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                       @RequestParam(value = "sessionId", required = true) Long sessionId) {
        restrictionValidator.validate(policyMetaCategoryRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        if (policyMetaCategory == null) {
            log.debug("policy meta category not found");
            return new JsonResponse(false);
        }

        Optional<PolicyMetaCategoryRestriction> storedRestriction = policyMetaCategory.getPolicyMetaCategoryRestrictions().stream()
                .filter(restriction -> restriction.getId() != null || policyMetaCategoryRestriction.getId() != null
                        ? Objects.equals(restriction.getId(), policyMetaCategoryRestriction.getId())
                        : Objects.equals(restriction.getTempId(), policyMetaCategoryRestriction.getTempId())
                ).findAny();
        if (storedRestriction.isPresent()) {
            policyMetaCategory.getPolicyMetaCategoryRestrictions().remove(storedRestriction.get());
        } else {
            policyMetaCategoryRestriction.setPolicyMetaCategory(policyMetaCategory);
        }
        policyMetaCategoryRestriction.setTempId(UUID.randomUUID().toString());
        policyMetaCategory.getPolicyMetaCategoryRestrictions().add(policyMetaCategoryRestriction);
        log.debug("Added new PolicyMetaCategoryRestriction to session map");
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/updateMinValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updateMinValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                 @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                 @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                 @RequestParam("restrictionIndex") int restrictionIndex,
                                 @RequestParam("minValue") Integer minValue,
                                 @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex)
                .getPolicyMetaCategoryRestrictions().get(restrictionIndex);
        policyMetaCategoryRestriction.setMinValue(minValue);
        return "success";
    }

    @RequestMapping(value = "/updateMaxValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updateMaxValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                 @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                 @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                 @RequestParam("restrictionIndex") int restrictionIndex,
                                 @RequestParam("maxValue") Integer maxValue,
                                 @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex).getPolicyMetaCategoryRestrictions().get(restrictionIndex);
        policyMetaCategoryRestriction.setMaxValue(maxValue);
        return "success";
    }

    @RequestMapping(value = "/updateCalculatedRestrictionValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse updateCalculatedRestrictionValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                         @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                                         @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                                         @RequestParam("restrictionIndex") int restrictionIndex,
                                                         @RequestParam("calculatedRestrictionValue") String calculatedRestrictionValue,
                                                         @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex).getPolicyMetaCategoryRestrictions().get(restrictionIndex);
        policyMetaCategoryRestriction.setCalculatedRestrictions(calculatedRestrictionValue);
        BindException bindException = new BindException(policyMetaCategoryRestriction, "policyMetaCategoryRestriction");
        restrictionValidator.validateCalculatedRestrictions(calculatedRestrictionValue, bindException);
        if (bindException.hasErrors()) {
            ObjectError fieldError = new FieldError(
                    "policyMetaCategoryRestriction", "restriction-" + restrictionIndex, bindException.getAllErrors().get(0).getDefaultMessage());
            JsonResponse jsonResponse = new JsonResponse(false, Arrays.asList(fieldError));
            return jsonResponse;
        }
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/updatePlanCalculatedRestrictionValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updatePlanCalculatedRestrictionValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                   @RequestParam("restrictionIndex") int restrictionIndex,
                                                   @RequestParam("calculatedRestrictionValue") String calculatedRestrictionValue,
                                                   @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaRestriction policyMetaRestriction  = getPolicyMetaRestrictionByIndex(sessionId, policyMetaId, restrictionIndex);
        policyMetaRestriction.setCalculatedRestrictions(calculatedRestrictionValue);
        return "success";
    }

    @RequestMapping(value = "/saveAll", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String saveAll(@RequestParam("policyMetaId") Long policyMetaId,
                          @RequestParam(value = "sessionId", required = true) Long sessionId) {
        policyMatrixService.saveAll(policyMatrixService.getLocalStorage(sessionId, policyMetaId).getCategoriesMap(),
                policyMatrixService.getLocalStorage(sessionId, policyMetaId).getRestrictionsList(),
                policyMetaId);
        return "success";
    }

    @RequestMapping(value = "/editCategoryValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse editCategoryValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                          @Valid @ModelAttribute("categoryValue") PolicyMetaCategoryValue changedPolicyMetaCategoryValue,
                                          BindingResult bindingResult,
                                          @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                          @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                          @RequestParam("selectedCategoryValueIndex") int selectedCategoryValueIndex,
                                          @RequestParam("update") boolean update,
                                          @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        changedPolicyMetaCategoryValue.setSortOrder(policyMetaCategory.getValues().size());
        if (update) {
            PolicyMetaCategoryValue policyMetaCategoryValue = policyMetaCategory.getValues().get(selectedCategoryValueIndex);
            policyMetaCategoryValue.setCaption(changedPolicyMetaCategoryValue.getCaption());
            policyMetaCategoryValue.setValue(changedPolicyMetaCategoryValue.getValue());
            policyMetaCategoryValue.setValueType(changedPolicyMetaCategoryValue.getValueType());
            policyMetaCategoryValue.setApiValue(changedPolicyMetaCategoryValue.getApiValue());
            policyMetaCategoryValue.setDaysAfterInitialDeposit(changedPolicyMetaCategoryValue.getDaysAfterInitialDeposit());
            policyMetaCategoryValue.setDaysAfterFinalPayment(changedPolicyMetaCategoryValue.getDaysAfterFinalPayment());
            policyMetaCategoryValue.setMinAge(changedPolicyMetaCategoryValue.getMinAge());
            policyMetaCategoryValue.setMaxAge(changedPolicyMetaCategoryValue.getMaxAge());
            policyMetaCategoryValue.setSecondary(changedPolicyMetaCategoryValue.isSecondary());
        }
        policyMetaCategoryValueValidator.validateCategoryValue(changedPolicyMetaCategoryValue, policyMetaCategory, bindingResult);
        if (bindingResult.hasErrors()) {
            JsonResponse jsonResponse = new JsonResponse(false, bindingResult.getAllErrors());
            return jsonResponse;
        }
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/getCategoryValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonMatrixCategoryValue getCategoryValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                    @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                                    @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                                    @RequestParam("selectedCategoryValueIndex") int selectedCategoryValueIndex,
                                                    @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        PolicyMetaCategoryValue policyMetaCategoryValue = policyMetaCategory.getValues().get(selectedCategoryValueIndex);
        policyMetaCategoryValue.setSortOrder(policyMetaCategory.getValues().size());
        Map<String, SubcategoryValue> subcategoryValueMap = new HashMap<>();
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
        for (Subcategory subcategory : subcategoryList) {
            boolean foundSubcategoryValue = false;
            for (SubcategoryValue subcategoryValue : policyMetaCategoryValue.getSubcategoryValuesList()) {
                if (subcategoryValue.getSubcategory().getSubcategoryCode().equals(subcategory.getSubcategoryCode())) {
                    foundSubcategoryValue = true;
                    subcategoryValueMap.put(subcategoryValue.getSubcategory().getSubcategoryName(), subcategoryValue);
                    break;
                }
            }
            if (!foundSubcategoryValue) {
                SubcategoryValue subcategoryValue = new SubcategoryValue();
                subcategoryValue.setPolicyMetaCategoryValue(policyMetaCategoryValue);
                subcategoryValue.setSubcategory(subcategory);
                subcategoryValue.setSubcategoryValue("");
                policyMetaCategoryValue.getSubcategoryValuesList().add(subcategoryValue);
                SubcategoryValue savedSubcategoryValue = subcategoryValueService.save(subcategoryValue);
                subcategoryValueMap.put(subcategory.getSubcategoryName(), savedSubcategoryValue);
            }
        }
        return new JsonMatrixCategoryValue(policyMetaCategoryValue, subcategoryValueMap);
    }

    private PolicyMetaCategory getSelectedPolicyMetaCategory(Long sessionId, Long policyMetaId, Long selectedPolicyMetaCategoryId, int selectedPolicyMetaCategoryIndex) {
        List<PolicyMetaCategory> policyMetaCategories = policyMatrixService.getLocalStorage(sessionId, policyMetaId).getCategoriesById(selectedPolicyMetaCategoryId);
        if (CollectionUtils.isEmpty(policyMetaCategories)) {
            return null;
        }
        return policyMetaCategories.get(selectedPolicyMetaCategoryIndex);
    }

    @RequestMapping(value = "/getPlanRestrictions/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonRestrictionsResponse getPlanRestrictions(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                        @RequestParam(value = "sessionId", required = true) Long sessionId) {
        List<ObjectError> objectErrorsList = new ArrayList<>();
        List<PolicyMetaRestriction> policyMetaRestrictionsList = policyMatrixService.getLocalStorage(sessionId, policyMetaId).getRestrictionsList();
        int i = 0;
        for (PolicyMetaRestriction policyMetaCategoryRestriction : policyMetaRestrictionsList) {
            BindException bindException = new BindException(policyMetaCategoryRestriction, "policyMetaCategoryRestriction");
            restrictionValidator.validateRestrictionRange(
                    policyMetaCategoryRestriction, bindException);
            if (bindException.hasErrors()) {
                ObjectError fieldError = new FieldError("policyMetaCategoryRestriction", "restriction-" + i, "Min or Max values should be filled!");
                objectErrorsList.add(fieldError);
            }
            i++;
        }
        JsonRestrictionsResponse jsonRestrictionsResponse = new JsonRestrictionsResponse(true, objectErrorsList, policyMetaRestrictionsList);
        return jsonRestrictionsResponse;
    }

    @RequestMapping(value = "/getPlanRestriction/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public PolicyMetaRestriction getPlanRestriction(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                                    @RequestParam("restrictionIndex") int restrictionIndex,
                                                    @RequestParam(value = "sessionId", required = true) Long sessionId) {
        return getPolicyMetaRestrictionByIndex(sessionId, policyMetaId, restrictionIndex);
    }

    @RequestMapping(value = "/removePlanRestriction/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String removePlanRestriction(@PathVariable("policyMetaId") Long policyMetaId,
                                        @RequestParam("restrictionIndex") int restrictionIndex,
                                        @RequestParam(value = "sessionId", required = true) Long sessionId) {

        policyMatrixService.getLocalStorage(sessionId, policyMetaId).getRestrictionsList().remove(restrictionIndex);
        log.debug("Removed PolicyMetaRestriction from local list");
        return "success";
    }

    @RequestMapping(value = "/addPlanRestriction/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public JsonResponse addPlanRestriction(@PathVariable("policyMetaId") Long policyMetaId,
                                           @Valid @ModelAttribute("restriction") PolicyMetaRestriction policyMetaRestriction,
                                           BindingResult bindingResult,
                                           @RequestParam(value = "sessionId", required = true) Long sessionId) {
        restrictionValidator.validate(policyMetaRestriction, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonResponse(false, bindingResult.getAllErrors());
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        List<PolicyMetaRestriction> policyMetaRestrictions = policyMatrixService.getLocalStorage(sessionId, policyMetaId).getRestrictionsList();

        Optional<PolicyMetaRestriction> storedRestriction = policyMetaRestrictions.stream()
                .filter(restriction -> restriction.getId() != null || policyMetaRestriction.getId() != null
                        ? Objects.equals(restriction.getId(), policyMetaRestriction.getId())
                        : Objects.equals(restriction.getTempId(), policyMetaRestriction.getTempId())
                ).findAny();
        if (storedRestriction.isPresent()) {
            policyMetaRestrictions.remove(storedRestriction.get());
        } else {
            policyMetaRestriction.setPolicyMeta(policyMeta);
        }
        policyMetaRestriction.setTempId(UUID.randomUUID().toString());
        policyMetaRestrictions.add(policyMetaRestriction);
        log.debug("Added new PolicyMetaRestriction in local list");
        return new JsonResponse(true);
    }

    @RequestMapping(value = "/updatePlanStatesCountries/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updatePlanStatesCountries(@PathVariable("policyMetaId") Long policyMetaId,
                                        @RequestParam("restrictionIndex") int restrictionIndex,
                                        @RequestParam(value = "statesArray[]", required = false) StateCode[] statesArray,
                                        @RequestParam(value = "countriesArray[]", required = false) CountryCode[] countriesArray,
                                        @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaRestriction policyMetaRestriction = getPolicyMetaRestrictionByIndex(sessionId, policyMetaId, restrictionIndex);
        Set<StateCode> statesOrigin = policyMetaRestriction.getStates();
        if (!ArrayUtils.isEmpty(statesArray)) {
            if (statesOrigin == null) {
                statesOrigin = new HashSet<>();
                policyMetaRestriction.setStates(statesOrigin);
            }
            for (StateCode stateCode : statesArray) {
                if (!statesOrigin.contains(stateCode)) {
                    statesOrigin.add(stateCode);
                }
            }
            List<StateCode> statesList = Arrays.asList(statesArray);
            for (Iterator<StateCode> iterator = statesOrigin.iterator(); iterator.hasNext();) {
                StateCode state = iterator.next();
                if (!statesList.contains(state)) {
                    iterator.remove();
                }
            }
        } else {
            if (statesOrigin == null) {
                statesOrigin = new HashSet<>();
            } else {
                statesOrigin.clear();
            }
        }
        Set<CountryCode> countriesOrigin = policyMetaRestriction.getCountries();
        if (!ArrayUtils.isEmpty(countriesArray)) {
            if (countriesOrigin == null) {
                countriesOrigin = new HashSet<>();
                policyMetaRestriction.setCountries(countriesOrigin);
            }
            for (CountryCode countryCode : countriesArray) {
                if (!countriesOrigin.contains(countryCode)) {
                    countriesOrigin.add(countryCode);
                }
            }
            List<CountryCode> countriesList = Arrays.asList(countriesArray);
            for (Iterator<CountryCode> iterator = countriesOrigin.iterator(); iterator.hasNext();) {
                CountryCode country = iterator.next();
                if (!countriesList.contains(country)) {
                    iterator.remove();
                }
            }
        } else {
            if (countriesOrigin == null) {
                countriesOrigin = new HashSet<>();
            } else {
                countriesOrigin.clear();
            }
        }

        return "success";
    }

    @RequestMapping(value = "/updatePlanRestrictionsSelect/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updatePlanRestrictionsSelect(@PathVariable("policyMetaId") Long policyMetaId,
                                           @RequestParam("restrictionIndex") int restrictionIndex,
                                           @RequestParam("selectValue") String selectValue,
                                           @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaRestriction policyMetaRestriction  = getPolicyMetaRestrictionByIndex(sessionId, policyMetaId, restrictionIndex);
        policyMetaRestriction.setRestrictionPermit(EnumUtils.getEnum(Restriction.RestrictionPermit.class, selectValue));
        return "success";
    }

    @RequestMapping(value = "/updatePlanMinValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updatePlanMinValue(@PathVariable("policyMetaId") Long policyMetaId,
                                 @RequestParam("restrictionIndex") int restrictionIndex,
                                 @RequestParam("minValue") Integer minValue,
                                 @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaRestriction policyMetaRestriction  = getPolicyMetaRestrictionByIndex(sessionId, policyMetaId, restrictionIndex);
        policyMetaRestriction.setMinValue(minValue);
        return "success";
    }

    @RequestMapping(value = "/updatePlanMaxValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String updatePlanMaxValue(@PathVariable("policyMetaId") Long policyMetaId,
                                 @RequestParam("restrictionIndex") int restrictionIndex,
                                 @RequestParam("maxValue") Integer maxValue,
                                 @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaRestriction policyMetaRestriction = getPolicyMetaRestrictionByIndex(sessionId, policyMetaId, restrictionIndex);
        policyMetaRestriction.setMaxValue(maxValue);
        return "success";
    }

    private PolicyMetaRestriction getPolicyMetaRestrictionByIndex(Long sessionId, Long policyMetaId, int restrictionIndex) {
        return policyMatrixService.getLocalStorage(sessionId, policyMetaId).getRestrictionsList().get(restrictionIndex);
    }

    @RequestMapping(value = "/editSubcategoryValue/{policyMetaId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String editSubcategoryValue(@PathVariable (value = "policyMetaId") Long policyMetaId,
                                       @RequestParam("selectedPolicyMetaCategoryId") Long selectedPolicyMetaCategoryId,
                                       @RequestParam("selectedPolicyMetaCategoryIndex") int selectedPolicyMetaCategoryIndex,
                                       @RequestParam("selectedCategoryValueIndex") int selectedCategoryValueIndex,
                                       @RequestParam("subcategoryValueId") Long subcategoryValueId,
                                       @RequestParam("subcategoryValue") String subcategoryValueEdited,
                                       @RequestParam(value = "sessionId", required = true) Long sessionId) {
        PolicyMetaCategory policyMetaCategory = getSelectedPolicyMetaCategory(sessionId, policyMetaId, selectedPolicyMetaCategoryId, selectedPolicyMetaCategoryIndex);
        PolicyMetaCategoryValue policyMetaCategoryValue = policyMetaCategory.getValues().get(selectedCategoryValueIndex);
        for (SubcategoryValue subcategoryValue : policyMetaCategoryValue.getSubcategoryValuesList()) {
            if (subcategoryValue.getId().equals(subcategoryValueId)) {
                subcategoryValue.setSubcategoryValue(subcategoryValueEdited);
            }
        }
        return "success";
    }
}
