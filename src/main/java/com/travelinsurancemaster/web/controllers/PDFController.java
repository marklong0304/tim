package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.converters.PDFService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.FileAttachmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by Chernov Artur on 19.06.15.
 */

@Controller
public class PDFController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(PDFController.class);

    private final PurchaseService purchaseService;
    private final PDFService pdfService;

    public PDFController(PurchaseService purchaseService, PDFService pdfService) {
        this.purchaseService = purchaseService;
        this.pdfService = pdfService;
    }

    @GetMapping(value = "/results/purchase/getPdf/{purchaseUuid}")
    public ResponseEntity<byte[]> getPDF(@PathVariable("purchaseUuid") String purchaseUuid) {
        log.debug("Get request for PDF purchase document with uuid={}", purchaseUuid);
        HttpHeaders headers = new HttpHeaders();
        if (purchaseUuid == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        Purchase purchase = purchaseService.getPurchase(purchaseUuid);
        byte[] contents = pdfService.getPurchasePdfByteArray(purchase);
        if (contents == null) {
            return FileAttachmentUtils.get404RedirectEntity(headers);
        }
        String filename = purchaseService.getPurchasePdfName(purchase);
        FileAttachmentUtils.setHeaders(headers, FileAttachmentUtils.PDF_MEDIA_TYPE, DateUtil.fromLocalDate(purchase.getPurchaseDate()), filename);
        return new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
    }


}
