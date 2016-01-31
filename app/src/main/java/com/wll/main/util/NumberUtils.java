package com.wll.main.util;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 操作数字相关的工具类。
 * 
 * @since App Version 4.0
 * @version Version 1.0, 2013-8-14
 * @author wuzhen
 */
public final class NumberUtils {

	/**
	 * 把字符串转换成 integer 类型。
	 * 
	 * @param value
	 * @return 若字符串为空或转换时异常则返回 0。
	 */
	public static int parseInt(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			int result = Integer.parseInt(value.trim());
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 把字符串转换成 long 类型。
	 * 
	 * @param value
	 * @return 若字符串为空或转换时异常则返回 0。
	 */
	public static long parseLong(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			long result = Long.parseLong(value.trim());
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 把字符串转换成 float 类型。
	 * 
	 * @param value
	 * @return 若字符串为空或转换时异常则返回 0。
	 */
	public static float parseFloat(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			float result = Float.parseFloat(value);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 把字符串转换成 double 类型。
	 * 
	 * @param value
	 * @return 若字符串为空或转换时异常则返回 0。
	 */
	public static double parseDouble(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			int length = value.length();
			double result = Double.parseDouble(value.trim());
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 格式化 double。
	 * 
	 * @param value
	 * @param pattern
	 *            格式化模式
	 * @return
	 */
	public static String formatDouble(double value, String pattern) {
		try {
			DecimalFormat df = new DecimalFormat(pattern);
			return df.format(value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 格式化 double，pattern 为 "#[.0]"。double 向上取整（1.4 -> 2）。
	 * 
	 * @param value
	 *            要格式化的 double 的值。
	 * @param scale
	 *            指定要保留的小数位数。
	 * @return
	 */
	public static String formatDouble(double value, int scale) {
		try {
			String pattern = "0";
			if (scale > 0) {
				StringBuilder sb = new StringBuilder(".");
				for (int i = 0; i < scale; i++) {
					sb.append("0");
				}
				pattern += sb.toString();
			}
			double val = Math.ceil(value);
			DecimalFormat df = new DecimalFormat(pattern);
			return df.format(val);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 格式化 double，pattern 为 "#[.#]"。double 向上取整（1.4 -> 2）。
	 * 
	 * @param value
	 *            要格式化的 double 的值。
	 * @param scale
	 *            指定要保留的小数位数。
	 * @return
	 */
	public static String formatDoubleMaxScale(double value, int maxScale) {
		try {
			String pattern = "0";
			if (maxScale > 0) {
				StringBuilder sb = new StringBuilder(".");
				for (int i = 0; i < maxScale; i++) {
					sb.append("#");
				}
				pattern += sb.toString();
			}
			double val = Math.ceil(value);
			DecimalFormat df = new DecimalFormat(pattern);
			return df.format(val);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 格式化给定的 double 字符串。<br>
	 * 
	 * <ol>
	 * <b>格式化规则如下：</b>
	 * <li>
	 * 如果字符串中含有非数字，则剔除掉所有非数字。</li>
	 * <li>
	 * 如果字符串形式为 ".01"，则返回 0.01。</li>
	 * <li>
	 * 如果字符串形式为 "00002.12"，则返回 2.12。</li>
	 * <li>
	 * 如果字符串形式为 "1.0.1.3.4"，则返回 1.0134。</li>
	 * <li>
	 * </ol>
	 * 
	 * @param value
	 * @param scale
	 *            保留的小数位数
	 * @param mode
	 *            小数舍入的规则
	 * @return
	 */
	public static String formatDouble(String value, int scale, RoundingMode mode) {
		String str;
		if (value != null) {
			str = new String(value);
		}
		else {
			str = "0";
		}

		str = str.replaceAll("[^\\d\\.]", "");
		str = (str.length() == 0 ? "0" : str);

		String regex1 = "0*\\.\\d*";
		if (str.matches(regex1)) {
			str = str.replaceFirst("0*\\.", "00.");
		}

		final int index = str.indexOf(".");
		if (index != -1) {
			final String main = str.substring(0, index);
			final String minor = str.substring(index + 1);
			str = main + "." + minor.replaceAll("\\D", "");
		}

		str = str.replaceFirst("0*", "0");

		try {
			BigDecimal bd = new BigDecimal(str);
			bd = bd.setScale(scale, mode);
			return bd.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 判断 double 是否为整型。<br>
	 * 
	 * double 参数不支持科学计数法格式。
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isInteger(double value) {
		return isInteger(value + "");
	}

	/**
	 * 判断给定的字符串是否为整型。
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isInteger(String value) {
		int index = value.indexOf(".");
		if (index == -1) {
			return true;
		}
		else {
			String sub = value.substring(index + 1);
			if (sub.matches("[0]*")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 把 double 转换为 integer 类型的。如果该 double 不是 integer，则尝试对其进行格式化，返回 1
	 * 位小数，若格式化失败则返回原字符串。
	 * 
	 * @param value
	 * @return
	 */
	public static String format2Integer(double value) {
		return format2Integer(value, 1);
	}

	/**
	 * 把字符串转换为 integer 类型的。如果该字符串不是 integer，则尝试对其进行格式化，返回 1 位小数，若格式化失败则返回原字符串。
	 * 
	 * @param value
	 * @return
	 */
	public static String format2Integer(String value) {
		return format2Integer(value, 1);
	}

	/**
	 * 把 double 转换为 integer 类型的。如果该 double 不是
	 * integer，则根据指定的小数位数尝试对其进行格式化，若格式化失败则返回原字符串。
	 * 
	 * @param value
	 * @param scale
	 *            格式化的小数位数
	 * @return
	 */
	public static String format2Integer(double value, int scale) {
		return format2Integer(value + "", scale);
	}

	/**
	 * 把 double 转换为 integer 类型的。如果该 double 不是
	 * integer，则根据指定的小数位数尝试对其进行格式化，若格式化失败则返回原字符串。
	 * 
	 * @param value
	 * @param scale
	 *            格式化的小数位数
	 * @return
	 */
	public static String format2Integer(String value, int scale) {
		if (value == null) {
			return "0";
		}
		int index = value.indexOf(".");
		if (index == -1) {
			return parseInt(value) + "";
		}
		else {
			String sub = value.substring(index + 1);
			if (sub.matches("[0]*")) {
				return parseInt(value.substring(0, index)) + "";
			}
		}
		try {
			double result = Double.parseDouble(value.trim());
			String pattern = "0";
			if (scale > 0) {
				StringBuilder sb = new StringBuilder(".");
				for (int i = 0; i < scale; i++) {
					sb.append("0");
				}
				pattern += sb.toString();
			}
			final DecimalFormat df = new DecimalFormat(pattern);
			return df.format(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String format2IntegerMaxScale(String value, int maxScale) {
		if (value == null) {
			return "0";
		}
		int index = value.indexOf(".");
		if (index == -1) {
			return parseInt(value) + "";
		}
		else {
			String sub = value.substring(index + 1);
			if (sub.matches("[0]*")) {
				return parseInt(value.substring(0, index)) + "";
			}
		}
		try {
			double result = Double.parseDouble(value.trim());
			String pattern = "0";
			if (maxScale > 0) {
				StringBuilder sb = new StringBuilder(".");
				for (int i = 0; i < maxScale; i++) {
					sb.append("#");
				}
				pattern += sb.toString();
			}
			final DecimalFormat df = new DecimalFormat(pattern);
			return df.format(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 把字符串强制转换成 double 类型。<br>
	 * 
	 * <ol>
	 * <b>转换规则如下：</b>
	 * <li>
	 * 如果字符串为空，则返回 0.0。</li>
	 * <li>
	 * 如果字符串形式为 "1.01"，则返回 1.01。</li>
	 * <li>
	 * 如果字符串形式为 "1.0.1.3.4"，则返回 1.0134。</li>
	 * <li>
	 * 如果字符串中包含字母，先剔除掉字母再进行转换，例如："1.0.1dev.1" 返回 1.01。</li>
	 * </ol>
	 * 
	 * @param value
	 * @return
	 */
	public static double convert2Double(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0.0;
		}
		String filtered = value.replaceAll("[^\\d\\.]", "");
		int index = filtered.indexOf(".");
		if (index < 0) {
			return parseDouble(filtered);
		}
		String main = filtered.substring(0, index);
		String minor = filtered.substring(index + 1);
		return parseDouble(main + "." + minor.replaceAll("\\D", ""));
	}

	/**
	 * 把 byte[2] 数组转换成 short。
	 * 
	 * @param buf
	 * @param asc
	 * @return
	 */
	public final static short getShort(byte[] buf, boolean asc) {
		if (buf == null) {
			return 0;
		}
		if (buf.length > 2) {
			return -1;
		}
		short r = 0;
		if (asc) {
			for (int i = buf.length - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		}
		else {
			for (int i = 0; i < buf.length; i++) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		}
		return r;
	}
}