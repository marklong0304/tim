package com.travelinsurancemaster.services.converters;

import com.itextpdf.text.DocumentException;
import com.travelinsurancemaster.MailConfig;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.repository.CategoryRepository;
import com.travelinsurancemaster.repository.PolicyMetaPackageRepository;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 19.06.15.
 */


@Service
public class PDFService {

    private static final Logger log = LoggerFactory.getLogger(PDFService.class);

    private SpringTemplateEngine templateEngine;
    private ApplicationContext appContext;
    private PolicyMetaPackageRepository packageRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private VendorService vendorService;

    public PDFService(SpringTemplateEngine templateEngine, ApplicationContext appContext, PolicyMetaPackageRepository packageRepository, CategoryRepository categoryRepository) {
        this.templateEngine = templateEngine;
        this.appContext = appContext;
        this.packageRepository = packageRepository;
        this.categoryRepository = categoryRepository;
    }

    public byte[] getPurchasePdfByteArray(Purchase purchase) {
        String htmlContent = getHtmlContentFormPurchase(purchase);
        if (htmlContent == null) {
            return null;
        }
        return getPdfFormHtmlAsBytes(htmlContent);
    }

    private byte[] getPdfFormHtmlAsBytes(String content) {
        ITextRenderer renderer = new ITextRenderer();
        byte[] pdfAsBytes = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            renderer.getSharedContext().setReplacedElementFactory(new ImageReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory(), appContext));
            renderer.setDocumentFromString(content);
            renderer.layout();
            renderer.createPDF(os);
            pdfAsBytes = os.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
        return pdfAsBytes;
    }

    private String getHtmlContentFormPurchase(Purchase purchase) {
        Context ctx = new Context();
        ctx.setVariable("quoteRequest", purchase.getQuoteRequest());
        ctx.setVariable("purchaseRequest", purchase.getPurchaseRequest());
        ctx.setVariable("policyNumber", purchase.getPolicyNumber());
        ctx.setVariable("totalPrice", purchase.getTotalPrice());

        List<String> packages = new ArrayList<>();

        packageRepository.findAllByCodeIn(purchase.getQuoteRequest().getEnabledPackages()).forEach(item -> packages.add(item.getName()));
        ctx.setVariable("packages", packages);

        List<String> upsales = purchase.getQuoteRequest().getCategories().entrySet()
                .stream()
                .filter(m -> !m.getKey().equals(CategoryCodes.TRIP_CANCELLATION))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<String> options = new LinkedList<>();
        categoryRepository.findAllByCodeIn(upsales).forEach(
                o -> options.add(
                        o.getName()
                        + (CategoryCodes.RENTAL_CAR.equals(o.getCode()) ?
                        " ("
                            + (
                                    vendorService.hasRentalCarDates(purchase.getPolicyMeta().getVendor()) ?
                                    (
                                        DateUtil.getLocalDateStr(purchase.getQuoteRequest().getRentalCarStartDate())
                                        + " - "
                                        + DateUtil.getLocalDateStr(purchase.getQuoteRequest().getRentalCarEndDate())
                                    )
                                    :
                                            (
                                                    purchase.getQuoteRequest().getRentalCarLength() + " day"
                                                    + (purchase.getQuoteRequest().getRentalCarLength() > 1 ? "s" : "")
                                            )
                            )
                        +")"
                        : "")
                )
        );
        ctx.setVariable("upsales", options);

        ctx.setVariable("quoteRequest", purchase.getQuoteRequest());
        ctx.setVariable("purchaseRequest", purchase.getPurchaseRequest());
        ctx.setVariable("policyNumber", purchase.getPolicyNumber());
        ctx.setVariable("totalPrice", purchase.getTotalPrice());

        ctx.setVariable("userName", purchase.getPurchaseQuoteRequest().getPrimaryTraveler().getFirstName());
        ctx.setVariable("quoteRequest", purchase.getQuoteRequest());
        ctx.setVariable("purchaseRequest", purchase.getPurchaseRequest());
        ctx.setVariable("purchase", purchase);

        ctx.setVariable("siteAddress", mailConfig.getSiteAddress());

        StringBuilder address = new StringBuilder();
        address.append(purchase.getPurchaseRequest().getAddress()).append(", ");
        if (StringUtils.isNotBlank(purchase.getPurchaseRequest().getCity())) {
            address.append(purchase.getPurchaseRequest().getCity()).append(", ");
        }
        if (purchase.getQuoteRequest().getResidentState() != null
                && StringUtils.isNotBlank(purchase.getQuoteRequest().getResidentState().getCaption())) {
            address.append(purchase.getQuoteRequest().getResidentState().getCaption()).append(", ");
        }
        if (purchase.getQuoteRequest().getResidentCountry() != null
                && StringUtils.isNotBlank(purchase.getQuoteRequest().getResidentCountry().getCaption())) {
            address.append(purchase.getQuoteRequest().getResidentCountry().getCaption()).append(" ");
        }
        address.append(purchase.getPurchaseRequest().getPostalCode());
        ctx.setVariable("address", address);
        ctx.setVariable("certificateLink", null);
        return templateEngine.process("purchase/pdf", ctx);
    }
}
