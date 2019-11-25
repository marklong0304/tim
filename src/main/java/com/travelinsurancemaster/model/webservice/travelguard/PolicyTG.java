package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"product", "travelers", "trip", "payment", "fulfillment", "agency"})
public class PolicyTG {
    @XmlElement(name = "Product", required = true)
    private ProductRequestTG product;
    @XmlElement(name = "Travelers", required = true)
    private TravelersTG travelers;
    @XmlElement(name = "Trip", required = true)
    private TripTG trip;
    @XmlElement(name = "Payment", required = true)
    private PaymentTG payment;
    @XmlElement(name = "Fulfillment", required = true)
    private FulfillmentTG fulfillment;
    @XmlElement(name = "Agency", required = true)
    private AgencyTG agency;

    public ProductRequestTG getProduct() {
        return product;
    }

    public void setProduct(ProductRequestTG product) {
        this.product = product;
    }

    public TravelersTG getTravelers() {
        return travelers;
    }

    public void setTravelers(TravelersTG travelers) {
        this.travelers = travelers;
    }

    public TripTG getTrip() {
        return trip;
    }

    public void setTrip(TripTG trip) {
        this.trip = trip;
    }

    public PaymentTG getPayment() {
        return payment;
    }

    public void setPayment(PaymentTG payment) {
        this.payment = payment;
    }

    public FulfillmentTG getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(FulfillmentTG fulfillment) {
        this.fulfillment = fulfillment;
    }

    public AgencyTG getAgency() {
        return agency;
    }

    public void setAgency(AgencyTG agency) {
        this.agency = agency;
    }
}
