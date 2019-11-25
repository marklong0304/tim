package com.travelinsurancemaster.model.dto.json.datatable.salary;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonResponse;

import java.util.List;

public class SalarySearchDataTableJsonResponse extends AbstractDataTableJsonResponse {

    private List<SalarySearchJson> data;

    public List<SalarySearchJson> getData() {
        return data;
    }

    public void setData(List<SalarySearchJson> data) {
        this.data = data;
    }
}
