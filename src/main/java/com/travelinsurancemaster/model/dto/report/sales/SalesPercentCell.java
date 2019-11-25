package com.travelinsurancemaster.model.dto.report.sales;

import java.io.Serializable;

/**
 * Created by Chernov Artur on 02.10.15.
 */
public class SalesPercentCell implements Serializable {
    private static final long serialVersionUID = 6268528809766032785L;

    private double amountPercent = 0;
    private double count = 0;

    public SalesPercentCell() {
    }

    public SalesPercentCell(double amountPercent, double count) {
        this.amountPercent = amountPercent;
        this.count = count;
    }

    public double getAmountPercent() {
        return amountPercent;
    }

    public void setAmountPercent(double amountPercent) {
        this.amountPercent = amountPercent;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
