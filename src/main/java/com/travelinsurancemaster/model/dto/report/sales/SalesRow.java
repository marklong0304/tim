package com.travelinsurancemaster.model.dto.report.sales;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chernov Artur on 02.10.15.
 */
public class SalesRow implements Serializable {
    private static final long serialVersionUID = -941140920623056592L;

    private Map<String, SalesCell> cells = new HashMap<>();
    private SalesPercentCell percentCell;

    public SalesRow() {
    }

    public SalesRow(Map<String, SalesCell> cells, SalesPercentCell percentCell) {
        this.percentCell = percentCell;
        this.cells = cells;
    }

    public Map<String, SalesCell> getCells() {
        return cells;
    }

    public void setCells(Map<String, SalesCell> cells) {
        this.cells = cells;
    }

    public SalesPercentCell getPercentCell() {
        return percentCell;
    }

    public void setPercentCell(SalesPercentCell percentCell) {
        this.percentCell = percentCell;
    }
}
