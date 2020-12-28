package com.wll.main.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedUtils {

    public static SharedPreferences mPreference;

    public static SharedPreferences getPreference(Context context) {
        if (mPreference == null) {
            synchronized (SharedUtils.class) {
                if (mPreference == null) {
                    mPreference = PreferenceManager
                            .getDefaultSharedPreferences(context);
                }
            }
        }
        return mPreference;
    }

    /**
     * 设置Integer类型的配置
     */
    public static void setInteger(Context context, String name, int value) {
        getPreference(context).edit().putInt(name, value).commit();
    }


    /**
     * 获取Integer类型的配置
     */
    public static int getInteger(Context context, String name, int default_i) {
        return getPreference(context).getInt(name, default_i);
    }


    /**
     * 设置String类型的配置
     */
    public static void setString(Context context, String name, String value) {
        getPreference(context).edit().putString(name, value).commit();
    }

    /**
     * 获取String类型的配置
     */
    public static String getString(Context context, String name) {
        return getPreference(context).getString(name, null);
    }

    /**
     * 获取String类型的配置
     */
    public static String getString(Context context, String name, String defalt) {
        return getPreference(context).getString(name, defalt);
    }

    /**
     * 获取boolean类型的配置
     */
    public static boolean getBoolean(Context context, String name,
                                     boolean defaultValue) {
        return getPreference(context).getBoolean(name, defaultValue);
    }

    /**
     * 设置boolean类型的配置
     */
    public static void setBoolean(Context context, String name, boolean value) {
        getPreference(context).edit().putBoolean(name, value).commit();
    }

    /**
     * 获取Long类型的配置
     */
    public static Long getLong(Context context, String name, long defaultValue) {
        return getPreference(context).getLong(name, defaultValue);
    }
    /**
     * 设置Long类型的配置
     */
    public static void setLong(Context context, String name, long value) {
        getPreference(context).edit().putLong(name, value).commit();
    }

}
