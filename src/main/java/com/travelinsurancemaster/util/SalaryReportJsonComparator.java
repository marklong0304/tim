package com.travelinsurancemaster.util;

import com.amazonaws.util.StringUtils;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportJson;

import java.util.Comparator;

public class SalaryReportJsonComparator implements Comparator<SalaryReportJson> {

    @Override
    public int compare(SalaryReportJson sj1, SalaryReportJson sj2) {

        int compareCompany = StringUtils.compare(sj1.getCompany(), sj2.getCompany());

        int compareName = StringUtils.compare(sj1.getAffiliate(), sj2.getAffiliate());

        return (compareCompany == 0) ? compareName : compareCompany;
    }
}
