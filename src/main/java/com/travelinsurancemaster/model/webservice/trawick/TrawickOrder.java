package com.travelinsurancemaster.model.webservice.trawick;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ritchie on 1/27/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrawickOrder {
    @JsonProperty("ProductId")
    private String productId;
    @JsonProperty("OrderRequestId")
    private String orderRequestId;
    @JsonProperty("PrimaryMemberId")
    private String primaryMemberId;
    @JsonProperty("OrderStatusMessage")
    private String orderStatusMessage;
    @JsonProperty("OrderStatusCode")
    private String OrderStatusCode;
    @JsonProperty("TotalPrice")
    private String TotalPrice;
    @JsonProperty("QuoteNumber")
    private String QuoteNumber;
    @JsonProperty("BuyNowLink")
    private String BuyNowLink;
    @JsonProperty("OrderNo")
    private String orderNo;

    public String getProductId() {
        return productId;
    }

    public String getOrderRequestId() {
        return orderRequestId;
    }

    public String getPrimaryMemberId() {
        return primaryMemberId;
    }

    public String getOrderStatusMessage() {
        return orderStatusMessage;
    }

    public String getOrderStatusCode() {
        return OrderStatusCode;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public String getQuoteNumber() {
        return QuoteNumber;
    }

    public String getBuyNowLink() {
        return BuyNowLink;
    }

    public String getOrderNo() {
        return orderNo;
    }
}
