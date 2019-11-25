package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.webservice.common.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chernov Artur on 04.08.15.
 */
public class FilteredProducts implements Serializable {
    static final long serialVersionUID = 1L;

    private List<Product> includedProducts = new ArrayList<>();
    private List<Product> excludedProducts = new ArrayList<>();

    public List<Product> getExcludedProducts() {
        return excludedProducts;
    }

    public void setExcludedProducts(List<Product> excludedProducts) {
        this.excludedProducts = excludedProducts;
    }

    public List<Product> getIncludedProducts() {
        return includedProducts;
    }

    public void setIncludedProducts(List<Product> includedProducts) {
        this.includedProducts = includedProducts;
    }

    public static FilteredProducts newInstance(FilteredProducts filteredProducts){
        FilteredProducts newFilteredProducts = new FilteredProducts();
        newFilteredProducts.setAsyncRequestId(filteredProducts.getAsyncRequestId());
        newFilteredProducts.setComplete(filteredProducts.isComplete());
        newFilteredProducts.setIncludedProducts(filteredProducts.getIncludedProducts());
        newFilteredProducts.setExcludedProducts(filteredProducts.getExcludedProducts());
        return newFilteredProducts;
    }

    public synchronized void addIncludedProduct(Product product) {
        if (product == null || product.getTotalPrice() == null || product.getPolicyMeta() == null) {
            return;
        }
        this.includedProducts.add(product);
    }

    public synchronized void addExcludedProduct(Product product) {
        if (product == null || product.getTotalPrice() == null || product.getPolicyMeta() == null) {
            return;
        }
        this.excludedProducts.add(product);
    }

    private UUID asyncRequestId = null;

    public UUID getAsyncRequestId(){
        return asyncRequestId;
    }

    public FilteredProducts setAsyncRequestId(UUID asyncRequestId){
        this.asyncRequestId = asyncRequestId;
        return this;
    }

    private boolean complete = false;

    public boolean isComplete(){
        return complete;
    }

    public void setComplete(boolean complete){
        this.complete = complete;
    }
}
