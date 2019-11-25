package com.travelinsurancemaster.model.dto.json.datatable;

/**
 * Created by Chernov Artur on 20.08.15.
 */
public class Order {
    private Integer column;
    private String dir;

    public Order() {
    }

    public Order(Integer column, String dir) {
        this.column = column;
        this.dir = dir;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
