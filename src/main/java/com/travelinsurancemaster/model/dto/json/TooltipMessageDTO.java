package com.travelinsurancemaster.model.dto.json;

import java.io.Serializable;

/**
 * @author Artur Chernov
 */
public class TooltipMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private String upsaleCategoryId;
    private String upsaleCategoryValue;

    public TooltipMessageDTO() {
    }

    public TooltipMessageDTO(String message, String upsaleCategoryId, String upsaleCategoryValue) {
        this.message = message;
        this.upsaleCategoryId = upsaleCategoryId;
        this.upsaleCategoryValue = upsaleCategoryValue;
    }

    public String getMessage() {
        return message;
    }

    public void addMessage(String message){
        this.message = this.message.concat("; ").concat(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUpsaleCategoryId() {
        return upsaleCategoryId;
    }

    public void setUpsaleCategoryId(String upsaleCategoryId) {
        this.upsaleCategoryId = upsaleCategoryId;
    }

    public String getUpsaleCategoryValue() {
        return upsaleCategoryValue;
    }

    public void setUpsaleCategoryValue(String upsaleCategoryValue) {
        this.upsaleCategoryValue = upsaleCategoryValue;
    }
}
