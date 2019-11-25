package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.ValueType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Artur Chernov
 */
public class PolicyMetaCategoryValueDTO implements Comparable, Serializable {
    private static final long serialVersionUID = -1;

    private String value;
    private String caption;
    private ValueType valueType;

    public PolicyMetaCategoryValueDTO(String value, String caption, ValueType valueType) {
        this.value = value;
        this.caption = caption;
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public int compareTo(Object aThat) {
        if (aThat == null) return 0;
        if (this == aThat) return 0;
        final PolicyMetaCategoryValueDTO that = (PolicyMetaCategoryValueDTO) aThat;
        if (that.getValue() == null || this.getValue() == null) return 0;
        if (Objects.equals(valueType, ValueType.NAN)) {
            return StringUtils.compare(this.getValue(), that.getValue());
        }
        return Integer.compare(Integer.valueOf(this.getValue()), Integer.valueOf(that.getValue()));
    }
}
