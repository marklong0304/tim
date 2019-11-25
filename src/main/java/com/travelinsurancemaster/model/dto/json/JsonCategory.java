package com.travelinsurancemaster.model.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.ValueType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Chernov Artur on 29.05.15.
 */
public class JsonCategory {
    private long id;
    private String name;
    private String type;
    private String code;
    private boolean disabled = true;
    private boolean checked;
    private Long currentValue;
    private int countAfterEnabled = 0;
    private Set<PolicyMetaCategoryValueDTO> values = new TreeSet<>();

    public JsonCategory(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.type = category.getType().name();
        this.code = category.getCode();
    }

    public JsonCategory(Category category, boolean disabled) {
        this.id = category.getId();
        this.name = category.getName();
        this.type = category.getType().name();
        this.code = category.getCode();
        this.disabled = disabled;
    }

    public void put(Set<PolicyMetaCategoryValueDTO> values) {
        this.values.addAll(values);
    }

    public void put(Set<PolicyMetaCategoryValue> values, ValueType valueType, BigDecimal tripCostPerTraveler) {
        for (PolicyMetaCategoryValue value : values) {
            String formattedValue = String.valueOf(value.getValueByType(valueType, tripCostPerTraveler));
            if (formattedValue != null) {
                this.values.add(new PolicyMetaCategoryValueDTO(formattedValue, value.getCaption(), valueType));
            }
        }
    }

    public void put(PolicyMetaCategoryValue value, ValueType valueType, BigDecimal tripCostPerTraveler) {
        String formattedValue = String.valueOf(value.getValueByType(valueType, tripCostPerTraveler));
        if (formattedValue != null) {
            this.values.add(new PolicyMetaCategoryValueDTO(formattedValue, value.getCaption(), valueType));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Set<PolicyMetaCategoryValueDTO> getValues() {
        return values;
    }

    public void setValues(Set<PolicyMetaCategoryValueDTO> values) {
        this.values = values;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }

    public int getCountAfterEnabled() {
        return countAfterEnabled;
    }

    public void setCountAfterEnabled(int countAfterEnabled) {
        this.countAfterEnabled = countAfterEnabled;
    }

    @JsonIgnore
    public void incCountAfterEnabled() {
        countAfterEnabled++;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Id: " + this.id + ", Name: " + this.name + ", Type: " + this.type + ", Code: " + this.code + ", Disabled: " + this.disabled;
    }
}
