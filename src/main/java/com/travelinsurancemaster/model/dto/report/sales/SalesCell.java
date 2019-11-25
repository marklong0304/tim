package com.travelinsurancemaster.model.dto.report.sales;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Chernov Artur on 02.10.15.
 */

public class SalesCell implements Serializable {
    private static final long serialVersionUID = 1073194615692513356L;

    private BigDecimal amount = BigDecimal.ZERO;
    private int count = 0;

    public SalesCell() {
    }

    public SalesCell(BigDecimal amount, int count) {
        this.amount = amount;
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void inc(BigDecimal val) {
        ++this.count;
        this.amount = this.amount.add(val);
    }
}
