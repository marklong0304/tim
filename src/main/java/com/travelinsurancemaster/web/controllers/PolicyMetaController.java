package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.dto.validator.PolicyMetaCategoryValidator;
import com.travelinsurancemaster.model.dto.validator.PolicyMetaValidator;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.PolicyMetaCategoryRepository;
import com.travelinsurancemaster.repository.PolicyMetaCodeRepository;
import com.travelinsurancemaster.repository.PolicyMetaPackageRepository;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import com.travelinsurancemaster.services.cms.VendorPageService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by Chernov Artur on 03.06.15.
 */


@Controller
@Scope(value = "request")
@RequestMapping(value = "/vendors/policy")
public class PolicyMetaController extends AbstractController {

    @Autowired
    private PolicyMetaCategoryRepository policyMetaCategoryRepository;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private PolicyMetaValidator policyMetaValidator;

    @Autowired
    private PolicyMetaCategoryValidator policyMetaCategoryValidator;

    @Autowired
    private PolicyMetaCodeRepository policyMetaCodeRepository;

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Autowired
    private PolicyMetaPackageRepository policyMetaPackageRepository;

    @Autowired
    private PolicyMetaPackageService policyMetaPackageService;

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private PolicyMetaCategoryRestrictionService policyMetaCategoryRestrictionService;

    @ModelAttribute("restrictionValidateQuoteRequest")
    public QuoteRequest getEmptyQuoteRequest() {
        return new QuoteRequest();
    }

    private void setNavigation(PolicyMeta policyMeta, Vendor vendor, Model model) {
        setNavigation(null, null, policyMeta, vendor, model, null);
    }

    private void setNavigation(PolicyMetaCategory policyMetaCategory, PolicyMetaRestriction policyMetaRestriction, PolicyMeta policyMeta, Vendor vendor, Model model) {
        setNavigation(policyMetaCategory, policyMetaRestriction, policyMeta, vendor, model, null);
    }

    private void setNavigation(PolicyMetaCategory policyMetaCategory, PolicyMetaRestriction policyMetaRestriction, PolicyMeta policyMeta, Vendor vendor, Model model, PolicyMetaCode policyMetaCode) {
        this.setNavigation(null, policyMetaCategory, policyMetaRestriction, policyMeta, vendor, model, policyMetaCode);
    }

    private void setNavigation(PolicyMetaPackage policyMetaPackage, PolicyMetaCategory policyMetaCategory, PolicyMetaRestriction policyMetaRestriction, PolicyMeta policyMeta, Vendor vendor, Model model, PolicyMetaCode policyMetaCode) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/vendors", "Vendors");
        String backUrl = "/vendors/edit/" + String.valueOf(vendor.getId());
        map.put(backUrl, vendor.getName());
        if (policyMeta != null && policyMeta.getId() != null) {
            String policyUrl = "/vendors/policy/edit/" + String.valueOf(policyMeta.getId());
            map.put(policyUrl, policyMeta.getDisplayName());
            if (policyMetaPackage != null || policyMetaCategory != null || policyMetaRestriction != null || policyMetaCode != null) {
                backUrl = policyUrl;
            }
        }
        if (policyMetaRestriction != null && policyMetaRestriction.getId() != null) {
            map.put("/vendors/policy/metaRestriction/edit/" + String.valueOf(policyMetaRestriction.getId()), String.valueOf(policyMetaRestriction.getRestrictionType()));
        }
        if (policyMetaCategory != null && policyMetaCategory.getId() != null) {
            map.put("/vendors/policy/metaCategory/edit/" + String.valueOf(policyMetaCategory.getId()), policyMetaCategory.getCategory().getName());
        }
        if (policyMetaCode != null && policyMetaCode.getId() != null) {
            map.put("/vendors/policy/policyMetaCode/edit/" + String.valueOf(policyMetaCode.getId()), policyMetaCode.getName());
        }
        if (policyMetaPackage != null && policyMetaPackage.getId() != null) {
            map.put("/vendors/policy/policyMetaPackage/edit/" + String.valueOf(policyMetaPackage.getId()), policyMetaPackage.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", backUrl);
    }

    private void fillPolicyMeta(PolicyMeta policyMeta) {
        PolicyMeta oldPolicyMeta = policyMetaService.getPolicyMetaById(policyMeta.getId());
        policyMeta.setPolicyQuoteParams(oldPolicyMeta.getPolicyQuoteParams());
        policyMeta.setPolicyMetaRestrictions(oldPolicyMeta.getPolicyMetaRestrictions());
        policyMeta.setPolicyMetaCategories(oldPolicyMeta.getPolicyMetaCategories());
        policyMeta.setPercentInfo(oldPolicyMeta.getPercentInfo());
    }

    private boolean isPackagesAvailable(PolicyMeta policyMeta) {
        int cnt = 0;
        List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
        String firstUpsaleCategoryCode = "";
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                if (cnt == 0) {
                    firstUpsaleCategoryCode = policyMetaCategory.getCategory().getCode();
                }
                if (++cnt > 1 && !firstUpsaleCategoryCode.equals(policyMetaCategory.getCategory().getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMapping(value = "/edit/{policyMetaId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String editPolicy(@PathVariable("policyMetaId") Long policyMetaId, Model model) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        model.addAttribute("policyMeta", policyMeta);

        List<PolicyMetaPackage> policyMetaPackages = this.policyMetaPackageRepository.findByPolicyMeta(policyMeta);
        model.addAttribute("policyMetaPackages", policyMetaPackages);

        List<Category> allCategories = categoryService.getAllCategoriesWithoutConditional();
        model.addAttribute("categories", allCategories);
        addEmptyPolicyMetaRestriction(model, policyMeta);
        model.addAttribute("policyMetaCode", new PolicyMetaCode(policyMeta));

        model.addAttribute("isPackagesAvailable", this.isPackagesAvailable(policyMeta));

        setNavigation(policyMeta, policyMeta.getVendor(), model);
        return "admin/policies/policyEdit";
    }

    @RequestMapping(value = "/edit/{policyMetaId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String editPolicy(@Valid @ModelAttribute("policyMeta") PolicyMeta policyMeta, BindingResult bindingResult, Model model) {
        PolicyMeta cachedPolicyMeta = policyMetaService.getPolicyMetaById(policyMeta.getId());
        setNavigation(cachedPolicyMeta, cachedPolicyMeta.getVendor(), model);
        model.addAttribute("isPackagesAvailable", this.isPackagesAvailable(policyMeta));

        policyMetaValidator.validate(policyMeta, bindingResult);
        if (bindingResult.hasErrors()) {
            fillPolicyMeta(policyMeta);
            List<Category> allCategories = categoryService.getAllCategoriesWithoutConditional();
            model.addAttribute("categories", allCategories);
            return "admin/policies/policyEdit";
        }

        boolean prevActive = cachedPolicyMeta.isActive();
        boolean active = policyMeta.isActive();

        policyMeta = policyMetaService.save(policyMeta);

        PolicyMetaPage policyMetaPage = policyMeta.getPolicyMetaPage();
        if (policyMetaPage != null) {
            if (prevActive && !active && policyMetaPage.getStatus() != PageStatus.DISABLED) {
                policyMetaPage.setStatus(PageStatus.DISABLED);
                this.policyMetaPageService.savePolicyMetaPage(policyMetaPage);
            } else if (!prevActive && active && policyMetaPage.getStatus() == PageStatus.DISABLED) {
                policyMetaPage.setStatus(PageStatus.PUBLISHED);
                this.policyMetaPageService.savePolicyMetaPage(policyMetaPage);
            }
        }


        addMessageSuccess("Saved", model);
        return editPolicy(policyMeta.getId(), model);
    }

    @RequestMapping(value = "/create/{vendorId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createPolicyMeta(@PathVariable("vendorId") Long vendorId, Model model) {
        Vendor vendor = vendorService.getById(vendorId);
        if (vendor == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = new PolicyMeta();
        policyMeta.setVendor(vendor);
        policyMeta.getPolicyMetaCodes().add(new PolicyMetaCode());
        model.addAttribute("policyMeta", policyMeta);

        setNavigation(policyMeta, policyMeta.getVendor(), model);
        return "admin/policies/policyCreate";
    }

    @RequestMapping(value = "/create/{vendorId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String createPolicy(@Valid @ModelAttribute("policyMeta") PolicyMeta policyMeta, BindingResult bindingResult, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        policyMetaValidator.validate(policyMeta, bindingResult);
        policyMetaValidator.validatePolicyMetaCode(policyMeta.getPolicyMetaCodes().get(0), bindingResult, "policyMetaCodes[0].");
        if (bindingResult.hasErrors()) {
            return "admin/policies/policyCreate";
        }

        VendorPage vendorPage = this.vendorPageService.getVendorPageByVendor(policyMeta.getVendor());
        if (vendorPage != null) {
            PolicyMetaPage policyMetaPage = new PolicyMetaPage();
            policyMetaPage.setName(policyMeta.getDisplayName());
            policyMetaPage.setCaption(policyMeta.getDisplayName());
            policyMetaPage.setDescription("");
            policyMetaPage.setContent("");
            policyMetaPage.setStatus(PageStatus.DRAFT);
            policyMetaPage.setVendorPage(vendorPage);
            policyMeta.setPolicyMetaPage(policyMetaPage);

            this.policyMetaPageService.savePolicyMetaPage(policyMetaPage);
        }

        policyMeta = policyMetaService.save(policyMeta);

        model.addAttribute("isPackagesAvailable", this.isPackagesAvailable(policyMeta));

        return "redirect:/vendors/policy/edit/" + policyMeta.getId();
    }

    @RequestMapping(value = "/delete/{policyMetaId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String deletePolicyMeta(@PathVariable("policyMetaId") Long policyMetaId) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        long vendorId = policyMeta.getVendor().getId();

        PolicyMetaPage policyMetaPage = policyMeta.getPolicyMetaPage();
        if (policyMetaPage != null) {
            this.policyMetaPageService.deletePolicyMetaPage(policyMetaPage.getId());
        }
        policyMeta.setDeletedDate(new Date());
        policyMetaService.save(policyMeta);
        return "redirect:/vendors/edit/" + vendorId;
    }

    @RequestMapping(value = "/create/{vendorId}", method = RequestMethod.POST, params = {"updateValues"})
    @Secured("ROLE_ADMIN")
    public String updateValuesCreate(@ModelAttribute("policyMeta") PolicyMeta policyMeta, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        if (policyMeta.getPercentInfo() != null)
            policyMeta.getPercentInfo().clear();
        if (policyMeta.getPercentType().getId() != PercentType.NONE.getId()) {
            if (policyMeta.getPercentInfo() == null) {
                policyMeta.setPercentInfo(new ArrayList<>());
            }
            policyMeta.getPercentInfo().add(new PercentInfo());
        }
        return "admin/policies/policyCreate";
    }

    @RequestMapping(value = "/create/{vendorId}", method = RequestMethod.POST, params = {"addRange"})
    @Secured("ROLE_ADMIN")
    public String addRangeCreate(PolicyMeta policyMeta, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        policyMeta.getPercentInfo().add(new PercentInfo());
        return "admin/policies/policyCreate";
    }

    @RequestMapping(value = "/create/{vendorId}", method = RequestMethod.POST, params = {"removeRange"})
    @Secured("ROLE_ADMIN")
    public String removeRangeCreate(@RequestParam("removeRange") int rangeId, PolicyMeta policyMeta, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        policyMeta.getPercentInfo().remove(rangeId);
        return "admin/policies/policyCreate";
    }

    @RequestMapping(value = "/edit/{policyMetaId}", method = RequestMethod.POST, params = {"updateValues"})
    @Secured("ROLE_ADMIN")
    public String updateValues(@ModelAttribute("policyMeta") PolicyMeta policyMeta, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        fillPolicyMeta(policyMeta);
        if (policyMeta.getPercentInfo() != null)
            policyMeta.getPercentInfo().clear();
        if (policyMeta.getPercentType().getId() != PercentType.NONE.getId()) {
            if (policyMeta.getPercentInfo() == null) {
                policyMeta.setPercentInfo(new ArrayList<PercentInfo>());
            }
            policyMeta.getPercentInfo().add(new PercentInfo());
        }
        addEmptyPolicyMetaRestriction(model, policyMeta);
        return "admin/policies/policyEdit";
    }

    private void addEmptyPolicyMetaRestriction(Model model, PolicyMeta policyMeta) {
        PolicyMetaRestriction policyMetaRestriction = new PolicyMetaRestriction();
        policyMetaRestriction.setPolicyMeta(policyMeta);
        model.addAttribute("restriction", policyMetaRestriction);
    }

    @RequestMapping(value = "/edit/{policyMetaId}", method = RequestMethod.POST, params = {"addRange"})
    @Secured("ROLE_ADMIN")
    public String addRange(PolicyMeta policyMeta, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        fillPolicyMeta(policyMeta);
        policyMeta.getPercentInfo().add(new PercentInfo());
        model.addAttribute("isPackagesAvailable", this.isPackagesAvailable(policyMeta));
        addEmptyPolicyMetaRestriction(model, policyMeta);
        return "admin/policies/policyEdit";
    }

    @RequestMapping(value = "/edit/{policyMetaId}", method = RequestMethod.POST, params = {"removeRange"})
    @Secured("ROLE_ADMIN")
    public String removeRange(@RequestParam("removeRange") int rangeId, PolicyMeta policyMeta, Model model) {
        setNavigation(policyMeta, policyMeta.getVendor(), model);
        fillPolicyMeta(policyMeta);
        policyMeta.getPercentInfo().remove(rangeId);
        model.addAttribute("isPackagesAvailable", this.isPackagesAvailable(policyMeta));
        return "admin/policies/policyEdit";
    }

    @RequestMapping(value = "/policyMetaCode/create/{policyMetaId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createPolicyMetaCode(@PathVariable("policyMetaId") Long policyMetaId, @RequestParam(value = "type", required = false) String typeParam, Model model) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId, false, false, false, false, false, true, false);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        PolicyMetaCode policyMetaCode = new PolicyMetaCode();
        policyMetaCode.setPolicyMeta(policyMeta);
        model.addAttribute("policyMetaCode", policyMetaCode);
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("restriction", new PolicyMetaCodeRestriction(policyMetaCode));
        setNavigation(null, null, policyMetaCode.getPolicyMeta(), policyMetaCode.getPolicyMeta().getVendor(), model, policyMetaCode);
        return "admin/policies/policyMetaCodeCreate";
    }

    @RequestMapping(value = "/policyMetaCode/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String createPolicyMetaCode(@Valid @ModelAttribute("policyMetaCode") PolicyMetaCode policyMetaCode,
                                       BindingResult bindingResult,
                                       Model model) {
        policyMetaValidator.validatePolicyMetaCode(policyMetaCode, bindingResult);
        if (bindingResult.hasErrors()) {
            PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaCode.getPolicyMeta().getId());
            setNavigation(null, null, policyMeta, policyMeta.getVendor(), model, policyMetaCode);
            model.addAttribute("restriction", new PolicyMetaCodeRestriction(policyMetaCode));
            model.addAttribute("policyMeta", policyMeta);
            return "admin/policies/policyMetaCodeCreate";
        }
        policyMetaCodeService.save(policyMetaCode);
        return "redirect:/vendors/policy/policyMetaCode/edit/" + policyMetaCode.getId();
    }

    @RequestMapping(value = "/policyMetaCode/edit/{metaCodeId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String editPolicyMetaCode(@PathVariable("metaCodeId") Long policyMetaCodeId, Model model) {
        PolicyMetaCode policyMetaCode = policyMetaCodeRepository.findOne(policyMetaCodeId);
        model.addAttribute("policyMetaCode", policyMetaCode);
        model.addAttribute("restriction", new PolicyMetaCodeRestriction(policyMetaCode));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaCode.getPolicyMeta().getId());
        setNavigation(null, null, policyMeta, policyMeta.getVendor(), model, policyMetaCode);
        return "admin/policies/policyMetaCodeEdit";
    }

    @RequestMapping(value = "/policyMetaCode/edit/{metaCodeId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String editPolicyMetaCode(@Valid @ModelAttribute("policyMetaCode") PolicyMetaCode policyMetaCode, BindingResult bindingResult, Model model) {
        policyMetaValidator.validatePolicyMetaCode(policyMetaCode, bindingResult);
        if (bindingResult.hasErrors()) {
            PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaCode.getPolicyMeta().getId());
            setNavigation(null, null, policyMeta, policyMeta.getVendor(), model, policyMetaCode);
            model.addAttribute("restriction", new PolicyMetaCodeRestriction(policyMetaCode));
            return policyMetaCode.getId() != null ? "admin/policies/policyMetaCodeEdit" : "admin/policies/policyMetaCodeCreate";
        }
        policyMetaCode = policyMetaCodeService.save(policyMetaCode);
        addMessageSuccess("Saved", model);
        return editPolicyMetaCode(policyMetaCode.getId(), model);
    }

    @RequestMapping(value = "/policyMetaCode/delete/{policyMetaCodeId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaCode(@PathVariable("policyMetaCodeId") Long policyMetaCodeId) {
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCodeById(policyMetaCodeId);
        if (policyMetaCode == null) {
            return "redirect:/404";
        }
        policyMetaCodeService.remove(policyMetaCode);
        return "success";
    }

    @RequestMapping(value = "/metaCategory/edit/{metaCategoryId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String editPolicyMetaCategory(@PathVariable("metaCategoryId") Long policyMetaCategoryId, Model model) {
        PolicyMetaCategory policyMetaCategory = policyMetaCategoryRepository.findOne(policyMetaCategoryId);
        if (policyMetaCategory == null) {
            return "redirect:/404";
        }
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
        PolicyMeta policyMetaCached = policyMetaService.getPolicyMetaById(policyMetaCategory.getPolicyMeta().getId());
        policyMetaCategory.setPolicyMeta(policyMetaCached);
        model.addAttribute("policyMetaCategory", policyMetaCategory);
        model.addAttribute("subcategories", subcategoryList);
        model.addAttribute("restriction", new PolicyMetaCategoryRestriction(policyMetaCategory));
        setNavigation(policyMetaCategory, null, policyMetaCached, policyMetaCategory.getPolicyMeta().getVendor(), model);
        return "admin/policies/policyMetaCategoryEdit";
    }

    @RequestMapping(value = "/metaCategory/edit/{metaCategoryId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String editPolicyMetaCategory(@Valid @ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, BindingResult bindingResult, Model model) {
        policyMetaCategoryService.formatValues(policyMetaCategory);
        policyMetaCategoryValidator.validate(policyMetaCategory, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
            model.addAttribute("subcategories", subcategoryList);
            List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictionList = policyMetaCategoryRestrictionService.getPolicyMetaCategoryRestrictions(policyMetaCategory.getId());
            policyMetaCategory.setPolicyMetaCategoryRestrictions(policyMetaCategoryRestrictionList);
            model.addAttribute("restriction", new PolicyMetaCategoryRestriction(policyMetaCategory));
            setNavigation(policyMetaCategory, null, policyMetaCategory.getPolicyMeta(), policyMetaCategory.getPolicyMeta().getVendor(), model);
            return "admin/policies/policyMetaCategoryEdit";
        }
        policyMetaCategory = policyMetaCategoryService.save(policyMetaCategory);
        addMessageSuccess("Saved", model);
        return editPolicyMetaCategory(policyMetaCategory.getId(), model);
    }

    @RequestMapping(value = "/metaCategory/create/{policyMetaId}/{categoryCode}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createPolicyMetaCategory(@PathVariable("policyMetaId") Long policyMetaId, @PathVariable("categoryCode") String categoryCode, Model model) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        Category category = categoryService.getByCode(categoryCode);
        if (category == null) {
            return "redirect:/404";
        }
        PolicyMetaCategory policyMetaCategory = new PolicyMetaCategory();
        policyMetaCategory.getValues().add(new PolicyMetaCategoryValue("100%", "100", ValueType.PERCENT, 1));
        policyMetaCategory.setPolicyMeta(policyMeta);
        policyMetaCategory.setCategory(category);
        model.addAttribute("policyMetaCategory", policyMetaCategory);
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(category.getId());
        model.addAttribute("subcategories", subcategoryList);
        setNavigation(policyMetaCategory, null, policyMetaCategory.getPolicyMeta(), policyMetaCategory.getPolicyMeta().getVendor(), model);
        return "admin/policies/policyMetaCategoryCreate";
    }

    @RequestMapping(value = "/metaCategory/create/{policyMetaId}/{categoryCode}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String createPolicyMetaCategory(@Valid @ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, BindingResult bindingResult, Model model) {
        policyMetaCategoryService.formatValues(policyMetaCategory);
        policyMetaCategoryValidator.validate(policyMetaCategory, bindingResult);
        if (bindingResult.hasErrors()) {
            PolicyMeta policyMetaCached = policyMetaService.getPolicyMetaById(policyMetaCategory.getPolicyMeta().getId());
            policyMetaCategory.setPolicyMeta(policyMetaCached);
            setNavigation(policyMetaCategory, null, policyMetaCached, policyMetaCategory.getPolicyMeta().getVendor(), model);
            return "admin/policies/policyMetaCategoryCreate";
        }
        policyMetaCategory = policyMetaCategoryService.createPolicyMetaCategory(policyMetaCategory);
        addMessageSuccess("Saved", model);
        return "redirect:/vendors/policy/metaCategory/edit/" + policyMetaCategory.getId();
    }

    @RequestMapping(value = "/metaCategory/delete/{policyMetaCategoryId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaCategory(@PathVariable("policyMetaCategoryId") Long policyMetaCategoryId) {
        PolicyMetaCategory policyMetaCategory = policyMetaCategoryRepository.findOne(policyMetaCategoryId);
        if (policyMetaCategory == null) {
            return "redirect:/404";
        }
        policyMetaCategoryService.deletePolicyMetaCategory(policyMetaCategory);
        return "redirect:/vendors/policy/edit/" + policyMetaCategory.getPolicyMeta().getId();
    }

    @RequestMapping(value = "/metaCategory/edit/{metaCategoryId}", method = RequestMethod.POST, params = {"addValue"})
    @Secured("ROLE_ADMIN")
    public String addValue(@ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, Model model) {
        if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
            policyMetaCategory.getValues().add(new PolicyMetaCategoryValue("100%", "100", ValueType.PERCENT, policyMetaCategory.getValues().size() + 1));
        }
        List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictionList = policyMetaCategoryRestrictionService.getPolicyMetaCategoryRestrictions(policyMetaCategory.getId());
        policyMetaCategory.setPolicyMetaCategoryRestrictions(policyMetaCategoryRestrictionList);
        PolicyMeta policyMetaCached = policyMetaService.getPolicyMetaById(policyMetaCategory.getPolicyMeta().getId());
        policyMetaCategory.setPolicyMeta(policyMetaCached);
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
        model.addAttribute("subcategories", subcategoryList);
        model.addAttribute("restriction", new PolicyMetaCategoryRestriction(policyMetaCategory));
        setNavigation(policyMetaCategory, null, policyMetaCached, policyMetaCached.getVendor(), model);
        return "admin/policies/policyMetaCategoryEdit";
    }

    @RequestMapping(value = "/metaCategory/edit/{metaCategoryId}", method = RequestMethod.POST, params = {"removeValue"})
    @Secured("ROLE_ADMIN")
    public String removeValue(@RequestParam("removeValue") int value, @ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, Model model) {
        if (policyMetaCategory.getValues().size() > 1) {
            policyMetaCategory.getValues().remove(value);
            resort(policyMetaCategory);
        }
        List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictionList = policyMetaCategoryRestrictionService.getPolicyMetaCategoryRestrictions(policyMetaCategory.getId());
        policyMetaCategory.setPolicyMetaCategoryRestrictions(policyMetaCategoryRestrictionList);
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
        model.addAttribute("subcategories", subcategoryList);
        model.addAttribute("restriction", new PolicyMetaCategoryRestriction(policyMetaCategory));
        setNavigation(policyMetaCategory, null, policyMetaCategory.getPolicyMeta(), policyMetaCategory.getPolicyMeta().getVendor(), model);
        return "admin/policies/policyMetaCategoryEdit";
    }

    @RequestMapping(value = "/metaCategory/edit/{metaCategoryId}", method = RequestMethod.POST, params = {"down"})
    @Secured("ROLE_ADMIN")
    public String downValue(@RequestParam("down") int value, @ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, Model model) {
        if (policyMetaCategory.getValues().size() > 1) {
            policyMetaCategory.getValues().get(value).setSortOrder(value + 2);
            policyMetaCategory.getValues().get(value + 1).setSortOrder(value + 1);
            Collections.sort(policyMetaCategory.getValues());
            resort(policyMetaCategory);
        }
        List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictionList = policyMetaCategoryRestrictionService.getPolicyMetaCategoryRestrictions(policyMetaCategory.getId());
        policyMetaCategory.setPolicyMetaCategoryRestrictions(policyMetaCategoryRestrictionList);
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
        model.addAttribute("subcategories", subcategoryList);
        model.addAttribute("restriction", new PolicyMetaCategoryRestriction(policyMetaCategory));
        setNavigation(policyMetaCategory, null, policyMetaCategory.getPolicyMeta(), policyMetaCategory.getPolicyMeta().getVendor(), model);
        return "admin/policies/policyMetaCategoryEdit";
    }

    @RequestMapping(value = "/metaCategory/edit/{metaCategoryId}", method = RequestMethod.POST, params = {"up"})
    @Secured("ROLE_ADMIN")
    public String upValue(@RequestParam("up") int value, @ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, Model model) {
        if (policyMetaCategory.getValues().size() > 1 && policyMetaCategory.getValues().get(value).getSortOrder() != 1) {
            policyMetaCategory.getValues().get(value).setSortOrder(value);
            policyMetaCategory.getValues().get(value - 1).setSortOrder(value + 1);
            Collections.sort(policyMetaCategory.getValues());
            resort(policyMetaCategory);
        }
        List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictionList = policyMetaCategoryRestrictionService.getPolicyMetaCategoryRestrictions(policyMetaCategory.getId());
        policyMetaCategory.setPolicyMetaCategoryRestrictions(policyMetaCategoryRestrictionList);
        List<Subcategory> subcategoryList = subcategoryService.getSubcategories(policyMetaCategory.getCategory().getId());
        model.addAttribute("subcategories", subcategoryList);
        model.addAttribute("restriction", new PolicyMetaCategoryRestriction(policyMetaCategory));
        setNavigation(policyMetaCategory, null, policyMetaCategory.getPolicyMeta(), policyMetaCategory.getPolicyMeta().getVendor(), model);
        return "admin/policies/policyMetaCategoryEdit";
    }

    @RequestMapping(value = "/metaCategory/create/{policyMetaId}/{categoryCode}", method = RequestMethod.POST, params = {"addValue"})
    @Secured("ROLE_ADMIN")
    public String addValueCreate(@ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, Model model) {
        addValue(policyMetaCategory, model);
        return "admin/policies/policyMetaCategoryCreate";
    }

    @RequestMapping(value = "/metaCategory/create/{policyMetaId}/{categoryCode}", method = RequestMethod.POST, params = {"removeValue"})
    @Secured("ROLE_ADMIN")
    public String removeValueCreate(@RequestParam("removeValue") int value, @ModelAttribute("policyMetaCategory") PolicyMetaCategory policyMetaCategory, Model model) {
        removeValue(value, policyMetaCategory, model);
        return "admin/policies/policyMetaCategoryCreate";
    }

    @RequestMapping(value = "/policyMetaPackage/create/{policyMetaId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createPolicyMetaPackage(@PathVariable("policyMetaId") Long policyMetaId, @RequestParam(value = "type", required = false) String typeParam, Model model) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId, false, false, false, false, false, true, false);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        PolicyMetaPackage policyMetaPackage = new PolicyMetaPackage();
        policyMetaPackage.setPolicyMeta(policyMeta);
        model.addAttribute("policyMetaPackage", policyMetaPackage);
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
        setNavigation(policyMetaPackage, null, null, policyMeta, policyMeta.getVendor(), model, null);
        return "admin/policies/policyMetaPackageCreate";
    }

    @RequestMapping(value = "/policyMetaPackage/create/", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String createPolicyMetaPackage(@Valid @ModelAttribute("policyMetaPackage") PolicyMetaPackage policyMetaPackage, BindingResult bindingResult, Model model) {
        policyMetaPackageService.generatePolicyMetaPackageCode(policyMetaPackage);
        policyMetaValidator.validatePolicyMetaPackage(policyMetaPackage, bindingResult);
        if (bindingResult.hasErrors()) {
            PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
            setNavigation(policyMetaPackage, null, null, policyMeta, policyMeta.getVendor(), model, null);
            model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
            model.addAttribute("policyMeta", policyMeta);
            return "admin/policies/policyMetaPackageCreate";
        }
        this.policyMetaPackageRepository.saveAndFlush(policyMetaPackage);

        policyMetaCategoryContentService.savePackegeForContent(policyMetaPackage);

        return "redirect:/vendors/policy/policyMetaPackage/edit/" + policyMetaPackage.getId();
    }

    @RequestMapping(value = "/policyMetaPackage/edit/{metaPackageId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String editPolicyMetaPackage(@PathVariable("metaPackageId") Long policyMetaPackageId, Model model) {
        PolicyMetaPackage policyMetaPackage = this.policyMetaPackageRepository.findOne(policyMetaPackageId);
        model.addAttribute("policyMetaPackage", policyMetaPackage);
        model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
        List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
        List<PolicyMetaCategory> policyMetaCategoriesUpSale = new ArrayList<>();
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                policyMetaCategoriesUpSale.add(policyMetaCategory);
            }
        }
        model.addAttribute("policyMetaCategories", policyMetaCategoriesUpSale);

        setNavigation(policyMetaPackage, null, null, policyMeta, policyMeta.getVendor(), model, null);
        return "admin/policies/policyMetaPackageEdit";
    }

    @RequestMapping(value = "/policyMetaPackage/edit/{metaPackageId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String updatePolicyMetaPackage(@Valid @ModelAttribute("policyMetaPackage") PolicyMetaPackage policyMetaPackage, BindingResult bindingResult, Model model) {
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
        List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
        List<PolicyMetaCategory> policyMetaCategoriesUpSale = new ArrayList<>();
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                policyMetaCategoriesUpSale.add(policyMetaCategory);
            }
        }
        model.addAttribute("policyMetaCategories", policyMetaCategoriesUpSale);
        model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));

        PolicyMetaPackage policyMetaPackage1 = policyMetaPackageRepository.findOne(policyMetaPackage.getId());
        policyMetaPackage.setPolicyMetaPackageRestrictions(policyMetaPackage1.getPolicyMetaPackageRestrictions());
        model.addAttribute("policyMetaPackage", policyMetaPackage);

        policyMetaValidator.validatePolicyMetaPackage(policyMetaPackage, bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(policyMetaPackage, null, null, policyMeta, policyMeta.getVendor(), model, null);
            //model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
            return policyMetaPackage.getId() != null ? "admin/policies/policyMetaPackageEdit" : "admin/policies/policyMetaPackageCreate";
        }

        setNavigation(policyMetaPackage, null, null, policyMeta, policyMeta.getVendor(), model, null);
        return "admin/policies/policyMetaPackageEdit";
    }

    @RequestMapping(value = "/policyMetaPackage/edit/{metaPackageId}", method = RequestMethod.POST, params = {"save"})
    @Secured("ROLE_ADMIN")
    public String savePolicyMetaPackage(@Valid @ModelAttribute("policyMetaPackage") PolicyMetaPackage policyMetaPackage, BindingResult bindingResult, Model model) {
        policyMetaValidator.validatePolicyMetaPackage(policyMetaPackage, bindingResult);
        if (bindingResult.hasErrors()) {
            PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
            setNavigation(policyMetaPackage, null, null, policyMeta, policyMeta.getVendor(), model, null);
            model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
            if (policyMetaPackage.getId() != null) {
                List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
                List<PolicyMetaCategory> policyMetaCategoriesUpSale = new ArrayList<>();
                for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
                    if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                        policyMetaCategoriesUpSale.add(policyMetaCategory);
                    }
                }
                model.addAttribute("policyMetaCategories", policyMetaCategoriesUpSale);
                return "admin/policies/policyMetaPackageEdit";
            } else {
                return "admin/policies/policyMetaPackageCreate";
            }
        }

        PolicyMetaPackage metaPackage = this.policyMetaPackageRepository.findOne(policyMetaPackage.getId());
        policyMetaPackage.setPolicyMetaPackageRestrictions(metaPackage.getPolicyMetaPackageRestrictions());

        policyMetaPackage = this.policyMetaPackageService.save(policyMetaPackage);
        addMessageSuccess("Saved", model);

        policyMetaCategoryContentService.savePackegeForContent(policyMetaPackage);

        return this.editPolicyMetaPackage(policyMetaPackage.getId(), model);
    }

    @RequestMapping(value = "/policyMetaPackage/edit/{metaPackageId}", method = RequestMethod.POST, params = {"addValue"})
    @Secured("ROLE_ADMIN")
    public String addPolicyMetaPackageValue(@ModelAttribute("policyMetaPackage") PolicyMetaPackage policyMetaPackage, Model model) {

        PolicyMeta policyMetaCached = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
        setNavigation(policyMetaPackage, null, null, policyMetaCached, policyMetaCached.getVendor(), model, null);
        List<PolicyMetaCategory> policyMetaCategories = policyMetaCached.getPolicyMetaCategories();
        List<PolicyMetaCategory> policyMetaCategoriesUpSale = new ArrayList<>();
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                policyMetaCategoriesUpSale.add(policyMetaCategory);
            }
        }

        if (CollectionUtils.isEmpty(policyMetaCategoriesUpSale)) {
            return "admin/policies/policyMetaPackageEdit";
        }

        model.addAttribute("policyMetaCategories", policyMetaCategoriesUpSale);

        PolicyMetaPackage metaPackage = this.policyMetaPackageRepository.findOne(policyMetaPackage.getId());

        PolicyMetaPackageValue policyMetaPackageValue = new PolicyMetaPackageValue();
        policyMetaPackageValue.setPolicyMetaPackage(policyMetaPackage);

        PolicyMetaCategory policyMetaCategory = policyMetaCategoriesUpSale.get(0);
        policyMetaPackageValue.setPolicyMetaCategory(policyMetaCategory);
        List<PolicyMetaCategoryValue> values = policyMetaCategory.getValues();
        policyMetaPackageValue.setValue(!CollectionUtils.isEmpty(values) ? values.get(0).getValue() : "");
        policyMetaPackage.getPolicyMetaPackageValues().add(policyMetaPackageValue);

        policyMetaPackage.setPolicyMetaPackageRestrictions(metaPackage.getPolicyMetaPackageRestrictions());

        model.addAttribute("policyMetaPackage", policyMetaPackage);
        model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
        return "admin/policies/policyMetaPackageEdit";
    }

    @RequestMapping(value = "/policyMetaPackage/edit/{metaPackageId}", method = RequestMethod.POST, params = {"removeValue"})
    @Secured("ROLE_ADMIN")
    public String removePolicyMetaPackageValue(@RequestParam("removeValue") int value, @ModelAttribute("policyMetaPackage") PolicyMetaPackage policyMetaPackage, Model model) {
        if (policyMetaPackage.getPolicyMetaPackageValues().size() > 0) {
            policyMetaPackage.getPolicyMetaPackageValues().remove(value);
        }
        PolicyMeta policyMetaCached = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
        List<PolicyMetaCategory> policyMetaCategories = policyMetaCached.getPolicyMetaCategories();
        List<PolicyMetaCategory> policyMetaCategoriesUpSale = new ArrayList<>();
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                policyMetaCategoriesUpSale.add(policyMetaCategory);
            }
        }
        model.addAttribute("policyMetaCategories", policyMetaCategoriesUpSale);

        PolicyMetaPackage metaPackage = this.policyMetaPackageRepository.findOne(policyMetaPackage.getId());
        policyMetaPackage.setPolicyMetaPackageRestrictions(metaPackage.getPolicyMetaPackageRestrictions());

        model.addAttribute("restriction", new PolicyMetaPackageRestriction(policyMetaPackage));
        setNavigation(policyMetaPackage, null, null, policyMetaCached, policyMetaCached.getVendor(), model, null);
        return "admin/policies/policyMetaPackageEdit";
    }

    @RequestMapping(value = "/policyMetaPackage/delete/{policyMetaPackageId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String deletePolicyMetaPackage(@PathVariable("policyMetaPackageId") Long policyMetaPackageId) {
        PolicyMetaPackage policyMetaPackage = this.policyMetaPackageRepository.findOne(policyMetaPackageId);
        if (policyMetaPackage == null) {
            return "redirect:/404";
        }
        this.policyMetaPackageService.deletePolicyMetaPackage(policyMetaPackage);
        return "success";
    }

    private static void resort(PolicyMetaCategory policyMetaCategory) {
        List<PolicyMetaCategoryValue> policyMetaCategoryValueList = policyMetaCategory.getValues();
        for (int i = 0; i < policyMetaCategoryValueList.size(); i++) {
            if (!policyMetaCategoryValueList.get(i).getSortOrder().equals(i + 1))
                policyMetaCategoryValueList.get(i).setSortOrder(i + 1);
        }
    }
}
