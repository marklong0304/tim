package com.travelinsurancemaster.model.webservice.travelinsure;

import java.math.BigDecimal;

public interface TiPlanTable {

    BigDecimal getPrice(TiAgeGroup ageGroup, TiCostGroup costGroup);

}
