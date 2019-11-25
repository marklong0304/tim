package com.travelinsurancemaster.model.webservice.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.serialiazation.MultiFormatDateDeserializer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteRequest implements Serializable {
    static final long serialVersionUID = 1L;

    private String quoteId;

    @NotEmpty
    private List<GenericTraveler> travelers = new ArrayList<>();

    private Long timezoneOffset;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate departDate;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate returnDate;

    @NotNull
    private BigDecimal tripCost;

    @NotNull
    private CountryCode residentCountry;

    private StateCode residentState;

    @NotNull
    private CountryCode citizenCountry;

    @NotNull
    private CountryCode destinationCountry;

    private Boolean includesUS;

    @Deprecated
    private StateCode destinationState;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate depositDate;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate paymentDate;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate rentalCarStartDate;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private LocalDate rentalCarEndDate;

    @Deprecated
    private boolean tripCancellation = true;

    private boolean tripCostTotal = true;

    private boolean preExistingMedicalAndCancellation = false;

    @Enumerated(EnumType.STRING)
    private BaseTripCancellation baseTripCancellation = BaseTripCancellation.NAN;

    private Map<String, String> categories = new HashMap<>();
    private Map<String, Map<String, BigDecimal>> basePrices = new HashMap<>();
    private Map<String, Map<String, Map<String, Map<String, BigDecimal>>>> categoryPrices = new HashMap<>();
    private Map<String, String> upsaleValues = new HashMap<>();
    private Map<String, String> sliderCategories = new HashMap<>();
    private List<String> enabledPackages = new ArrayList<>();

    private PlanType planType = PlanType.COMPREHENSIVE;

    public static QuoteRequest newInstance(QuoteRequest quoteRequest) {
        return SerializationUtils.clone(quoteRequest);
    }

    public String getQuoteId() { return quoteId; }
    public void setQuoteId(String quoteId) { this.quoteId = quoteId; }

    public String getCategoryValue(String name){ return getCategories().get(name); }

    public Map<String, Map<String, BigDecimal>> getBasePrices() { return basePrices; }
    public void setBasePrices(Map<String, Map<String, BigDecimal>> basePrices) { this.basePrices = basePrices; }

    public Map<String, Map<String, Map<String, Map<String, BigDecimal>>>> getCategoryPrices() { return categoryPrices; }
    public void setCategoryPrices(Map<String, Map<String, Map<String, Map<String, BigDecimal>>>> categoryPrices) { this.categoryPrices = categoryPrices; }

    public String getUpsaleValue(String name) {
        return getUpsaleValues().get(name);
    }
    public String getUpsaleValue(String name, String defaultVal) {
        String upsaleVal = getUpsaleValues().get(name);
        if (upsaleVal == null) {
            upsaleVal = defaultVal;
        }
        return upsaleVal;
    }

    public Map<String, String> getUpsaleValues() {
        return upsaleValues;
    }

    public List<GenericTraveler> getTravelers() {
        return travelers;
    }
    public void setTravelers(List<GenericTraveler> travelers) {
        this.travelers = travelers;
    }

    public Long getTimezoneOffset() {
        return timezoneOffset;
    }
    public void setTimezoneOffset(Long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public String getTripCostText() {
        BigDecimal tripCost = getTripCost();
        DecimalFormat formatter = new DecimalFormat("$#,###");
        return formatter.format(tripCost);
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

    @Deprecated
    public StateCode getDestinationState() {
        return destinationState;
    }
    @Deprecated
    public void setDestinationState(StateCode destinationState) {
        this.destinationState = destinationState;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }
    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }


    public LocalDate getRentalCarStartDate() { return rentalCarStartDate; }
    @JsonIgnore
    public LocalDate getRentalCarStartDateSafe() { return rentalCarStartDate != null ? rentalCarStartDate : departDate; }
    public void setRentalCarStartDate(LocalDate rentalCarStartDate) { this.rentalCarStartDate = rentalCarStartDate; }

    public LocalDate getRentalCarEndDate() { return rentalCarEndDate; }
    @JsonIgnore
    public LocalDate getRentalCarEndDateSafe() { return rentalCarEndDate != null ? rentalCarEndDate : returnDate; }
    public void setRentalCarEndDate(LocalDate rentalCarEndDate) { this.rentalCarEndDate = rentalCarEndDate; }

    @Deprecated
    public boolean isTripCancellation() {
        return tripCancellation;
    }
    @Deprecated
    public void setTripCancellation(boolean tripCancellation) {
        this.tripCancellation = tripCancellation;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Map<String, String> getSliderCategories() {
        return sliderCategories;
    }
    public void setSliderCategories(Map<String, String> sliderCategories) {
        this.sliderCategories = sliderCategories;
    }

    public boolean isTripCostTotal() {
        return tripCostTotal;
    }
    public void setTripCostTotal(boolean tripCostTotal) {
        this.tripCostTotal = tripCostTotal;
    }

    public boolean isPreExistingMedicalAndCancellation() {
        return preExistingMedicalAndCancellation;
    }
    public void setPreExistingMedicalAndCancellation(boolean preExistingMedicalAndCancellation) {
        this.preExistingMedicalAndCancellation = preExistingMedicalAndCancellation;
    }

    public BaseTripCancellation getBaseTripCancellation() {
        return baseTripCancellation;
    }
    public void setBaseTripCancellation(BaseTripCancellation baseTripCancellation) {
        this.baseTripCancellation = baseTripCancellation;
    }

    public List<String> getEnabledPackages() {
        return enabledPackages;
    }

    public Map<String, String> getCleanSliderCategories() {
        Map<String, String> cleanSliderCategories = new HashMap<>();
        for (Map.Entry<String, String> sliderCategoriesEntry : sliderCategories.entrySet()) {
            //убираем из слайдеров все значения 0
            if (!"0".equals(sliderCategoriesEntry.getValue())) {
                cleanSliderCategories.put(sliderCategoriesEntry.getKey(), sliderCategoriesEntry.getValue());
            }
        }
        return cleanSliderCategories;
    }

    public Map<String, String> getUnionCategoriesFromQuoteRequest(boolean isSliderValue) {
        Map<String, String> unionCategories = new HashMap<>();
        unionCategories.putAll(this.getCategories());
        for (Map.Entry<String, String> sliderValue : this.getSliderCategories().entrySet()) {
            //ignore all 0 slider values
            if (!"0".equals(sliderValue.getValue()) && (isSliderValue ||
                    (this.categories.get(sliderValue.getKey()) == null))) {
                unionCategories.put(sliderValue.getKey(), sliderValue.getValue());
            }
        }
        return unionCategories;
    }

    @JsonIgnore
    public boolean isZeroCost() {
        return tripCost.doubleValue() == 0;
    }

    @JsonIgnore
    public boolean isDepositAndPaymentDates() {
        return this.depositDate != null && this.paymentDate != null;
    }

    @JsonIgnore
    public long getTripLength() {
        return DAYS.between(departDate, returnDate) + 1;
    }

    @JsonIgnore
    public long getRentalCarLength() { return DAYS.between(getRentalCarStartDateSafe(), getRentalCarEndDateSafe()) + 1; }

    public static QuoteRequest getClearCategoriesQuoteRequest(QuoteRequest quoteRequest) {
        QuoteRequest clearCategoryQuoteRequest = QuoteRequest.newInstance(quoteRequest);
        clearCategoryQuoteRequest.getCategories().clear();
        clearCategoryQuoteRequest.getUpsaleValues().clear();
        clearCategoryQuoteRequest.getSliderCategories().clear();
        return clearCategoryQuoteRequest;
    }

    public BigDecimal getTripCostPerTraveler() {
        if (CollectionUtils.isNotEmpty(travelers)) {
            return travelers.get(0).getTripCost();
        }
        return tripCost;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public String getTripCostPerTravelerText() {
        BigDecimal tripCostPerTraveler = getTripCostPerTraveler();
        DecimalFormat formatter = new DecimalFormat("$#,###.00");
        return formatter.format(tripCostPerTraveler);
    }

    public PlanType getPlanType() {
        return planType;
    }
    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public boolean isEmpty(){
        // check empty request for validation on cms categories
        return this.getTravelers().isEmpty();
    }

    public boolean hasNoCategroriesOrOnlyTripCancellation() {
        Set<String> keys = categories.keySet();
        int size = keys.size();
        return size == 0 || size == 1 && keys.iterator().next().equals(CategoryCodes.TRIP_CANCELLATION);
    }

    @Override
    public String toString() {
        return "QuoteRequest{" +
                "travelers=" + travelers +
                ", departDate=" + departDate +
                ", returnDate=" + returnDate +
                ", tripCost=" + tripCost +
                ", residentCountry=" + residentCountry +
                ", residentState=" + residentState +
                ", citizenCountry=" + citizenCountry +
                ", destinationCountry=" + destinationCountry +
                ", destinationState=" + destinationState +
                ", includesUS=" + includesUS +
                ", depositDate=" + depositDate +
                ", paymentDate=" + paymentDate +
                ", rentalCarStartDate=" + rentalCarStartDate +
                ", rentalCarEndDate=" + rentalCarEndDate +
                ", tripCancellation=" + tripCancellation +
                ", tripCostTotal=" + tripCostTotal +
                ", preExistingMedicalAndCancellation=" + preExistingMedicalAndCancellation +
                ", baseTripCancellation=" + baseTripCancellation +
                ", categories=" + categories +
                ", upsaleValues=" + upsaleValues +
                ", sliderCategories=" + sliderCategories +
                ", enabledPackages=" + enabledPackages +
                ", planType=" + planType +
                '}';
    }
}