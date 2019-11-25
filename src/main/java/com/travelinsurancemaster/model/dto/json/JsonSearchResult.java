package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.util.JsonUtils;

import java.util.List;

/**
 * Created by Chernov Artur on 28.05.15.
 */
public class JsonSearchResult {
    private List<JsonSearchProduct> products;
    private List<JsonCategory> categories;
    private JsonSearchResultType type = JsonSearchResultType.NAN;
    private String categoryReducedMessage;
    private boolean showCountAfterEnabledFilter;
    private boolean zeroCost = false;
    private boolean depositAndPaymentDates = false;
    private boolean finished = false;
    private String requestId;
    private String quoteRequestJson;
    private PlanType planType;

    public JsonSearchResult(List<JsonSearchProduct> products, List<JsonCategory> categories, JsonSearchResultType type,
                            QuoteRequest quoteRequest, String categoryReducedMessage, boolean showCountAfterEnabledFilter) {
        this.products = products;
        this.categories = categories;
        this.type = type;
        this.categoryReducedMessage = categoryReducedMessage;
        this.showCountAfterEnabledFilter = showCountAfterEnabledFilter;
        this.zeroCost = quoteRequest.isZeroCost();
        this.depositAndPaymentDates = isDepositAndPaymentDates();
        this.quoteRequestJson = JsonUtils.getJsonString(quoteRequest);
        this.planType = quoteRequest.getPlanType();
    }

    public JsonSearchResult(List<JsonSearchProduct> products, List<JsonCategory> categories, JsonSearchResultType type,
                            QuoteRequest quoteRequest, String categoryReducedMessage, boolean showCountAfterEnabledFilter,  boolean finished) {
        this(products, categories, type, quoteRequest, categoryReducedMessage, showCountAfterEnabledFilter);
        this.finished = finished;
    }


    public JsonSearchResult(QuoteRequest quoteRequest, String requestId){
        this.depositAndPaymentDates = quoteRequest.isDepositAndPaymentDates();
        this.requestId = requestId;
        this.quoteRequestJson = JsonUtils.getJsonString(quoteRequest);
        this.planType = quoteRequest.getPlanType();
    }


    public List<JsonSearchProduct> getProducts() {
        return products;
    }

    public void setProducts(List<JsonSearchProduct> products) {
        this.products = products;
    }

    public List<JsonCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<JsonCategory> categories) {
        this.categories = categories;
    }

    public JsonSearchResultType getType() {
        return type;
    }

    public void setType(JsonSearchResultType type) {
        this.type = type;
    }

    public String getCategoryReducedMessage() {
        return categoryReducedMessage;
    }

    public boolean isShowCountAfterEnabledFilter() {
        return showCountAfterEnabledFilter;
    }

    public void setShowCountAfterEnabledFilter(boolean showCountAfterEnabledFilter) {
        this.showCountAfterEnabledFilter = showCountAfterEnabledFilter;
    }

    public void setCategoryReducedMessage(String categoryReducedMessage) {
        this.categoryReducedMessage = categoryReducedMessage;
    }

    public boolean isZeroCost() {
        return zeroCost;
    }

    public void setZeroCost(boolean zeroCost) {
        this.zeroCost = zeroCost;
    }

    public boolean isDepositAndPaymentDates() {
        return depositAndPaymentDates;
    }

    public void setDepositAndPaymentDates(boolean depositAndPaymentDates) {
        this.depositAndPaymentDates = depositAndPaymentDates;
    }

    public boolean isFinished()
    {
        return finished;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getQuoteRequestJson() {
        return quoteRequestJson;
    }

    public void setQuoteRequestJson(String quoteRequestJson) {
        this.quoteRequestJson = quoteRequestJson;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public static enum JsonSearchResultType {
        NAN, CHANGE_SLIDER, ORIGINAL, RESET;
    }
}
