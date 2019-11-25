package com.travelinsurancemaster.model.webservice.travelinsure;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


public class TiPlanTablesTest {

    private TiPlanTables planTables = new TiPlanTables();

    @Test
    public void testRubyPlanCost() throws Exception {
        BigDecimal price = planTables.find(55, 2345, TiPlanType.Ruby);
        assertEquals(new BigDecimal(140), price);
    }

    @Test
    public void testDiamondPlanCost() throws Exception {
        BigDecimal price = planTables.find(55, 2345, TiPlanType.Diamond);
        assertEquals(new BigDecimal(160), price);
    }
}