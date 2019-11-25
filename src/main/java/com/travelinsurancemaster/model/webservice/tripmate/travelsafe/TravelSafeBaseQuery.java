package com.travelinsurancemaster.model.webservice.tripmate.travelsafe;

import com.travelinsurancemaster.model.webservice.tripmate.TripMateTraveler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 02.02.2015.
 */
public class TravelSafeBaseQuery {

    public String location;
    public String uname;
    public String pwd;
    public String total_travs;
    public LocalDate departdate;
    public LocalDate returndate;
    public String individual_dates;
    public String state;
    public String country;
    public List<TripMateTraveler> travelers = new ArrayList<>();

}
