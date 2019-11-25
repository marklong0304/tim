package com.travelinsurancemaster.services.cms;

import com.maxmind.geoip.Location;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.services.GeoIpDatabaseService;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Chernov Artur on 23.12.2015.
 */

@Service
public class LocationService {

    @Autowired
    private GeoIpDatabaseService geoIpDatabaseService;

    public CountryStatePair getCountryStatePair(HttpServletRequest httpServletRequest) {
        Location location = geoIpDatabaseService.getLocation(httpServletRequest);
        if (location != null) {
            CountryCode locCountry = EnumUtils.getEnum(CountryCode.class, location.countryCode);
            StateCode locRegion = EnumUtils.getEnum(StateCode.class, location.region);
            return new CountryStatePair(locCountry, locRegion);
        }
        return null;
    }

    public static class CountryStatePair {
        CountryCode countryCode;
        StateCode stateCode;

        public CountryStatePair(CountryCode countryCode, StateCode stateCode) {
            this.countryCode = countryCode;
            this.stateCode = stateCode;
        }

        public CountryCode getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(CountryCode countryCode) {
            this.countryCode = countryCode;
        }

        public StateCode getStateCode() {
            return stateCode;
        }

        public void setStateCode(StateCode stateCode) {
            this.stateCode = stateCode;
        }
    }
}
