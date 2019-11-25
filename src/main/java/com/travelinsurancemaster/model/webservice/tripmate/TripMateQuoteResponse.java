package com.travelinsurancemaster.model.webservice.tripmate;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.travelinsurancemaster.services.clients.MHRossClient;
import com.travelinsurancemaster.services.clients.TravelSafeClient;
import com.travelinsurancemaster.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 02.02.2015.
 */
public class TripMateQuoteResponse {

    public List<TripMatePlan> plans = new ArrayList<>();
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    private static final Logger log = LoggerFactory.getLogger(TripMateQuoteResponse.class);

    @JsonAnySetter
    private void setParams(String key, Map<String, Map<String, ?>> value) throws ParseException {
        if (!(key.equals(TravelSafeClient.BASIC_PLAN) || key.equals(TravelSafeClient.CLASSIC_PLAN)
                || key.equals(TravelSafeClient.CLASSIC_PLAN_PLUS) || key.equals(MHRossClient.ASSET)
                || key.equals(MHRossClient.ASSET_PLUS) || key.equals(MHRossClient.BRIDGE)
                || key.equals(MHRossClient.COMPLETE))) {
            return;
        }
        TripMatePlan plan = new TripMatePlan();
        plan.planName = key;
        for (Map.Entry<String, Map<String, ?>> entry : value.entrySet()) {
            TripMatePlanResponse codePlan = new TripMatePlanResponse();
            codePlan.code = entry.getKey();

            Map<String, ?> fields = entry.getValue();
            try {
                BigDecimal totalCost = new BigDecimal(String.valueOf(fields.get("total_cost")));
                totalCost = totalCost.setScale(2, BigDecimal.ROUND_HALF_UP);
                codePlan.total_cost = totalCost;
            } catch (NumberFormatException e) {
                // N/A, error
            }
            codePlan.subtotal = String.valueOf(fields.get("subtotal"));
            codePlan.error = String.valueOf(fields.get("error"));
            codePlan.traveler_count = String.valueOf(fields.get("traveler_count"));
            codePlan.processing_fee = String.valueOf(fields.get("processing_fee"));
            codePlan.product_name = String.valueOf(fields.get("product_name"));
            codePlan.policy_link = String.valueOf(fields.get("policy_link"));
            codePlan.domain_id = String.valueOf(fields.get("domain_id"));
            codePlan.domain_name = String.valueOf(fields.get("domain_name"));
            codePlan.quote_link = String.valueOf(fields.get("quote_link"));

            @SuppressWarnings("unchecked")
            Map<String, String> travelers = (Map<String, String>) fields.get("travelers");
//            codePlan.travelers = travelers;
            for (Map.Entry<String, String> travelersEntry : travelers.entrySet()) {
                TripMateTraveler traveler = new TripMateTraveler();
                traveler.number = Integer.valueOf(travelersEntry.getKey());
                Object age = fields.get("age");
                if (age != null) traveler.age = Integer.valueOf(age.toString());
                traveler.citizen = String.valueOf(fields.get("citizen"));

                traveler.dob = String.valueOf(fields.get("dob"));
                traveler.firstname = String.valueOf(fields.get("firstname"));
                traveler.lastname = String.valueOf(fields.get("lastname"));
                traveler.middlename = String.valueOf(fields.get("middlename"));
                traveler.suffix = String.valueOf(fields.get("suffix"));
                traveler.gender = String.valueOf(fields.get("gender"));
                Object tripcost = fields.get("tripcost");
                if (tripcost != null) traveler.tripcost = new BigDecimal(tripcost.toString());
                Object triplength = fields.get("triplength");
                if (triplength != null) traveler.triplength = Integer.valueOf(triplength.toString());
                traveler.state = String.valueOf(fields.get("state"));
                traveler.location_id = String.valueOf(fields.get("location_id"));
                traveler.site_id = String.valueOf(fields.get("site_id"));
                traveler.premium = String.valueOf(fields.get("premium"));
                traveler.state = String.valueOf(fields.get("state"));
                traveler.state = String.valueOf(fields.get("state"));

                String departdate = (String) fields.get("departdate");
                String returndate = (String) fields.get("returndate");
                String depositdate = (String) fields.get("depositdate");

                traveler.departdate = departdate != null ? DateUtil.getLocalDate(departdate) : null;
                traveler.returndate = returndate != null ? DateUtil.getLocalDate(returndate) : null;
                traveler.depositdate = depositdate != null ? DateUtil.getLocalDate(depositdate) : null;

                codePlan.travelers.add(traveler);
            }

            plan.plansByCode.add(codePlan);
        }

        plans.add(plan);

    }
}