package com.travelinsurancemaster;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ritchie on 2/27/15.
 */
public class TestUtils {
    public static Date getIncrementedDate(int daysNum) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, daysNum); // number of days to add
        return calendar.getTime();
    }

    public static LocalDate getIncrementedLocalDate(int daysNum) {
        return LocalDate.now().plusDays(daysNum);
    }

    public static Calendar getIncrementedCalendar(int daysNum) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, daysNum); // number of days to add
        return calendar;
    }

    public static String getFormattedDate(Date date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String getFormattedLocalDate(LocalDate date, String dateFormat) {
        DateTimeFormatter localDateFormat =DateTimeFormatter.ofPattern(dateFormat);
        String dateStr = localDateFormat.format(date);
        return dateStr;
    }
}
