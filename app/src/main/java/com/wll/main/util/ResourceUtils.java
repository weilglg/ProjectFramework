package com.wll.main.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 操作资源的工具类。
 */
public final class ResourceUtils {

    /**
     * 通过 id 的名称获取资源 id。
     *
     * @param context
     * @param idName
     * @return
     * @Title getIdByName
     * @Description 通过 id 的名称获取资源 id。
     */
    public static int getIdByName(Context context, String idName) {
        return context.getResources().getIdentifier(idName, "id", context.getPackageName());
    }

    /**
     * 封装 {@link Resources#getColor(int)} 方法。若指定的资源不存在返回 R.color.transparent。
     *
     * @param context
     * @param resId
     * @return
     * @Title getColor
     * @Description 封装 {@link Resources#getColor(int)} 方法。若指定的资源不存在返回 R.color.transparent。
     */
    public static int getColor(Context context, int resId) {
        int color = 0;
        try {
            return context.getResources().getColor(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        color = Color.argb(0, 0, 0, 0);
        return color;
    }

    /**
     * 获取指定的 {@link Context} 的 {@link ApplicationInfo}。
     *
     * @param context
     * @return
     * @Title getApplicationInfo
     * @Description 获取指定的 {@link Context} 的 {@link ApplicationInfo}。
     */
    public static ApplicationInfo getApplicationInfo(Context context) {
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ai;
    }

    /**
     * 获取 manifest 中 application 节点设置的字符串类型的 meta-data。
     *
     * @param context
     * @param key
     * @return
     * @Title getApplicationMetaData
     * @Description 获取 manifest 中 application 节点设置的字符串类型的 meta-data。
     */
    public static String getApplicationMetaData(Context context, String key) {
        ApplicationInfo ai = getApplicationInfo(context);
        if (ai != null) {
            return ai.metaData.getString(key);
        }
        return "";
    }

    /**
     * 获取 manifest 中 application 节点设置的 int 类型的 meta-data。
     *
     * @param context
     * @param key
     * @param defValue 如果没有设置该值时返回的默认值
     * @return
     * @Title getApplicationMetaDataInt
     * @Description 获取 manifest 中 application 节点设置的 int 类型的 meta-data。
     */
    public static int getApplicationMetaDataInt(Context context, String key, int defValue) {
        ApplicationInfo ai = getApplicationInfo(context);
        if (ai != null) {
            return ai.metaData.getInt(key, defValue);
        }
        return defValue;
    }

    /**
     * 获取当前版本号。
     *
     * @param context
     * @return
     * @Title getAppVersionName
     * @Description 获取当前版本号。
     */
    public static String getAppVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            String versionName = pi.versionName;
            if (versionName != null) {
                return versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 把 dp 值转换为 px 值。
     *
     * @param context
     * @param dpValue
     * @return
     * @Title dp2px
     * @Description 把 dp 值转换为 px 值。
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 把 sp 值转换为 px 值。
     *
     * @param context
     * @param spValue
     * @return
     * @Title sp2px
     * @Description 把 sp 值转换为 px 值。
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 把资源文件中定义的 dimens 转换为 px 值。
     *
     * @param context
     * @param dimensId
     * @return
     * @Title dimen2px
     * @Description 把资源文件中定义的 dimens 转换为 px 值。
     */
    public static int dimen2px(Context context, int dimensId) {
        try {
            return context.getResources().getDimensionPixelSize(dimensId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 从 assets 文件中读取数据。
     *
     * @param context
     * @param fileName
     * @return
     * @Title readFromAssets
     * @Description 从 assets 文件中读取数据。
     */
    public static String readFromAssets(Context context, String fileName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = null;
        byte buf[] = new byte[1024];
        int len;
        try {
            is = context.getResources().getAssets().open(fileName);
            while ((len = is.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(bos);
            FileUtils.close(is);
        }
        return "";
    }

    /**
     * 从 raw 文件中读取数据。
     *
     * @param context
     * @param resId
     * @return
     * @Title readFromRaw
     * @Description 从 raw 文件中读取数据。
     */
    public static String readFromRaw(Context context, int resId) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = null;
        byte[] buf = new byte[1024];
        int len;
        try {
            is = context.getResources().openRawResource(resId);
            while ((len = is.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(bos);
            FileUtils.close(is);
        }
        return "";
    }

    /**
     * 获取给定的 Class 所在包目录下指定文件名的 Properties 文件。
     *
     * @param clazz
     * @param fileName
     * @return
     * @Title loadProperties
     * @Description 获取给定的 Class 所在包目录下指定文件名的 Properties 文件。
     */
    public static Properties loadProperties(Class<?> clazz, String fileName) {
        InputStream is = null;
        try {
            Properties prop = new Properties();
            is = clazz.getResourceAsStream(fileName);
            if (is != null) {
                prop.load(is);
                return prop;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(is);
        }
        return null;
    }

    /**
     * 把给定的 Class 所在包目录下的 properties 文件转换成 {@link Map}。
     *
     * @param clazz
     * @param fileName
     * @return
     * @Title convertProperties2Map
     * @Description 把给定的 Class 所在包目录下的 properties 文件转换成 {@link Map}。
     */
    public static Map<String, String> convertProperties2Map(Class<?> clazz, String fileName) {
        Map<String, String> result = new HashMap<String, String>();
        InputStream is = null;
        try {
            Properties prop = new Properties();
            is = clazz.getResourceAsStream(fileName);
            prop.load(is);
            if (prop != null) {
                for (Entry<Object, Object> entry : prop.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    result.put(key.toString(), value.toString());
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(is);
        }
        return result;
    }

    /**
     * 从给定的 Class 所在的包目录下读取指定名称的 JSON 文件，并将文件中的内容转换成 {@link JSONObject}。
     *
     * @param clazz
     * @param name
     * @return
     * @Title readJSONObject
     * @Description 从给定的 Class 所在的包目录下读取指定名称的 JSON 文件。
     */
    public static JSONObject readJSONObject(Class<?> clazz, String name) {
        InputStream is = null;
        try {
            is = clazz.getResourceAsStream(name);
            if (is != null) {
                String result = new String(FileUtils.readStream(is, "UTF-8"));

                JSONObject json = JSON.parseObject(result);
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(is);
        }
        return new JSONObject();
    }

    /**
     * 从给定的 Class 所在的包目录下读取指定名称的 JSON 文件，并将文件中的内容转换成 {@link JSONArray}。
     *
     * @param clazz
     * @param name
     * @return
     * @Title readJSONArray
     * @Description 从给定的 Class 所在的包目录下读取指定名称的 JSON 文件。
     */
    public static JSONArray readJSONArray(Class<?> clazz, String name) {
        InputStream is = null;
        try {
            is = clazz.getResourceAsStream(name);
            if (is != null) {
                String result = new String(FileUtils.readStream(is, "UTF-8"));

                JSONArray json = JSON.parseArray(result);
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(is);
        }
        return new JSONArray();
    }

    /**
     * 从 assets 文件中读取指定名称的 JSON 文件，并将文件中的内容转换成 {@link JSONArray}。
     *
     * @param context
     * @param fileName
     * @return
     * @Title readJSONArrayFromAssets
     * @Description 从 assets 文件中读取指定名称的 JSON 文件。
     */
    public static JSONArray readJSONArrayFromAssets(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(fileName);
            String result = new String(FileUtils.readStream(is, "UTF-8"));

            JSONArray json = JSON.parseArray(result);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    /**
     * 根据名称获得Assets中的图片资源
     */
    public static Drawable getDrawableFromAssets(Context c, String name) {
        BitmapDrawable drawable = null;
        InputStream is = null;
        AssetManager manager = c.getAssets();
        try {
            is = manager.open(name);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            drawable = new BitmapDrawable(c.getResources(), bitmap);
        } catch (Exception e) {
            Toast.makeText(c, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }


}
