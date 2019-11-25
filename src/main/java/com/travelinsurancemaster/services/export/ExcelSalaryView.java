package com.travelinsurancemaster.services.export;

import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 16.09.15.
 */

public class ExcelSalaryView extends AbstractNewExcelView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=salary.xlsx");

        @SuppressWarnings("unchecked")
        List<Purchase> purchases = (List<Purchase>) model.get("purchases");
        Sheet sheet = workbook.createSheet("Salary");
        sheet.setDefaultColumnWidth(30);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Affiliate");
        header.getCell(0).setCellStyle(style);

        header.createCell(1).setCellValue("Vendor");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("Policy");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("Policy #");
        header.getCell(3).setCellStyle(style);

        header.createCell(4).setCellValue("Policy $");
        header.getCell(4).setCellStyle(style);

        header.createCell(5).setCellValue("Purch Date");
        header.getCell(5).setCellStyle(style);

        header.createCell(6).setCellValue("Exp. Salary $");
        header.getCell(6).setCellStyle(style);

        header.createCell(7).setCellValue("Vendor commission");
        header.getCell(7).setCellStyle(style);

        header.createCell(8).setCellValue("Salary");
        header.getCell(8).setCellStyle(style);

        header.createCell(9).setCellValue("Pay");
        header.getCell(9).setCellStyle(style);

        header.createCell(10).setCellValue("Note");
        header.getCell(10).setCellStyle(style);

        int rowCount = 1;

        for (Purchase purchase : purchases) {
            Row aRow = sheet.createRow(rowCount++);

            User affiliate = purchase.getAffiliateCommission().getAffiliate();

            aRow.createCell(0).setCellValue(affiliate.getFullName());
            aRow.createCell(1).setCellValue(purchase.getPolicyMeta() != null ? purchase.getPolicyMeta().getVendor().getName() : "");
            aRow.createCell(2).setCellValue(purchase.getPolicyMeta().getDisplayName());
            aRow.createCell(3).setCellValue(purchase.getPolicyNumber());
            aRow.createCell(4).setCellValue(purchase.getTotalPrice().toString());
            aRow.createCell(5).setCellValue(DateUtil.getLocalDateStr(purchase.getPurchaseDate()));
            aRow.createCell(6).setCellValue(purchase.getAffiliateCommission().getSalary() != null ? purchase.getAffiliateCommission().getSalary().toString() : StringUtils.EMPTY);
            aRow.createCell(7).setCellValue(purchase.getVendorCommission().getReceivedCommission() != null ? purchase.getVendorCommission().getReceivedCommission().toString() : StringUtils.EMPTY);
            aRow.createCell(8).setCellValue(purchase.getAffiliateCommission().getSalary() != null ? purchase.getAffiliateCommission().getSalary().toString() : StringUtils.EMPTY);
            aRow.createCell(9).setCellValue(purchase.getAffiliateCommission().getPaid() != null);
            aRow.createCell(10).setCellValue(purchase.getNote());
        }

    }
}
