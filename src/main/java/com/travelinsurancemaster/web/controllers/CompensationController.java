package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.CommissionValueType;
import com.travelinsurancemaster.model.dto.PercentInfo;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.commission.CompensationWrapper;
import com.travelinsurancemaster.model.dto.validator.CompensationValidator;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.VendorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by N.Kurennoy on 12.05.2016.
 */

@Controller
@Scope(value = "session")
@RequestMapping(value = "/vendors/compensation")
@Secured("ROLE_ADMIN")
public class CompensationController extends AbstractController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CompensationValidator compensationValidator;

    @RequestMapping(value = "/showMatrix/{vendorId}", method = RequestMethod.GET)
    public String showMatrix(@PathVariable("vendorId") Long vendorId, Model model) {
        Vendor vendor = vendorService.getById(vendorId);
        if (vendor == null) {
            return "redirect:/404";
        }
        vendor.getPolicyMetaList().sort((p1, p2) -> (p1.getDisplayName().compareTo(p2.getDisplayName())));
        model.addAttribute("vendor", vendor);

        setNavigation(vendor, model);
        return "admin/vendors/compensationMatrix";
    }

    private void setNavigation(Vendor vendor, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/vendors", "Vendors");
        if (vendor != null && vendor.getId() != null) {
            map.put("/vendors/compensation/showMatrix/" + String.valueOf(vendor.getId()), vendor.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/vendors");
    }

    @RequestMapping(value = "vendor/{vendorId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CompensationWrapper getVendorCompensation(@PathVariable(value = "vendorId") Long vendorId) {

        //Vendor commission is deprecated
        //This function returns the common policy compensation if all policy compensations are identical

        Vendor vendor = vendorService.getById(vendorId);
        CompensationWrapper compensationWrapper = new CompensationWrapper();

        if (vendor == null) {
            return compensationWrapper;
        }

        //Check if all policy commissions for the vendor are identical
        boolean allCompensationsEqual = true;
        List<PolicyMeta> policyMetas = vendor.getPolicyMetaList();
        PolicyMeta firstPolicyMeta = policyMetas.size() > 0 ? policyMetas.get(0) : null;
        if(firstPolicyMeta != null) {
            PercentType percentType = firstPolicyMeta.getPercentType();
            List<PercentInfo> percentInfos = firstPolicyMeta.getPercentInfo();
            Optional<PolicyMeta> policyMetaOptional = policyMetas.stream().filter(
                    pm -> pm.getPercentType() != percentType || !CollectionUtils.isEqualCollection(pm.getPercentInfo(), percentInfos)
            ).findFirst();
            allCompensationsEqual = !policyMetaOptional.isPresent();
        } else {
            allCompensationsEqual = false;
        }

        compensationWrapper.setAllCompensationsEqual(allCompensationsEqual);
        compensationWrapper.setId(vendorId);
        compensationWrapper.setObject("vendor");
        compensationWrapper.setName(vendor.getName());

        if(allCompensationsEqual) {
            //If all policy commissions are identical show them as the vendor policy commission
            compensationWrapper.setPercentType(firstPolicyMeta.getPercentType());
            compensationWrapper.setPercentInfo(firstPolicyMeta.getPercentInfo());
            compensationWrapper.setCommissionValueType(firstPolicyMeta.getPercentType().getCommissionValueType());
        } else {
            compensationWrapper.setPercentType(PercentType.NONE);
            compensationWrapper.setPercentInfo(new ArrayList<>());
            compensationWrapper.setCommissionValueType(CommissionValueType.FIX);
        }

        return compensationWrapper;
    }

    @RequestMapping(value = "policy/{policyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CompensationWrapper getPolicyCompensation(@PathVariable(value = "policyId") Long policyId){
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyId);
        CompensationWrapper compensationWrapper = new CompensationWrapper();
        if (policyMeta == null) {
            return compensationWrapper;
        }

        compensationWrapper.setId(policyId);
        compensationWrapper.setObject("policy");
        compensationWrapper.setName(policyMeta.getDisplayName());
        compensationWrapper.setPercentType(policyMeta.getPercentType());
        compensationWrapper.setPercentInfo(policyMeta.getPercentInfo());
        compensationWrapper.setCommissionValueType(policyMeta.getPercentType().getCommissionValueType());

        return compensationWrapper;
    }

    @RequestMapping(value = "vendor", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map> saveVendorCompensation(@RequestBody CompensationWrapper compensationWrapper){
        Vendor vendor = vendorService.getById(compensationWrapper.getId());
        Map<String, String> errors = new HashMap<>();
        if (vendor == null) {
            errors.put("Error", "Vendor not found");
        }

        compensationValidator.validate(errors, compensationWrapper.getPercentInfo(),
                compensationWrapper.getPercentType(), "percentInfo");

        if (errors.isEmpty()) {
            vendor.getPercentInfo().clear();
            vendor.getPercentInfo().addAll(compensationWrapper.getPercentInfo());
            vendor.setPercentType(compensationWrapper.getPercentType());
            vendorService.save(vendor);
            //Update all vendor's policies
            for(PolicyMeta policyMeta : vendor.getPolicyMetaList()) {
                policyMeta.getPercentInfo().clear();
                policyMeta.getPercentInfo().addAll(compensationWrapper.getPercentInfo());
                policyMeta.setPercentType(compensationWrapper.getPercentType());
                policyMetaService.save(policyMeta);
            }
            
        } else {
            return new ResponseEntity<Map>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Map>(null, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "policy", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map> savePolicyCompensation(@RequestBody CompensationWrapper compensationWrapper){
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(compensationWrapper.getId());
        Map<String, String> errors = new HashMap<>();
        if (policyMeta == null) {
            errors.put("Error", "Policy not found");
        }

        compensationValidator.validate(errors, compensationWrapper.getPercentInfo(),
                compensationWrapper.getPercentType(), "percentInfo");

        if (errors.isEmpty()) {
            policyMeta.getPercentInfo().clear();
            policyMeta.getPercentInfo().addAll(compensationWrapper.getPercentInfo());
            policyMeta.setPercentType(compensationWrapper.getPercentType());
            policyMetaService.save(policyMeta);

        } else {
            return new ResponseEntity<Map>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Map>(null, new HttpHeaders(), HttpStatus.OK);
    }
}
