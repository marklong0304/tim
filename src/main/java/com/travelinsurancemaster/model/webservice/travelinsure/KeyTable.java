package com.travelinsurancemaster.model.webservice.travelinsure;

import java.util.Objects;

public class KeyTable {
    private TiAgeGroup ageGroup;
    private TiCostGroup costGroup;

    public KeyTable(TiAgeGroup ageGroup, TiCostGroup costGroup) {
        this.ageGroup = ageGroup;
        this.costGroup = costGroup;
    }

    public TiAgeGroup getAgeGroup() {
        return ageGroup;
    }

    public TiCostGroup getCostGroup() {
        return costGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyTable keyTable = (KeyTable) o;
        return ageGroup == keyTable.ageGroup &&
                costGroup == keyTable.costGroup;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ageGroup, costGroup);
    }
}
