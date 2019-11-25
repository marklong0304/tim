package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 05.08.15.
 */
public class UpsaleResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal price;
    private QuoteRequest quoteRequest;
    private String planDescriptionCategoryCaption1;
    private String planDescriptionCategoryCaption2;
    private String planDescriptionCategoryCaption3;
    private String planDescriptionCategoryCaption4;
    private String planDescriptionCategoryCaption5;
    private String planDescriptionCategoryCaption6;
    private List<NotificationDTO> notifications = new ArrayList<>();
    private List<TooltipMessageDTO> tooltips = new ArrayList<>();
    private String error = StringUtils.EMPTY;

    public UpsaleResponseDTO() {
    }

    public UpsaleResponseDTO(BigDecimal price, QuoteRequest quoteRequest) {
        this.price = price;
        this.quoteRequest = quoteRequest;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public String getPlanDescriptionCategoryCaption1() {
        return planDescriptionCategoryCaption1;
    }

    public void setPlanDescriptionCategoryCaption1(String planDescriptionCategoryCaption1) {
        this.planDescriptionCategoryCaption1 = planDescriptionCategoryCaption1;
    }

    public String getPlanDescriptionCategoryCaption2() {
        return planDescriptionCategoryCaption2;
    }

    public void setPlanDescriptionCategoryCaption2(String planDescriptionCategoryCaption2) {
        this.planDescriptionCategoryCaption2 = planDescriptionCategoryCaption2;
    }

    public String getPlanDescriptionCategoryCaption3() {
        return planDescriptionCategoryCaption3;
    }

    public void setPlanDescriptionCategoryCaption3(String planDescriptionCategoryCaption3) {
        this.planDescriptionCategoryCaption3 = planDescriptionCategoryCaption3;
    }

    public String getPlanDescriptionCategoryCaption4() {
        return planDescriptionCategoryCaption4;
    }

    public void setPlanDescriptionCategoryCaption4(String planDescriptionCategoryCaption4) {
        this.planDescriptionCategoryCaption4 = planDescriptionCategoryCaption4;
    }

    public String getPlanDescriptionCategoryCaption5() {
        return planDescriptionCategoryCaption5;
    }

    public void setPlanDescriptionCategoryCaption5(String planDescriptionCategoryCaption5) {
        this.planDescriptionCategoryCaption5 = planDescriptionCategoryCaption5;
    }

    public String getPlanDescriptionCategoryCaption6() {
        return planDescriptionCategoryCaption6;
    }

    public void setPlanDescriptionCategoryCaption6(String planDescriptionCategoryCaption6) {
        this.planDescriptionCategoryCaption6 = planDescriptionCategoryCaption6;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public List<TooltipMessageDTO> getTooltips() {
        return tooltips;
    }

    public void setTooltips(List<TooltipMessageDTO> tooltips) {
        this.tooltips = tooltips;
    }
}
