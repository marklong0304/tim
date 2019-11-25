package com.travelinsurancemaster.model.dto;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by ritchie on 7/6/16.
 */
@MappedSuperclass
public class CategoryValueSuperclass implements Serializable, Comparable {
    private static final long serialVersionUID = -1;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty(message = "Caption is empty!")
    @Column(nullable = false)
    private String caption;

    @NotNull(message = "Value is null!")
    @Column(nullable = false)
    private String value;

    @Column
    private String apiValue;

    @NotNull(message = "Value type is null!")
    @Column
    @Enumerated(EnumType.STRING)
    private ValueType valueType = ValueType.FIX;

    @Column
    private Integer daysAfterInitialDeposit;

    @Column
    private Integer daysAfterFinalPayment;

    @Column
    private Integer minAge;

    @Column
    private Integer maxAge;

   /* @Column(nullable = false)
    private String order;*/

    public CategoryValueSuperclass() {
    }

    public CategoryValueSuperclass(String caption, String value, ValueType valueType) {
        this.caption = caption;
        this.value = value;
        this.valueType = valueType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getApiValue() {
        return apiValue;
    }

    public void setApiValue(String apiValue) {
        this.apiValue = apiValue;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public Integer getDaysAfterInitialDeposit() {
        return daysAfterInitialDeposit;
    }

    public void setDaysAfterInitialDeposit(Integer daysAfterInitialDeposit) {
        this.daysAfterInitialDeposit = daysAfterInitialDeposit;
    }

    public Integer getDaysAfterFinalPayment() {
        return daysAfterFinalPayment;
    }

    public void setDaysAfterFinalPayment(Integer daysAfterFinalPayment) {
        this.daysAfterFinalPayment = daysAfterFinalPayment;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public int compareTo(Object aThat) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == aThat) return EQUAL;
        final CategoryValueSuperclass that = (CategoryValueSuperclass) aThat;

        if (this.valueType == that.valueType && this.valueType == ValueType.NAN) {
            return this.value.compareTo(that.value);
        }

        if (this.valueType == ValueType.NAN) {
            return BEFORE;
        }

        if (that.valueType == ValueType.NAN) {
            return AFTER;
        }
        Integer thisInt = this.getIntValue();
        Integer thatInt = that.getIntValue();
        if (thisInt == null || thatInt == null) {
            return thisInt == null ? BEFORE : AFTER;
        }
        return this.getIntValue().compareTo(that.getIntValue());
    }

    public Integer getIntValue() {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Integer getValueByType(ValueType type, BigDecimal pricePerTraveler) {
        if (type == null) {
            type = ValueType.FIX;
        }

        if (pricePerTraveler == null) {
            return null;
        }
        if (this.valueType.equals(ValueType.FIX) && type.equals(ValueType.PERCENT)) {
            if (!pricePerTraveler.equals(BigDecimal.ZERO)) {
                return (int) Math.floor((getIntValue() * 100) / pricePerTraveler.doubleValue());
            } else {
                return getIntValue();
            }
        } else if (this.valueType.equals(ValueType.PERCENT) && type.equals(ValueType.FIX) && getIntValue() != null) {
            return (int) Math.floor(pricePerTraveler.multiply(new BigDecimal(getIntValue() * 0.01)).doubleValue());
        } else {
            return getIntValue();
        }
    }

    public static boolean isEmptyValue(String val) {
        return StringUtils.isBlank(val) || "NO".equals(val) || "0".equals(val) || "NONE".equals(val);
    }

}
