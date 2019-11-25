package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuoteFieldDefinition {

    private String fieldCode;
    private String label;
    private boolean isRequired = false;
    private boolean isRateAffecting = false;
    private List<Option> options;

    public String getFieldCode() { return fieldCode; }
    public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public boolean getIsRequired() { return isRequired; }
    public void setIsRequired(boolean isRequired) { this.isRequired = isRequired; }

    public boolean getIsRateAffecting() { return isRateAffecting; }
    public void setIsRateAffecting(boolean isRateAffecting) { this.isRateAffecting = isRateAffecting; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }
}