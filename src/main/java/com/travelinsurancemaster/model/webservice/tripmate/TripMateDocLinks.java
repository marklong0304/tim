package com.travelinsurancemaster.model.webservice.tripmate;

import java.util.Map;

/**
 * Created by Vlad on 03.02.2015.
 */
public class TripMateDocLinks {

    public String confirmation_link;
    public Map<String, String> policies;

    public TripMateDocLinks(String confirmation_link) {
        this.confirmation_link = confirmation_link;
    }

    public TripMateDocLinks() {
    }
}
