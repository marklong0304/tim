package com.travelinsurancemaster.model.util.datatable;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 09.09.15.
 */

public class DataTableField implements Serializable {

    private static final long serialVersionUID = -6279345243690541965L;

    private String id;
    private String affiliateId;
    private String salary;
    private String name;
    private String value;

    public DataTableField() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAffiliateId() { return affiliateId; }
    public void setAffiliateId(String affiliateId) { this.affiliateId = affiliateId; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}