package com.travelinsurancemaster.services.export.report;

import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.services.export.AbstractNewExcelView;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 16.09.15.
 */

public class ExcelCommissionReportView extends AbstractNewExcelView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=commissions.xlsx");

        @SuppressWarnings("unchecked")
        List<Purchase> purchases = (List<Purchase>) model.get("purchases");
        Sheet sheet = workbook.createSheet("Commissions");
        sheet.setDefaultColumnWidth(30);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Rec Date");
        header.getCell(0).setCellStyle(style);

        header.createCell(1).setCellValue("Traveler");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("Vendor");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("Depart date");
        header.getCell(3).setCellStyle(style);

        header.createCell(4).setCellValue("Policy $");
        header.getCell(4).setCellStyle(style);

        header.createCell(5).setCellValue("Vendor commission");
        header.getCell(5).setCellStyle(style);

        header.createCell(6).setCellValue("Salary");
        header.getCell(6).setCellStyle(style);

        header.createCell(7).setCellValue("Pay");
        header.getCell(7).setCellStyle(style);

        header.createCell(8).setCellValue("Confirm");
        header.getCell(8).setCellStyle(style);


        int rowCount = 1;

        for (Purchase purchase : purchases) {
            Row aRow = sheet.createRow(rowCount++);
            aRow.createCell(0).setCellValue(DateUtil.getDateStr(purchase.getVendorCommission().getReceivedDate()));
            aRow.createCell(1).setCellValue(purchase.getPurchaseQuoteRequest().getPrimaryTraveler().toString());
            aRow.createCell(2).setCellValue(purchase.getPolicyMeta().getVendor().getName());
            aRow.createCell(3).setCellValue(DateUtil.getLocalDateStr(purchase.getQuoteRequest().getDepartDate()));
            aRow.createCell(4).setCellValue(purchase.getTotalPrice().toString());
            aRow.createCell(5).setCellValue(purchase.getVendorCommission().getReceivedCommission() != null ? purchase.getVendorCommission().getReceivedCommission().toString() : StringUtils.EMPTY);
            aRow.createCell(6).setCellValue(purchase.getAffiliateCommission().getSalary() != null ? purchase.getAffiliateCommission().getSalary().toString() : StringUtils.EMPTY);
            aRow.createCell(7).setCellValue(BooleanUtils.toString(purchase.getAffiliateCommission().getPaid() != null, "True", "False"));
            aRow.createCell(8).setCellValue(BooleanUtils.toString(purchase.getVendorCommission().isConfirm(), "True", "False"));
        }

    }
}
