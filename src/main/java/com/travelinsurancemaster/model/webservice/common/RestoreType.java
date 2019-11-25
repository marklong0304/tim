package com.travelinsurancemaster.model.webservice.common;

public enum RestoreType {
    NAN(0),
    STORAGE(1),
    ORIGINAL(2);

    private int id;

    RestoreType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}