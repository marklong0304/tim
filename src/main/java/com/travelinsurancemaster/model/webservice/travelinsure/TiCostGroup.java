package com.travelinsurancemaster.model.webservice.travelinsure;

public enum TiCostGroup {
    CostGroup_1_500(1, 500),
    CostGroup_501_1000(501, 1000),
    CostGroup_1001_1500(1001, 1500),
    CostGroup_1501_2000(1501, 2000),
    CostGroup_2001_2500(2001, 2500),
    CostGroup_2501_3000(2501, 3000),
    CostGroup_3001_3500(3001, 3500),
    CostGroup_3501_4000(3501, 4000),
    CostGroup_4001_4500(4001, 4500),
    CostGroup_4501_5000(4501, 5000),
    CostGroup_5001_5500(5001, 5500),
    CostGroup_5501_6000(5501, 6000),
    CostGroup_6001_6500(6001, 6500),
    CostGroup_6501_7000(6501, 7000),
    CostGroup_7001_8000(7001, 8000),
    CostGroup_8001_9000(8001, 9000),
    CostGroup_9001_10000(9001, 10000),
    CostGroup_10001_11000(10001, 11000),
    CostGroup_11001_12000(11001, 12000),
    CostGroup_12001_13000(12001, 13000),
    CostGroup_13001_14000(13001, 14000),
    CostGroup_14001_15000(14001, 15000);

    private Integer costBegin;
    private Integer costEnd;

    TiCostGroup(Integer costBegin, Integer costEnd) {
        this.costBegin = costBegin;
        this.costEnd = costEnd;
    }


    public static TiCostGroup getCostGroup(Integer tripCost) {
        for(TiCostGroup costGroup : values()) {
            if(costGroup.belongsCostGroup(tripCost))
                return costGroup;
        }
        return CostGroup_1_500;
    }

    public boolean belongsCostGroup(Integer cost) {
        return cost >= costBegin && cost <= costEnd;
    }

}
