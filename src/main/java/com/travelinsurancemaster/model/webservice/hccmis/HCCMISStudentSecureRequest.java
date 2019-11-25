package com.travelinsurancemaster.model.webservice.hccmis;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ritchie on 3/16/15.
 */
public class HCCMISStudentSecureRequest {
    @JsonProperty("ReferId")
    private String referId;
    @JsonProperty("Culture")
    private String culture;
    @JsonProperty("CoverageArea")
    private String coverageArea;
    @JsonProperty("CoverageBeginDt")
    private String coverageBeginDate;
    @JsonProperty("CoverageEndDt")
    private String coverageEndDate;
    @JsonProperty("DepCoverage")
    private int dependentCoverage;
    @JsonProperty("OnlineFulFill")
    private int onlineFulfillment;
    @JsonProperty("SelectedPaymentType")
    private String selectedPaymentType;
    @JsonProperty("SelectedPlanType")
    private String selectedPlanType;
    @JsonProperty("PrimaryBeneficiary")
    private String primaryBeneficiary;
    @JsonProperty("PrimaryCitizenship")
    private String primaryCitizenship;
    @JsonProperty("PrimaryDob")
    private String primaryBirthDate;
    @JsonProperty("PrimaryEligibleRequirements")
    private int primaryEligibleRequirements;
    @JsonProperty("PrimaryEmailAddr")
    private String primaryEmailAddress;
    @JsonProperty("PrimaryGender")
    private String primaryGender;
    @JsonProperty("PrimaryHomeCountry")
    private String primaryHomeCountry;
    @JsonProperty("PrimaryHostCountry")
    private String primaryHostCountry;
    @JsonProperty("PrimaryMailAddr1")
    private String primaryMailAddress;
    @JsonProperty("PrimaryMailCity")
    private String primaryMailCity;
    @JsonProperty("PrimaryMailCountry")
    private String primaryMailCountry;
    @JsonProperty("PrimaryMailState")
    private String primaryMailState;
    @JsonProperty("PhysicallyLocated")
    private int physicallyLocated;
    @JsonProperty("PrimaryMailZip")
    private String primaryMailZip;
    @JsonProperty("PrimaryNameFirst")
    private String primaryNameFirst;
    @JsonProperty("PrimaryNameLast")
    private String primaryNameLast;
    @JsonProperty("PrimaryNameMiddle")
    private String primaryNameMiddle;
    @JsonProperty("PrimaryPhone")
    private String primaryPhone;
    @JsonProperty("PrimaryStudentScholarStatus")
    private int primaryStudentScholarStatus;
    @JsonProperty("PrimaryUniversity")
    private String primaryUniversityName;
    @JsonProperty("PrimaryUsCitizenOrResident")
    private int primaryUsCitizenOrResident;
    @JsonProperty("PrimaryUsState")
    private String primaryUsState;
    @JsonProperty("PrimaryVisaType")
    private String primaryVisaType;
    @JsonProperty("CreditCard")
    private CreditCard creditCard;

    public String getReferId() {
        return referId;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public String getCoverageArea() {
        return coverageArea;
    }

    public void setCoverageArea(String coverageArea) {
        this.coverageArea = coverageArea;
    }

    public String getCoverageBeginDate() {
        return coverageBeginDate;
    }

    public void setCoverageBeginDate(String coverageBeginDate) {
        this.coverageBeginDate = coverageBeginDate;
    }

    public String getCoverageEndDate() {
        return coverageEndDate;
    }

    public void setCoverageEndDate(String coverageEndDate) {
        this.coverageEndDate = coverageEndDate;
    }

    public int getDependentCoverage() {
        return dependentCoverage;
    }

    public void setDependentCoverage(int dependentCoverage) {
        this.dependentCoverage = dependentCoverage;
    }

    public int getOnlineFulfillment() {
        return onlineFulfillment;
    }

    public void setOnlineFulfillment(int onlineFulfillment) {
        this.onlineFulfillment = onlineFulfillment;
    }

    public String getSelectedPaymentType() {
        return selectedPaymentType;
    }

    public void setSelectedPaymentType(String selectedPaymentType) {
        this.selectedPaymentType = selectedPaymentType;
    }

    public String getSelectedPlanType() {
        return selectedPlanType;
    }

    public void setSelectedPlanType(String selectedPlanType) {
        this.selectedPlanType = selectedPlanType;
    }

    public String getPrimaryBeneficiary() {
        return primaryBeneficiary;
    }

    public void setPrimaryBeneficiary(String primaryBeneficiary) {
        this.primaryBeneficiary = primaryBeneficiary;
    }

    public String getPrimaryCitizenship() {
        return primaryCitizenship;
    }

    public void setPrimaryCitizenship(String primaryCitizenship) {
        this.primaryCitizenship = primaryCitizenship;
    }

    public String getPrimaryBirthDate() {
        return primaryBirthDate;
    }

    public void setPrimaryBirthDate(String primaryBirthDate) {
        this.primaryBirthDate = primaryBirthDate;
    }

    public int getPrimaryEligibleRequirements() {
        return primaryEligibleRequirements;
    }

    public void setPrimaryEligibleRequirements(int primaryEligibleRequirements) {
        this.primaryEligibleRequirements = primaryEligibleRequirements;
    }

    public String getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(String primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public String getPrimaryGender() {
        return primaryGender;
    }

    public void setPrimaryGender(String primaryGender) {
        this.primaryGender = primaryGender;
    }

    public String getPrimaryHomeCountry() {
        return primaryHomeCountry;
    }

    public void setPrimaryHomeCountry(String primaryHomeCountry) {
        this.primaryHomeCountry = primaryHomeCountry;
    }

    public String getPrimaryHostCountry() {
        return primaryHostCountry;
    }

    public void setPrimaryHostCountry(String primaryHostCountry) {
        this.primaryHostCountry = primaryHostCountry;
    }

    public String getPrimaryMailAddress() {
        return primaryMailAddress;
    }

    public void setPrimaryMailAddress(String primaryMailAddress) {
        this.primaryMailAddress = primaryMailAddress;
    }

    public String getPrimaryMailCity() {
        return primaryMailCity;
    }

    public void setPrimaryMailCity(String primaryMailCity) {
        this.primaryMailCity = primaryMailCity;
    }

    public String getPrimaryMailCountry() {
        return primaryMailCountry;
    }

    public void setPrimaryMailCountry(String primaryMailCountry) {
        this.primaryMailCountry = primaryMailCountry;
    }

    public String getPrimaryMailState() {
        return primaryMailState;
    }

    public void setPrimaryMailState(String primaryMailState) {
        this.primaryMailState = primaryMailState;
    }

    public int getPhysicallyLocated() {
        return physicallyLocated;
    }

    public void setPhysicallyLocated(int physicallyLocated) {
        this.physicallyLocated = physicallyLocated;
    }

    public String getPrimaryMailZip() {
        return primaryMailZip;
    }

    public void setPrimaryMailZip(String primaryMailZip) {
        this.primaryMailZip = primaryMailZip;
    }

    public String getPrimaryNameFirst() {
        return primaryNameFirst;
    }

    public void setPrimaryNameFirst(String primaryNameFirst) {
        this.primaryNameFirst = primaryNameFirst;
    }

    public String getPrimaryNameLast() {
        return primaryNameLast;
    }

    public void setPrimaryNameLast(String primaryNameLast) {
        this.primaryNameLast = primaryNameLast;
    }

    public String getPrimaryNameMiddle() {
        return primaryNameMiddle;
    }

    public void setPrimaryNameMiddle(String primaryNameMiddle) {
        this.primaryNameMiddle = primaryNameMiddle;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public int getPrimaryStudentScholarStatus() {
        return primaryStudentScholarStatus;
    }

    public void setPrimaryStudentScholarStatus(int primaryStudentScholarStatus) {
        this.primaryStudentScholarStatus = primaryStudentScholarStatus;
    }

    public String getPrimaryUniversityName() {
        return primaryUniversityName;
    }

    public void setPrimaryUniversityName(String primaryUniversityName) {
        this.primaryUniversityName = primaryUniversityName;
    }

    public int getPrimaryUsCitizenOrResident() {
        return primaryUsCitizenOrResident;
    }

    public void setPrimaryUsCitizenOrResident(int primaryUsCitizenOrResident) {
        this.primaryUsCitizenOrResident = primaryUsCitizenOrResident;
    }

    public String getPrimaryUsState() {
        return primaryUsState;
    }

    public void setPrimaryUsState(String primaryUsState) {
        this.primaryUsState = primaryUsState;
    }

    public String getPrimaryVisaType() {
        return primaryVisaType;
    }

    public void setPrimaryVisaType(String primaryVisaType) {
        this.primaryVisaType = primaryVisaType;
    }

    public class CreditCard {
        @JsonProperty("CardExpirationMonth")
        private String cardExpirationMonth;
        @JsonProperty("CardExpirationYear")
        private String cardExpirationYear;
        @JsonProperty("CardHolderAddress1")
        private String cardHolderAddress;
        @JsonProperty("CardHolderCity")
        private String cardHolderCity;
        @JsonProperty("CardHolderCountry")
        private String cardHolderCountry;
        @JsonProperty("CardHolderName")
        private String cardHolderName;
        @JsonProperty("CardHolderState")
        private String cardHolderState;
        @JsonProperty("CardHolderZip")
        private String cardHolderZip;
        @JsonProperty("CardNumber")
        private long cardNumber;
        @JsonProperty("CardSecurityCode")
        private String cardSecurityCode;

        public String getCardExpirationMonth() {
            return cardExpirationMonth;
        }

        public void setCardExpirationMonth(String cardExpirationMonth) {
            this.cardExpirationMonth = cardExpirationMonth;
        }

        public String getCardExpirationYear() {
            return cardExpirationYear;
        }

        public void setCardExpirationYear(String cardExpirationYear) {
            this.cardExpirationYear = cardExpirationYear;
        }

        public String getCardHolderAddress() {
            return cardHolderAddress;
        }

        public void setCardHolderAddress(String cardHolderAddress) {
            this.cardHolderAddress = cardHolderAddress;
        }

        public String getCardHolderCity() {
            return cardHolderCity;
        }

        public void setCardHolderCity(String cardHolderCity) {
            this.cardHolderCity = cardHolderCity;
        }

        public String getCardHolderCountry() {
            return cardHolderCountry;
        }

        public void setCardHolderCountry(String cardHolderCountry) {
            this.cardHolderCountry = cardHolderCountry;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getCardHolderState() {
            return cardHolderState;
        }

        public void setCardHolderState(String cardHolderState) {
            this.cardHolderState = cardHolderState;
        }

        public String getCardHolderZip() {
            return cardHolderZip;
        }

        public void setCardHolderZip(String cardHolderZip) {
            this.cardHolderZip = cardHolderZip;
        }

        public long getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(long cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardSecurityCode() {
            return cardSecurityCode;
        }

        public void setCardSecurityCode(String cardSecurityCode) {
            this.cardSecurityCode = cardSecurityCode;
        }
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
