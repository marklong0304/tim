package com.travelinsurancemaster.model.webservice.tripmate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 06.02.2015.
 */
public class TripMatePlanResponse {

    public String code;
    public BigDecimal total_cost;
    public String subtotal;
    public String error;
    public String traveler_count;
    public String processing_fee;
    public List<TripMateTraveler> travelers = new ArrayList<>();
    public String product_name;
    public String policy_link;
    public String domain_id;
    public String domain_name;
    public String quote_link;

}
