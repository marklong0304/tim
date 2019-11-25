package com.travelinsurancemaster.model.webservice.tripmate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Vlad on 02.02.2015.
 */
public class TripMateTraveler {

    public Integer number;
    public String prefix;

    public String firstname;
    public String lastname;
    public String middlename;
    public String suffix;
    public Integer age;
    public String dob;
    public String gender;
    public BigDecimal tripcost;
    public Integer triplength;

    public LocalDate departdate;
    public LocalDate returndate;
    public LocalDate depositdate;

    public String state;
    public String citizen;
    public String location_id;
    public String site_id;
    public String premium;
}
