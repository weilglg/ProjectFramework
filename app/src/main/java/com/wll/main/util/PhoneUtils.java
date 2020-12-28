package com.wll.main.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by wll on 2015/10/30.
 */
public class PhoneUtils {

    /** 最大启动的服务数量 **/
    public static final int MAX_LIST_SRV = 50;
    /**
     * 检查网络情况
     **/
    public static boolean checkNetWork(Context mContext) {
        if (isNetworkAvailable(mContext)
                || isWifiAvailable(mContext)) {
            return true;
        }
        return false;
    }

    /**
     * 检查手机网络的链接情况
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        if (!checkInternetPermission(context)) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * 检查wifi的链接情况
     */
    public static boolean isWifiAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        if (!checkInternetPermission(context)) {
            return false;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return info.getType() == ConnectivityManager.TYPE_WIFI;
    }



    public static String getIMEI(Context mContext) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();// 手机的唯一标识
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    public static String getIMSI(Context mContext) {
        String imsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();// sim卡的唯一标识
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imsi;
    }

    /** 判断SIM卡是否可用 **/
    public static boolean isCanUseSim(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 停止服务 **/
    public static void stopService(Context context, String srvName,
                                   Class<?> srvClass) {
        if (isServiceRunning(context, srvName)) {
            context.stopService(new Intent(context, srvClass));
        }
    }

    /** 判断服务是否运行 **/
    public static boolean isServiceRunning(Context context, String srvName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = activityManager
                .getRunningServices(MAX_LIST_SRV);
        for (int i = 0; i < list.size(); i++) {
            if (srvName.equals(list.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /** 检查应用是否获取某个权限 **/
    public static boolean checkPermission(Context mContext,
                                          String permissionName) {
        PackageManager pm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pm.checkPermission(
                permissionName, mContext.getPackageName()));
    }

    /** 检查应用是否获取读取以及修改联系人权限 **/
    public static boolean checkContactsPermission(Context mContext) {
        return checkPermission(mContext, "android.permission.READ_CONTACTS")
                && checkPermission(mContext,
                "android.permission.WRITE_CONTACTS");
    }

    /** 检查应用是否获取读取以及修改通话记录权限 **/
    public static boolean checkCallLogPermission(Context mContext) {
        return checkPermission(mContext, "android.permission.READ_CALL_LOG")
                && checkPermission(mContext,
                "android.permission.WRITE_CALL_LOG");
    }

    /** 检查应用是否获取定位权限 **/
    public static boolean checkLocatinPermission(Context mContext) {
        return checkPermission(mContext,
                "android.permission.ACCESS_COARSE_LOCATION")
                && checkPermission(mContext,
                "android.permission.ACCESS_FINE_LOCATION");
    }

    /** 检查应用是否获取拨打电话权限 **/
    public static boolean checkDialPermission(Context mContext) {
        return checkPermission(mContext, "android.permission.CALL_PHONE");
    }

    /** 检查应用是否获取访问网络的权限 **/
    public static boolean checkInternetPermission(Context mContext) {
        return checkPermission(mContext, "android.permission.INTERNET");
    }

    /** 获取权限列表 **/
    public static void getPermisson(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // 得到自己的包名
            String pkgName = pi.packageName;

            PackageInfo pkgInfo = pm.getPackageInfo(pkgName,
                    PackageManager.GET_PERMISSIONS);// 通过包名，返回包信息
            String sharedPkgList[] = pkgInfo.requestedPermissions;// 得到权限列表
            StringBuffer tv = new StringBuffer();
            for (int i = 0; i < sharedPkgList.length; i++) {
                String permName = sharedPkgList[i];

                PermissionInfo tmpPermInfo = pm.getPermissionInfo(permName, 0);// 通过permName得到该权限的详细信息
                PermissionGroupInfo pgi = pm.getPermissionGroupInfo(
                        tmpPermInfo.group, 0);// 权限分为不同的群组，通过权限名，我们得到该权限属于什么类型的权限。
                tv.append(i + "-" + permName + "\n");
                tv.append(i + "-" + pgi.loadLabel(pm).toString() + "\n");
                tv.append(i + "-" + tmpPermInfo.loadLabel(pm).toString() + "\n");
                tv.append(i + "-" + tmpPermInfo.loadDescription(pm).toString()
                        + "\n");

            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("UserUtil", "Could'nt retrieve permissions for package");
        }
    }



}
