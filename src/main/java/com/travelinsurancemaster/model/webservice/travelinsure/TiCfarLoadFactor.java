package com.travelinsurancemaster.model.webservice.travelinsure;

import java.math.BigDecimal;

public class TiCfarLoadFactor {

    final BigDecimal rubyK = new BigDecimal(1.5);
    final BigDecimal diamondK = new BigDecimal(1.55);

    public TiCfarLoadFactor() {
    }

    public BigDecimal estimate(TiPlanType planType, BigDecimal premium) {
        if(planType.getId() == TiPlanType.Ruby.getId()) {
            return rubyK.multiply(premium);
        }
        if(planType.getId() == TiPlanType.Diamond.getId()) {
            return diamondK.multiply(premium);
        }
        return premium;
    }

}
