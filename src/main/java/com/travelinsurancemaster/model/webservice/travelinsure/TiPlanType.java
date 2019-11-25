package com.travelinsurancemaster.model.webservice.travelinsure;

public enum TiPlanType {
    Ruby(1, "Ruby"),
    Diamond(2, "Diamond");

    private int id;
    private String name;

    TiPlanType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static TiPlanType getByName(String name) {
        if(name.equalsIgnoreCase(Diamond.getName()))
            return Diamond;
        else if(name.equalsIgnoreCase(Ruby.getName()))
            return Ruby;
        return Ruby;
    }

}
