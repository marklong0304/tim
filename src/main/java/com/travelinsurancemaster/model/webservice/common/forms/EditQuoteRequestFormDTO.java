package com.travelinsurancemaster.model.webservice.common.forms;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artur Chernov
 */
public class EditQuoteRequestFormDTO implements Serializable {
    static final long serialVersionUID = 1L;

    private String requestId;
    private String policyUniqueCode;
    private Step1QuoteRequestForm step1Form;
    private Step2QuoteRequestForm step2Form;
    private Step3QuoteRequestForm step3Form;
    private List<CountryDTO> countries = new ArrayList<>();
    private StatesDto states;
    private Boolean comparePlans;

    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPolicyUniqueCode() {
        return policyUniqueCode;
    }
    public void setPolicyUniqueCode(String policyUniqueCode) { this.policyUniqueCode = policyUniqueCode; }

    public Step1QuoteRequestForm getStep1Form() {
        return step1Form;
    }
    public void setStep1Form(Step1QuoteRequestForm step1Form) {
        this.step1Form = step1Form;
    }

    public Step2QuoteRequestForm getStep2Form() { return step2Form; }
    public void setStep2Form(Step2QuoteRequestForm step2Form) {
        this.step2Form = step2Form;
    }

    public Step3QuoteRequestForm getStep3Form() {
        return step3Form;
    }
    public void setStep3Form(Step3QuoteRequestForm step3Form) {
        this.step3Form = step3Form;
    }

    public List<CountryDTO> getCountries() {
        return countries;
    }
    public void setCountries(CountryCode[] codes) {
        this.countries = Arrays.stream(codes).map(CountryDTO::new).collect(Collectors.toList());
    }
    public void setCountries(List<CountryDTO> countries) {
        this.countries = countries;
    }

    public StatesDto getStates() {
        return states;
    }
    public void setStates(StatesDto states) {
        this.states = states;
    }
    public void setStates(List<StateCode> statesUS, List<StateCode> statesCa) {
        this.states = new StatesDto(statesUS, statesCa);
    }

    public Boolean getComparePlans() { return comparePlans; }
    public void setComparePlans(Boolean comparePlans) { this.comparePlans = comparePlans; }

    private class CountryDTO implements Serializable {

        static final long serialVersionUID = 1L;

        private CountryCode code;
        private String caption;

        CountryDTO(CountryCode code) {
            this.code = code;
            this.caption = code.getCaption();
        }

        public CountryCode getCode() {
            return code;
        }
        public void setCode(CountryCode code) {
            this.code = code;
        }

        public String getCaption() {
            return caption;
        }
        public void setCaption(String caption) {
            this.caption = caption;
        }
    }

    private class StateDTO implements Serializable {

        static final long serialVersionUID = 1L;

        private StateCode code;
        private String caption;

        StateDTO(StateCode code) {
            this.code = code;
            this.caption = code.getCaption();
        }

        public StateCode getCode() {
            return code;
        }
        public void setCode(StateCode code) {
            this.code = code;
        }

        public String getCaption() {
            return caption;
        }
        public void setCaption(String caption) {
            this.caption = caption;
        }
    }

    private class StatesDto implements Serializable {

        static final long serialVersionUID = 1L;

        private List<StateDTO> usStates;
        private List<StateDTO> caStates;
        private List<CountryCode> excludedCountryCodes = new ArrayList<>();

        StatesDto(List<StateCode> statesUS, List<StateCode> statesCa) {
            this.usStates = statesUS.stream().map(StateDTO::new).collect(Collectors.toList());
            this.caStates = statesCa.stream().map(StateDTO::new).collect(Collectors.toList());
            excludedCountryCodes.add((CountryCode.US));
            excludedCountryCodes.add((CountryCode.CA));
        }

        public List<StateDTO> getUsStates() {
            return usStates;
        }
        public void setUsStates(List<StateDTO> usStates) {
            this.usStates = usStates;
        }

        public List<StateDTO> getCaStates() {
            return caStates;
        }
        public void setCaStates(List<StateDTO> caStates) {
            this.caStates = caStates;
        }

        public List<CountryCode> getExcludedCountryCodes() {
            return excludedCountryCodes;
        }
        public void setExcludedCountryCodes(List<CountryCode> excludedCountryCodes) {
            this.excludedCountryCodes = excludedCountryCodes;
        }
    }
}