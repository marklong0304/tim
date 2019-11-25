package com.travelinsurancemaster.model.webservice.travelinsure;

public enum TiAgeGroup {
    AgeGroup_0_34(0, 34),
    AgeGroup_35_59(35, 59),
    AgeGroup_60_69(60, 69),
    AgeGroup_70_74(70, 74),
    AgeGroup_75_79(75, 79),
    AgeGroup_80_84(80, 84),
    AgeGroup_85_200(85, 200);

    private Integer ageBegin;
    private Integer ageEnd;

    TiAgeGroup(Integer ageBegin, Integer ageEnd) {
        this.ageBegin = ageBegin;
        this.ageEnd = ageEnd;
    }


    public static TiAgeGroup getAgeGroup(Integer age) {
        for(TiAgeGroup ageGroup : values()) {
            if(ageGroup.belongsAgeGroup(age))
                return ageGroup;
        }
        return null;
    }

    public boolean belongsAgeGroup(Integer age) {
        return age >= ageBegin && age <= ageEnd;
    }

}