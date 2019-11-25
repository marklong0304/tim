package com.travelinsurancemaster.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * @author Alexander.Isaenco
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryValue extends CategoryValueSuperclass {

    @JsonIgnore
    @ManyToOne(optional = false)
    private Category category;

    public CategoryValue() {
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
