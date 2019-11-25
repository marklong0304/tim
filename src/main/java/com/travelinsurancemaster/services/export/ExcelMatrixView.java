package com.travelinsurancemaster.services.export;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by ritchie on 3/10/16.
 */
public class ExcelMatrixView extends AbstractNewExcelView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=matrix.xlsx");
        Sheet sheet = workbook.createSheet("Category Matrix");

        CellStyle multilineCellStyle = workbook.createCellStyle();
        multilineCellStyle.setWrapText(true);

        @SuppressWarnings("unchecked")
        List<PolicyMeta> policyMetasList = (List<PolicyMeta>) model.get("policyMetas");
        Row header = sheet.createRow(0);
        int headerCellCount = 1;
        for (PolicyMeta policyMeta : policyMetasList) {
            Cell cell = header.createCell(headerCellCount++);
            cell.setCellValue(policyMeta.getVendor().getName() + " - " + policyMeta.getDisplayName());
        }

        @SuppressWarnings("unchecked")
        List<Category> categories = (List<Category>) model.get("categories");
        int rowCount = 1;
        for (Category category : categories) {
            String categoryCode = category.getCode();
            Row row = sheet.createRow(rowCount++);
            Cell categoryNameCell = row.createCell(0);
            categoryNameCell.setCellValue(category.getName());
            int categoryCellCount = 1;
            int maxLines = 0;
            for (PolicyMeta policyMeta : policyMetasList) {
                PolicyMetaCategory policyMetaCategory = policyMeta.getCategoryValue(categoryCode);
                if (policyMetaCategory != null) {
                    int lines = 1;
                    Cell categoryValueCell = row.createCell(categoryCellCount);
                    categoryValueCell.setCellStyle(multilineCellStyle);
                    String categoryData = policyMetaCategory.getType().name();
                    List<PolicyMetaCategoryValue> categoryValues = policyMetaCategory.getValues();
                    for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                        categoryData += "\n" + policyMetaCategoryValue.getCaption() + " (" + policyMetaCategoryValue.getValue() + " "
                                + policyMetaCategoryValue.getValueType().getShortCaption() + ")";
                        lines++;
                        if (categoryCode.equals(CategoryCodes.PRE_EX_WAIVER) ||
                                categoryCode.equals(CategoryCodes.CANCEL_FOR_ANY_REASON) ||
                                categoryCode.equals(CategoryCodes.CANCEL_FOR_WORK_REASONS) ||
                                categoryCode.equals(CategoryCodes.PRE_EX_WAIVER_ON_TRIP)) {
                            if (policyMetaCategoryValue.getDaysAfterInitialDeposit() != null) {
                                categoryData += "\nd after dep: " + policyMetaCategoryValue.getDaysAfterInitialDeposit();
                                lines++;
                            }
                            if (policyMetaCategoryValue.getDaysAfterFinalPayment() != null) {
                                categoryData += "\nfinal pay: " + policyMetaCategoryValue.getDaysAfterFinalPayment();
                                lines++;
                            }
                            if (policyMetaCategoryValue.getMinAge() != null || policyMetaCategoryValue.getMaxAge() != null) {
                                categoryData += "\nmin-max age: " +
                                        policyMetaCategoryValue.getMinAge() != null ? policyMetaCategoryValue.getMinAge() : "-1" +
                                        policyMetaCategoryValue.getMaxAge() != null ? policyMetaCategoryValue.getMaxAge() : "-1";
                                lines++;
                            }
                        }
                    }
                    categoryValueCell.setCellValue(categoryData);
                    if (maxLines < lines) {
                        maxLines = lines;
                    }
                }
                categoryCellCount++;

            }
            row.setHeight((short) (maxLines * sheet.getDefaultRowHeight()));
        }

        @SuppressWarnings("unchecked")
        PolicyMetaRestriction.RestrictionType[] restrictionTypes = (PolicyMetaRestriction.RestrictionType[]) model.get("restrictionTypes");
        rowCount++;
        for (PolicyMetaRestriction.RestrictionType restrictionType : restrictionTypes) {
            Row restrictionRow = sheet.createRow(rowCount++);
            Cell restrictionNameCell = restrictionRow.createCell(0);
            restrictionNameCell.setCellValue(restrictionType.name());
            int restrictionCellCount = 1;
            int maxLines = 0;
            for (PolicyMeta policyMeta : policyMetasList) {
                int lines = 0;
                List<PolicyMetaRestriction> policyMetaRestrictions = policyMeta.getRestrictions(restrictionType);
                if (!CollectionUtils.isEmpty(policyMetaRestrictions)) {
                    Cell restrictionValueCell = restrictionRow.createCell(restrictionCellCount);
                    restrictionValueCell.setCellStyle(multilineCellStyle);
                    String restrictionCellData = "";
                    for (PolicyMetaRestriction policyMetaRestriction : policyMetaRestrictions) {
                        if (!restrictionCellData.isEmpty()) {
                            restrictionCellData += "\n";
                        }
                        restrictionCellData += policyMetaRestriction.getRestrictionPermit().name();
                        lines++;
                        if (policyMetaRestriction.getRestrictionType() == PolicyMetaRestriction.RestrictionType.CITIZEN ||
                                policyMetaRestriction.getRestrictionType() == PolicyMetaRestriction.RestrictionType.DESTINATION ||
                                policyMetaRestriction.getRestrictionType() == PolicyMetaRestriction.RestrictionType.RESIDENT) {
                            if (!CollectionUtils.isEmpty(policyMetaRestriction.getCountries())) {
                                restrictionCellData += "\n" + StringUtils.join(policyMetaRestriction.getCountries(), ",");
                                lines++;
                            }
                            if (!CollectionUtils.isEmpty(policyMetaRestriction.getStates())) {
                                restrictionCellData += "\n" + StringUtils.join(policyMetaRestriction.getStates(), ",");
                                lines++;
                            }
                        }
                        if (policyMetaRestriction.getRestrictionType() == PolicyMetaRestriction.RestrictionType.AGE ||
                                policyMetaRestriction.getRestrictionType() == PolicyMetaRestriction.RestrictionType.TRIP_COST_PER_TRAVELER ||
                                policyMetaRestriction.getRestrictionType() == PolicyMetaRestriction.RestrictionType.TRIP_COST) {
                            restrictionCellData += "\n" + (policyMetaRestriction.getMinValue() != null ?
                                    policyMetaRestriction.getMinValue() : "*") + " - " +
                                    (policyMetaRestriction.getMaxValue() != null ? policyMetaRestriction.getMaxValue() : "*");
                            lines++;
                        }
                    }
                    restrictionValueCell.setCellValue(restrictionCellData);
                }
                restrictionCellCount++;
                if (maxLines < lines) {
                    maxLines = lines;
                }
            }
            restrictionRow.setHeight((short) (maxLines * sheet.getDefaultRowHeight()));
        }

        for (int i = 0; i<= header.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
