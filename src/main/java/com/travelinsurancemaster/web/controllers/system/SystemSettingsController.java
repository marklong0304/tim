package com.travelinsurancemaster.web.controllers.system;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.PercentInfo;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.dto.system.validator.SystemSettingsValidator;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.cache.CacheService;
import com.travelinsurancemaster.services.cms.util.ImportCertificateService;
import com.travelinsurancemaster.services.cms.util.ImportGrabberCertificateService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import com.travelinsurancemaster.util.ValidationUtils;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Chernov Artur on 14.08.15.
 */
@Controller
@RequestMapping(value = "admin/settings")
@Secured("ROLE_ADMIN")
public class SystemSettingsController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(SystemSettingsController.class);

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private SystemSettingsValidator systemSettingsValidator;

    @Autowired
    private ImportCertificateService importCertificateService;

    @Autowired
    private ImportGrabberCertificateService importGrabberCertificateService;

    @Autowired
    private VendorService vendorService;

    @ModelAttribute("vendors")
    public List<Vendor> vendors() {
        List<Vendor> list = vendorService.findAllSortedByName();
        return list;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getManagementPage(Model model) {
        model.addAttribute("systemSettings", systemSettingsService.getDefault());
        return "admin/system/settings";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"updateDefaultLinkValues"})
    public String updateValues(@ModelAttribute("systemSettings") SystemSettings systemSettings) {
        systemSettings.getDefaultLinkPercentInfo().clear();
        if (systemSettings.getDefaultLinkPercentType().getId() != PercentType.NONE.getId()) {
            systemSettings.getDefaultLinkPercentInfo().add(new PercentInfo());
        }
        return "admin/system/settings";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"addDefaultLinkRange"})
    public String addValues(@ModelAttribute("systemSettings") SystemSettings systemSettings) {
        systemSettings.getDefaultLinkPercentInfo().add(new PercentInfo());
        return "admin/system/settings";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"removeDefaultLinkRange"})
    public String removeValues(@RequestParam("removeDefaultLinkRange") int rangeInd, @ModelAttribute("systemSettings") SystemSettings systemSettings) {
        systemSettings.getDefaultLinkPercentInfo().remove(rangeInd);
        return "admin/system/settings";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String saveSystemSettings(@Valid @ModelAttribute("systemSettings") SystemSettings systemSettings, BindingResult bindingResult) {
        log.debug("Processing user update systemSettings={}, bindingResult = {}", systemSettings, bindingResult);
        systemSettingsValidator.validate(systemSettings, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/system/settings";
        }
        systemSettingsService.updateDefaultCommission(systemSettings);
        return "redirect:/admin/settings";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"updatePhone"})
    public String updatePhone(@ModelAttribute("systemSettings") SystemSettings systemSettings, BindingResult bindingResult) {
        ValidationUtils.validatePhone(bindingResult, systemSettings.getPhone(), "phone");
        if (bindingResult.hasErrors()) {
            return "admin/system/settings";
        }
        systemSettingsService.updateDefaultPhone(systemSettings);
        return "redirect:/admin/settings";
    }

    @RequestMapping(value = "clearProductCache", method = RequestMethod.POST)
    @ResponseBody
    public String clearProductCache() {
        cacheService.evictProducts();
        return "SUCCESS";
    }

    @RequestMapping(value = "clearPolicyMetaCache", method = RequestMethod.POST)
    @ResponseBody
    public String clearCache() {
        cacheService.invalidateCategoriesAndMetas();
        return "SUCCESS";
    }

    @RequestMapping(value = "importCertificates", method = RequestMethod.POST)
    @ResponseBody
    public String importCertificates() {
        return importCertificateService.importCertificates();
    }

    @RequestMapping(value = "importGrabberCertificates", method = RequestMethod.POST)
    @ResponseBody
    public String importGrabberCertificates() {
        importGrabberCertificateService.importCertificatesFromGrabber();
        return "SUCCESS";
    }
}
