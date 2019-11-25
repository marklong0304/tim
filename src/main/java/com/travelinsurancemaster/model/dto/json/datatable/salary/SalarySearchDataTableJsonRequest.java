package com.travelinsurancemaster.model.dto.json.datatable.salary;

import com.travelinsurancemaster.model.dto.json.datatable.AbstractDataTableJsonRequest;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;


public class SalarySearchDataTableJsonRequest extends AbstractDataTableJsonRequest {
    private static final long serialVersionUID = 7759986619638810415L;

    private DateRange payDate;
    private BigDecimalRange salary;

    public DateRange getPayDate() {
        return payDate;
    }

    public void setPayDate(DateRange payDate) {
        this.payDate = payDate;
    }

    public BigDecimalRange getSalary() {
        return salary;
    }

    public void setSalary(BigDecimalRange salary) {
        this.salary = salary;
    }
}
