package com.wll.main.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AUAS on 2015/11/27.
 */
public class TimeUtils {


    /**
     * 计算当前日期是星期几
     *
     * @param pTime 时间格式是：yyyy-MM-dd
     * @return 当前日期对应的星期
     * @throws ParseException
     */
    public static String getWorkOfDate(String pTime, boolean isReplace) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Date tmpDate = format.parse(pTime);
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE",
                Locale.getDefault());
        String work = dateFm.format(tmpDate);
        if (!TextUtils.isEmpty(work)) {
            work = work.replace("星期", "周");
        }
        return work;
    }

    /**
     * 将时间格式化成制定的格式
     *
     * @param formatDate 时间格式是：yyyy-MM-dd
     */
    public static String formatDate(String formatDate, String format)
            throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Date date = dateFormat.parse(formatDate);
        SimpleDateFormat newFormat = new SimpleDateFormat(format,
                Locale.getDefault());
        return newFormat.format(date);
    }

    /**
     * 判断给定的时间是否在当前时间之后。
     *
     * @Title isAfterNow
     * @Description 判断给定的时间是否在当前时间之后。
     *
     * @param time
     * @return 若给定的时间在当前时间之后则返回 true，否则返回 false。
     */
    public static boolean isAfterNow(long time) {
        long now = System.currentTimeMillis();
        return (time > now);
    }

    /**
     * 把 "yyyy-MM-dd HH:mm:ss" 格式的字符串分割成数组，数组的格式为：{"yyyy", "MM-dd", "HH:mm"}。
     *
     * @Title splitFormatTime
     * @Description 把 "yyyy-MM-dd HH:mm:ss" 格式的字符串分割成数组，数组的格式为：{"yyyy", "MM-dd", "HH:mm"}。
     *
     * @param time
     * @return
     */
    public static String[] splitFormatTime(String time) {
        Date date = new Date(formatTime(time));

        final String separator = ";";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + separator + "MM-dd" + separator + "HH:mm",
                Locale.getDefault());
        String format = sdf.format(date);

        return format.split(separator);
    }

    /**
     * 格式化 "yyyy-MM-dd HH:mm:ss" 格式的时间字符串，获取到对应的毫秒值。
     *
     * @Title formatTime
     * @Description 格式化 "yyyy-MM-dd HH:mm:ss" 格式的时间字符串，获取到对应的毫秒值。
     *
     * @param time
     * @return
     */
    public static long formatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return -1L;
        }
        long timeLong = -1L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(time);
            timeLong = date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return timeLong;
    }

    /**
     * 根据给定的格式对时间字符串进行格式化，获取到对应的毫秒值。
     *
     * @Title formatTime
     * @Description 根据给定的格式对时间字符串进行格式化，获取到对应的毫秒值。
     *
     * @param time
     * @param pattern
     * @return
     */
    public static long formatTime(String time, String pattern) {
        if (TextUtils.isEmpty(time)) {
            return -1L;
        }
        long timeLong = -1L;

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            Date date = sdf.parse(time);
            timeLong = date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return timeLong;
    }

    /**
     * 使用 "yyyy-MM-dd HH:mm:ss" 格式对给定的毫秒值进行格式化，返回对应的时间字符串。
     *
     * @Title formatTime
     * @Description 使用 "yyyy-MM-dd HH:mm:ss" 格式对给定的毫秒值进行格式化，返回对应的时间字符串。
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String format = sdf.format(new Date(time));
        return format;
    }

    /**
     * 根据给定的格式对毫秒值进行格式化，返回对应的时间字符串。
     *
     * @Title formatTime
     * @Description 根据给定的格式对毫秒值进行格式化，返回对应的时间字符串。
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String formatTime(long time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        String format = sdf.format(new Date(time));
        return format;
    }

    /**
     * 判断指定的时间是否与当前时间为同一年。
     *
     * @Title isCurrentYear
     * @Description 判断指定的时间是否与当前时间为同一年。
     *
     * @param time
     * @return
     */
    public static boolean isCurrentYear(long time) {
        Calendar now = Calendar.getInstance();
        int yearNow = now.get(Calendar.YEAR);

        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(time);
        int yearTarget = target.get(Calendar.YEAR);

        return (yearNow == yearTarget);
    }

    /**
     * 根据给定的格式格式化昨天的日期。
     *
     * @Title formatYesterday
     * @Description 根据给定的格式格式化昨天的日期。
     *
     * @param pattern
     * @return
     */
    public static String formatYesterday(String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return formatTime(cal.getTimeInMillis(), pattern);
    }

    /**
     * 根据给定的格式格式化今天的日期。
     *
     * @Title formatToday
     * @Description 根据给定的格式格式化今天的日期。
     *
     * @param pattern
     * @return
     */
    public static String formatToday(String pattern) {
        final long nowl = System.currentTimeMillis();
        return formatTime(nowl, pattern);
    }

    /**
     * 根据给定的格式格式化明天的日期。
     *
     * @Title formatTomorrow
     * @Description 根据给定的格式格式化明天的日期。
     *
     * @param pattern
     * @return
     */
    public static String formatTomorrow(String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return formatTime(cal.getTimeInMillis(), pattern);
    }

    /**
     * 根据给定的格式格式化后天的日期。
     *
     * @Title formatDayAfterTomorrow
     * @Description 根据给定的格式格式化后天的日期。
     *
     * @param pattern
     * @return
     */
    public static String formatDayAfterTomorrow(String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        return formatTime(cal.getTimeInMillis(), pattern);
    }

    /**
     * 根据给定的日期差值格式化日期。
     *
     * @param dateDiff
     * @param pattern
     * @return
     */
    public static String formatDateByDiff(int dateDiff, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dateDiff);
        return formatTime(cal.getTimeInMillis(), pattern);
    }

    /**
     * 根据指定的格式获取昨天的时间字符串。
     *
     * @Title getYesterday
     * @Description 根据指定的格式获取昨天的时间字符串。
     * @deprecated 使用 {@link #formatYesterday(String)} 方法替代该方法。
     *
     * @param pattern
     * @return
     */
    public static String getYesterday(String pattern) {
        return formatYesterday(pattern);
    }

    /**
     * 格式化今天的日期。<br>
     *
     * <b>格式为：<i>今天 5月6日</i></b>
     *
     * @Title getDateToday
     * @Description 格式化今天的日期。
     * @deprecated 使用 {@link #formatToday(String)} 方法替代该方法。
     *
     * @return
     */
    public static String getDateToday() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        return "今天 " + month + "月" + day + "日";
    }

    /**
     * 格式化明天的日期。<br>
     *
     * <b>格式为：<i>明天 5月6日</i></b>
     *
     * @Title getDateTomorrow
     * @Description 格式化明天的日期。
     * @deprecated 使用 {@link #formatTomorrow(String)} 方法替代该方法。
     *
     * @return
     */
    public static String getDateTomorrow() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 1);

        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        return "明天 " + month + "月" + day + "日";
    }

    /**
     * 格式化后天的日期。<br>
     *
     * <b>格式为：<i>后天 5月6日</i></b>
     *
     * @Title getDateDayAfterTomorrow
     * @Description 格式化后天的日期。
     * @deprecated 使用 {@link #formatDayAfterTomorrow(String)} 方法替代该方法。
     *
     * @return
     */
    public static String getDateDayAfterTomorrow() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 2);

        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        return "后天 " + month + "月" + day + "日";
    }

    /**
     * 获取给定的时间相对于当前时间的日期。
     *
     * @Title getDateRelativeToday
     * @Description 获取给定的时间相对于当前时间的日期。
     * @deprecated 使用 {@link #getDateRelativeNow(long)} 方法获取时间间隔的天数，替代该方法。
     *
     * @param time
     * @return 返回格式为： "昨天", "今天", "明天", "后天"。
     */
    public static String getDateRelativeToday(long time) {
        long diff = getDateRelativeNow(time);

        if (diff == 0) {
            return "今天";
        }
        else if (diff == 1) {
            return "明天";
        }
        else if (diff == 2) {
            return "后天";
        }
        else if (diff == -1) {
            return "昨天";
        }
        return "";
    }

    /**
     * 获取给定的时间与当前时间相隔的天数。
     *
     * @Title getDateRelativeNow
     * @Description 获取给定的时间与当前时间相隔的天数。
     *
     * @param time
     * @return
     */
    public static int getDateRelativeNow(long time) {
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(time);
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        final long minllisDiff = target.getTimeInMillis() - now.getTimeInMillis();
        return (int) (minllisDiff / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取给定的两个时间之间相隔的天数。
     *
     * @Title getDateDifferences
     * @Description 获取给定的两个时间之间相隔的天数。
     *
     * @param timeOrigin
     *            参照的时间
     * @param timeTarget
     *            比较的时间
     * @return timeTarget 相对于 timeOrigin 间隔的天数
     */
    public static long getDateDifferences(long timeOrigin, long timeTarget) {
        Calendar origin = Calendar.getInstance();
        origin.setTimeInMillis(timeOrigin);

        // 置空 小时、分钟、秒、毫秒，只保留日期。
        origin.set(Calendar.HOUR_OF_DAY, 0);
        origin.set(Calendar.MINUTE, 0);
        origin.set(Calendar.SECOND, 0);
        origin.set(Calendar.MILLISECOND, 0);

        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(timeTarget);

        // 置空 小时、分钟、秒、毫秒，只保留日期。
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        final long minllisDiff = target.getTimeInMillis() - origin.getTimeInMillis();
        return (int) (minllisDiff / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取给定的时间与当前时间相隔的时间间隔。<br>
     *
     * 返回的结果例如：20分钟、1小时、4天等。
     *
     * @Title getTimeDifferences
     * @Description 获取给定的时间与当前时间相隔的时间间隔。
     * @see {@link #getTimeDifferences(long, long, String[])}
     *
     * @param time
     * @return
     */
    public static String getTimeDifferences(long time) {
        return getTimeDifferences(time, Long.MAX_VALUE, new String[] {
                "秒", "分钟", "小时", "天", "M月d日 HH:mm", "yyyy-MM-dd HH:mm" });
    }

    /**
     * 获取给定的时间与当前时间相隔的时间间隔。<br>
     *
     * 返回的结果例如：20分钟、1小时、4天等。
     * 按照给定的格式格式化时间。
     *
     * @Title getTimeDifferences
     * @Description 获取给定的时间与当前时间相隔的时间间隔。
     *
     * @param time
     * @param maxNotDetail
     *            最长多少天之后显示具体的日期。例如：该值为 5 时，间隔 5 天后显示具体的日期，日期格式为 pattern
     * @param patterns
     *            各个时间间隔内格式化时间的格式，依次为：秒、分钟、小时、天、本年、非本年
     * @return
     */
    public static String getTimeDifferences(long time, long maxNotDetail, String[] patterns) {
        final String sencondFormatter = (patterns.length > 0 ? patterns[0] : "s");
        final String minFormatter = (patterns.length > 1 ? patterns[1] : "m");
        final String hourFormatter = (patterns.length > 2 ? patterns[2] : "h");
        final String dayFormatter = (patterns.length > 3 ? patterns[3] : "d");
        final String curYearFormatter = (patterns.length > 4 ? patterns[4] : "M-d HH:mm");
        final String notCurYearFormatter = (patterns.length > 5 ? patterns[5] : "yyyy-M-d HH:mm");

        String result = "";
        long seconds = 0, min = 0, hour = 0, day = 0;
        long dif = System.currentTimeMillis() - time;
        if (dif < 5) {
            result = "刚刚";
        }
        else {
            seconds = dif / 1000;
            if (seconds < 60) {
                result = seconds + sencondFormatter;
            }
            else {
                min = dif / (1000 * 60);
                if (min < 60) {
                    result = min + minFormatter;
                }
                else {
                    hour = min / 60;
                    if (hour < 24) {
                        result = hour + hourFormatter;
                    }
                    else {
                        day = hour / 24;
                        if (day <= maxNotDetail) {
                            result = day + dayFormatter;
                        }
                        else if (isCurrentYear(time)) {
                            result = formatTime(time, curYearFormatter);
                        }
                        else {
                            result = formatTime(time, notCurYearFormatter);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取给定的时间与当前时间相差的周数。<br>
     *
     * <b>最多返回 10 周</b>
     *
     * @Title getWeekDiff
     * @Description 获取给定的时间与当前时间相差的周数。
     *
     * @param time
     * @return
     */
    public static int getWeekDiff(long time) {
        return getWeekDiff(time, 10);
    }

    /**
     * 获取给定的时间与当前时间相差的周数。
     *
     * @Title getWeekDiff
     * @Description 获取给定的时间与当前时间相差的周数。
     *
     * @param time
     * @param maxWeekDiff
     *            可以判断的最大的周数。
     * @return
     */
    public static int getWeekDiff(long time, int maxWeekDiff) {
        final Calendar now = Calendar.getInstance();

        final Calendar target = Calendar.getInstance();
        target.setTimeInMillis(time);

        int diff = 0;
        while (true) {
            if (isCurrentWeek(now, target)) {
                break;
            }
            now.add(Calendar.WEEK_OF_YEAR, 1);

            if (diff >= maxWeekDiff) { // 避免死循环
                break;
            }
            diff++;
        }
        return diff;
    }

    /**
     * 判断两个日期是否为同一周。
     *
     * @Title isCurrentWeek
     * @Description 判断两个日期是否为同一周。
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isCurrentWeek(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);

        return isCurrentWeek(cal1, cal2);
    }

    /*
     * 判断两个日期是否为同一周。
     */
    private static boolean isCurrentWeek(Calendar cal1, Calendar cal2) {
        final int year1 = cal1.get(Calendar.YEAR);
        final int week1 = cal1.get(Calendar.WEEK_OF_YEAR);

        final int year2 = cal2.get(Calendar.YEAR);
        final int week2 = cal2.get(Calendar.WEEK_OF_YEAR);

        return (year1 == year2 && week1 == week2);
    }

    public static boolean isCurrentDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);

        final int year1 = cal1.get(Calendar.YEAR);
        final int day1 = cal1.get(Calendar.DAY_OF_YEAR);

        final int year2 = cal2.get(Calendar.YEAR);
        final int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        return (year1 == year2 && day1 == day2);
    }

    public static String formatTimeByInterval(long time) {
        Calendar now = Calendar.getInstance();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time);

        final int year1 = now.get(Calendar.YEAR);
        final int day1 = now.get(Calendar.DAY_OF_YEAR);

        final int year2 = cal2.get(Calendar.YEAR);
        final int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        if (year1 == year2 && day1 == day2) { // 本日
            return formatTime(time, "HH:mm");
        }
        else if (year1 == year2) { // 本年
            return formatTime(time, "M月d日 HH:mm");
        }
        return formatTime(time, "yyyy-MM-dd HH:mm");
    }

    public static String
    formatTimeByInterval(long time, String patternToday, String patternYear, String patternNotYear) {
        Calendar now = Calendar.getInstance();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time);

        final int year1 = now.get(Calendar.YEAR);
        final int day1 = now.get(Calendar.DAY_OF_YEAR);

        final int year2 = cal2.get(Calendar.YEAR);
        final int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        if (year1 == year2 && day1 == day2) { // 本日
            return formatTime(time, patternToday);
        }
        else if (year1 == year2) { // 本年
            return formatTime(time, patternYear);
        }
        return formatTime(time, patternNotYear);
    }

    /**
     * 获取给定日期的星期。
     *
     * @param time
     * @param dayOfWeeks
     *            每个星期的返回值，排序为：{"星期日"、"星期一"、 ... "星期六"}
     * @return
     */
    public static String getDayOfWeek(long time, String[] dayOfWeeks) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        return getDayOfWeek(cal, dayOfWeeks);
    }

    /**
     * 获取给定日期的星期。
     *
     * @param cal
     * @param dayOfWeeks
     *            每个星期的返回值，排序为：{"星期日"、"星期一"、 ... "星期六"}
     * @return
     */
    public static String getDayOfWeek(Calendar cal, String[] dayOfWeeks) {
        if (dayOfWeeks == null || dayOfWeeks.length < 7) {
            return "";
        }
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return dayOfWeeks[0];

            case Calendar.MONDAY:
                return dayOfWeeks[1];

            case Calendar.TUESDAY:
                return dayOfWeeks[2];

            case Calendar.WEDNESDAY:
                return dayOfWeeks[3];

            case Calendar.THURSDAY:
                return dayOfWeeks[4];

            case Calendar.FRIDAY:
                return dayOfWeeks[5];

            case Calendar.SATURDAY:
            default:
                return dayOfWeeks[6];
        }
    }

}
