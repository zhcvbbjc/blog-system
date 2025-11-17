package com.blog.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * 日期时间工具类
 */
@Slf4j
public class DateUtil {

    // 常用日期时间格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String READABLE_FORMAT = "MMM dd, yyyy 'at' HH:mm";
    public static final String SHORT_DATE_FORMAT = "MM/dd/yyyy";

    // 格式化器（线程安全）
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern(ISO_FORMAT).withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter READABLE_FORMATTER =
            DateTimeFormatter.ofPattern(READABLE_FORMAT, Locale.ENGLISH);

    /**
     * 获取当前时间（默认时区）
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前UTC时间
     */
    public static LocalDateTime nowUTC() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * 格式化日期为字符串
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }

    /**
     * 格式化日期时间为字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 格式化日期时间为ISO格式
     */
    public static String formatISO(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.atZone(ZoneId.systemDefault()).format(ISO_FORMATTER);
    }

    /**
     * 格式化日期时间为可读格式
     */
    public static String formatReadable(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(READABLE_FORMATTER);
    }

    /**
     * 解析字符串为日期
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("日期解析失败: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 解析字符串为日期时间
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("日期时间解析失败: {}", dateTimeStr, e);
            return null;
        }
    }

    /**
     * 获取相对时间描述（如"2小时前"）
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "未知时间";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else if (weeks < 4) {
            return weeks + "周前";
        } else if (months < 12) {
            return months + "个月前";
        } else {
            return years + "年前";
        }
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return Math.abs(ChronoUnit.DAYS.between(start, end));
    }

    /**
     * 计算年龄
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 检查日期是否在范围内
     */
    public static boolean isDateInRange(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null) return false;

        boolean afterStart = start == null || !date.isBefore(start);
        boolean beforeEnd = end == null || !date.isAfter(end);

        return afterStart && beforeEnd;
    }

    /**
     * 获取当天的开始时间（00:00:00）
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) return null;
        return date.atStartOfDay();
    }

    /**
     * 获取当天的结束时间（23:59:59）
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) return null;
        return date.atTime(23, 59, 59);
    }

    /**
     * 获取本周的开始日期（周一）
     */
    public static LocalDate startOfWeek() {
        return LocalDate.now().with(DayOfWeek.MONDAY);
    }

    /**
     * 获取本周的结束日期（周日）
     */
    public static LocalDate endOfWeek() {
        return LocalDate.now().with(DayOfWeek.SUNDAY);
    }

    /**
     * 获取本月的开始日期
     */
    public static LocalDate startOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    /**
     * 获取本月的结束日期
     */
    public static LocalDate endOfMonth() {
        return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
    }

    /**
     * 添加天数
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) return null;
        return dateTime.plusDays(days);
    }

    /**
     * 减去天数
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) return null;
        return dateTime.minusDays(days);
    }

    /**
     * 转换为时间戳（毫秒）
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) return 0;
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 从时间戳转换为LocalDateTime
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 获取友好的时间范围描述
     */
    public static String getFriendlyDateRange(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return "所有时间";
        } else if (start != null && end != null) {
            if (start.equals(end)) {
                return formatDate(start);
            } else {
                return formatDate(start) + " 至 " + formatDate(end);
            }
        } else if (start != null) {
            return formatDate(start) + " 之后";
        } else {
            return formatDate(end) + " 之前";
        }
    }

    /**
     * 检查是否为今天
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) return false;
        return date.equals(LocalDate.now());
    }

    /**
     * 检查是否为未来日期
     */
    public static boolean isFuture(LocalDate date) {
        if (date == null) return false;
        return date.isAfter(LocalDate.now());
    }

    /**
     * 检查是否为过去日期
     */
    public static boolean isPast(LocalDate date) {
        if (date == null) return false;
        return date.isBefore(LocalDate.now());
    }

    /**
     * 获取月份名称
     */
    public static String getMonthName(int month) {
        if (month < 1 || month > 12) {
            return "未知";
        }

        String[] monthNames = {"一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"};
        return monthNames[month - 1];
    }

    /**
     * 获取星期几名称
     */
    public static String getDayOfWeekName(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return "未知";
        }

        String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return dayNames[dayOfWeek.getValue() - 1];
    }
}