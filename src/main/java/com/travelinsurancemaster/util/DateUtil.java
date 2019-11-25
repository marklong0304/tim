package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.dto.report.sales.ReportInterval;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Chernov Artur on 13.05.15.
 */
public final class DateUtil {

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";
    public static final String DEFAULT_PLAIN_DATE_FORMAT = "MMddyyyy";

    private static final SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter localeDateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter plainLocaleDateFormatter = DateTimeFormatter.ofPattern(DEFAULT_PLAIN_DATE_FORMAT);

    public static LocalDate getLocalDateNow(Long timezoneOffset) {
        LocalDateTime now = LocalDateTime.now();
        if(timezoneOffset != null) now = now.minusMinutes(timezoneOffset);
        return now.toLocalDate();
    }

    public static String getDateStr(Date date) {
        if (date != null) {
            return formatter.format(date);
        }
        return "";
    }

    public static String getDateStr(Date date, String format) {
        if (date != null) {
            return new SimpleDateFormat(format).format(date);
        }
        return "";
    }

    public static Date getDate(String dateStr) {
        return getDate(dateStr, DEFAULT_DATE_FORMAT);
    }

    public static Date getDate(String dateStr, String format) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return date;
    }

    public static Date incDate(Date date, int days) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.plusDays(days).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate getLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String getLocalDateStr(LocalDate date) {
        return date != null ? localeDateFormatter.format(date) : null;
    }

    public static String getLocalDateStr(LocalDateTime dateTime) {
        return dateTime != null ? localeDateFormatter.format(dateTime) : null;
    }

    public static String getPlainLocalDateStr(LocalDate date) { return date!= null ? plainLocaleDateFormatter.format(date) : null; }

    public static String getLocalDateStr(LocalDate date, String format) {
        String dateStr = "";
        if(date != null) {
            DateTimeFormatter localeDateFormatter = DateTimeFormatter.ofPattern(format);
            dateStr = localeDateFormatter.format(date);
        }
        return dateStr;
    }

    public static LocalDate getLocalDate(String dateStr, DateTimeFormatter localDateFormatter) {
        LocalDate date = null;
        try {
            date = LocalDate.parse(dateStr, localDateFormatter);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return date;
    }

    public static LocalDate getLocalDate(String dateStr, String format) {
        return getLocalDate(dateStr, DateTimeFormatter.ofPattern(format));
    }

    public static LocalDate getLocalDate(String dateStr) {
        return getLocalDate(dateStr, DEFAULT_DATE_FORMAT);
    }

    public static Date fromLocalDate(LocalDate date) {
        return getDate(getLocalDateStr(date));
    }

    public static LocalDate toLocalDate(Date date) {
        return getLocalDate(getDateStr(date));
    }

    public static Date getEndOfDay(String dateTo, String dateFormat) {
        Date date = getDate(dateTo, dateFormat);
        return getEndOfDay(date);
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static List<Date> getDateRange(DateTime start, DateTime end) {
        List<Date> ret = new ArrayList<Date>();
        DateTime tmp = start;
        while (tmp.isBefore(end) || tmp.equals(end)) {
            ret.add(tmp.toDate());
            tmp = tmp.plusDays(1);
        }
        return ret;
    }

    private static String getDateHashFormat(ReportInterval interval) {
        String format = "MM/dd/yy";
        if (interval == ReportInterval.MONTHLY) {
            format = "MM/yy";
        }
        if (interval == ReportInterval.ANNUALLY) {
            format = "yyyy";
        }
        return format;
    }

    public static String getDateHash(Date date, ReportInterval interval) {
        if (date != null) {
            return new SimpleDateFormat(getDateHashFormat(interval)).format(date);
        }
        return "";
    }

    public static String getDateHash(LocalDate date, ReportInterval interval) {
        if (date != null) {
            return getLocalDateStr(date, getDateHashFormat(interval));
        }
        return "";
    }

    public static boolean isTheSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    public static boolean isBeforeOrEqualsCurrentDate(Date date) {
        Date currentDate = new Date();
        if (date.before(currentDate) || date.equals(currentDate)) {
            return true;
        }
        return false;
    }

    public static boolean isBeforeOrEqualsCurrentDate(LocalDate date) {
        return date.compareTo(LocalDate.now()) <= 0;
    }

    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
        return getXMLGregorianCalendar(date, false);
    }

    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date, boolean noTimezone) {
        if (date == null) {
            return null;
        }
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            if (noTimezone) {
                cal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            }
            return cal;
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day);
        return calendar.getTime();
    }
}