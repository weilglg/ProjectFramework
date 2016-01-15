package com.wll.main.util.http;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wll.main.util.http.callback.ResultCallBack;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * 网络请求管理类
 * Created by wll on 2015/11/17.
 */
public class OkHttpManager {
    //请求管理对象
    private static OkHttpManager mOkHttpManager;
    //请求链接对象
    private OkHttpClient mOkHttpClient;
    //解析数据的Gson对象
    private Gson mGson;
    //用于开启更新UI线程的Handler
    private Handler uiHandler;


    private OkHttpManager() {
        //初始化
        mOkHttpClient = new OkHttpClient();
        uiHandler = new Handler(Looper.getMainLooper());
        //根据不同的SDK版本采用不同的方法实例化Gson
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 23) {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .excludeFieldsWithModifiers(
                            Modifier.FINAL,
                            Modifier.TRANSIENT,
                            Modifier.STATIC);
            mGson = gsonBuilder.create();
        } else {
            mGson = new Gson();
        }
    }

    /**
     * 获得OkHttpManager对象
     */
    public static OkHttpManager getInstance() {
        //使用单例模式保证只有一个获得OkHttpManager对象
        if (mOkHttpManager == null) {
            synchronized (OkHttpManager.class) {
                if (mOkHttpManager == null) {
                    mOkHttpManager = new OkHttpManager();
                }
            }
        }
        return mOkHttpManager;
    }


    /**
     * 获得Handler对象
     */
    public Handler getHandler() {
        return uiHandler;
    }

    /**
     * 获得OkHttpClient对象
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 根据标签取消请求
     */
    public void cancelTag(Object tag) {
        mOkHttpClient.cancel(tag);
    }

    /**
     * 开启网络请求的方法（异步）
     *
     * @param request        请求
     * @param resultCallBack 请求回调函数
     */
    public void execute(final Request request, ResultCallBack resultCallBack) {
        //如果传入的回调函数为空，则设置默认
        if (resultCallBack == null) {
            resultCallBack = ResultCallBack.DEFAULT_RESULT_CALLBACK;
        }
        final ResultCallBack callBack = resultCallBack;
        callBack.onBefore(request);
        //将请求加入调度
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                //请求失败时调用
                sendFailureResultCallBack(request, e, callBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //请求成功时调用
                try {
                    //判断请求响应码
                    if (response.code() >= 400 && response.code() <= 599) {
                        sendFailureResultCallBack(request, new RuntimeException(response.body().string()), callBack);
                    } else {
                        //获得请求返回的字符串
                        final String resultStr = response.body().string();
                        //如果泛型的类型是String，则不用进行解析直接将字符串返回
                        if (callBack.mType == String.class) {
                            sendSuccessResultCallBack(resultStr, callBack);
                        } else {//如果传入的泛型不是String,则对返回结果进行解析
                            Object o = mGson.fromJson(resultStr, callBack.mType);
                            sendSuccessResultCallBack(o, callBack);
                        }
                    }
                } catch (IOException e) {
                    sendFailureResultCallBack(request, e, callBack);
                } catch (JsonParseException e) {
                    sendFailureResultCallBack(request, e, callBack);
                }
            }
        });
    }


    /**
     * 开启网络请求的方法（同步）
     */
    public <T> T execute(Request request, Class<T> clazz) throws IOException {
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        String respStr = execute.body().string();
        return mGson.fromJson(respStr, clazz);
    }

    /**
     * 请求成功时回调此方法
     */
    public void sendSuccessResultCallBack(final Object object, final ResultCallBack callBack) {
        if (callBack == null) {
            return;
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onResponse(object);
                callBack.onAfter();
            }
        });
    }

    /**
     * 请求失败时调动此方法
     */
    public void sendFailureResultCallBack(final Request request, final Exception e, final ResultCallBack resultCallBack) {
        //如果没有设置回调函数，直接结束此方法
        if (resultCallBack == null) {
            return;
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                //调用相应的回调方法
                resultCallBack.onAfter();
                resultCallBack.onError(request, e);
            }
        });
    }


}
