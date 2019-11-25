package com.travelinsurancemaster.services.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.report.sales.*;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Chernov Artur on 01.10.15.
 */

@Service
public class SalesService {
    private static final Logger log = LoggerFactory.getLogger(SalesService.class);

    @Autowired
    private PurchaseService purchaseService;

    public SalesResponse getSalesResponse(SalesRequest salesRequest) {
        SalesResponse salesResponse = new SalesResponse();
        List<Purchase> purchases = purchaseService.getAllSuccessBySalesRequest(salesRequest);
        salesResponse.setSalesRequest(salesRequest);

        Set<PolicyMeta> notEmptySalesPolicyMetasHash = new HashSet<>();
        Set<PolicyMeta> notEmptyCommissionPolicyMetasHash = new HashSet<>();
        Set<String> notEmptySalesAffiliates = new HashSet<>();
        Set<String> notEmptyCommissionAffiliates = new HashSet<>();

        for (Purchase purchase : purchases){
            BigDecimal totalPrice = purchase.getTotalPrice();
            User affiliate = purchase.getAffiliateCommission().getAffiliate();

            PolicyMeta policyMeta = purchase.getPolicyMeta();
            if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) == 1){
                setNotEmpty(notEmptySalesPolicyMetasHash, notEmptySalesAffiliates, affiliate, policyMeta);
            }

            BigDecimal receivedCommission = purchase.getVendorCommission().getReceivedCommission();
            if (receivedCommission != null && receivedCommission.compareTo(BigDecimal.ZERO) == 1){
                setNotEmpty(notEmptyCommissionPolicyMetasHash, notEmptyCommissionAffiliates, affiliate, policyMeta);
            }
        }

        TreeMap<String, SalesCell> datesHash = getDatesHash(salesRequest);

        TreeMap<String, SalesCell> affiliatesHash = getAffiliatesHash(salesRequest);
        TreeMap<String, SalesCell> notEmptySalesAffiliatesHash = SerializationUtils.clone(affiliatesHash);
        notEmptySalesAffiliatesHash.entrySet().removeIf(e -> !notEmptySalesAffiliates.contains(e.getKey()));
        TreeMap<String, SalesCell> notEmptyCommissionsAffiliatesHash = SerializationUtils.clone(affiliatesHash);
        notEmptyCommissionsAffiliatesHash.entrySet().removeIf(e -> !notEmptyCommissionAffiliates.contains(e.getKey()));

        fillTableHeader(salesResponse, datesHash, notEmptySalesAffiliates, notEmptyCommissionAffiliates);

        SalesTable dailySalesTable = salesResponse.getDailySalesTable();
        SalesTable dailyCommissionsTable = salesResponse.getDailyCommissionsTable();
        SalesTable salesPerAffiliateTable = salesResponse.getSalesPerAffiliateTable();
        SalesTable commissionsPerAffiliateTable = salesResponse.getCommissionsPerAffiliateTable();

        for (PolicyMeta policyMeta : salesRequest.getPolicyMetas()) {
            if (notEmptySalesPolicyMetasHash.contains(policyMeta)) {
                createRow(datesHash, dailySalesTable, policyMeta);
                createRow(notEmptySalesAffiliatesHash, salesPerAffiliateTable, policyMeta);
            }

            if (notEmptyCommissionPolicyMetasHash.contains(policyMeta)) {
                createRow(datesHash, dailyCommissionsTable, policyMeta);
                createRow(notEmptyCommissionsAffiliatesHash, commissionsPerAffiliateTable, policyMeta);
            }
        }

        for (Purchase purchase : purchases) {
            PolicyMeta policyMeta = purchase.getPolicyMeta();

            ReportInterval interval = salesRequest.getInterval();
            LocalDate purchaseDate = purchase.getPurchaseDate();
            String dateHash = DateUtil.getDateHash(purchaseDate, interval);
            BigDecimal totalPrice = purchase.getTotalPrice();
            User affiliate = purchase.getAffiliateCommission().getAffiliate();
            String affiliateName = null;
            if (affiliate != null){
                affiliateName = affiliate.getFullName();
            }


            if (policyMeta != null && notEmptySalesPolicyMetasHash.contains(policyMeta)) {
                incCell(dailySalesTable, policyMeta, dateHash, totalPrice);

                if (affiliate != null) {
                    incCell(salesPerAffiliateTable, policyMeta, affiliateName, totalPrice);
                }
            }

            if (policyMeta != null && notEmptyCommissionPolicyMetasHash.contains(policyMeta)) {
                BigDecimal receivedCommission = purchase.getVendorCommission().getReceivedCommission();

                if (receivedCommission != null && receivedCommission.compareTo(BigDecimal.ZERO) == 1) {
                    incCell(dailyCommissionsTable, policyMeta, dateHash, receivedCommission);
                    if (affiliate != null) {
                        incCell(commissionsPerAffiliateTable, policyMeta, affiliateName, receivedCommission);
                    }
                }
            }

            incFooter(dailySalesTable, dateHash, totalPrice);
            incFooter(salesPerAffiliateTable, affiliateName, totalPrice);

            BigDecimal receivedCommission = purchase.getVendorCommission().getReceivedCommission();
            if (receivedCommission != null && receivedCommission.compareTo(BigDecimal.ZERO) == 1) {
                incFooter(commissionsPerAffiliateTable, affiliateName, receivedCommission);
                incFooter(dailyCommissionsTable, dateHash, receivedCommission);
            }

        }

        fillTotalByRowForTable(salesResponse);

        return salesResponse;
    }

    private void createRow(TreeMap<String, SalesCell> datesHash, SalesTable dailySalesTable, PolicyMeta policyMeta) {
        dailySalesTable.getContent().put(policyMeta, new SalesRow(SerializationUtils.clone(datesHash), new SalesPercentCell()));
        dailySalesTable.setFooter(new SalesRow(SerializationUtils.clone(datesHash), new SalesPercentCell()));
    }

    private void setNotEmpty(Set<PolicyMeta> notEmptySalesPolicyMetasHash, Set<String> notEmptySalesAffiliates, User affiliate, PolicyMeta policyMeta) {
        if (policyMeta != null) {
            notEmptySalesPolicyMetasHash.add(policyMeta);
        }

        if (affiliate != null){
            notEmptySalesAffiliates.add(affiliate.getFullName());
        }
    }

    private void incFooter(SalesTable table, String name, BigDecimal amount) {
        table.getFooter().getCells().get(name).inc(amount);
    }

    private void incCell(SalesTable table, PolicyMeta policyMeta, String name, BigDecimal amount) {
        table.getContent().get(policyMeta).getCells().get(name).inc(amount);
    }

    private TreeMap<String, SalesCell> getDatesHash(SalesRequest salesRequest) {
        TreeMap<String, SalesCell> datesHash = new TreeMap<>();
        List<Date> dates = DateUtil.getDateRange(new DateTime(salesRequest.getFrom()), new DateTime(salesRequest.getTo()));
        for (Date date : dates) {
            datesHash.put(DateUtil.getDateHash(date, salesRequest.getInterval()), new SalesCell());
        }
        return datesHash;
    }

    private TreeMap<String, SalesCell> getAffiliatesHash(SalesRequest salesRequest) {
        TreeMap<String, SalesCell> affiliatesHash = new TreeMap<>();
        for (User affiliate : salesRequest.getAffiliates()) {
            affiliatesHash.put(affiliate.getFullName(), new SalesCell());
        }
        return affiliatesHash;
    }

    private void fillTotalByRowForTable(SalesResponse salesResponse) {
        fillTotalForRow(salesResponse.getDailySalesTable().getContent());
        fillTotalForFooterRow(salesResponse.getDailySalesTable().getFooter());
        fillTotalForRow(salesResponse.getDailyCommissionsTable().getContent());
        fillTotalForFooterRow(salesResponse.getDailyCommissionsTable().getFooter());
        fillTotalForRow(salesResponse.getSalesPerAffiliateTable().getContent());
        fillTotalForFooterRow(salesResponse.getSalesPerAffiliateTable().getFooter());
        fillTotalForRow(salesResponse.getCommissionsPerAffiliateTable().getContent());
        fillTotalForFooterRow(salesResponse.getCommissionsPerAffiliateTable().getFooter());
    }

    private void fillTotalForRow(Map<PolicyMeta, SalesRow> map) {
        BigDecimal finalAmount = BigDecimal.ZERO;
        int finalCount = 0;
        String last = StringUtils.EMPTY;
        for (Map.Entry<PolicyMeta, SalesRow> entry : map.entrySet()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalCount = 0;
            for (Map.Entry<String, SalesCell> cellEntry : entry.getValue().getCells().entrySet()) {
                totalAmount = totalAmount.add(cellEntry.getValue().getAmount());
                totalCount += cellEntry.getValue().getCount();
                last = cellEntry.getKey();
            }
            entry.getValue().getCells().put(last + "Total", new SalesCell(totalAmount, totalCount));
            finalAmount = finalAmount.add(totalAmount);
            finalCount += totalCount;
        }
        for (Map.Entry<PolicyMeta, SalesRow> entry : map.entrySet()) {
            BigDecimal totalAmount = entry.getValue().getCells().get(last + "Total").getAmount();
            int totalCount = entry.getValue().getCells().get(last + "Total").getCount();
            entry.getValue().setPercentCell(new SalesPercentCell(finalAmount.compareTo(BigDecimal.ZERO) == 0 ? 0 : totalAmount.divide(finalAmount, RoundingMode.HALF_UP).doubleValue(), finalCount == 0 ? 0 : (double) totalCount / finalCount));
        }
    }

    private void fillTotalForFooterRow(SalesRow row) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = 0;
        String last = StringUtils.EMPTY;
        for (Map.Entry<String, SalesCell> cellEntry : row.getCells().entrySet()) {
            totalAmount = totalAmount.add(cellEntry.getValue().getAmount());
            totalCount += cellEntry.getValue().getCount();
            last = cellEntry.getKey();
        }
        row.getCells().put(last + "Total", new SalesCell(totalAmount, totalCount));
    }

    private void fillTableHeader(SalesResponse salesResponse, TreeMap<String, SalesCell> datesHash,
                                 Set<String> notEmptySalesAffiliates, Set<String> notEmptyCommissionAffiliates) {
        SalesRequest salesRequest = salesResponse.getSalesRequest();
        for (String dateHash : datesHash.keySet()) {
            salesResponse.getDailySalesTable().getHeader().add(dateHash);
            salesResponse.getDailyCommissionsTable().getHeader().add(dateHash);
        }
        for (User affiliate : salesRequest.getAffiliates()) {
            String affiliateName = affiliate.getFullName();

            if (notEmptySalesAffiliates.contains(affiliateName)) {
                salesResponse.getSalesPerAffiliateTable().getHeader().add(affiliateName);
            }

            if (notEmptyCommissionAffiliates.contains(affiliateName)){
                salesResponse.getCommissionsPerAffiliateTable().getHeader().add(affiliateName);
            }
        }
        salesResponse.getDailySalesTable().getHeader().add("Total");
        salesResponse.getDailyCommissionsTable().getHeader().add("Total");
        salesResponse.getSalesPerAffiliateTable().getHeader().add("Total");
        salesResponse.getCommissionsPerAffiliateTable().getHeader().add("Total");
        salesResponse.getDailySalesTable().getHeader().add("%");
        salesResponse.getDailyCommissionsTable().getHeader().add("%");
        salesResponse.getSalesPerAffiliateTable().getHeader().add("%");
        salesResponse.getCommissionsPerAffiliateTable().getHeader().add("%");
    }
}
