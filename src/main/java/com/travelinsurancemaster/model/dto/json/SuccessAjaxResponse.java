package com.travelinsurancemaster.model.dto.json;

import java.io.Serializable;

/**
 * @author Artur Chernov
 */
public class SuccessAjaxResponse implements AjaxResponse, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String redirectUrl;
    private boolean equals = false;

    public SuccessAjaxResponse(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public SuccessAjaxResponse(boolean equals) {
        this.equals = equals;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isEquals() {
        return equals;
    }

    public void setEquals(boolean equals) {
        this.equals = equals;
    }
}
