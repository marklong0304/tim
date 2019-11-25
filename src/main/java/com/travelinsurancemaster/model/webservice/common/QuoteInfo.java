package com.travelinsurancemaster.model.webservice.common;

import com.travelinsurancemaster.model.dto.FilteredProducts;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Chernov Artur on 08.05.15.
 */
public class QuoteInfo {

    private final String quoteUuid;

    private QuoteRequest quoteRequest;

    private Product bestPlan;

    private SortOrder sortOrder = SortOrder.LTH;

    private boolean original = false;

    private FilteredProducts baseProducts = null;

    private FilteredProducts productsWithCancellation = null;

    private String originalCategoryReduceMessage;

    public QuoteInfo(String quoteUuid) {
        this.quoteUuid = quoteUuid;
    }

    public String getQuoteUuid() {
        return quoteUuid;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public Product getBestPlan() {
        return bestPlan;
    }

    public void setBestPlan(Product bestPlan) {
        this.bestPlan = bestPlan;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public String getOriginalCategoryReduceMessage() {
        if (isOriginal()) {
            return originalCategoryReduceMessage != null ? originalCategoryReduceMessage : StringUtils.EMPTY;
        } else {
            return StringUtils.EMPTY;
        }
    }

    public void setOriginalCategoryReduceMessage(String originalCategoryReduceMessage) {
        this.originalCategoryReduceMessage = originalCategoryReduceMessage;
    }

    public FilteredProducts getProductsWithCancellation(){
        return productsWithCancellation;
    }

    public boolean hasProductsWithCancellation(){
        return this.productsWithCancellation != null;
    }

    public void setProductsWithCancellation(FilteredProducts productsWithCancellation){
        this.productsWithCancellation = productsWithCancellation;
    }

    public FilteredProducts getBaseProducts(){
        return baseProducts;
    }

    public void setBaseProducts(FilteredProducts baseProducts){
        this.baseProducts = baseProducts;
    }

    public boolean hasBaseProducts(){
        return this.baseProducts != null;
    }



}
