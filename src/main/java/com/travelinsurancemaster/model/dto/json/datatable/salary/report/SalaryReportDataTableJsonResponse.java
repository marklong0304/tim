package com.travelinsurancemaster.model.dto.json.datatable.salary.report;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.List;

/**
 * Created by Chernov Artur on 10.09.15.
 */
public class SalaryReportDataTableJsonResponse extends AbstractDataTableJsonResponse {

    private List<SalaryReportJson> data;

    public List<SalaryReportJson> getData() {
        return data;
    }

    public void setData(List<SalaryReportJson> data) {
        this.data = data;
    }
}
