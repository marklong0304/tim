package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.dto.cms.certificate.validator.CertificateValidator;
import com.travelinsurancemaster.services.PolicyMetaCodeService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.util.FileAttachmentUtils;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 23.12.15.
 */
@Controller
@RequestMapping(value = "/cms/certificate")
public class PolicyMetaCertificateController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(PolicyMetaCertificateController.class);

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private MultipartConfigElement multipartConfigElement;

    @Autowired
    private CertificateValidator certificateValidator;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/{policyMetaId}", method = RequestMethod.GET)
    public String getCertificate(@PathVariable("policyMetaId") Long policyMetaId, Model model) {
        if (policyMetaId == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), null);
        List<Certificate> certificates = certificateService.getCertificatesByPolicyMeta(policyMeta, false, policyMetaCode.getCode());
        List<Certificate> defaultCertificateList = new ArrayList<>();
        Certificate defaultCertificate = certificateService.getDefaultCertificate(policyMeta, policyMetaCode.getCode());
        if (defaultCertificate != null) {
            defaultCertificateList.add(defaultCertificate);
        }
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("certificates", certificates);
        model.addAttribute("defaultCertificateList", defaultCertificate);
        return "/cms/certificate/list";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/create/{policyMetaId}", method = RequestMethod.GET)
    public String createCertificate(@PathVariable("policyMetaId") Long policyMetaId, @RequestParam Map<String, String> requestParams, Model model) {
        if (policyMetaId == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        Certificate certificate = new Certificate();
        certificate.setDefaultPolicy(requestParams.containsKey("default"));
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("certificate", certificate);
        model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
        model.addAttribute("allowedFileTypes", CertificateValidator.getAllowedFileType());
        return "/cms/certificate/edit";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/edit/{policyMetaId}/{certificateId}", method = RequestMethod.GET)
    public String editCertificate(@PathVariable("policyMetaId") Long policyMetaId, @PathVariable("certificateId") Long certificateId, Model model) {
        Certificate certificate = certificateService.getCertificateById(certificateId);
        if (certificate == null || policyMetaId == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("certificate", certificate);
        model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
        model.addAttribute("allowedFileTypes", CertificateValidator.getAllowedFileType());
        return "cms/certificate/edit";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/create/{policyMetaId}", method = RequestMethod.POST)
    public String createCertificate(@PathVariable("policyMetaId") Long policyMetaId,
                                    @ModelAttribute("certificate") Certificate certificate,
                                    @RequestParam("file") MultipartFile file,
                                    Model model,
                                    BindingResult bindingResult) {
        if (policyMetaId == null || certificate == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), null);
        certificateValidator.validateCreateCertificate(certificate, file, policyMeta, bindingResult, policyMetaCode.getCode());
        model.addAttribute("policyMeta", policyMeta);
        model.addAttribute("certificate", certificate);
        model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
        model.addAttribute("allowedFileTypes", CertificateValidator.getAllowedFileType());
        if (bindingResult.hasErrors()) {
            return "/cms/certificate/edit";
        }
        try {
            certificate.setVendorCode(policyMeta.getVendor().getCode());
            certificate.setPolicyMetaCode(policyMetaCode.getCode());
            certificate.setSize(file.getSize());
            certificate.setMimeType(file.getContentType());
            certificate = certificateService.save(certificate, file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return editCertificate(policyMetaId, certificate.getId(), model);
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/edit/{policyMetaId}/{certificateId}", method = RequestMethod.POST)
    public String editCertificate(@PathVariable("policyMetaId") Long policyMetaId,
                                  @ModelAttribute("certificate") Certificate certificate,
                                  @RequestParam("file") MultipartFile file,
                                  Model model, BindingResult bindingResult) {
        if (policyMetaId == null || certificate == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        Certificate storedCertificate = certificateService.getCertificateById(certificate.getId());
        if (storedCertificate == null) {
            return "redirect:/404";
        }
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), null);
        certificateValidator.validateUpdateCertificate(certificate, file, policyMeta, !file.isEmpty(), bindingResult, policyMetaCode.getCode());
        if (bindingResult.hasErrors()) {
            model.addAttribute("policyMeta", policyMeta);
            model.addAttribute("certificate", certificate);
            model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
            model.addAttribute("allowedFileTypes", CertificateValidator.getAllowedFileType());
            return "/cms/certificate/edit";
        }
        try {
            certificate.setVendorCode(policyMeta.getVendor().getCode());
            certificate.setPolicyMetaCode(policyMetaCode.getCode());
            if (!file.isEmpty()) {
                certificate.setSize(file.getSize());
                certificate.setMimeType(file.getContentType());
            }
            certificate = certificateService.save(certificate, file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return editCertificate(policyMetaId, certificate.getId(), model);
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/delete/{policyMetaId}/{certificateId}", method = RequestMethod.POST)
    public String deleteCertificate(@PathVariable("policyMetaId") Long policyMetaId, @PathVariable("certificateId") Long certificateId) {
        if (policyMetaId == null) {
            return "redirect:/404";
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        if (policyMeta == null) {
            return "redirect:/404";
        }
        certificateService.removeById(certificateId);
        return "redirect:/cms/certificate/" + policyMeta.getId();
    }

    @RequestMapping(value = "/show/{fileUid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getCertificate(@PathVariable("fileUid") String fileUid) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isEmpty(fileUid)) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        Certificate certificate = certificateService.getCertificateByUuid(fileUid);
        if (certificate == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        File certificateFile = certificateService.getCertificateFile(fileUid);
        if (certificateFile == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        List<PolicyMetaCode> policyMetaCodes = policyMetaService.getPolicyMetas(certificate.getVendorCode(), certificate.getPolicyMetaCode());
        String filename = "certificate";
        if (CollectionUtils.isNotEmpty(policyMetaCodes)) {
            PolicyMeta policyMeta = policyMetaCodes.get(0).getPolicyMeta();
            filename = policyMeta.getVendor().getName() + " - " + policyMeta.getDisplayName();
        }
        try {
            FileInputStream fis = new FileInputStream(certificateFile);
            byte[] file = IOUtils.toByteArray(fis);
            if (file == null) {
                return FileAttachmentUtils.get404RedirectEntity(headers);
            }
            FileAttachmentUtils.setHeaders(headers, certificate.getMimeType(), certificate.getModifiedDate(), filename + ".pdf");
            return new ResponseEntity<>(file, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
    }

}
