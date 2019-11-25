package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.dto.json.datatable.salary.SalaryJson;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class SalaryJsonComparator implements Comparator<SalaryJson> {

    @Override
    public int compare(SalaryJson sj1, SalaryJson sj2) {

        int compareCompany = StringUtils.compare(sj1.getCompany(), sj2.getCompany());

        int compareName = StringUtils.compare(sj1.getAffiliate(), sj2.getAffiliate());

        return (compareCompany == 0) ? compareName : compareCompany;
    }
}
