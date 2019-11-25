package com.travelinsurancemaster.model.dto.cms.certificate.validator;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.cms.CertificateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Chernov Artur on 16.12.15.
 */
@Component
public class CertificateValidator implements Validator {

    private static final String ALLOWED_FILE_TYPE = "application/pdf";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CertificateValidator.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    public void validateCreateCertificate(Certificate certificate, MultipartFile file, PolicyMeta policyMeta, Errors errors, String policyCode) {
        validateCreateCertificate(file, certificate.getFileName(), policyMeta.getVendor().getCode(), policyCode,
                certificate.getCountries(), certificate.getStates(), certificate.isDefaultPolicy(), errors);
    }

    public void validateCreateCertificate(MultipartFile file, String fileName, String vendorCode, String policyMetaCode,
                                          Set<CountryCode> countries, Set<StateCode> states, boolean defaultPolicy, Errors errors) {
        validateFileName(fileName, errors);
        validateFileExist(file, errors);
        validateFileType(file, errors);
        validateVendorAndPolicyMetaCodes(vendorCode, policyMetaCode, errors);
        if (!defaultPolicy) {
            validateFileUnique(null, vendorCode, policyMetaCode, countries, states, errors);
            validateCountries(countries, errors);
            validateStates(countries, states, errors);
        } else {
            checkDefaultCertificateExist(vendorCode, policyMetaCode, errors);
        }
    }

    public void validateUpdateCertificate(Certificate certificate, MultipartFile file, PolicyMeta policyMeta, boolean withFile, Errors errors, String policyCode) {
        validateUpdateCertificate(certificate.getUuid(), file, certificate.getFileName(), policyMeta.getVendor().getCode(), policyCode,
                certificate.getCountries(), certificate.getStates(), certificate.isDefaultPolicy(), withFile, errors);
    }

    public void validateUpdateCertificate(String uuid, MultipartFile file, String fileName, String vendorCode, String policyMetaCode,
                                          Set<CountryCode> countries, Set<StateCode> states, boolean defaultPolicy, boolean withFile, Errors errors) {
        validateFileName(fileName, errors);
        validateExistsCertificate(uuid, errors);
        if (errors.hasErrors()) {
            return;
        }
        Certificate certificate = certificateService.getCertificateByUuid(uuid);
        if (withFile) {
            validateFileExist(file, errors);
            validateFileType(file, errors);
        }
        validateVendorAndPolicyMetaCodes(vendorCode, policyMetaCode, errors);
        if (!defaultPolicy) {
            validateCountries(countries, errors);
            validateStates(countries, states, errors);
            if (!certificate.getVendorCode().equals(vendorCode) || !certificate.getPolicyMetaCode().equals(policyMetaCode)) {
                // change vendorCode/policyMetaCode eq add new Certificate
                validateFileUnique(null, vendorCode, policyMetaCode, countries, states, errors);
            } else {
                validateFileUnique(certificate, vendorCode, policyMetaCode, countries, states, errors);
            }
        }
        if (!certificate.isDefaultPolicy() && defaultPolicy) {
            checkDefaultCertificateExist(vendorCode, policyMetaCode, errors);
        }
    }

    public void validateRemoveCertificate(String uuid, Errors errors) {
        Certificate certificate = certificateService.getCertificateByUuid(uuid);
        if (certificate == null) {
            errors.rejectValue(null, "1001", "Certificate does not exist");
        }
    }

    public void validateRemoveCertificate(String uuid, String vendorCode, Errors errors) {
        if (vendorService.getByCode(vendorCode) == null) {
            errors.rejectValue(null, "1007", "Incorrect vendor code");
        } else {
            List<Certificate> certificates = certificateService.getCertificateByVendorCode(vendorCode);
            if (CollectionUtils.isEmpty(certificates)) {
                errors.rejectValue(null, "1013", "There is no certificate for this vendor");
            }
        }
    }

    public void validateRemoveCertificate(String uuid, String vendorCode, String policyMetaCode, Errors errors) {
        if (vendorService.getByCode(vendorCode) == null) {
            errors.rejectValue(null, "1007", "Incorrect vendor code");
        } else if (CollectionUtils.isEmpty(policyMetaService.getPoliciesByCode(policyMetaCode))) {
            errors.rejectValue(null, "1008", "Incorrect policy meta code");
        } else {
            List<Certificate> certificates = certificateService.getCertificateByVendorCodeAndPolicyMetaCode(vendorCode, policyMetaCode);
            if (CollectionUtils.isEmpty(certificates)) {
                errors.rejectValue(null, "1013", "There is no certificate for this vendor and policy meta");
            }
        }
    }

    private void validateExistsCertificate(String uuid, Errors errors) {
        Certificate certificate = certificateService.getCertificateByUuid(uuid);
        if (certificate == null) {
            errors.rejectValue(null, "1001", "Certificate does not exist");
        }
    }

    private void validateFileName(String fileName, Errors errors) {
        if (StringUtils.isBlank(fileName)) {
            errors.rejectValue(null, "1002", "File name is empty/blank");
        }
    }

    private void validateFileExist(MultipartFile file, Errors errors) {
        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            errors.rejectValue(null, "1003", "No file uploaded");
        }
    }

    private void validateFileType(MultipartFile file, Errors errors) {
        if (!FilenameUtils.wildcardMatch(file.getContentType(), ALLOWED_FILE_TYPE, IOCase.INSENSITIVE)) {
            errors.rejectValue(null, "1004", file.getContentType() + " not allowed");
        }
    }

    private void validateVendorAndPolicyMetaCodes(String vendorCode, String policyMetaCode, Errors errors) {
        if (StringUtils.isBlank(vendorCode)) {
            errors.rejectValue(null, "1005", "Vendor code is empty");
        } else if (vendorService.getByCode(vendorCode) == null) {
            errors.rejectValue(null, "1007", "Incorrect vendor code");
        }
        if (StringUtils.isBlank(policyMetaCode)) {
            errors.rejectValue(null, "1006", "Policy meta code is empty");
        } else if (CollectionUtils.isEmpty(policyMetaService.getPoliciesByCode(policyMetaCode))) {
            errors.rejectValue(null, "1008", "Incorrect policy meta code");
        }
    }

    private void validateCountries(Set<CountryCode> countries, Errors errors) {
        if (countries == null || CollectionUtils.isEmpty(countries)) {
            errors.rejectValue(null, "1009", "Countries is empty");
        }
    }

    private void validateStates(Set<CountryCode> countries, Set<StateCode> states, Errors errors) {
        if (states == null || countries == null){
            return;
        }
        for (StateCode stateCode : states) {
            if (!countries.contains(stateCode.getCountryCode())) {
                errors.rejectValue(null, "1014", "States doesn't match the Countries");
                break;
            }
        }
    }

    private void validateFileUnique(Certificate certificateParam, String vendorCode, String policyMetaCode, Set<CountryCode> countries, Set<StateCode> states, Errors errors) {
        if (countries == null) {
            return;
        }
        List<Certificate> certificates = certificateService.getCertificateByVendorCodeAndPolicyMetaCode(vendorCode, policyMetaCode, false);
        Set<CountryCode> usedCountries = new HashSet<>();
        Set<StateCode> usedStates = new HashSet<>();
        for (Certificate certificate : certificates) {
            if (certificateParam != null && certificateParam.getUuid().equals(certificate.getUuid())) {
                continue;
            }
            usedCountries.addAll(certificate.getCountries());
            usedStates.addAll(certificate.getStates());
        }
        boolean uniqueCountry = true;
        boolean uniqueState = true;
        for (CountryCode countryCode : countries) {
            if (usedCountries.contains(countryCode) && CountryCode.US != countryCode && CountryCode.CA != countryCode) {
                uniqueCountry = false;
                break;
            }
        }
        if (states != null) {
            for (StateCode code : states) {
                if (usedStates.contains(code)) {
                    uniqueState = false;
                    break;
                }
            }
        }
        if (!uniqueCountry) {
            errors.rejectValue(null, "1010", "Duplicate certificate for input countries");
        }
        if (!uniqueState) {
            errors.rejectValue(null, "1011", "Duplicate certificate for input states");
        }
    }

    private void checkDefaultCertificateExist(String vendorCode, String policyMetaCode, Errors errors) {
        if (certificateService.getDefaultCertificate(vendorCode, policyMetaCode) != null) {
            errors.rejectValue(null, "1012", "Default certificate for " + vendorCode + " and " + policyMetaCode + " already exists");
        }
    }

    public static String getAllowedFileType() {
        return ALLOWED_FILE_TYPE;
    }
}