package com.wll.main.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.wll.main.MyApplication;


/**
 * 日志输出工具类
 *
 * @author zmq 2015-07-06
 */
public class LogUtils {
    /**
     * 日志输出标签TAG[包名]
     **/
    public static String TAG = MyApplication.getMyApplication()
            .getPackageName();
    /**
     * 日志输出模式[true/false]
     **/
    public static String debugMode = getDebugMode();

    /**
     * 获取配置文件中的调试模式值
     **/
    public static String getDebugMode() {
        Context context = MyApplication.getMyApplication();
        ApplicationInfo ai;
        String debugMode = "";
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get("debugMode");
            if (value != null && !TextUtils.isEmpty(value.toString())) {
                debugMode = value.toString();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return debugMode;
    }

    public static void e(String exMsg) {
        log(Log.ERROR, exMsg);
    }

    public static void e(String tag, String exMsg) {
        log(Log.ERROR, tag, exMsg);
    }

    public static void i(String exMsg) {
        log(Log.INFO, exMsg);
    }

    public static void i(String tag, String exMsg) {
        log(Log.INFO, tag, exMsg);
    }

    public static void v(String exMsg) {
        log(Log.VERBOSE, exMsg);
    }

    public static void v(String tag, String exMsg) {
        log(Log.VERBOSE, tag, exMsg);
    }

    public static void d(String exMsg) {
        log(Log.DEBUG, exMsg);
    }

    public static void d(String tag, String exMsg) {
        log(Log.DEBUG, tag, exMsg);
    }

    public static void w(String exMsg) {
        log(Log.WARN, exMsg);
    }

    public static void w(String tag, String exMsg) {
        log(Log.WARN, tag, exMsg);
    }

    /**
     * 【自定义标签】输出系统日志
     *
     * @param mode  日志输出模式：int类型值[2/3/4/5/6]，可用Log的常量值来确定,例如：Log.ERROR
     * @param exMsg 日志信息
     **/
    private static void log(int mode, String tag, String exMsg) {
        doLog(mode, tag, exMsg);
    }

    /**
     * 【用包名作为默认标签】输出系统日志
     *
     * @param mode  日志输出模式：int类型值[2/3/4/5/6]，可用Log的常量值来确定,例如：Log.ERROR
     * @param exMsg 日志信息
     **/
    private static void log(int mode, String exMsg) {
        doLog(mode, TAG, exMsg);
    }

    private static void doLog(int mode, String tag, String exMsg) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (TextUtils.isEmpty(exMsg)) {
            exMsg = "no exMsg";
        }
        if ("true".equals(debugMode)) {
            switch (mode) {
                case Log.ASSERT:
                    Log.v(tag, exMsg);
                    break;
                case Log.DEBUG:
                    Log.d(tag, exMsg);
                    break;
                case Log.ERROR:
                    Log.e(tag, exMsg);
                    break;
                case Log.INFO:
                    Log.i(tag, exMsg);
                    break;
                case Log.VERBOSE:
                    Log.v(tag, exMsg);
                    break;
                case Log.WARN:
                    Log.w(tag, exMsg);
                    break;
                default:
                    Log.e(tag, exMsg);
                    break;
            }
        }
    }

}
