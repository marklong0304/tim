package com.travelinsurancemaster.model.util;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Chernov Artur on 21.08.15.
 */
public class BigDecimalRange implements Serializable {
    private static final long serialVersionUID = 3186302055747771741L;

    private BigDecimal from;
    private BigDecimal to;

    public BigDecimalRange() {
    }

    public BigDecimalRange(BigDecimal from, BigDecimal to) {
        this.from = from;
        this.to = to;
    }

    public BigDecimal getFrom() {
        return from;
    }

    public void setFrom(BigDecimal from) {
        this.from = from;
    }

    public BigDecimal getTo() {
        return to;
    }

    public void setTo(BigDecimal to) {
        this.to = to;
    }

    public boolean isEmpty() { return from == null && to == null; }
}