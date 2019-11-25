package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 23.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FulfillmentLink {

    private String url;
    private String description;
    private int fulfillmentType;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFulfillmentType() { return fulfillmentType; }
    public void setFulfillmentType(int fulfillmentType) { this.fulfillmentType = fulfillmentType; }
}