package com.wll.main.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wll on 2015/10/23.
 */
public class StringUtils {
    /**
     * 判断是否是中文
     */
    public static boolean validatorChinese(String string, Context context) {
        if (!TextUtils.isEmpty(string)) {
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]*$");
            Matcher m = p.matcher(string);
            m = p.matcher(string);
            if (!m.matches()) {
                return false;
            }
            return true;
        }
        return false;

    }

    /**
     * 关键字高亮显示
     *
     * @param text 需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;
        if (TextUtils.isEmpty(target)) {
            return spannable.append(text);
        }
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.parseColor("#F4AC02"));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 判断字符串是否都是数值类型(整数类型和浮点数类型)
     */
    public static boolean isNum(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("\\d+(.\\d+)?");
    }

    /** 格式化号码 **/
    public static String formatNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            try {
                if (number.startsWith("86")) {
                    number = number.substring(2);
                }
                if (number.startsWith("+86") || number.startsWith("086")) {
                    number = number.substring(3);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            number = number.replaceAll(" ", "");
            number = number.replaceAll("-", "");
            number = number.replaceAll("_", "");
        } else {
            number = "";
        }
        return number;
    }


    /** 判断号码是否为手机号 **/
    public static boolean isMobileNO(String number) {
        number = formatNumber(number);
        String reg = "^1[3,4,5,7,8][0-9]{9}$";
        if (!TextUtils.isEmpty(number) && number.matches(reg)) {
            return true;
        }
        return false;
    }
    /**
     * 判断输入的是否是号码
     */
    public static boolean isPhoneNumber(String num) {
        if (TextUtils.isEmpty(num) || num.length() < 3 || num.length() > 20) {
            return false;
        }
        num = formatNumber(num);
        if (!TextUtils.isEmpty(num)) {
            if (num.startsWith("1") && num.length() == 11
                    && isMobileNO(num)) {
                return true;
            }
            if (!isOrder(num)) {
                if (!isSame(num)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有顺序
     **/
    private static boolean isOrder(String str) {
        String orderStr = "";
        for (int i = 33; i < 127; i++) {
            orderStr += Character.toChars(i)[0];
        }
        if (!TextUtils.isEmpty(str) && !str.matches("(\\d)+")) {
            return false;
        }
        return orderStr.contains(str);
    }

    /**
     * 判断是否相同
     **/
    private static boolean isSame(String str) {
        String regex = "";
        try {
            if (!TextUtils.isEmpty(str)) {
                regex = str.substring(0, 1) + "{" + str.length() + "}";
                return str.matches(regex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断给定的字符串是否为完整的 URL 地址<b>（形如：<i>http://www.xxx.com</i> 格式）</b>。
     *
     * @Title isFullUrl
     * @Description 判断给定的字符串是否为完整的 URL 地址。
     *
     * @param url
     * @return
     */
    public static boolean isFullUrl(String url) {
        if (url == null) {
            return false;
        }
        final String urlLowerCase = url.toLowerCase(Locale.getDefault());
        if (urlLowerCase.startsWith("http://")) {
            return true;
        }
        if (urlLowerCase.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * 获取给定 URL 的 host。
     *
     * @Title getHostFromUrl
     * @Description 获取给定 URL 的 host。
     *
     * @param url
     * @return
     */
    public static String getHostFromUrl(String url) {
        if (url == null) {
            return "";
        }
        int indexEnd = -1;
        final String urlLowerCase = url.toLowerCase(Locale.getDefault());
        if (urlLowerCase.startsWith("http://")) {
            indexEnd = url.indexOf("/", url.indexOf("http://") + 8);
        }
        if (urlLowerCase.startsWith("https://")) {
            indexEnd = url.indexOf("/", url.indexOf("https://") + 9);
        }
        if (indexEnd == -1) {
            return url;
        }
        String host = url.substring(0, indexEnd);
        return host;
    }
    /**
     * 获取给定的 URL 的路径部分。
     *
     * @Title getPathFromUrl
     * @Description 获取给定的 URL 的路径部分。
     *
     * @param url
     * @return
     */
    public static String getPathFromUrl(String url) {
        if (url == null) {
            return "";
        }
        int indexStart = -1;
        final String urlLowerCase = url.toLowerCase(Locale.getDefault());
        if (urlLowerCase.startsWith("http://")) {
            indexStart = url.indexOf("/", url.indexOf("http://") + 8);
        }
        if (urlLowerCase.startsWith("https://")) {
            indexStart = url.indexOf("/", url.indexOf("https://") + 9);
        }
        if (indexStart == -1) {
            return url;
        }
        String uri = url.substring(indexStart);
        return uri;
    }

    /**
     * 截取字符串，根据开始和结束的字符串截取中间部分（不包括开始和结束的字符串）。<br>
     *
     * 如果 finish 为空则截取到源字符串的末尾。
     *
     * @Title getSubString
     * @Description 截取字符串，根据开始和结束的字符串截取中间部分（不包括开始和结束的字符串）。
     *
     * @param src
     * @param begin
     * @param finish
     * @return
     */
    public static String getSubString(String src, String begin, String finish) {
        int start = src.indexOf(begin);

        if (finish == null || "".equals(finish)) {
            if (start >= 0) {
                String result = src.substring(start + begin.length());
                return result;
            }
        }

        int end = src.lastIndexOf(finish);
        if (end <= 0) {
            end = src.length();
        }

        if (start >= 0 && (start + begin.length()) < end) {
            String result = src.substring((start + begin.length()), end);
            return result;
        }
        return "";
    }


    /**
     * 把给定字符串的第一个字母变成大写。
     *
     * @Title toFirstLetterUpperCase
     * @Description 把给定字符串的第一个字母变成大写。
     *
     * @param value
     * @return
     */
    public String toFirstLetterUpperCase(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        if (value.length() == 1) {
            return value.toUpperCase(Locale.getDefault());
        }
        String firstLetter = value.substring(0, 1).toUpperCase(Locale.getDefault());
        return firstLetter + value.substring(1, value.length());
    }

    /**
     * 使用 UTF-8 编码对给定的字符串做 URLDecode。<br>
     *
     * @Title decodeUTF8
     * @Description 使用 UTF-8 编码对给定的字符串做 URLDecoder。
     *
     * @param value
     * @return
     */
    public static String decodeUTF8(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 对给定的字符串做 URLEncode，并把 encode 结果中的 "+" 全部替换成 "%20"。
     *
     * @Title encodeUTF8
     * @Description 对给定的字符串做 URLEncode，并把 encode 结果中的 "+" 全部替换成 "%20"。
     *
     * @param value
     * @return
     */
    public static String encodeUTF8(String value) {
        String result = "";
        try {
            result = URLEncoder.encode(value, "UTF-8");
            if (result != null) {
                result = result.replaceAll("\\+", "%20");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || "null".equals(str);
    }
}
