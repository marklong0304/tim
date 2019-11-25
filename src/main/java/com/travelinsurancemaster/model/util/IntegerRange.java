package com.travelinsurancemaster.model.util;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 21.08.15.
 */
public class IntegerRange implements Serializable {
    private static final long serialVersionUID = 7165806205257537390L;

    private Integer from;
    private Integer to;

    public IntegerRange() {
    }

    public IntegerRange(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public boolean isEmpty() { return from == null && to == null; }
}
