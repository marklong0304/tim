package com.travelinsurancemaster.model;


public enum PlanType {
    COMPREHENSIVE("Trip Cancellation", 0),
    LIMITED("No Trip Cancellation", 1);

    private String value;
    private int id;

    PlanType(String value, int id) {
        this.value = value;
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        PlanType planType = (PlanType) o;
//        return id == planType.id &&
//                Objects.equals(value, planType.value);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(value, id);
//    }
}
