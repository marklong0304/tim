package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.cms.page.FilingClaimContact;
import com.travelinsurancemaster.model.dto.cms.page.FilingClaimPage;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.dto.cms.page.validator.FilingClaimPageValidator;
import com.travelinsurancemaster.services.cms.FilingClaimContactService;
import com.travelinsurancemaster.services.cms.FilingClaimPageService;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import com.travelinsurancemaster.services.cms.VendorPageService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 04.12.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/cms/filing_claim_page")
public class FilingClaimPageController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(FilingClaimPageController.class);

    @Autowired
    private FilingClaimPageService filingClaimPageService;

    @Autowired
    private FilingClaimContactService filingClaimContactService;

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private FilingClaimPageValidator filingClaimPageValidator;

    @ModelAttribute("vendorPages")
    public List<VendorPage> getVendorPages() {
        return vendorPageService.findAllSorted();
    }

    private void setNavigation(FilingClaimPage filingClaimPage, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/filing_claim_page", "Filing claim page");
        if (filingClaimPage != null && filingClaimPage.getId() != null) {
            map.put("/cms/filing_claim_page/edit/" + String.valueOf(filingClaimPage.getId()), filingClaimPage.getCaption());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/cms/filing_claim_page");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getFilingClaimPages(Model model) {
        model.addAttribute("filingClaimPages", filingClaimPageService.findAllSortedByName());
        return "cms/page/filing_claim/list";
    }

    @RequestMapping(value = {"/create", "/edit"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createFilingClaimPage(Model model) {
        model.addAttribute("filingClaimPage", new FilingClaimPage());
        setNavigation(new FilingClaimPage(), model);
        return "cms/page/filing_claim/edit";
    }

    @RequestMapping(value = "/edit/{pageId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editFilingClaimPage(@PathVariable("pageId") Long pageId, Model model) {
        FilingClaimPage filingClaimPage = filingClaimPageService.getFilingClaimPage(pageId);
        if (filingClaimPage == null) {
            return "redirect:/404";
        }
        setNavigation(filingClaimPage, model);
        model.addAttribute("filingClaimPage", filingClaimPage);
        model.addAttribute("filingClaimContacts", filingClaimContactService.getFilingClaimContactsByPage(filingClaimPage));

        VendorPage vendorPage = filingClaimPage.getVendorPage();
//        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, null);
        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageFillingClaim(vendorPage);
        boolean isNotEmpty = CollectionUtils.isNotEmpty(policyMetaPages);
        model.addAttribute("hasPolicyMetaPages", isNotEmpty);

        return "cms/page/filing_claim/edit";
    }

    @RequestMapping(value = {"/edit/{filingClaimPageId}", "create"}, method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editFilingClaimPage(@Valid @ModelAttribute("filingClaimPage") FilingClaimPage filingClaimPage, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        filingClaimPage.setName(filingClaimPage.getName() != null ? filingClaimPage.getName().trim() : null);
        filingClaimPage.setDescription(filingClaimPage.getDescription() != null ? filingClaimPage.getDescription().trim() : null);
        filingClaimPageValidator.validate(filingClaimPage, bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(filingClaimPage, model);
            return "cms/page/filing_claim/edit";
        }
        try {
            filingClaimPage = filingClaimPageService.saveFilingClaimPage(filingClaimPage);
        } catch (ObjectOptimisticLockingFailureException e) {
            bindingResult.reject("optimistic.locking.error");
            log.warn("Optimistic locking error occurred when trying to create the page", e);
            return "cms/page/filing_claim/edit";
        }
        if (filingClaimPage.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedPageId", filingClaimPage.getId());
        }
        addMessageSuccess("Saved", model);
        return editFilingClaimPage(filingClaimPage.getId(), model);
    }

    @RequestMapping(value = "/delete/{filingClaimPageId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deleteFilingClaimPage(@PathVariable("filingClaimPageId") Long pageId) {
        filingClaimPageService.deleteFilingClaimPage(pageId);
        return "redirect:/cms/filing_claim_page";
    }

    @RequestMapping(value = {"contact/create/{filingClaimPageId}"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createFilingClaimContact(@PathVariable("filingClaimPageId") Long pageId, Model model) {
        FilingClaimPage filingClaimPage = filingClaimPageService.getFilingClaimPage(pageId);
        if (filingClaimPage == null) {
            return "redirect:/404";
        }
        FilingClaimContact contact = new FilingClaimContact();
        contact.setFilingClaimPage(filingClaimPage);
        model.addAttribute("filingClaimContact", contact);
        VendorPage vendorPage = vendorPageService.getVendorPage(filingClaimPage.getVendorPage().getId());

//        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, null);
        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageSort(vendorPage);

        model.addAttribute("policyMetaPages", policyMetaPages);


//        List<Long> policyMetaPageIds = new ArrayList<>();
//        model.addAttribute("policyMetaPageIds", policyMetaPageIds);


        setPolicyMetaPageIds(model, null, vendorPage);
        setNavigation(filingClaimPage, model);
        return "cms/page/filing_claim/edit_contact";
    }

    @RequestMapping(value = {"contact/edit/{contactId}"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editFilingClaimContact(@PathVariable("contactId") Long contactId, Model model) {
        FilingClaimContact contact = filingClaimContactService.getFilingClaimContact(contactId);
        FilingClaimPage filingClaimPage = contact.getFilingClaimPage();
        model.addAttribute("filingClaimContact", contact);
        VendorPage vendorPage = vendorPageService.getVendorPage(filingClaimPage.getVendorPage().getId());

//        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, contact);
//        policyMetaPages.addAll(policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, null));

        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageSort(vendorPage);
        model.addAttribute("policyMetaPages", policyMetaPages);

        setPolicyMetaPageIds(model, contact, vendorPage);
        setNavigation(filingClaimPage, model);
        return "cms/page/filing_claim/edit_contact";
    }

    @RequestMapping(value = {"/contact/edit/{contactId}", "/contact/create/{filingClaimPageId}"}, method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editFilingClaimContact(@Valid @ModelAttribute("filingClaimContact") FilingClaimContact filingClaimContact, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.isEmpty(filingClaimContact.getPolicyMetaPagesStr())) {
            bindingResult.rejectValue("policyMetaPages", null, "Policy meta pages cannot be empty");
        }
        if (filingClaimContact.getContent() == null || StringUtils.isBlank(filingClaimContact.getContent().replaceAll("\\<.*?>", ""))) {
            bindingResult.rejectValue("content", null, "Contact content cannot be empty.");
        }
        if (bindingResult.hasErrors()) {
            FilingClaimPage filingClaimPage = filingClaimPageService.getFilingClaimPage(filingClaimContact.getFilingClaimPage().getId());
            filingClaimContact.setFilingClaimPage(filingClaimPage);
            setNavigation(filingClaimContact.getFilingClaimPage(), model);
            VendorPage vendorPage = vendorPageService.getVendorPage(filingClaimPage.getVendorPage().getId());
            List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, null);
            FilingClaimContact contact = null;
            if (filingClaimContact.getId() != null) {
                contact = filingClaimContactService.getFilingClaimContact(filingClaimContact.getId());
                if (contact != null && CollectionUtils.isNotEmpty(contact.getPolicyMetaPages())) {
                    policyMetaPages.addAll(policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, contact));
                }
            }
            model.addAttribute("policyMetaPages", policyMetaPages);
            setPolicyMetaPageIds(model, contact != null ? contact : filingClaimContact, vendorPage);
            return "cms/page/filing_claim/edit_contact";
        }
        if (filingClaimContact.getFilingClaimPage() == null) {
            return "redirect:/404";
        }
        FilingClaimPage filingClaimPage = filingClaimPageService.getFilingClaimPage(filingClaimContact.getFilingClaimPage().getId());
        filingClaimContact.setFilingClaimPage(filingClaimPage);
        VendorPage vendorPage = vendorPageService.getVendorPage(filingClaimPage.getVendorPage().getId());

        FilingClaimContact contact2 = null;
        if(filingClaimContact.getId() != null)
            contact2 = filingClaimContactService.getFilingClaimContact(filingClaimContact.getId());

        filingClaimContact = filingClaimContactService.saveFilingClaimContact(filingClaimContact, vendorPage, contact2);
        if (filingClaimContact.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedPageId", filingClaimContact.getId());
        }
        addMessageSuccess("Saved", model);
        return editFilingClaimContact(filingClaimContact.getId(), model);
    }

    @RequestMapping(value = "/contact/delete/{contactId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deleteFilingClaimContact(@PathVariable("contactId") Long contactId) {
        filingClaimContactService.deleteFilingClaimContact(contactId);
        return "redirect:/cms/filing_claim_page";
    }

    @RequestMapping(value = "/wysiwyg")
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getWysiwyg(Model model) {
        return "cms/page/wysiwyg";
    }

    private void setPolicyMetaPageIds(Model model, FilingClaimContact contact, VendorPage vendorPage) {

        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, contact);

        List<Long> policyMetaPageIds = new ArrayList<>();
//        for (PolicyMetaPage policyMetaPage : contact.getPolicyMetaPages()) {

        for (PolicyMetaPage policyMetaPage : policyMetaPages) {
            policyMetaPageIds.add(policyMetaPage.getId());
        }

        model.addAttribute("policyMetaPageIds", policyMetaPageIds);
    }
}

