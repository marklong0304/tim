package com.travelinsurancemaster.model.dto.report.sales;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 01.10.15.
 */
public class SalesResponse implements Serializable {
    private static final long serialVersionUID = 2774812166503122282L;

    private SalesRequest salesRequest;

    private SalesTable dailySalesTable = new SalesTable();
    private SalesTable dailyCommissionsTable = new SalesTable();
    private SalesTable salesPerAffiliateTable = new SalesTable();
    private SalesTable commissionsPerAffiliateTable = new SalesTable();

    public SalesRequest getSalesRequest() {
        return salesRequest;
    }

    public void setSalesRequest(SalesRequest salesRequest) {
        this.salesRequest = salesRequest;
    }

    public SalesTable getDailySalesTable() {
        return dailySalesTable;
    }

    public void setDailySalesTable(SalesTable dailySalesTable) {
        this.dailySalesTable = dailySalesTable;
    }

    public SalesTable getDailyCommissionsTable() {
        return dailyCommissionsTable;
    }

    public void setDailyCommissionsTable(SalesTable dailyCommissionsTable) {
        this.dailyCommissionsTable = dailyCommissionsTable;
    }

    public SalesTable getSalesPerAffiliateTable() {
        return salesPerAffiliateTable;
    }

    public void setSalesPerAffiliateTable(SalesTable salesPerAffiliateTable) {
        this.salesPerAffiliateTable = salesPerAffiliateTable;
    }

    public SalesTable getCommissionsPerAffiliateTable() {
        return commissionsPerAffiliateTable;
    }

    public void setCommissionsPerAffiliateTable(SalesTable commissionsPerAffiliateTable) {
        this.commissionsPerAffiliateTable = commissionsPerAffiliateTable;
    }
}
