package com.travelinsurancemaster.model.dto.cms.attachment.validator;

import com.travelinsurancemaster.model.dto.cms.attachment.FileAttachment;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Chernov Artur on 28.07.15.
 */
@Component
public class FileAttachmentValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(FileAttachmentValidator.class);

    @Value("${attachment.allowedFileTypes}")
    private String allowedFileTypes;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(FileAttachment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
    }

    public void validateFile(MultipartFile file, Errors errors) {
        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            errors.rejectValue(null, null, "No file uploaded");
            return;
        }
        validateFileType(file, errors);
    }

    private void validateFileType(MultipartFile file, Errors errors) {
        if (StringUtils.isEmpty(allowedFileTypes)) {
            return;
        }
        String[] types = allowedFileTypes.split(",");
        boolean isAllowed = false;
        for (String type : types) {
            if (FilenameUtils.wildcardMatch(file.getContentType(), type, IOCase.INSENSITIVE)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            errors.rejectValue("content", null, file.getContentType() + " not allowed");
        }
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }
}
