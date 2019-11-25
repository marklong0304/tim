package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"departureDate", "returnDate", "initialTripDepositDate", "finalPaymentDate", "destinations"})
public class TripTG {
    @XmlElement(name = "DepartureDate", required = true)
    private String departureDate;
    @XmlElement(name = "ReturnDate", required = true)
    private String returnDate;
    @XmlElement(name = "InitialTripDepositDate", required = true)
    private String initialTripDepositDate;
    @XmlElement(name = "FinalPaymentDate", required = true)
    private String finalPaymentDate;
    @XmlElement(name = "Destinations", required = true)
    private DestinationsTG destinations;

    public TripTG() {
    }

    public TripTG(String departureDate, String returnDate, String initialTripDepositDate, String finalPaymentDate, DestinationsTG destinations) {
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.initialTripDepositDate = initialTripDepositDate;
        this.finalPaymentDate = finalPaymentDate;
        this.destinations = destinations;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setInitialTripDepositDate(String initialTripDepositDate) {
        this.initialTripDepositDate = initialTripDepositDate;
    }

    public void setFinalPaymentDate(String finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public DestinationsTG getDestinations() {
        return destinations;
    }

    public void setDestinations(DestinationsTG destinations) {
        this.destinations = destinations;
    }
}
