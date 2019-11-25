package com.travelinsurancemaster.web.controllers.cms.rest;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.dto.cms.certificate.validator.CertificateValidator;
import com.travelinsurancemaster.services.cms.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Chernov Artur on 17.12.2015.
 */
@RestController
@RequestMapping("/api/certificate")
public class PolicyMetaCertificateRestController {

    @Autowired
    private CertificateValidator certificateValidator;

    @Autowired
    private CertificateService certificateService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Certificate> getCertificateList() {
        return certificateService.getAllCertificates();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object createCertificate(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
                                    @RequestParam("vendorCode") String vendorCode, @RequestParam("policyMetaCode") String policyMetaCode,
                                    @RequestParam(value = "countries", required = false) Set<CountryCode> countries, @RequestParam(required = false, value = "states") Set<StateCode> states,
                                    @RequestParam("defaultPolicy") boolean defaultPolicy) {
        BindingResult errors = new BeanPropertyBindingResult(new Certificate(), "certificate");
        certificateValidator.validateCreateCertificate(file, fileName, vendorCode, policyMetaCode, countries, states, defaultPolicy, errors);
        if (errors.hasErrors()) {
            return getErrorResponse(errors);
        }
        Certificate certificate = certificateService.addCertificate(file, fileName, vendorCode, policyMetaCode, countries, states, defaultPolicy);
        return certificate;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object updateCertificate(@RequestParam("uuid") String uuid, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
                                    @RequestParam("vendorCode") String vendorCode, @RequestParam("policyMetaCode") String policyMetaCode,
                                    @RequestParam(value = "countries", required = false) Set<CountryCode> countries, @RequestParam(required = false, value = "states") Set<StateCode> states,
                                    @RequestParam("defaultPolicy") boolean defaultPolicy) {
        BindingResult errors = new BeanPropertyBindingResult(new Certificate(), "certificate");
        certificateValidator.validateUpdateCertificate(uuid, file, fileName, vendorCode, policyMetaCode, countries, states, defaultPolicy, true, errors);
        if (errors.hasErrors()) {
            return getErrorResponse(errors);
        }
        Certificate certificate = certificateService.updateCertificate(uuid, file, fileName, vendorCode, policyMetaCode, countries, states, defaultPolicy);
        return certificate;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, params = {"uuid"})
    public Object removeCertificate(@RequestParam("uuid") String uuid) {
        BindingResult errors = new BeanPropertyBindingResult(new Certificate(), "certificate");
        certificateValidator.validateRemoveCertificate(uuid, errors);
        if (errors.hasErrors()) {
            return getErrorResponse(errors);
        }
        Certificate removedCertificate = certificateService.removeByUuid(uuid);
        return RemovedCertificateResponse.getResponse(Collections.singletonList(removedCertificate));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, params = {"vendorCode"})
    public Object removeCertificateByVendor(@RequestParam("vendorCode") String vendorCode) {
        BindingResult errors = new BeanPropertyBindingResult(new Certificate(), "certificate");
        certificateValidator.validateRemoveCertificate(null, vendorCode, errors);
        if (errors.hasErrors()) {
            return getErrorResponse(errors);
        }
        List<Certificate> removedCertificates = certificateService.removeByVendor(vendorCode);
        return RemovedCertificateResponse.getResponse(removedCertificates);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, params = {"vendorCode", "policyMetaCode"})
    public Object removeCertificateByVendorAndPolicyMetaCode(@RequestParam("vendorCode") String vendorCode, @RequestParam("policyMetaCode") String policyMetaCode) {
        BindingResult errors = new BeanPropertyBindingResult(new Certificate(), "certificate");
        certificateValidator.validateRemoveCertificate(null, vendorCode, policyMetaCode, errors);
        if (errors.hasErrors()) {
            return getErrorResponse(errors);
        }
        List<Certificate> removedCertificates = certificateService.removeByVendorAndPolicy(vendorCode, policyMetaCode);
        return RemovedCertificateResponse.getResponse(removedCertificates);
    }

    private static class RemovedCertificateResponse {
        private String uuid;
        private boolean removed;

        public RemovedCertificateResponse(String uuid, boolean removed) {
            this.uuid = uuid;
            this.removed = removed;
        }

        public static List<RemovedCertificateResponse> getResponse(List<Certificate> certificates) {
            List<RemovedCertificateResponse> response = new ArrayList<>();
            for (Certificate certificate : certificates) {
                response.add(new RemovedCertificateResponse(certificate.getUuid(), certificate.isDeleted()));
            }
            return response;
        }

        public boolean isRemoved() {
            return removed;
        }

        public void setRemoved(boolean removed) {
            this.removed = removed;
        }
    }

    private ErrorResponse getErrorResponse(Errors errors) {
        ErrorResponse errorResponse = new ErrorResponse();
        for (ObjectError objectError : errors.getAllErrors()) {
            errorResponse.getErrors().add(new Error(objectError.getDefaultMessage(), objectError.getCode()));
        }
        return errorResponse;
    }

    private class ErrorResponse implements Serializable {
        private static final long serialVersionUID = -3911824077585251609L;

        List<Error> errors = new ArrayList<>();

        public ErrorResponse() {
        }

        public ErrorResponse(List<Error> errors) {
            this.errors = errors;
        }

        public List<Error> getErrors() {
            return errors;
        }

        public void setErrors(List<Error> errors) {
            this.errors = errors;
        }
    }

    private class Error implements Serializable {
        private static final long serialVersionUID = -2904173109893479230L;

        private String message;
        private String code;

        public Error(String message, String code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }


}
