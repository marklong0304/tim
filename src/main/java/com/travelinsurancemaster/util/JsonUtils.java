package com.travelinsurancemaster.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

/**
 * Created by Chernov Artur on 27.05.15.
 */
public final class JsonUtils {

    private static ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public static void setDateFormat(String dateFormat) {
        objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
    }

    public static <T> String writeValueAsString(T obj) {
        String valueAsString = null;
        try {
            valueAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return valueAsString;
    }

    public static <T> String getJsonString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getObject(String json, Class<T> tClass) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, tClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}