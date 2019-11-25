package com.travelinsurancemaster.model.dto.report.sales;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.security.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 01.10.15.
 */
public class SalesRequest implements Serializable {
    private static final long serialVersionUID = -6971516803992879566L;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date from;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date to;
    private ReportInterval interval;
    private List<User> affiliates = new ArrayList<>();
    private List<PolicyMeta> policyMetas = new ArrayList<>();

    public List<PolicyMeta> getPolicyMetas() {
        return policyMetas;
    }

    public void setPolicyMetas(List<PolicyMeta> policyMetas) {
        this.policyMetas = policyMetas;
    }

    public List<User> getAffiliates() {
        return affiliates;
    }

    public void setAffiliates(List<User> affiliates) {
        this.affiliates = affiliates;
    }

    public ReportInterval getInterval() {
        return interval;
    }

    public void setInterval(ReportInterval interval) {
        this.interval = interval;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }
}
