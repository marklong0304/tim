package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.Category;

import java.io.Serializable;

/**
 * @author Artur Chernov
 */

public class CategoryDTO extends BasicEntityDTO<Category> implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private Category.CategoryType type;
    private Integer filterOrder;

    public CategoryDTO(Category from) {
        super(from);
        this.code = from.getCode();
        this.name = from.getName();
        this.type = from.getType();
        this.filterOrder = from.getFilterOrder();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category.CategoryType getType() {
        return type;
    }

    public void setType(Category.CategoryType type) {
        this.type = type;
    }

    public Integer getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(Integer filterOrder) {
        this.filterOrder = filterOrder;
    }

    @Override
    public Category toEntityObject() {
        return null;
    }

    @Override
    public int compareTo(Object aThat) {
        if (aThat == null) return 0;
        if (this == aThat) return 0;
        final CategoryDTO that = (CategoryDTO) aThat;
        if (that.getFilterOrder() == null || this.getFilterOrder() == null) return 0;
        return Integer.compare(this.getFilterOrder(), that.getFilterOrder());
    }
}
