package com.travelinsurancemaster.model.webservice.travelinsure;

import java.math.BigDecimal;
import java.util.HashMap;

public class TiPlanTableRuby implements TiPlanTable {

    private HashMap<KeyTable, Integer> table;

    public TiPlanTableRuby() {
        createMapping();
    }

    @Override
    public BigDecimal getPrice(TiAgeGroup ageGroup, TiCostGroup costGroup) {
        KeyTable key = new KeyTable(ageGroup, costGroup);
        return new BigDecimal(table.get(key));
    }

    private void createMapping() {
        table = new HashMap<>();
        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_1_500), 26);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_1_500), 46);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_1_500), 59);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_1_500), 71);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_1_500), 93);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_1_500), 111);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_1_500), 126);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_501_1000), 42);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_501_1000), 69);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_501_1000), 89);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_501_1000), 107);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_501_1000), 139);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_501_1000), 172);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_501_1000), 186);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_1001_1500), 55);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_1001_1500), 81);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_1001_1500), 119);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_1001_1500), 142);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_1001_1500), 185);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_1001_1500), 230);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_1001_1500), 246);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_1501_2000), 75);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_1501_2000), 116);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_1501_2000), 149);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_1501_2000), 203);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_1501_2000), 235);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_1501_2000), 297);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_1501_2000), 306);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_2001_2500), 96);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_2001_2500), 140);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_2001_2500), 180);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_2001_2500), 272);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_2001_2500), 327);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_2001_2500), 364);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_2001_2500), 366);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_2501_3000), 117);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_2501_3000), 162);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_2501_3000), 210);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_2501_3000), 354);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_2501_3000), 407);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_2501_3000), 429);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_2501_3000), 425);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_3001_3500), 137);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_3001_3500), 186);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_3001_3500), 240);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_3001_3500), 397);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_3001_3500), 457);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_3001_3500), 500);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_3001_3500), 546);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_3501_4000), 156);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_3501_4000), 190);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_3501_4000), 270);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_3501_4000), 440);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_3501_4000), 484);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_3501_4000), 619);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_3501_4000), 616);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_4001_4500), 174);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_4001_4500), 204);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_4001_4500), 343);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_4001_4500), 475);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_4001_4500), 523);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_4001_4500), 657);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_4001_4500), 719);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_4501_5000), 195);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_4501_5000), 225);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_4501_5000), 377);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_4501_5000), 508);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_4501_5000), 559);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_4501_5000), 745);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_4501_5000), 793);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_5001_5500), 224);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_5001_5500), 280);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_5001_5500), 412);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_5001_5500), 541);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_5001_5500), 598);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_5001_5500), 878);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_5001_5500), 870);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_5501_6000), 253);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_5501_6000), 304);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_5501_6000), 450);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_5501_6000), 574);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_5501_6000), 639);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_5501_6000), 909);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_5501_6000), 945);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_6001_6500), 276);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_6001_6500), 331);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_6001_6500), 485);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_6001_6500), 612);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_6001_6500), 692);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_6001_6500), 968);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_6001_6500), 1021);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_6501_7000), 299);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_6501_7000), 359);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_6501_7000), 522);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_6501_7000), 660);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_6501_7000), 743);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_6501_7000), 1031);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_6501_7000), 1095);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_7001_8000), 329);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_7001_8000), 398);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_7001_8000), 589);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_7001_8000), 754);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_7001_8000), 939);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_7001_8000), 1254);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_7001_8000), 1327);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_8001_9000), 369);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_8001_9000), 445);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_8001_9000), 659);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_8001_9000), 854);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_8001_9000), 1065);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_8001_9000), 1431);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_8001_9000), 1488);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_9001_10000), 410);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_9001_10000), 458);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_9001_10000), 728);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_9001_10000), 941);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_9001_10000), 1197);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_9001_10000), 1605);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_9001_10000), 1653);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_10001_11000), 471);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_10001_11000), 530);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_10001_11000), 818);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_10001_11000), 1048);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_10001_11000), 1324);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_10001_11000), 1775);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_10001_11000), 1929);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_11001_12000), 532);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_11001_12000), 600);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_11001_12000), 899);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_11001_12000), 1167);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_11001_12000), 1448);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_11001_12000), 1942);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_11001_12000), 2114);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_12001_13000), 593);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_12001_13000), 674);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_12001_13000), 975);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_12001_13000), 1284);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_12001_13000), 1575);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_12001_13000), 2112);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_12001_13000), 2309);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_13001_14000), 657);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_13001_14000), 747);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_13001_14000), 1062);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_13001_14000), 1400);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_13001_14000), 1712);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_13001_14000), 2281);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_13001_14000), 2519);

        table.put(new KeyTable(TiAgeGroup.AgeGroup_0_34, TiCostGroup.CostGroup_14001_15000), 722);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_35_59, TiCostGroup.CostGroup_14001_15000), 822);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_60_69, TiCostGroup.CostGroup_14001_15000), 1151);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_70_74, TiCostGroup.CostGroup_14001_15000), 1516);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_75_79, TiCostGroup.CostGroup_14001_15000), 1883);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_80_84, TiCostGroup.CostGroup_14001_15000), 2451);
        table.put(new KeyTable(TiAgeGroup.AgeGroup_85_200, TiCostGroup.CostGroup_14001_15000), 2727);
    }

}
