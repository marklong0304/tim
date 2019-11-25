package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.services.VendorService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander.Isaenco
 */
@Controller
@RequestMapping(value = "/vendorLogo")
public class VendorLogoController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(VendorLogoController.class);

    @Autowired
    private VendorService vendorService;

    @RequestMapping(value = "get/{vendorCode}.png", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getLogo(@PathVariable("vendorCode") String vendorCode) {
        if (StringUtils.isEmpty(vendorCode)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Vendor vendor = vendorService.getVendorWithLogo(vendorCode);
        if (vendor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Date iconLastModified = vendor.getIconLastModified();
        byte[] img = vendor.getIcon();
        if (img == null) {
            ClassPathResource classPathResource = new ClassPathResource("static/images/vendors/" + vendorCode + ".png");
            try (InputStream imgStream = classPathResource.getInputStream()) {
                if (imgStream != null) {
                    img = IOUtils.toByteArray(imgStream);
                }
                iconLastModified = new Date(classPathResource.lastModified());
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        if (img == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setLastModified(iconLastModified.getTime());
        headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).getHeaderValue());
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("vendorId") Long vendorId, @RequestParam("logo") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("uploadError", "empty file");

        } else if(file.getContentType().equals(MediaType.IMAGE_PNG_VALUE) && FilenameUtils.isExtension(file.getOriginalFilename().toLowerCase(), "png")) {
            try {
                vendorService.updateLogo(vendorId, file.getBytes());
                redirectAttributes.addFlashAttribute("uploadSuccess", "Upload Success");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                redirectAttributes.addFlashAttribute("uploadError", ExceptionUtils.getRootCauseMessage(e));
            }

        } else if(file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) && FilenameUtils.isExtension(file.getOriginalFilename().toLowerCase(), "jpg")) {
            try {
                vendorService.updateLogo(vendorId, file.getBytes());
                redirectAttributes.addFlashAttribute("uploadSuccess", "Upload Success");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                redirectAttributes.addFlashAttribute("uploadError", ExceptionUtils.getRootCauseMessage(e));
            }

        } else {
            redirectAttributes.addFlashAttribute("uploadError", "only image/png and image/jpeg are supported");
        }

//        } else if (
//            !file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)
//            ||
//            !FilenameUtils.isExtension(file.getOriginalFilename(), "png")
//        ) {
//            redirectAttributes.addFlashAttribute("uploadError", "only image/png is supported");
//        } else {
//        }
        return "redirect:/vendors/edit/" + String.valueOf(vendorId);
    }

}
