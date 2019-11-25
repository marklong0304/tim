package com.travelinsurancemaster.model.dto.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 18.09.2016.
 */
public class JsonGroup {
    private String name;
    private List<JsonCompareCategory> categories = new ArrayList<>();

    public JsonGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JsonCompareCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<JsonCompareCategory> categories) {
        this.categories = categories;
    }
}
