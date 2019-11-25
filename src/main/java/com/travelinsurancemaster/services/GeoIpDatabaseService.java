package com.travelinsurancemaster.services;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import com.travelinsurancemaster.util.CountryCodesUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by ritchie on 7/13/15.
 */
@Service
public class GeoIpDatabaseService {

    private static final Logger log = LoggerFactory.getLogger(GeoIpDatabaseService.class);

    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    @Value("${geoipdatabase.path}")
    private String geoipDatabasePath;

    public LookupService lookupService;

    /**
     * init LookupService if geoipDatabasePath is not empty in application.properties
     */
    @PostConstruct
    private void initLookupService() {
        if (StringUtils.isBlank(geoipDatabasePath)) {
            log.warn("geoipDatabasePath is blank");
            return;
        }
        File geoIpCityDatabase = new File(geoipDatabasePath);
        try {
            lookupService = new LookupService(geoIpCityDatabase, LookupService.GEOIP_MEMORY_CACHE);
            log.debug("LookupService initialization completed");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void fillUserPurchaseInfoByIP(PurchaseRequest purchaseRequest, HttpServletRequest httpServletRequest) {
        if (lookupService == null) {
            log.warn("lookupService is null");
            return;
        }
        Location location = getLocation(httpServletRequest);
        if (location == null) {
            return;
        }
        CountryCode locCountry = EnumUtils.getEnum(CountryCode.class, location.countryCode);
        StateCode locRegion = EnumUtils.getEnum(StateCode.class, location.region);
        if (Objects.equals(locCountry, purchaseRequest.getQuoteRequest().getResidentCountry())
                && Objects.equals(locRegion, purchaseRequest.getQuoteRequest().getResidentState())) {
            purchaseRequest.setCity(location.city);
            purchaseRequest.setPostalCode(location.postalCode);
            purchaseRequest.getCreditCard().setCcCity(location.city);
            purchaseRequest.getCreditCard().setCcZipCode(location.postalCode);
            purchaseRequest.getCreditCard().setCcCountry(locCountry);
            purchaseRequest.getCreditCard().setCcStateCode(locRegion);
            purchaseRequest.setCreditCardCountryStatePair(CountryCodesUtils.getCountryStatePair(purchaseRequest.getCreditCard().getCcCountry(), purchaseRequest.getCreditCard().getCcStateCode()));
        }
    }

    public boolean fillUserQuoteInfoByIP(Step2QuoteRequestForm quoteRequestForm, HttpServletRequest httpServletRequest) {
        if (lookupService == null) {
            log.warn("lookupService is null");
            return false;
        }
        Location location = getLocation(httpServletRequest);
        if (location == null) {
            return false;
        }
        CountryCode locCountry = EnumUtils.getEnum(CountryCode.class, location.countryCode);
        StateCode locRegion = EnumUtils.getEnum(StateCode.class, location.region);
        quoteRequestForm.setResidentCountry(locCountry);
        quoteRequestForm.setResidentState(locRegion);
        quoteRequestForm.setResidentCountryStatePair(CountryCodesUtils.getCountryStatePair(quoteRequestForm.getResidentCountry(), quoteRequestForm.getResidentState()));
        return true;
    }

    public Location getLocation(HttpServletRequest httpServletRequest) {
        if (lookupService == null) {
            log.warn("lookupService is null");
            return null;
        }
        if (httpServletRequest == null) {
            log.warn("httpServletRequest is null");
            return null;
        }
        // if user is behind a proxy server or access your web server through a load balancer (for example, in cloud hosting)
        String ipAddress = httpServletRequest.getHeader(X_FORWARDED_FOR);
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = httpServletRequest.getRemoteAddr();
        } else {
            // fixed using proxy format (X-Forwarded-For: client, proxy1, proxy2)
            String[] allAddresses = ipAddress.trim().split("\\s*,\\s*");
            if (ArrayUtils.isNotEmpty(allAddresses)) {
                ipAddress = allAddresses[0];
            }
        }
        log.debug("Finding locaiton by ip {}", ipAddress);
        Location location = lookupService.getLocation(ipAddress);
        if (location == null) {
            log.debug("Detected by GeoIP: Location is null");
        } else {
            log.debug("Detected by GeoIP: countryName={}, countryCode={}, region={}, city={}, postalCode={}", location.countryName, location.countryCode, location.region, location.city, location.postalCode);
        }
        return location;
    }
}
