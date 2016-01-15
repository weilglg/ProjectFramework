package com.wll.main.util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
public class JsonUtils {
	
	public static JSONObject getJSONObject(String json){
		JSONObject jo = null ;
		try {
			JSONTokener jsonTokener = getJsonTokener(json) ;
			if(jsonTokener!=null){
				jo = (JSONObject) getJsonTokener(json).nextValue() ;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo  ;
	}
	
	private static JSONTokener getJsonTokener(String json){
		return  new JSONTokener(json) ;
	}
	
	public static String bean2json(Object bean) {
		Gson gson=new Gson();
		String obj=gson.toJson(bean);
		obj =obj.substring(1,obj.length()-1);
		return obj; 
	}
	public static String bean2jsonhava(Object bean) {
		Gson gson=new Gson();
		String obj=gson.toJson(bean);
		return obj; 
	}
	public static Object json2bean(String json,Class obj){
		Gson gson=new Gson();
		return gson.fromJson(json, obj);
	}
}
