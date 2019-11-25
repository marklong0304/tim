package com.travelinsurancemaster.services.export;

import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 16.09.15.
 */

public class ExcelPaymentsView extends AbstractNewExcelView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=payments.xlsx");

        @SuppressWarnings("unchecked")
        List<Purchase> purchases = (List<Purchase>) model.get("purchases");
        Sheet sheet = workbook.createSheet("Payments");
        sheet.setDefaultColumnWidth(30);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Affiliate");
        header.getCell(0).setCellStyle(style);

        header.createCell(1).setCellValue("User");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("Traveler");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("Trip cost");
        header.getCell(3).setCellStyle(style);

        header.createCell(4).setCellValue("Depart date");
        header.getCell(4).setCellStyle(style);

        header.createCell(5).setCellValue("Vendor");
        header.getCell(5).setCellStyle(style);

        header.createCell(6).setCellValue("Policy");
        header.getCell(6).setCellStyle(style);

        header.createCell(7).setCellValue("Policy Number");
        header.getCell(7).setCellStyle(style);

        header.createCell(8).setCellValue("Purchase Date");
        header.getCell(8).setCellStyle(style);

        header.createCell(9).setCellValue("Policy $");
        header.getCell(9).setCellStyle(style);

        header.createCell(10).setCellValue("Note");
        header.getCell(10).setCellStyle(style);

        int rowCount = 1;

        for (Purchase purchase : purchases) {
            Row aRow = sheet.createRow(rowCount++);

            User affiliate = purchase.getAffiliateCommission().getAffiliate();
            User user = purchase.getUser();

            aRow.createCell(0).setCellValue(!user.getId().equals(affiliate.getId()) ? "" : affiliate.getFullName());
            aRow.createCell(1).setCellValue(user.getFullName());
            aRow.createCell(2).setCellValue(purchase.getPurchaseQuoteRequest().getPrimaryTraveler().toString());
            aRow.createCell(3).setCellValue(purchase.getQuoteRequest().getTripCost().toString());
            aRow.createCell(4).setCellValue(DateUtil.getLocalDateStr(purchase.getQuoteRequest().getDepartDate()));
            aRow.createCell(5).setCellValue(purchase.getPolicyMeta().getVendor().getName());
            aRow.createCell(6).setCellValue(purchase.getPolicyMeta().getDisplayName());
            aRow.createCell(7).setCellValue(purchase.getPolicyNumber());
            aRow.createCell(8).setCellValue(DateUtil.getLocalDateStr(purchase.getPurchaseDate()));
            aRow.createCell(9).setCellValue(purchase.getTotalPrice().toString());
            aRow.createCell(10).setCellValue(purchase.getNote());
        }

    }
}
