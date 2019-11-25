package com.travelinsurancemaster.model.webservice.travelinsure;

import java.math.BigDecimal;

public class TiPlanTables {

    private static TiPlanTable rubyTable;
    private static TiPlanTable diamondTable;

    public TiPlanTables() {
        rubyTable = new TiPlanTableRuby();
        diamondTable = new TiPlanTableDiamond();
    }

    public BigDecimal find(Integer age, Integer tripCost, TiPlanType planType) {
        TiPlanTable planTable = getTable(planType);
        TiAgeGroup ageGroup = TiAgeGroup.getAgeGroup(age);
        TiCostGroup costGroup = TiCostGroup.getCostGroup(tripCost);

        return planTable.getPrice(ageGroup, costGroup);
    }

    private TiPlanTable getTable(TiPlanType planType) {
        switch (planType){
            case Ruby:
                return rubyTable;
            case Diamond:
                return diamondTable;
            default:
                throw new IllegalArgumentException("Unsupported plan type");
        }
    }

}
