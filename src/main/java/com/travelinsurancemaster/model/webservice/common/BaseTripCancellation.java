package com.travelinsurancemaster.model.webservice.common;

public enum BaseTripCancellation {
    TRUE(0),
    FALSE(1),
    NAN(2);

    private int id;

    BaseTripCancellation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}