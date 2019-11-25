package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.dto.cms.page.validator.PolicyMetaCategoryContentValidator;
import com.travelinsurancemaster.model.dto.cms.page.validator.PolicyMetaPageValidator;
import com.travelinsurancemaster.services.*;
import com.travelinsurancemaster.services.cms.PageTypeService;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import com.travelinsurancemaster.services.cms.VendorPageService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Chernov Artur on 18.10.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/cms/vendor_page/policy_page")
public class PolicyMetaPageController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(PolicyMetaPageController.class);

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private PageTypeService pageTypeService;

    @Autowired
    private PolicyMetaPageValidator policyMetaPageValidator;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PolicyMetaCategoryContentValidator policyMetaCategoryContentValidator;

    @Autowired
    private PolicyMetaPackageService policyMetaPackageService;

    private List<Vendor> vendors;

    @ModelAttribute("vendors")
    public List<Vendor> getVendors() {
        return vendors;
    }

    @PostConstruct
    public void init() {
        vendors = vendorService.findAllSortedByName();
    }

    private void setNavigation(PolicyMetaPage policyMetaPage, VendorPage vendorPage, PolicyMetaCategoryContent policyMetaCategoryContent, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/vendor_page", "Vendor pages");
        String backUrl = "/cms/vendor_page";
        if (vendorPage != null && vendorPage.getId() != null) {
            backUrl = "/cms/vendor_page/edit/" + String.valueOf(vendorPage.getId());
            map.put(backUrl, vendorPage.getCaption());
            if (policyMetaPage != null) {
                if (policyMetaPage.getId() != null) {
                    map.put("/cms/vendor_page/policy_page/edit/" + String.valueOf(policyMetaPage.getId()), policyMetaPage.getCaption());
                    if (policyMetaCategoryContent != null) {
                        backUrl = "/cms/vendor_page/policy_page/edit/" + String.valueOf(policyMetaPage.getId());
                        if (policyMetaCategoryContent.getId() != null) {
                            map.put("/cms/vendor_page/policy_page/policy_meta_category/edit/" + String.valueOf(policyMetaCategoryContent.getId()), policyMetaCategoryContent.getName());
                        } else {
                            map.put(null, "Create");
                        }
                    }
                } else {
                    map.put("/cms/vendor_page/policy_page/create/" + String.valueOf(vendorPage.getId()), "Create");
                }
            }
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", backUrl);
    }

    @RequestMapping(value = "/create/{vendorPageId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createPolicyMetaPage(@PathVariable("vendorPageId") Long vendorPageId, Model model) {
        if (true) {
            throw new UnsupportedOperationException("This operation was disabled");
        }

        VendorPage vendorPage = vendorPageService.getVendorPage(vendorPageId);
        if (vendorPage == null) {
            return "redirect:/404";
        }
        PolicyMetaPage policyMetaPage = new PolicyMetaPage();
        policyMetaPage.setVendorPage(vendorPage);
        model.addAttribute("policyMetaPage", policyMetaPage);
        setNavigation(policyMetaPage, policyMetaPage.getVendorPage(), null, model);
        return "cms/page/policy/edit";
    }

    @RequestMapping(value = "/edit/{policyMetaPageId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editPolicyMetaPage(@PathVariable("policyMetaPageId") Long policyMetaPageId, Model model) {
        PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPageWithContent(policyMetaPageId);
        if (policyMetaPage == null) {
            return "redirect:/404";
        }
        policyMetaPageService.fillCanBeDeletedForPolicyMetaCategoriesContent(policyMetaPage);
        model.addAttribute("policyMetaPage", policyMetaPage);
        setNavigation(policyMetaPage, policyMetaPage.getVendorPage(), null, model);
        addUnusedPolicyMetaCategory(policyMetaPage, model);
        return "cms/page/policy/edit";
    }

    @RequestMapping(value = {"/create/{vendorPageId}", "/edit/{policyMetaPageId}"}, method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String updatePolicyMetaPage(@Valid @ModelAttribute("policyMetaPage") PolicyMetaPage policyMetaPage,
                                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes,
                                       @RequestParam(value = "policyPlans", required = false) String[] policyPlanIds) {
        setNavigation(policyMetaPage, policyMetaPage.getVendorPage(), null, model);
        policyMetaPageValidator.validate(policyMetaPage, bindingResult);
        boolean isNew = policyMetaPage.getId() == null;
        if (bindingResult.hasErrors()) {
            if (!isNew) {
                PolicyMetaPage policyMetaPage_ = policyMetaPageService.getPolicyMetaPageWithContent(policyMetaPage.getId());
                policyMetaPage.setPolicyMetaCategoryList(policyMetaPage_.getPolicyMetaCategoryList());
                policyMetaPage.setPolicyMetaPlanInfoList(policyMetaPage_.getPolicyMetaPlanInfoList());
                policyMetaPage.setPolicyMetaCustomCategoryList(policyMetaPage_.getPolicyMetaCustomCategoryList());
                policyMetaPage.setPolicyMetaRestrictionsList(policyMetaPage_.getPolicyMetaRestrictionsList());
                policyMetaPage.setPolicyMetaPackageList(policyMetaPage_.getPolicyMetaPackageList());
            }

            return "cms/page/policy/edit";
        }
        try {
            policyMetaPage = policyMetaPageService.savePolicyMetaPage(policyMetaPage);
        } catch (ObjectOptimisticLockingFailureException e) {
            bindingResult.reject("optimistic.locking.error");
            log.warn("Optimistic locking error occurred when trying to create the page", e);
            return "cms/page/policy/edit";
        }
        addMessageSuccess("Saved", model);
        return isNew ? "redirect:/cms/vendor_page/policy_page/edit/" + policyMetaPage.getId() : editPolicyMetaPage(policyMetaPage.getId(), model);
    }

    @RequestMapping(value = "/delete/{policyMetaPageId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deletePolicyMetaPage(@PathVariable("policyMetaPageId") Long pageId) {
        if (true) {
            throw new UnsupportedOperationException("This operation was disabled");
        }
        PolicyMetaPage page = policyMetaPageService.getPolicyMetaPage(pageId);
        policyMetaPageService.deletePolicyMetaPage(pageId);
        return "redirect:/cms/vendor_page/edit/" + page.getVendorPage().getId();
    }

    /* Policy meta category content */

    @RequestMapping(value = {"/policy_meta_category/create/{policyMetaPageId}/{categoryCode}"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createCategoryContent(@PathVariable("policyMetaPageId") Long policyMetaPageId, @PathVariable("categoryCode") String categoryCode, Model model) {
        PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaPageId);
        if (policyMetaPage == null) {
            return "redirect:/404";
        }
        Category category = categoryService.getByCode(categoryCode);
        if (category == null) {
            return "redirect:/404";
        }
        if (policyMetaCategoryContentService.getByCategoryIdAndPolicyMetaPageId(category.getId(), policyMetaPage.getId()) != null) {
            return "redirect:/404";
        }
        PolicyMetaCategoryContent policyMetaCategoryContent = new PolicyMetaCategoryContent();
        policyMetaCategoryContent.setCategory(category);
        policyMetaCategoryContent.setName(category.getName());
        policyMetaCategoryContent.setPolicyMetaPageForCategory(policyMetaPage);
        model.addAttribute("policyMetaCategoryContent", policyMetaCategoryContent);
        String multiselectDataProvidersJson = policyMetaCategoryContentService.prepareMultiselectData(policyMetaPage);
        model.addAttribute("multiselectDataProvidersJson", multiselectDataProvidersJson);
        setNavigation(policyMetaCategoryContent.getPolicyMetaPageForCategory(), policyMetaCategoryContent.getPolicyMetaPageForCategory().getVendorPage(), policyMetaCategoryContent, model);
        return "cms/page/content/category/policyMetaCategoryEdit";
    }

    @RequestMapping(value = {"/policy_meta_category/edit/{policyMetaCategoryContentId}"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editCategoryContent(@PathVariable("policyMetaCategoryContentId") Long policyMetaCategoryContentId, Model model) {
        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentService.getPolicyMetaCategoryContent(policyMetaCategoryContentId);
        if (policyMetaCategoryContent == null) {
            return "redirect:/404";
        }
        model.addAttribute("policyMetaCategoryContent", policyMetaCategoryContent);
        PolicyMetaPage policyMetaPage = policyMetaCategoryContentService.getPolicyMetaPage(policyMetaCategoryContent);
        if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null) {
            model.addAttribute("groups", groupService.getAll());
        }
        String multiselectDataProvidersJson = policyMetaCategoryContentService.prepareMultiselectData(policyMetaPage);
        model.addAttribute("multiselectDataProvidersJson", multiselectDataProvidersJson);
        setNavigation(policyMetaPage, policyMetaPage.getVendorPage(), policyMetaCategoryContent, model);
        return "cms/page/content/category/policyMetaCategoryEdit";
    }

    @RequestMapping(value = {"/policy_meta_category/edit/{policyMetaCategoryContentId}", "/policy_meta_category/edit"}, method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String updatePolicyMetaCategoryContent(@Valid @ModelAttribute("policyMetaCategoryContent") PolicyMetaCategoryContent policyMetaCategoryContent,
                                                  BindingResult bindingResult, Model model,
                                                  @RequestParam(value = "saveType", required = false) String[] saveType,
                                                  @RequestParam Map<String, String> params) {
        PolicyMetaPage policyMetaPage;
        if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null) {
            policyMetaPage = preparePolicyMetaCategoryContent(params.get("policyMetaPageForCategory.id"), policyMetaCategoryContent, policyMetaCategoryContent.getPolicyMetaPageForCategory());
        } else if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null) {
            policyMetaPage = preparePolicyMetaCategoryContent(params.get("policyMetaPageForCustomCategory.id"), policyMetaCategoryContent, policyMetaCategoryContent.getPolicyMetaPageForCustomCategory());
        } else if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null) {
            policyMetaPage = preparePolicyMetaCategoryContent(params.get("policyMetaPageForPlanInfo.id"), policyMetaCategoryContent, policyMetaCategoryContent.getPolicyMetaPageForPlanInfo());
        } else if (policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null) {
            policyMetaPage = preparePolicyMetaCategoryContent(params.get("policyMetaPageForRestrictions.id"), policyMetaCategoryContent, policyMetaCategoryContent.getPolicyMetaPageForRestrictions());
        } else {
            policyMetaPage = preparePolicyMetaCategoryContent(params.get("policyMetaPackage.id"), policyMetaCategoryContent, policyMetaCategoryContent.getPolicyMetaPackage());
        }
        policyMetaCategoryContentValidator.validate(policyMetaCategoryContent, bindingResult);

        boolean saveContent = ArrayUtils.contains(saveType, "content");
        boolean saveText = ArrayUtils.contains(saveType, "text");
        if (!saveContent && !saveText) {
            bindingResult.reject(null, "Please select elements to save");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("policyMetaCategoryContent", policyMetaCategoryContent);
            String multiselectDataProvidersJson = policyMetaCategoryContentService.prepareMultiselectData(policyMetaPage);
            model.addAttribute("multiselectDataProvidersJson", multiselectDataProvidersJson);
            model.addAttribute("groups", groupService.getAll());
            setNavigation(policyMetaPage, policyMetaPage.getVendorPage(), policyMetaCategoryContent, model);
            return "cms/page/content/category/policyMetaCategoryEdit";
        }
        try {
            policyMetaCategoryContent = policyMetaCategoryContentService.saveAllPolicyMetaCategoryContent(policyMetaCategoryContent, saveContent, saveText);
        } catch (ObjectOptimisticLockingFailureException e) {
            bindingResult.reject("optimistic.locking.error");
            log.warn("Optimistic locking error occurred when trying to create the policy meta category content", e);
            return "cms/page/content/category/policyMetaCategoryEdit";
        }
        addMessageSuccess("Saved", model);
        return editCategoryContent(policyMetaCategoryContent.getId(), model);
    }

    private PolicyMetaPage preparePolicyMetaCategoryContent(String policyMetaCategoryContentIdStr, PolicyMetaCategoryContent policyMetaCategoryContent, PolicyMetaPage policyMetaPage) {
        Long policyMetaPageId = Long.valueOf(policyMetaCategoryContentIdStr);
        policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaPageId);
        return policyMetaPage;
    }

    @RequestMapping(value = "/policy_meta_category/delete/{policyMetaCategoryContentId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deletePolicyMetaCategory(@PathVariable("policyMetaCategoryContentId") Long id) {
        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentService.getPolicyMetaCategoryContent(id);
        PolicyMetaPage policyMetaPage = policyMetaCategoryContentService.getPolicyMetaPage(policyMetaCategoryContent);
        policyMetaCategoryContentService.deletePolicyMetaCategoryContent(id);
        return "redirect:/cms/vendor_page/policy_page/edit/" + policyMetaPage.getId();
    }

    /* Policy meta custom category content */
    @RequestMapping(value = {"/policy_meta_category/createCategory/{type}/{policyMetaPageId}"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createCustomCategoryContent(@PathVariable("policyMetaPageId") Long policyMetaPageId,
                                              @PathVariable("type") String type,
                                              Model model) {
        PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaPageId);
        if (policyMetaPage == null) {
            return "redirect:/404";
        }
        PolicyMetaCategoryContent policyMetaCategoryContent = new PolicyMetaCategoryContent();
        if ("customCategory".equals(type)) {
            policyMetaCategoryContent.setPolicyMetaPageForCustomCategory(policyMetaPage);
        } else if ("planInfo".equals(type)) {
            policyMetaCategoryContent.setPolicyMetaPageForPlanInfo(policyMetaPage);
        } else if ("policyRestrictions".equals(type)) {
            policyMetaCategoryContent.setPolicyMetaPageForRestrictions(policyMetaPage);
        } else if ("Package".equals(type)) {
            policyMetaCategoryContent.setPolicyMetaPackage(policyMetaPage);
        } else {
            return "redirect:/404";
        }
        model.addAttribute("policyMetaCategoryContent", policyMetaCategoryContent);
        model.addAttribute("groups", groupService.getAll());
        String multiselectDataProvidersJson = policyMetaCategoryContentService.prepareMultiselectData(policyMetaPage);
        model.addAttribute("multiselectDataProvidersJson", multiselectDataProvidersJson);
        setNavigation(policyMetaPage, policyMetaPage.getVendorPage(), policyMetaCategoryContent, model);
        return "cms/page/content/category/policyMetaCategoryEdit";
    }

    private void addUnusedPolicyMetaCategory(PolicyMetaPage policyMetaPage, Model model) {
        Set<Category> categorySet = new HashSet<>(categoryService.getAllCategoriesWithoutConditional());
        List<PolicyMetaCategoryContent> policyMetaCategoryContentList = policyMetaPage.getPolicyMetaCategoryList();
        for (PolicyMetaCategoryContent policyMetaCategoryContent : policyMetaCategoryContentList) {
            categorySet.remove(policyMetaCategoryContent.getCategory());
        }
        model.addAttribute("categories", new ArrayList<>(categorySet));
    }
}

