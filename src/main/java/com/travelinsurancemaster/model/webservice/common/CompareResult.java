package com.travelinsurancemaster.model.webservice.common;

import com.travelinsurancemaster.model.dto.json.JsonCategory;
import com.travelinsurancemaster.model.dto.json.JsonGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 18.09.2016.
 */
public class CompareResult {
    private List<Product> products = new ArrayList<>();
    private List<JsonCategory> categories = new ArrayList<>();
    private boolean finished = false;
    private boolean showCountAfterEnabledFilter = false;

    public CompareResult(List<Product> products, List<JsonCategory> categories, boolean finished, boolean showCountAfterEnabledFilter) {
        this.products = products;
        this.categories = categories;
        this.finished = finished;
        this.showCountAfterEnabledFilter = showCountAfterEnabledFilter;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<JsonCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<JsonCategory> categories) {
        this.categories = categories;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isShowCountAfterEnabledFilter() {
        return showCountAfterEnabledFilter;
    }

}
