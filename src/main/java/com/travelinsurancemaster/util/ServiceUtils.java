package com.travelinsurancemaster.util;

import org.springframework.data.domain.Sort;

/**
 * Created by Chernov Artur on 23.07.15.
 */
public class ServiceUtils {
    public static Sort sortByFieldAscIgnoreCase(String fieldName) {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, fieldName).ignoreCase();
        return new Sort(order);
    }
}
