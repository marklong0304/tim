package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.cms.attachment.FileAttachment;
import com.travelinsurancemaster.model.dto.cms.attachment.validator.FileAttachmentValidator;
import com.travelinsurancemaster.services.cms.FileAttachmentService;
import com.travelinsurancemaster.util.FileAttachmentUtils;
import com.travelinsurancemaster.web.controllers.AbstractController;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.MultipartConfigElement;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chernov Artur on 29.07.15.
 */
@Controller
@RequestMapping(value = "/cms/attachment")
public class FileAttachmentController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(FileAttachmentController.class);

    @Autowired
    private FileAttachmentService fileAttachmentService;

    @Autowired
    private MultipartConfigElement multipartConfigElement;

    @Autowired
    private FileAttachmentValidator validator;

    private void setNavigation(FileAttachment attachment, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/attachment", "File attachment");
        if (attachment != null && attachment.getId() != null) {
            map.put("/cms/attachment/edit/" + String.valueOf(attachment.getId()), attachment.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/cms/attachment");
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.GET)
    public String getAttachments(Model model) {
        log.debug("Getting attachments");
        model.addAttribute("attachments", fileAttachmentService.getAll());
        return "cms/attachment/list";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createAttachment(Model model) {
        log.debug("Getting create attachment page");
        setNavigation(new FileAttachment(), model);
        model.addAttribute("attachment", new FileAttachment());
        model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
        model.addAttribute("allowedFileTypes", validator.getAllowedFileTypes());
        return "cms/attachment/create";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createAttachment(@ModelAttribute("attachment") FileAttachment attachment,
                                   @RequestParam("file") MultipartFile file,
                                   Model model,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        log.debug("Processing attachment update: attachment={}, bindingResult = {}", attachment, bindingResult);
        validator.validateFile(file, bindingResult);
        model.addAttribute("attachment", attachment);
        model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
        model.addAttribute("allowedFileTypes", validator.getAllowedFileTypes());
        if (bindingResult.hasErrors()) {
            setNavigation(new FileAttachment(), model);
            return "/cms/attachment/create";
        }
        try {
            attachment.setFileName(file.getOriginalFilename());
            attachment.setContent(file.getBytes());
            attachment.setSize(file.getSize());
            attachment.setMimeType(file.getContentType());
            attachment = fileAttachmentService.save(attachment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (attachment.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedAttachmentId", attachment.getId());
        }
        return "redirect:/cms/attachment";

    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/edit/{attachmentId}", method = RequestMethod.GET)
    public String editAttachment(@PathVariable("attachmentId") Long attachmentId, Model model) {
        log.debug("Getting edit attachment page");
        FileAttachment attachment = fileAttachmentService.getFileAttachment(attachmentId);
        if (attachment == null) {
            return "redirect:/404";
        }
        setNavigation(attachment, model);
        model.addAttribute("attachment", attachment);
        model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
        model.addAttribute("allowedFileTypes", validator.getAllowedFileTypes());
        return "cms/attachment/edit";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editAttachment(@Valid @ModelAttribute("attachment") FileAttachment attachment, @RequestParam("file") MultipartFile file,
                                 Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Processing attachment update: attachment={}, bindingResult = {}", attachment, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("attachment", attachment);
            model.addAttribute("maxFileSize", multipartConfigElement.getMaxFileSize());
            model.addAttribute("allowedFileTypes", validator.getAllowedFileTypes());
            setNavigation(attachment, model);
            return "/cms/attachment/edit";
        }
        try {
            if (!file.isEmpty()) {
                attachment.setFileName(file.getOriginalFilename());
                attachment.setContent(file.getBytes());
                attachment.setSize(file.getSize());
                attachment.setMimeType(file.getContentType());
            }
            attachment = fileAttachmentService.save(attachment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (attachment.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedAttachmentId", attachment.getId());
        }
        return "redirect:/cms/attachment";
    }

    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    @RequestMapping(value = "/delete/{attachmentId}", method = RequestMethod.POST)
    public String deleteAttachment(@PathVariable("attachmentId") Long attachmentId) {
        log.debug("Delete attachment by id={} action", attachmentId);
        fileAttachmentService.delete(attachmentId);
        return "redirect:/cms/attachment";
    }

    @RequestMapping(value = "/{fileUid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAttachment(@PathVariable("fileUid") String fileUid) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isEmpty(fileUid)) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        FileAttachment attachment = fileAttachmentService.getFileAttachmentWithContent(fileUid);
        if (attachment == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        byte[] file = attachment.getContent();
        if (file == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        FileAttachmentUtils.setHeaders(headers, attachment.getMimeType(), attachment.getModifiedDate(), attachment.getFileName());
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }


}
