package com.travelinsurancemaster.model.dto.json.datatable.commission.report;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.List;

/**
 * Created by Chernov Artur on 30.09.15.
 */
public class CommissionReportDataTableJsonResponse extends AbstractDataTableJsonResponse {

    private List<CommissionReportJson> data;

    public List<CommissionReportJson> getData() {
        return data;
    }

    public void setData(List<CommissionReportJson> data) {
        this.data = data;
    }
}
