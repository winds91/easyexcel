package com.alibaba.excel.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Date utils
 *
 * @author Jiaju Zhuang
 **/
public class DateUtils {
    /**
     * Is a cache of dates
     */
    private static final ThreadLocal<Map<Short, Boolean>> DATE_THREAD_LOCAL =
        new ThreadLocal<>();
    /**
     * Is a cache of dates
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_THREAD_LOCAL =
        new ThreadLocal<>();

    /**
     * The following patterns are used in {@link #isADateFormat(Short, String)}
     */
    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
    // add "\u5e74 \u6708 \u65e5" for Chinese/Japanese date format:2017 \u5e74 2 \u6708 7 \u65e5
    private static final Pattern date_ptrn3b =
        Pattern.compile("^[\\[\\]yYmMdDhHsS\\-T/\u5e74\u6708\u65e5,. :\"\\\\]+0*[ampAMP/]*$");
    // elapsed time patterns: [h],[m] and [s]
    private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
    // for format which start with "[DBNum1]" or "[DBNum2]" or "[DBNum3]" could be a Chinese date
    private static final Pattern date_ptrn5 = Pattern.compile("^\\[DBNum(1|2|3)\\]");
    // for format which start with "年" or "月" or "日" or "时" or "分" or "秒" could be a Chinese date
    private static final Pattern date_ptrn6 = Pattern.compile("(年|月|日|时|分|秒)+");

    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_14 = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_16 = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_16_FORWARD_SLASH = "yyyy/MM/dd HH:mm";
    public static final String DATE_FORMAT_17 = "yyyyMMdd HH:mm:ss";
    public static final String DATE_FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_19_FORWARD_SLASH = "yyyy/MM/dd HH:mm:ss";
    private static final String MINUS = "-";

    public static String defaultDateFormat = DATE_FORMAT_19;

    public static String defaultLocalDateFormat = DATE_FORMAT_10;

    private DateUtils() {}

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString, String dateFormat) throws ParseException {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        return getCacheDateFormat(dateFormat).parse(dateString);
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @param local
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String dateString, String dateFormat, Locale local) {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        if (local == null) {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(dateFormat));
        } else {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, local));
        }
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @param local
     * @return
     */
    public static LocalDate parseLocalDate(String dateString, String dateFormat, Locale local) {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        if (local == null) {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat));
        } else {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, local));
        }
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString) throws ParseException {
        return parseDate(dateString, switchDateFormat(dateString));
    }

    /**
     * switch date format
     *
     * @param dateString
     * @return
     */
    public static String switchDateFormat(String dateString) {
        int length = dateString.length();
        switch (length) {
            case 19:
                if (dateString.contains(MINUS)) {
                    return DATE_FORMAT_19;
                } else {
                    return DATE_FORMAT_19_FORWARD_SLASH;
                }
            case 16:
                if (dateString.contains(MINUS)) {
                    return DATE_FORMAT_16;
                } else {
                    return DATE_FORMAT_16_FORWARD_SLASH;
                }
            case 17:
                return DATE_FORMAT_17;
            case 14:
                return DATE_FORMAT_14;
            case 10:
                return DATE_FORMAT_10;
            default:
                throw new IllegalArgumentException("can not find date format for：" + dateString);
        }
    }

    /**
     * Format date
     * <p>
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = defaultDateFormat;
        }
        return getCacheDateFormat(dateFormat).format(date);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDateTime date, String dateFormat, Locale local) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = defaultDateFormat;
        }
        if (local == null) {
            return date.format(DateTimeFormatter.ofPattern(dateFormat));
        } else {
            return date.format(DateTimeFormatter.ofPattern(dateFormat, local));
        }
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDate date, String dateFormat) {
        return format(date, dateFormat, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDate date, String dateFormat, Locale local) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = defaultLocalDateFormat;
        }
        if (local == null) {
            return date.format(DateTimeFormatter.ofPattern(dateFormat));
        } else {
            return date.format(DateTimeFormatter.ofPattern(dateFormat, local));
        }
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(LocalDateTime date, String dateFormat) {
        return format(date, dateFormat, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(BigDecimal date, Boolean use1904windowing, String dateFormat) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = DateUtil.getLocalDateTime(date.doubleValue(),
            BooleanUtils.isTrue(use1904windowing), true);
        return format(localDateTime, dateFormat);
    }

    private static DateFormat getCacheDateFormat(String dateFormat) {
        Map<String, SimpleDateFormat> dateFormatMap = DATE_FORMAT_THREAD_LOCAL.get();
        if (dateFormatMap == null) {
            dateFormatMap = new HashMap<String, SimpleDateFormat>();
            DATE_FORMAT_THREAD_LOCAL.set(dateFormatMap);
        } else {
            SimpleDateFormat dateFormatCached = dateFormatMap.get(dateFormat);
            if (dateFormatCached != null) {
                return dateFormatCached;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        dateFormatMap.put(dateFormat, simpleDateFormat);
        return simpleDateFormat;
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.util.Date.
     *
     * Excel Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static Date getJavaDate(double date, boolean use1904windowing) {
        //To calculate the Date, in the use of `org.apache.poi.ss.usermodel.DateUtil.getJavaDate(double, boolean,
        // java.util.TimeZone, boolean), Date when similar `2023-01-01 00:00:00.500`, returns the`2023-01-01
        // 00:00:01`, but excel in fact shows the `2023-01-01 00:00:00`.
        // `org.apache.poi.ss.usermodel.DateUtil.getLocalDateTime(double, boolean, boolean)` There is no problem.
        return Date.from(getLocalDateTime(date, use1904windowing).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.time.LocalDateTime.
     *
     * Excel Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static LocalDateTime getLocalDateTime(double date, boolean use1904windowing) {
        return DateUtil.getLocalDateTime(date, use1904windowing, true);
    }

    /**
     * Given an Excel date with either 1900 or 1904 date windowing,
     * converts it to a java.time.LocalDate.
     *
     * Excel Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date             The Excel date.
     * @param use1904windowing true if date uses 1904 windowing,
     *                         or false if using 1900 date windowing.
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    public static LocalDate getLocalDate(double date, boolean use1904windowing) {
        LocalDateTime localDateTime = getLocalDateTime(date, use1904windowing);
        return localDateTime == null ? null : localDateTime.toLocalDate();
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormat(Short formatIndex, String formatString) {
        if (formatIndex == null) {
            return false;
        }
        Map<Short, Boolean> isDateCache = DATE_THREAD_LOCAL.get();
        if (isDateCache == null) {
            isDateCache = MapUtils.newHashMap();
            DATE_THREAD_LOCAL.set(isDateCache);
        } else {
            Boolean isDatecachedDataList = isDateCache.get(formatIndex);
            if (isDatecachedDataList != null) {
                return isDatecachedDataList;
            }
        }
        boolean isDate = isADateFormatUncached(formatIndex, formatString);
        isDateCache.put(formatIndex, isDate);
        return isDate;
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormatUncached(Short formatIndex, String formatString) {
        // First up, is this an internal date format?
        if (isInternalDateFormat(formatIndex)) {
            return true;
        }
        if (StringUtils.isEmpty(formatString)) {
            return false;
        }
        String fs = formatString;
        final int length = fs.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = fs.charAt(i);
            if (i < length - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case '-':
                        case ',':
                        case '.':
                        case ' ':
                        case '\\':
                            // skip current '\' and continue to the next char
                            continue;
                    }
                } else if (c == ';' && nc == '@') {
                    i++;
                    // skip ";@" duplets
                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();

        // short-circuit if it indicates elapsed time: [h], [m] or [s]
        if (date_ptrn4.matcher(fs).matches()) {
            return true;
        }
        // If it starts with [DBNum1] or [DBNum2] or [DBNum3]
        // then it could be a Chinese date
        fs = date_ptrn5.matcher(fs).replaceAll("");
        // If it starts with [$-...], then could be a date, but
        // who knows what that starting bit is all about
        fs = date_ptrn1.matcher(fs).replaceAll("");
        // If it starts with something like [Black] or [Yellow],
        // then it could be a date
        fs = date_ptrn2.matcher(fs).replaceAll("");
        // You're allowed something like dd/mm/yy;[red]dd/mm/yy
        // which would place dates before 1900/1904 in red
        // For now, only consider the first one
        final int separatorIndex = fs.indexOf(';');
        if (0 < separatorIndex && separatorIndex < fs.length() - 1) {
            fs = fs.substring(0, separatorIndex);
        }

        // Ensure it has some date letters in it
        // (Avoids false positives on the rest of pattern 3)
        if (!date_ptrn3a.matcher(fs).find()) {
            return false;
        }

        // If we get here, check it's only made up, in any case, of:
        // y m d h s - \ / , . : [ ] T
        // optionally followed by AM/PM
        boolean result = date_ptrn3b.matcher(fs).matches();
        if (result) {
            return true;
        }
        result = date_ptrn6.matcher(fs).find();
        return result;
    }

    /**
     * Given a format ID this will check whether the format represents an internal excel date format or not.
     *
     * @see #isADateFormat(Short, String)
     */
    public static boolean isInternalDateFormat(short format) {
        switch (format) {
            // Internal Date Formats as described on page 427 in
            // Microsoft Excel Dev's Kit...
            // 14-22
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            // 45-47
            case 0x2d:
            case 0x2e:
            case 0x2f:
                return true;
        }
        return false;
    }

    public static void removeThreadLocalCache() {
        DATE_THREAD_LOCAL.remove();
        DATE_FORMAT_THREAD_LOCAL.remove();
    }
}
