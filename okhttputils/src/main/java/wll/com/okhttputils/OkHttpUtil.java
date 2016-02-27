package wll.com.okhttputils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import wll.com.okhttputils.builder.OkHttpGetBuilder;
import wll.com.okhttputils.builder.OkHttpPostFileBuilder;
import wll.com.okhttputils.builder.OkHttpPostFormBuilder;
import wll.com.okhttputils.builder.OkHttpStringPostBuilder;
import wll.com.okhttputils.callback.Callback;
import wll.com.okhttputils.cookie.SimpleCookieJar;
import wll.com.okhttputils.request.RequestCall;
import wll.com.okhttputils.util.HttpsUtils;


/**
 * Created by WLL on 2016/2/19.
 */
public class OkHttpUtil {
    public static final String TAG = "OkHttpUtil";
    public static final long DEFAULT_MILLISECONDS = 10000;

    private static OkHttpUtil mInstance;

    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private boolean debug;
    private String tag;

    private OkHttpUtil() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        mOkHttpClient = okHttpClientBuilder.build();

        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtil();
                }
            }
        }
        return mInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpUtil setTag(String tag) {
        this.tag = tag;
        debug = true;
        return this;
    }

    /**
     * 发起网络请求
     *
     * @param requestCall
     * @param callback
     */
    public void execute(RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            Log.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest()
                    .toString() + "}");
        }
        // 没有设置回调函数时使用默认
        if (callback == null) {
            callback = Callback.CALLBACK_DEFAULT;
        }
        final Callback finalCallback = callback;
        // 发起请求
        requestCall.getCall().enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailureResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Object object = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(object, finalCallback);
                } catch (Exception e) {
                    sendFailureResultCallback(call, e, finalCallback);
                }
            }
        });
    }

    /**
     * 根据标志取消请求
     *
     * @param tag
     */
    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 设置证书
     *
     * @param certificates
     */
    public void setCertificates(InputStream... certificates) {
        mOkHttpClient = getOkHttpClient().newBuilder().sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates,
                null, null)).build();
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout
     * @param timeUnit
     */
    public void setConnectTimeOut(int timeout, TimeUnit timeUnit) {
        mOkHttpClient = getOkHttpClient().newBuilder().connectTimeout(timeout, timeUnit).build();
    }

    /**
     * 请求成功，将数据传递到UI线程中
     *
     * @param object
     * @param finalCallback
     */
    private void sendSuccessResultCallback(final Object object, final Callback finalCallback) {
        if (finalCallback == null) {
            return;
        }
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                finalCallback.onResponse(object);
                finalCallback.onAfter();
            }
        });
    }

    /**
     * 请求失败，将错误信息传递到UI线程中
     *
     * @param call
     * @param e
     * @param finalCallback
     */
    private void sendFailureResultCallback(final Call call, final Exception e, final Callback finalCallback) {
        if (finalCallback == null) {
            return;
        }
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                finalCallback.onError(call, e);
                finalCallback.onAfter();
            }
        });
    }

    /**
     * GET方式的请求
     *
     * @return
     */
    public static OkHttpGetBuilder get() {
        return new OkHttpGetBuilder();
    }

    /**
     * POST方式提交字符串
     *
     * @return
     */
    public static OkHttpStringPostBuilder postString() {
        return new OkHttpStringPostBuilder();
    }

    /**
     * 表单形式的请求（POST）
     * <p>
     * 支持单个多个文件，addFile的第一个参数为文件的key，即类别表单中<input type='file' name='mFile'/>的name属性。
     *
     * @return
     */
    public static OkHttpPostFormBuilder postForm() {
        return new OkHttpPostFormBuilder();
    }

    /**
     * 将文件作为请求体（POST）
     *
     * @return
     */
    public static OkHttpPostFileBuilder postFile() {
        return new OkHttpPostFileBuilder();
    }
}
