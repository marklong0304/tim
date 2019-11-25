package com.travelinsurancemaster.model.webservice.travelinsure;

import java.math.BigDecimal;
import java.util.HashMap;

public class TiPlanTableDiamond implements TiPlanTable {

    private HashMap<KeyTable, Integer> table;

    public TiPlanTableDiamond() {
        createMapping();
    }

    @Override
    public BigDecimal getPrice(TiAgeGroup ageGroup, TiCostGroup costGroup) {
        KeyTable key = new KeyTable(ageGroup, costGroup);
        return new BigDecimal(table.get(key));
    }

    private void createMapping() {
        table = new HashMap<>();

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_1_500), 52);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_1_500), 72);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_1_500), 91);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_1_500), 105);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_1_500), 117);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_1_500), 136);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_1_500), 153);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_501_1000), 64);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_501_1000), 93);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_501_1000), 123);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_501_1000), 143);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_501_1000), 161);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_501_1000), 211);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_501_1000), 220);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_1001_1500), 75);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_1001_1500), 115);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_1001_1500), 156);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_1001_1500), 182);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_1001_1500), 210);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_1001_1500), 329);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_1001_1500), 346);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_1501_2000), 90);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_1501_2000), 137);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_1501_2000), 189);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_1501_2000), 222);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_1501_2000), 283);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_1501_2000), 448);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_1501_2000), 467);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_2001_2500), 116);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_2001_2500), 160);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_2001_2500), 223);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_2001_2500), 272);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_2001_2500), 368);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_2001_2500), 570);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_2001_2500), 592);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_2501_3000), 140);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_2501_3000), 187);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_2501_3000), 257);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_2501_3000), 333);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_2501_3000), 448);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_2501_3000), 692);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_2501_3000), 717);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_3001_3500), 167);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_3001_3500), 235);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_3001_3500), 291);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_3001_3500), 395);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_3001_3500), 545);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_3001_3500), 821);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_3001_3500), 851);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_3501_4000), 189);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_3501_4000), 269);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_3501_4000), 334);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_3501_4000), 458);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_3501_4000), 632);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_3501_4000), 907);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_3501_4000), 925);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_4001_4500), 214);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_4001_4500), 310);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_4001_4500), 378);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_4001_4500), 535);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_4001_4500), 737);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_4001_4500), 1029);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_4001_4500), 1048);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_4501_5000), 238);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_4501_5000), 347);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_4501_5000), 422);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_4501_5000), 599);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_4501_5000), 831);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_4501_5000), 1199);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_4501_5000), 1243);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_5001_5500), 286);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_5001_5500), 402);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_5001_5500), 468);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_5001_5500), 665);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_5001_5500), 890);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_5001_5500), 1297);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_5001_5500), 1360);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_5501_6000), 310);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_5501_6000), 435);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_5501_6000), 527);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_5501_6000), 729);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_5501_6000), 977);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_5501_6000), 1314);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_5501_6000), 1378);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_6001_6500), 338);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_6001_6500), 493);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_6001_6500), 576);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_6001_6500), 819);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_6001_6500), 1059);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_6001_6500), 1458);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_6001_6500), 1532);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_6501_7000), 370);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_6501_7000), 531);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_6501_7000), 623);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_6501_7000), 886);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_6501_7000), 1149);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_6501_7000), 1633);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_6501_7000), 1729);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_7001_8000), 430);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_7001_8000), 595);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_7001_8000), 719);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_7001_8000), 992);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_7001_8000), 1286);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_7001_8000), 1846);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_7001_8000), 1958);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_8001_9000), 502);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_8001_9000), 700);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_8001_9000), 818);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_8001_9000), 1127);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_8001_9000), 1461);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_8001_9000), 1983);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_8001_9000), 2070);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_9001_10000), 582);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_9001_10000), 799);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_9001_10000), 916);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_9001_10000), 1263);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_9001_10000), 1634);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_9001_10000), 2352);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_9001_10000), 2493);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_10001_11000), 649);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_10001_11000), 903);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_10001_11000), 1064);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_10001_11000), 1466);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_10001_11000), 1895);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_10001_11000), 2631);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_10001_11000), 2763);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_11001_12000), 727);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_11001_12000), 1010);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_11001_12000), 1186);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_11001_12000), 1632);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_11001_12000), 2112);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_11001_12000), 2898);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_11001_12000), 3034);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_12001_13000), 803);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_12001_13000), 1115);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_12001_13000), 1309);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_12001_13000), 1800);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_12001_13000), 2327);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_12001_13000), 3165);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_12001_13000), 3303);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_13001_14000), 882);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_13001_14000), 1221);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_13001_14000), 1432);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_13001_14000), 1968);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_13001_14000), 2543);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_13001_14000), 3431);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_13001_14000), 3575);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_14001_15000), 959);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_14001_15000), 1327);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_14001_15000), 1555);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_14001_15000), 2137);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_14001_15000), 2758);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_14001_15000), 3697);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_14001_15000), 3845);
    }

}
