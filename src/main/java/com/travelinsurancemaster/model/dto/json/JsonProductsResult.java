package com.travelinsurancemaster.model.dto.json;

import java.util.List;

/**
 *
 */
public class JsonProductsResult{

    private List<JsonSearchProduct> products;
    private boolean finished = false;
    private String requestId;

    public JsonProductsResult(List<JsonSearchProduct> products, boolean finished, String requestId){
        this.products = products;
        this.finished = finished;
        this.requestId = requestId;
    }


    public List<JsonSearchProduct> getProducts() {
        return products;
    }

    public void setProducts(List<JsonSearchProduct> products) {
        this.products = products;
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

}
