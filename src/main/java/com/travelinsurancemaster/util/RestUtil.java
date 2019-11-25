package com.travelinsurancemaster.util;

import org.springframework.http.HttpStatus;

/**
 * Created by maleev on 20.05.2016.
 */
public class RestUtil {

    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}
