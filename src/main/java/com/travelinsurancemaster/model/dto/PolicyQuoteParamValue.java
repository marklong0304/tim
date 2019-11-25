package com.travelinsurancemaster.model.dto;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Alexander.Isaenco
 */
@Embeddable
public class PolicyQuoteParamValue {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuoteParamType type = QuoteParamType.SIMPLE;

    @Column(nullable = false)
    private Long valueFrom;

    @Column(nullable = false)
    private Long valueTo;

    public PolicyQuoteParamValue() {
    }

    public PolicyQuoteParamValue(Long value) {
        this(QuoteParamType.SIMPLE, value, value);
    }

    public PolicyQuoteParamValue(QuoteParamType type, Long valueFrom, Long valueTo) {
        this.type = type;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
    }

    public QuoteParamType getType() {
        return type;
    }

    public void setType(QuoteParamType type) {
        this.type = type;
    }

    public Long getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(Long valueFrom) {
        this.valueFrom = valueFrom;
    }

    public Long getValueTo() {
        return valueTo;
    }

    public void setValueTo(Long valueTo) {
        this.valueTo = valueTo;
    }

    public static enum QuoteParamType {
        NONE, SIMPLE, RANGE;
    }
}
