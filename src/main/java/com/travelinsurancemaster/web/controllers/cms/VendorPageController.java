package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.dto.cms.page.validator.VendorPageValidator;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import com.travelinsurancemaster.services.cms.VendorPageService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 15.10.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/cms/vendor_page")
public class VendorPageController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(VendorPageController.class);

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private VendorPageValidator vendorPageValidator;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    private List<Vendor> vendors;

    @ModelAttribute("vendors")
    public List<Vendor> getVendors() {
        return vendors;
    }

    @PostConstruct
    public void init() {
        vendors = vendorService.findAllSortedByName();
    }

    private void setNavigation(VendorPage vendorPage, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/vendor_page", "Vendor page");
        if (vendorPage != null && vendorPage.getId() != null && vendorPage.getDeletedDate() == null) {
            map.put("/cms/vendor_page/edit/" + String.valueOf(vendorPage.getId()), vendorPage.getCaption());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/cms/vendor_page");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getVendorPages(Model model) {
        model.addAttribute("vendorPages", vendorPageService.findAllSorted());
        return "cms/page/vendor/list";
    }

    @RequestMapping(value = {"/create", "/edit"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createVendorPage(Model model) {
        if (true) {
            throw new UnsupportedOperationException("This operation was disabled");
        }
        model.addAttribute("vendorPage", new VendorPage());
        setNavigation(new VendorPage(), model);
        return "cms/page/vendor/edit";
    }

    @RequestMapping(value = "/edit/{pageId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editVendorPage(@PathVariable("pageId") Long pageId, Model model) {
        VendorPage vendorPage = vendorPageService.getVendorPage(pageId);
        if (vendorPage == null) {
            return "redirect:/404";
        }
        setNavigation(vendorPage, model);
        model.addAttribute("vendorPage", vendorPage);
        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageSorted(vendorPage);
        model.addAttribute("policyMetaPages", policyMetaPages);
        return "cms/page/vendor/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editVendorPage(@Valid @ModelAttribute("vendorPage") VendorPage vendorPage, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        vendorPage.setName(vendorPage.getName() != null ? vendorPage.getName().trim() : null);
        vendorPage.setDescription(vendorPage.getDescription() != null ? vendorPage.getDescription().trim() : null);
        vendorPageValidator.validate(vendorPage, bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(vendorPage, model);
            if (vendorPage.getId() != null) {
                model.addAttribute("policyMetaPages", policyMetaPageService.getAllPolicyMetaPagesByVendorPageSorted(vendorPage));
            }
            return "cms/page/vendor/edit";
        }
        try {
            vendorPage = vendorPageService.saveVendorPage(vendorPage);
        } catch (ObjectOptimisticLockingFailureException e) {
            bindingResult.reject("optimistic.locking.error");
            log.warn("Optimistic locking error occurred when trying to create the page", e);
            return "cms/page/vendor/edit";
        }
        if (vendorPage.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedPageId", vendorPage.getId());
        }
        addMessageSuccess("Saved", model);
        return editVendorPage(vendorPage.getId(), model);
    }

    @RequestMapping(value = "/delete/{vendorPageId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deleteVendorPage(@PathVariable("vendorPageId") Long pageId) {
        if (true) {
            throw new UnsupportedOperationException("This operation was disabled");
        }
        vendorPageService.deleteVendorPage(pageId);
        return "redirect:/cms/vendor_page";
    }

    @RequestMapping(value = "/wysiwyg")
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getWysiwyg(Model model) {
        return "cms/page/wysiwyg";
    }
}

