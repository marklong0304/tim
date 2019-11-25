package com.travelinsurancemaster.model.dto.json.datatable.salary;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.List;

/**
 * Created by Chernov Artur on 10.09.15.
 */
public class SalaryDataTableJsonResponse extends AbstractDataTableJsonResponse {

    private List<SalaryJson> data;

    public List<SalaryJson> getData() {
        return data;
    }

    public void setData(List<SalaryJson> data) {
        this.data = data;
    }
}
