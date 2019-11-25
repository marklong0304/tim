package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.dto.json.datatable.filter.PolicyJson;
import com.travelinsurancemaster.model.dto.json.datatable.filter.VendorJson;
import com.travelinsurancemaster.model.dto.validator.VendorValidator;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.TestDb;
import com.travelinsurancemaster.repository.UserRepository;
import com.travelinsurancemaster.repository.VendorRepository;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.cms.VendorPageService;
import com.travelinsurancemaster.services.export.ExcelMatrixView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 02.06.15.
 */


@Controller
@Scope(value = "request")
@RequestMapping(value = "/vendors")
public class VendorController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(VendorController.class);

    private final VendorRepository vendorRepository;
    private final VendorService vendorService;
    private final VendorValidator vendorValidator;
    private final CategoryService categoryService;
    private final PolicyMetaService policyMetaService;
    private final VendorPageService vendorPageService;
    @Autowired
    protected InsuranceMasterApiProperties apiProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDb testDb;

    public VendorController(VendorRepository vendorRepository, VendorService vendorService, VendorValidator vendorValidator,
                            CategoryService categoryService, PolicyMetaService policyMetaService, VendorPageService vendorPageService) {
        this.vendorRepository = vendorRepository;
        this.vendorService = vendorService;
        this.vendorValidator = vendorValidator;
        this.categoryService = categoryService;
        this.policyMetaService = policyMetaService;
        this.vendorPageService = vendorPageService;
    }

    private void setNavigation(Vendor vendor, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/vendors", "Vendors");
        if (vendor != null && vendor.getId() != null) {
            map.put("/vendors/edit/" + String.valueOf(vendor.getId()), vendor.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/vendors");
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public String getVendors(Model model) {
        model.addAttribute("vendors", vendorService.findAllSortedByNameAndCanDelete());
        return "admin/vendors/vendors";
    }

    @Secured("ROLE_ADMIN")
    @ResponseBody
    @GetMapping(value = "/all")
    public Set<VendorJson> getAllVendors() {
        List<Vendor> allVendors = vendorService.findAllSortedByNameAndCanDelete();
        return allVendors.stream().map(vendor -> new VendorJson(vendor.getName(), vendor.getId())).collect(Collectors.toSet());
    }

    @Secured("ROLE_ADMIN")
    @ResponseBody
    @GetMapping(value = "/policy/{vendorId}")
    public Set<PolicyJson> getPoliciesByVendor(@PathVariable("vendorId") Long vendorId) {
        Vendor allVendors = vendorService.getById(vendorId);
        List<PolicyMeta> policyMetaList = allVendors.getPolicyMetaList();

        return policyMetaList.stream().map(policyMeta -> new PolicyJson(policyMeta.getDisplayName(), policyMeta.getId(), policyMeta.getVendor().getId())).collect(Collectors.toSet());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/edit/{vendorId}")
    public String editVendor(@PathVariable("vendorId") Long vendorId, Model model) {
        Vendor vendor = vendorService.getById(vendorId);
        if (vendor == null) {
            return "redirect:/404";
        }
        List<PolicyMeta> policyMetaList = policyMetaService.getByVendor(vendor);
        model.addAttribute("vendor", vendor);
        model.addAttribute("apiUrl", getApiUrl(vendor.getName()));
        model.addAttribute("policyMetaList", policyMetaList);
        model.addAttribute("chosenUserList", userRepository.findByIdInOrderByEmail(vendor.getLongTestUserIds()));
        model.addAttribute("userList", userRepository.findAllByDeletedNullOrderByEmail());
        setNavigation(vendor, model);
        return "admin/vendors/vendorEdit";
    }

    private String getApiUrl(String vendorName) {
        switch (vendorName){
            case "Berkshire Hathaway Travel Protection":
                return apiProperties.getbHTravelProtection().toString();
            case "Generali Global Assistance":
                return apiProperties.getCsa().toString();
            case "Global Alert":
                return apiProperties.getGlobalAlert().toString();
            case "HCC Medical Insurance Services":
                return apiProperties.gethCCMISAtlasMultiTrip().toString() +
                       apiProperties.gethCCMISAtlasTravel().toString() +
                       apiProperties.gethCCMISStudentSecure().toString();
            case "HTH Travel Insurance":
                return apiProperties.gethTHTravelInsurance().toString()+
                       apiProperties.gethTHTravelInsuranceSingleTrip().toString() +
                       apiProperties.gethTHTravelInsuranceTravelGap().toString() +
                       apiProperties.gethTHTravelInsuranceTripProtector().toString();
            case "iTravelInsured":
                return apiProperties.getiTravelInsured().toString();
            case "RoamRight":
                return apiProperties.getRoamRight().toString();
            case "Seven Corners":
                return apiProperties.getSevenCorners().toString();
            case "Travel Guard":
                return apiProperties.getTravelGuard().toString();
            case "Travelex Insurance":
                return apiProperties.getTravelexInsurance().toString();
            case "Travel Insured International":
                return apiProperties.getTravelInsured().toString();
            case "TravelSafe Insurance":
                return apiProperties.getTravelSafe().toString();
            case "Trawick International":
                return apiProperties.getTrawick().toString();
            case "TripAssure":
                return "Not supported yet";
            case "USA-ASSIST":
                return apiProperties.getUsaAssist().toString();
            case "USI":
                return apiProperties.getTravelInsure().toString();
        }
        return "No configuration found, please add in VendorController";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/edit/{vendorId}")
    public String editVendor(@Valid @ModelAttribute("vendor") Vendor vendor, BindingResult bindingResult, Model model) {
        Vendor vendorById = vendorService.getById(vendor.getId());
        if (vendorById == null) {
            return "redirect:/404";
        }
        setNavigation(vendor, model);
        vendorValidator.validate(vendor, bindingResult);
        if (bindingResult.hasErrors()) {
            vendor.setPolicyMetaList(vendorById.getPolicyMetaList());
            return "admin/vendors/vendorEdit";
        }

        boolean prevActive =  vendorById.isActive();
        boolean active = vendor.isActive();

        vendor = vendorService.save(vendor);

        VendorPage vendorPage = vendor.getVendorPage();
        if(vendorPage != null)
        {
            if (prevActive && !active && vendorPage.getStatus() != PageStatus.DISABLED)
            {
                vendorPage.setStatus(PageStatus.DISABLED);
                this.vendorPageService.saveVendorPage(vendorPage);
            } else if (!prevActive && active && vendorPage.getStatus() == PageStatus.DISABLED)
            {
                vendorPage.setStatus(PageStatus.PUBLISHED);
                this.vendorPageService.saveVendorPage(vendorPage);
            }
        }

        addMessageSuccess("Saved", model);
        return editVendor(vendor.getId(), model);
    }

    @GetMapping(value = "/create")
    @Secured("ROLE_ADMIN")
    public String createVendor(Model model) {
        model.addAttribute("vendor", new Vendor());
        setNavigation(null, model);
        return "admin/vendors/vendorCreate";
    }

    @PostMapping(value = "/create")
    @Secured("ROLE_ADMIN")
    public String createVendor(@Valid @ModelAttribute("vendor") Vendor vendor, BindingResult bindingResult, Model model) {
        if (vendorRepository.countByCode(vendor.getCode()) > 0) {
            bindingResult.rejectValue("code", null, "Code should be unique");
        }
        setNavigation(vendor, model);
        vendorValidator.validate(vendor, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/vendors/vendorCreate";
        }

        if(vendor.getTermsAndConditionsType() == null)
            vendor.setTermsAndConditionsType(TermsAndConditionsType.LINK);

        vendor = vendorService.save(vendor);

        VendorPage vendorPage = new VendorPage();
        vendorPage.setVendor(vendor);
        vendorPage.setName(vendor.getName());
        vendorPage.setCaption(vendor.getName());
        vendorPage.setDescription("");
        vendorPage.setContent("");
        vendorPage.setStatus(PageStatus.DRAFT);

        this.vendorPageService.saveVendorPage(vendorPage);

        return editVendor(vendor.getId(), model);
    }

    @PostMapping(value = "/delete/{vendorId}")
    @Secured("ROLE_ADMIN")
    public String deleteVendor(@PathVariable("vendorId") Long vendorId) {
        log.debug("Delete vendor by id={} action", vendorId);
        Vendor vendor = vendorRepository.findOne(vendorId);
        VendorPage vendorPage = vendorPageService.getVendorPageByVendor(vendor);
        vendor.setDeletedDate(new Date());
        vendor.setCanDeleted(true);
        vendorService.save(vendor);
        if (vendorPage!=null){vendorPage.setDeletedDate(new Date());
        vendorPageService.saveVendorPage(vendorPage);}
        return "redirect:/vendors";
    }

    @PostMapping(value = "/create", params = {"updateValues"})
    @Secured("ROLE_ADMIN")
    public String updateValuesCreate(@ModelAttribute("vendor") Vendor vendor, Model model) {
        if (vendor.getPercentInfo() != null)
            vendor.getPercentInfo().clear();
        if (vendor.getPercentType().getId() != PercentType.NONE.getId()) {
            if (vendor.getPercentInfo() == null) {
                vendor.setPercentInfo(new ArrayList<>());
            }
            vendor.getPercentInfo().add(new PercentInfo());
        }
        setNavigation(vendor, model);
        return "admin/vendors/vendorCreate";
    }

    @PostMapping(value = "/create", params = {"addRange"})
    @Secured("ROLE_ADMIN")
    public String addRangeCreate(Vendor vendor, Model model) {
        vendor.getPercentInfo().add(new PercentInfo());
        setNavigation(vendor, model);
        return "admin/vendors/vendorCreate";
    }

    @PostMapping(value = "/create", params = {"removeRange"})
    @Secured("ROLE_ADMIN")
    public String removeRangeCreate(@RequestParam("removeRange") int rangeId, Vendor vendor, Model model) {
        vendor.getPercentInfo().remove(rangeId);
        setNavigation(vendor, model);
        return "admin/vendors/vendorCreate";
    }

    @PostMapping(value = "/edit/{vendorId}", params = {"updateValues"})
    @Secured("ROLE_ADMIN")
    public String updateValues(@ModelAttribute("vendor") Vendor vendor, Model model) {
        if (vendor.getPercentInfo() != null)
            vendor.getPercentInfo().clear();
        if (vendor.getPercentType().getId() != PercentType.NONE.getId()) {
            if (vendor.getPercentInfo() == null) {
                vendor.setPercentInfo(new ArrayList<>());
            }
            vendor.getPercentInfo().add(new PercentInfo());
        }
        vendor.setPolicyMetaList(vendorRepository.findOne(vendor.getId()).getPolicyMetaList());
        setNavigation(vendor, model);
        return "admin/vendors/vendorEdit";
    }

    @PostMapping(value = "/edit/{vendorId}", params = {"addRange"})
    @Secured("ROLE_ADMIN")
    public String addRange(Vendor vendor, Model model) {
        vendor.getPercentInfo().add(new PercentInfo());
        vendor.setPolicyMetaList(vendorRepository.findOne(vendor.getId()).getPolicyMetaList());
        setNavigation(vendor, model);
        return "admin/vendors/vendorEdit";
    }

    @RequestMapping(value = "/sync/{vendorId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String syncVendor(@PathVariable("vendorId") Long vendorId, Model model) {
        Vendor vendor = vendorService.getById(vendorId);

        testDb.sync(vendor.getId());

        model.addAttribute("vendor", vendor);
        addMessageSuccess("Successfully synchronized to test environment", model);
        setNavigation(vendor, model);
        return "admin/vendors/vendorEdit";
    }

    @PostMapping(value = "/edit/{vendorId}", params = {"removeRange"})
    @Secured("ROLE_ADMIN")
    public String removeRange(@RequestParam("removeRange") int rangeId, Vendor vendor, Model model) {
        vendor.getPercentInfo().remove(rangeId);
        vendor.setPolicyMetaList(vendorRepository.findOne(vendor.getId()).getPolicyMetaList());
        setNavigation(vendor, model);
        return "admin/vendors/vendorEdit";
    }

    @GetMapping(value = "/matrix")
    @Secured("ROLE_ADMIN")
    public String matrix(Model model) {
        populateData(model);
        return "admin/vendors/matrix";
    }

    @GetMapping(value = "/export")
    @Secured("ROLE_ADMIN")
    public ModelAndView downloadMatrix(Model model) throws IOException {
        populateData(model);
        model.addAttribute("restrictionTypes", PolicyMetaRestriction.RestrictionType.values());
        return new ModelAndView(new ExcelMatrixView(), "model", model);
    }

    private void populateData(Model model) {
        Map<String, List<Category>> groupCategoryMap = categoryService.getGroupCategoryMap();
        List<Category> categories = new ArrayList<>();
        groupCategoryMap.values().forEach(categories::addAll);
        model.addAttribute("groupCategories", groupCategoryMap);
        model.addAttribute("categories", categories);
        model.addAttribute("policyMetas", policyMetaService.getAllSorted(true, false));
    }

    @GetMapping(value = "/restriction-matrix")
    @Secured("ROLE_ADMIN")
    public String restrictionMatrix(Model model) {
        model.addAttribute("restrictionTypes", PolicyMetaRestriction.RestrictionType.values());
        List<PolicyMeta> policyMetas = policyMetaService.getAllSorted(false, true);
        model.addAttribute("policyMetas", policyMetas);
        model.addAttribute("restrictionValidateQuoteRequest", new QuoteRequest());
        PolicyMetaRestriction policyMetaRestriction = new PolicyMetaRestriction();
        PolicyMeta defaultPolicyMeta = policyMetas.stream().findAny().orElseThrow(()-> new RuntimeException("Policy metas not found"));
        policyMetaRestriction.setPolicyMeta(defaultPolicyMeta);
        model.addAttribute("restriction", policyMetaRestriction);
        model.addAttribute("policyMeta", defaultPolicyMeta);
        return "admin/vendors/restrictionMatrix";
    }
}
