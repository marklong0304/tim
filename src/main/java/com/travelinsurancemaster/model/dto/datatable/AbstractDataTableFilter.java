package com.travelinsurancemaster.model.dto.datatable;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 21.08.15.
 */
public abstract class AbstractDataTableFilter implements Serializable {
    private String searchKeyword;

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
