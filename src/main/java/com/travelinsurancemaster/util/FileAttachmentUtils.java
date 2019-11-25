package com.travelinsurancemaster.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Date;

/**
 * @author Alexander.Isaenco
 */
public class FileAttachmentUtils {

    public static final String PDF_MEDIA_TYPE = "application/pdf";

    public static ResponseEntity<byte[]> get404RedirectEntity(HttpHeaders headers) {
        headers.add("Location", "/404");
        return new ResponseEntity<byte[]>(null, headers, HttpStatus.FOUND);
    }

    public static HttpHeaders setHeaders(HttpHeaders headers, String mediaType, Date modifiedDate, String fileName) {
        if (headers == null) {
            throw new IllegalArgumentException("null headers");
        }
        if (StringUtils.isNoneBlank(mediaType)) {
            headers.setContentType(MediaType.parseMediaType(mediaType));
        }
        if (modifiedDate != null) {
            headers.setLastModified(modifiedDate.getTime());
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        }
        if (StringUtils.isNotBlank(fileName)) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + fileName + "\"");
        }
        return headers;
    }
}
