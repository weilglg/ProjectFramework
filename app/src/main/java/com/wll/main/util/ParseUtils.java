package com.wll.main.util;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
* 解析 JSON 工具类，处理 JSONException。 <br>
* 
* 使用 fastjson 库。
* 
* @since App Version 4.0
* @author ly
*/
public final class ParseUtils {

	private static final String	TAG	= "ParseUtils";

	/**
	 * JSONObject 数据实例化。
	 * 
	 * @Title parseObject
	 * @Description JSONObject 数据实例化。
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		if (text != null && text.length() > 0) {
			try {
				return JSON.parseObject(text, clazz);
			}
			catch (Exception e) {
				Log.e(TAG, "parseObject exception, string text: " + text);
			}
		}
		try {
			return clazz.newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * JSONArray 数据实例化。
	 * 
	 * @Title parseArray
	 * @Description JSONArray 数据实例化。
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> parseArray(String text, Class<T> clazz) {
		if (text != null && text.length() > 0) {
			try {
				return JSON.parseArray(text, clazz);
			}
			catch (Exception e) {
				Log.e(TAG, "parseObject exception, string text: " + text);
			}
		}
		return new ArrayList<T>();
	}

	/**
	 * 解析 {@link JSONObject}。
	 * 
	 * @Title parseJSONObject
	 * @Description 解析 {@link JSONObject}。
	 * 
	 * @param text
	 * @return 若异常则返回空 {@link JSONObject}
	 */
	public static JSONObject parseJSONObject(String text) {
		if (text != null && text.length() > 0) {
			try {
				JSONObject object = JSON.parseObject(text);
				if (object != null) {
					return object;
				}
			}
			catch (Exception e) {
				Log.e(TAG, "parseJSONObject exception, string text: " + text);
			}
		}
		return new JSONObject();
	}

	/**
	 * 从 {@link JSONObject} 中解析 {@link JSONObject}。
	 * 
	 * @Title parseJSONObject
	 * @Description 从 {@link JSONObject} 中解析 {@link JSONObject}。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回 null
	 */
	public static JSONObject parseJSONObject(JSONObject object, String key) {
		if (object != null && object.containsKey(key)) {
			try {
				return object.getJSONObject(key);
			}
			catch (JSONException e) {
				Log.e(TAG, "parseJSONObject exception, key: " + key + "\n--> JSONObject: ");
				Log.e(TAG, object.toJSONString());
			}
		}
		return null;
	}

	/**
	 * 从 {@link JSONArray} 中解析 {@link JSONObject}。
	 * 
	 * @Title parseJSONObject
	 * @Description 从 {@link JSONArray} 中解析 {@link JSONObject}。
	 * 
	 * @param array
	 * @param index
	 * @return 若异常则返回 null
	 */
	public static JSONObject parseJSONObject(JSONArray array, int index) {
		if (array != null) {
			try {
				return array.getJSONObject(index);
			}
			catch (JSONException e) {
				Log.e(TAG, "parseJSONObject exception, index: " + index + "\n--> JSONArray: ");
				Log.e(TAG, array.toJSONString());
			}
		}
		return null;
	}

	/**
	 * 从 {@link JSONObject} 中解析 {@link JSONArray}。
	 * 
	 * @Title parseJSONArray
	 * @Description 从 {@link JSONObject} 中解析 {@link JSONArray}。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回空 {@link JSONArray}
	 */
	public static JSONArray parseJSONArray(JSONObject object, String key) {
		if (object != null) {
			try {
				return object.getJSONArray(key);
			}
			catch (JSONException e) {
				Log.e(TAG, "parseJSONArray exception, key: " + key + "\n--> JSONObject: ");
				Log.e(TAG, object.toJSONString());
			}
		}
		return new JSONArray();
	}

	/**
	 * 从 {@link JSONObject} 中解析 {@link String}。
	 * 
	 * @Title parseString
	 * @Description 从 {@link JSONObject} 中解析 {@link String}。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回 ""
	 */
	public static String parseString(JSONObject object, String key) {
		if (object != null && object.containsKey(key)) {
			try {
				String value = object.getString(key);
				return (value == null) ? "" : value.trim();
			}
			catch (JSONException e) {
				Log.e(TAG, "parseString exception, key: " + key + "\n--> JSONObject: ");
				Log.e(TAG, object.toJSONString());
			}
		}
		return "";
	}

	/**
	 * 从 {@link JSONArray} 中解析 {@link String}。
	 * 
	 * @Title parseString
	 * @Description 从 {@link JSONArray} 中解析 {@link String}。
	 * 
	 * @param array
	 * @param index
	 * @return 若异常则返回 ""
	 */
	public static String parseString(JSONArray array, int index) {
		if (array != null) {
			try {
				return array.getString(index);
			}
			catch (JSONException e) {
				Log.e(TAG, "parseString exception, index: " + index + "\n--> JSONArray: ");
				Log.e(TAG, array.toJSONString());
			}
		}
		return "";
	}

	/**
	 * 从 {@link JSONObject} 中解析 int。
	 * 
	 * @Title parseInt
	 * @Description 从 {@link JSONObject} 中解析 int。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回 0
	 */
	public static int parseInt(JSONObject object, String key) {
		String str = parseString(object, key);
		return NumberUtils.parseInt(str);
	}

	/**
	 * 从 {@link JSONObject} 中解析 long。
	 * 
	 * @Title parseLong
	 * @Description 从 {@link JSONObject} 中解析 long。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回 0
	 */
	public static long parseLong(JSONObject object, String key) {
		String str = parseString(object, key);
		return NumberUtils.parseLong(str);
	}

	/**
	 * 从 {@link JSONObject} 中解析 double。
	 * 
	 * @Title parseDouble
	 * @Description 从 {@link JSONObject} 中解析 double。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回 0
	 */
	public static double parseDouble(JSONObject object, String key) {
		String str = parseString(object, key);
		return NumberUtils.parseDouble(str);
	}

	/**
	 * 从 {@link JSONObject} 中解析 boolean。
	 * 
	 * @Title parseBoolean
	 * @Description 从 {@link JSONObject} 中解析 boolean。
	 * 
	 * @param object
	 * @param key
	 * @return 若异常则返回 false
	 */
	public static boolean parseBoolean(JSONObject object, String key) {
		if (object != null && object.containsKey(key)) {
			try {
				boolean value = object.getBoolean(key);
				return value;
			}
			catch (JSONException e) {
				Log.e(TAG, "parseBoolean exception, key: " + key + "\n--> JSONObject: ");
				Log.e(TAG, object.toJSONString());
			}
		}
		return false;
	}

}
