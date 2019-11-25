package com.travelinsurancemaster.model.webservice.tripmate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 06.02.2015.
 */
public class TripMatePlan {
    public String planName;
    public List<TripMatePlanResponse> plansByCode = new ArrayList<>();
}
