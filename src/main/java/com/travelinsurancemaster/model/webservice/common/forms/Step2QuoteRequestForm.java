package com.travelinsurancemaster.model.webservice.common.forms;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 26.04.15.
 */
public class Step2QuoteRequestForm implements Serializable {

    static final long serialVersionUID = 1L;

    private List<GenericTraveler> travelers = new ArrayList<>();

    @NotNull
    private String travelersString;

    @NotNull
    private CountryCode residentCountry;

    private StateCode residentState;

    private String residentCountryStatePair;

    @NotNull
    private CountryCode citizenCountry;

    private Boolean includesUS;

    public List<GenericTraveler> getTravelers() {
        return travelers;
    }
    public void setTravelers(List<GenericTraveler> travelers) {
        this.travelers = travelers;
    }

    public String getTravelersString() {
        return travelersString;
    }
    public void setTravelersString(String travelersString) {
        this.travelersString = travelersString;
    }

    public CountryCode getResidentCountry() {
        return residentCountry;
    }
    public void setResidentCountry(CountryCode residentCountry) {
        this.residentCountry = residentCountry;
    }

    public CountryCode getCitizenCountry() {
        return citizenCountry;
    }
    public void setCitizenCountry(CountryCode citizenCountry) {
        this.citizenCountry = citizenCountry;
    }

    public StateCode getResidentState() {
        return residentState;
    }
    public void setResidentState(StateCode residentState) {
        this.residentState = residentState;
    }

    public String getResidentCountryStatePair() {
        return residentCountryStatePair;
    }
    public void setResidentCountryStatePair(String residentCountryStatePair) {
        this.residentCountryStatePair = residentCountryStatePair;
    }

    public Boolean getIncludesUS() { return includesUS; }
    public void setIncludesUS(Boolean includesUS) { this.includesUS = includesUS; }
}