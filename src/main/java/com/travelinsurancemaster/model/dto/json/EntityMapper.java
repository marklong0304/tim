package com.travelinsurancemaster.model.dto.json;

/**
 * @author Artur Chernov
 */
public interface EntityMapper<T> {
    T toEntityObject();
}