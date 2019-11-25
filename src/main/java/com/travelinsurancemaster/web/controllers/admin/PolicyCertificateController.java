package com.travelinsurancemaster.web.controllers.admin;

import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.services.cms.PolicyMetaPageService;
import com.travelinsurancemaster.util.FileAttachmentUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.Optional;

/**
 * Created by Chernov Artur on 24.09.15.
 */

@Deprecated
@Controller
@Scope(value = "request")
@RequestMapping(value = "/policy/certificate")
public class PolicyCertificateController {
    private static final Logger log = LoggerFactory.getLogger(PolicyCertificateController.class);

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @RequestMapping(value = "get/{policyMetaPageId}.pdf", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getCertificate(@PathVariable("policyMetaPageId") Long policyMetaPageId) {
        HttpHeaders headers = new HttpHeaders();
        if (policyMetaPageId == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        byte[] certificate = policyMetaPageService.getCertificate(policyMetaPageId);
        if (certificate == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        Optional<PolicyMetaPage> policyMetaPage = Optional.ofNullable(policyMetaPageService.getPolicyMetaPage(policyMetaPageId));
        String name = "certificate";
        String extension = ".pdf";
        String fileName = policyMetaPage.isPresent() ? policyMetaPage.get().getCaption() + " " + name + extension : name + extension;
        Date modifiedDate = policyMetaPage.isPresent() ? policyMetaPage.get().getCertificateModifiedDate() : new Date();
        FileAttachmentUtils.setHeaders(headers, FileAttachmentUtils.PDF_MEDIA_TYPE, modifiedDate, fileName);
        return new ResponseEntity<>(certificate, headers, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("policyMetaPageId") Long policyMetaPageId, @RequestParam("certificate") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("uploadError", "empty file");
        } else if (!file.getContentType().equals(MediaType.parseMediaType("application/pdf").toString())
                || !FilenameUtils.isExtension(file.getOriginalFilename(), "pdf")) {
            redirectAttributes.addFlashAttribute("uploadError", "only application/pdf is supported");
        } else {
            try {
                policyMetaPageService.updateCertificate(policyMetaPageId, file.getBytes());
                redirectAttributes.addFlashAttribute("uploadSuccess", "Upload Success");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                redirectAttributes.addFlashAttribute("uploadError", ExceptionUtils.getRootCauseMessage(e));
            }
        }
        return "redirect:/cms/vendor_page/policy_page/edit/" + policyMetaPageService.getPolicyMetaPage(policyMetaPageId).getId();
    }

}
