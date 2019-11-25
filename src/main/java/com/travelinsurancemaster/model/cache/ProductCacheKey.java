package com.travelinsurancemaster.model.cache;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Chernov Artur on 31.07.15.
 */
public class ProductCacheKey implements Serializable {
    private static final long serialVersionUID = 8884091974739129269L;
    private final int hashCode;

    private List<Integer> travelersAge = new ArrayList<>();
    private LocalDate departDate;
    private LocalDate returnDate;
    private BigDecimal tripCost;
    private CountryCode residentCountry;
    private StateCode residentState;
    private CountryCode citizenCountry;
    private CountryCode destinationCountry;
    private Boolean includesUS;
    private LocalDate depositDate;
    private LocalDate paymentDate;
    private LocalDate rentalCarStartDate;
    private LocalDate rentalCarEndDate;
    private String policyUniqueCode;
    private PlanType planType;
    private Map<String, String> upsaleValues = new HashMap<>();
    private Map<String, String> categories = new HashMap<>();

    public ProductCacheKey(QuoteRequest request, PolicyMeta policyMeta) {
        if (!CollectionUtils.isEmpty(request.getTravelers())) {
            for (GenericTraveler traveler : request.getTravelers()) {
                this.travelersAge.add(traveler.getAge());
            }
        }
        Collections.sort(this.travelersAge);
        this.departDate = request.getDepartDate();
        this.returnDate = request.getReturnDate();
        this.tripCost = request.getTripCost();
        this.residentCountry = request.getResidentCountry();
        this.residentState = request.getResidentState();
        this.citizenCountry = request.getCitizenCountry();
        this.destinationCountry = request.getDestinationCountry();
        this.includesUS = request.getIncludesUS();
        this.depositDate = request.getDepositDate();
        this.paymentDate = request.getPaymentDate();
        this.rentalCarStartDate = request.getRentalCarStartDate();
        this.rentalCarEndDate = request.getRentalCarEndDate();
        this.upsaleValues = request.getUpsaleValues();
        this.categories = request.getCategories();
        if (policyMeta != null) {
            this.policyUniqueCode = policyMeta.getUniqueCode();
        }
        this.planType = request.getPlanType();

        // build hash code
        HashCodeBuilder hcb = new HashCodeBuilder(17, 31)
                .append(travelersAge).append(departDate).append(returnDate).append(tripCost)
                .append(residentCountry).append(residentState).append(citizenCountry)
                .append(destinationCountry).append(depositDate).append(includesUS)
                .append(paymentDate).append(upsaleValues).append(categories).append(policyUniqueCode).append(planType)
                .append(rentalCarStartDate).append(rentalCarEndDate);
        this.hashCode = hcb.toHashCode();
    }

    public LocalDate getDepartDate() {
        return departDate;
    }
    public void setDepartDate(LocalDate departDate) {
        this.departDate = departDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getTripCost() {
        return tripCost;
    }
    public void setTripCost(BigDecimal tripCost) {
        this.tripCost = tripCost;
    }

    public CountryCode getResidentCountry() {
        return residentCountry;
    }
    public void setResidentCountry(CountryCode residentCountry) {
        this.residentCountry = residentCountry;
    }

    public StateCode getResidentState() {
        return residentState;
    }
    public void setResidentState(StateCode residentState) {
        this.residentState = residentState;
    }

    public CountryCode getCitizenCountry() {
        return citizenCountry;
    }
    public void setCitizenCountry(CountryCode citizenCountry) {
        this.citizenCountry = citizenCountry;
    }

    public CountryCode getDestinationCountry() {
        return destinationCountry;
    }
    public void setDestinationCountry(CountryCode destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public Boolean getIncludesUS() { return includesUS; }
    public void setIncludesUS(Boolean includesUS) { this.includesUS = includesUS; }

    public LocalDate getDepositDate() {
        return depositDate;
    }
    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getRentalCarEndDate() { return rentalCarEndDate; }
    public void setRentalCarEndDate(LocalDate rentalCarEndDate) { this.rentalCarEndDate = rentalCarEndDate; }

    public Map<String, String> getUpsaleValues() {
        return upsaleValues;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public String getPolicyUniqueCode() {
        return policyUniqueCode;
    }
    public void setPolicyUniqueCode(String policyUniqueCode) {
        this.policyUniqueCode = policyUniqueCode;
    }

    public PlanType getPlanType() {
        return planType;
    }
    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProductCacheKey)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        ProductCacheKey rhs = (ProductCacheKey) obj;
        EqualsBuilder eqb = new EqualsBuilder()
                .append(travelersAge, rhs.travelersAge)
                .append(departDate, rhs.departDate)
                .append(returnDate, rhs.returnDate)
                .append(tripCost, rhs.tripCost)
                .append(residentCountry, rhs.residentCountry)
                .append(residentState, rhs.residentState)
                .append(citizenCountry, rhs.citizenCountry)
                .append(destinationCountry, rhs.destinationCountry)
                .append(includesUS, rhs.includesUS)
                .append(depositDate, rhs.depositDate)
                .append(paymentDate, rhs.paymentDate)
                .append(rentalCarStartDate, rhs.rentalCarStartDate)
                .append(rentalCarEndDate, rhs.rentalCarEndDate)
                .append(upsaleValues, rhs.upsaleValues)
                .append(categories, rhs.categories)
                .append(policyUniqueCode, rhs.policyUniqueCode)
                .append(planType, rhs.planType);
        return eqb.isEquals();
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public LocalDate getRentalCarStartDate() {
        return rentalCarStartDate;
    }

    public void setRentalCarStartDate(LocalDate rentalCarStartDate) {
        this.rentalCarStartDate = rentalCarStartDate;
    }
}