package com.travelinsurancemaster.model.webservice.common;

/**
 * @author Artur Chernov
 */
public enum SortOrder {
    LTH(0),
    HTL(1),
    PROVIDER(2);

    private int id;

    SortOrder(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
