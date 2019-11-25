package com.travelinsurancemaster.model.dto;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by ritchie on 5/13/15.
 */
@Embeddable
public class PercentInfo implements Serializable {

    @Column
    private Integer value;

    @Column
    private Integer valueFrom;

    @Column
    private Integer valueTo;

    @Column
    private String textValue;

    public PercentInfo() {
    }

    public PercentInfo(Integer value) {
        this(value, null, null, null);
    }

    public PercentInfo(Integer value, Integer valueFrom, Integer valueTo, String textValue) {
        this.value = value;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.textValue = textValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(Integer valueFrom) {
        this.valueFrom = valueFrom;
    }

    public Integer getValueTo() {
        return valueTo;
    }

    public void setValueTo(Integer valueTo) {
        this.valueTo = valueTo;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public boolean isValueBetween(Integer value) {
        if (valueFrom == null || valueTo == null || value == null) {
            return false;
        }
        return valueFrom < value && value <= valueTo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PercentInfo)) return false;
        PercentInfo percentInfo = (PercentInfo) o;
        return value == percentInfo.value
                && valueFrom == percentInfo.valueFrom
                && valueTo == percentInfo.valueTo
                && StringUtils.equals(textValue, percentInfo.textValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueFrom, valueTo, textValue);
    }
}
