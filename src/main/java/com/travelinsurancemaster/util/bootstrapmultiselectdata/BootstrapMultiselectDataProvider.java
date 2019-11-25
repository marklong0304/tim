package com.travelinsurancemaster.util.bootstrapmultiselectdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ritchie on 11/24/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BootstrapMultiselectDataProvider {

    private String label;
    private String value;
    private boolean selected;
    private boolean disabled;

    public BootstrapMultiselectDataProvider(String label, String value, boolean selected, boolean disabled) {
        this.label = label;
        this.value = value;
        this.selected = selected;
        this.disabled = disabled;
    }

    public BootstrapMultiselectDataProvider(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
