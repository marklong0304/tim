package com.travelinsurancemaster.model.dto.json.datatable;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 21.08.15.
 */
@Data
public abstract class AbstractDataTableJsonRequest implements Serializable {

    private List<Long> affiliates = new ArrayList<>();
    private List<Long> companies = new ArrayList<>();
    private List<Long> users = new ArrayList<>();
    private List<Long> vendors = new ArrayList<>();
    private List<Long> policies = new ArrayList<>();
    private Integer draw;
    private List<Column> columns;
    private List<Order> order;
    private Integer start;
    private Integer length;
    private Search search;
}
