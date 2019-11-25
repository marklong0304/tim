package com.travelinsurancemaster.services.cms.util;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.dto.cms.certificate.validator.CertificateValidator;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.services.PolicyMetaCodeService;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Chernov Artur on 12.01.2016.
 */
@Service
@Transactional
public class ImportCertificateService {
    private static final Logger log = LoggerFactory.getLogger(ImportCertificateService.class);

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateValidator certificateValidator;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    public String importCertificates() {
        clearAllCertificates();
        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getPolicyMetaPageSorted();
        int success = 0;
        int error = 0;
        for (PolicyMetaPage policyMetaPage : policyMetaPages) {
            byte[] certificate = policyMetaPageService.getCertificate(policyMetaPage.getId());
            if (certificate == null) {
                continue;
            }
            PolicyMeta policyMeta = policyMetaPage.getPolicyMeta();
            String name = "certificate";
            String extension = ".pdf";
            String fileName = policyMetaPage.getCaption() + " " + name + extension;
            MultipartFile file = getMultipartFile(certificate, fileName);
            BindingResult errors = new BeanPropertyBindingResult(new Certificate(), "certificate");
            PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), null);
            String policyCode = policyMetaCode.getCode();
            certificateValidator.validateCreateCertificate(file, fileName, policyMetaPage.getVendorPage().getVendor().getCode(), policyCode, null, null, true, errors);
            if (errors.hasErrors()) {
                error++;
                for (ObjectError objectError : errors.getAllErrors()) {
                    log.debug("Certificate {} - {} import error: = {}", policyCode, policyMeta.getVendor().getCode(), objectError.getDefaultMessage());
                }
                continue;
            }
            Certificate newCertificate = certificateService.addCertificate(file, fileName, policyMetaPage.getVendorPage().getVendor().getCode(), policyCode, null, null, true);
            if (newCertificate != null) {
                success++;
                log.debug("Certificate {} - {} success", policyCode, policyMeta.getVendor().getCode());
            } else {
                log.debug("Certificate {} - {} import error: = {}", policyCode, policyMeta.getVendor().getCode(), "Null after save");
                error++;
            }
        }
        return "Imported {success: " + success + "; error: " + error + "}";
    }

    private void clearAllCertificates() {
        List<Certificate> certificates = certificateService.getAllCertificates();
        for (Certificate certificate : certificates) {
            certificateService.remove(certificate);
        }
    }

    private MultipartFile getMultipartFile(byte[] certificate, String fileName) {
        return new MockMultipartFile("defaultCertificate", fileName, CertificateValidator.getAllowedFileType(), certificate);
    }

}
