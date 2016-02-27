package wll.com.okhttputils.request;


import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import wll.com.okhttputils.OkHttpUtil;
import wll.com.okhttputils.callback.Callback;

/**
 * Created by WLL on 2016/2/19.<p/>
 * 网络请求的核心类
 */
public class RequestCall {
    private OkHttpRequest mOkHttpRequest;
    private Request mRequest;
    private OkHttpClient mOkHttpClient;
    private Call mCall;

    /**
     * 获取数据超时时间
     */
    private long mReadTimeOut;
    /**
     * 操作数据超时时间
     */
    private long mWritTimeOut;
    /**
     * 网络连接超时时间
     */
    private long mConnTimeOut;

    public RequestCall(OkHttpRequest okHttpRequest) {
        this.mOkHttpRequest = okHttpRequest;
    }

    public OkHttpRequest getOkHttpRequest() {
        return mOkHttpRequest;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.mReadTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.mWritTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.mConnTimeOut = connTimeOut;
        return this;
    }

    public Request getRequest() {
        return mRequest;
    }

    public Call getCall() {
        return mCall;
    }

    /**
     * 生成请求
     *
     * @param callback
     * @return
     */
    private Request generateRequest(Callback callback) {
        return mOkHttpRequest.generateRequest(callback);
    }

    /**
     * 发起网络请求
     *
     * @param callback
     */
    public void execute(Callback callback) {
        generateCall(callback);
        if (callback != null) {
            callback.onBefore(mRequest);
        }
        OkHttpUtil.getInstance().execute(this, callback);
    }

    private Call generateCall(Callback callback) {
        mRequest = generateRequest(callback);
        if (mReadTimeOut > 0 || mWritTimeOut > 0 || mConnTimeOut > 0) {
            mReadTimeOut = mReadTimeOut > 0 ? mReadTimeOut : OkHttpUtil.DEFAULT_MILLISECONDS;
            mWritTimeOut = mWritTimeOut > 0 ? mWritTimeOut : OkHttpUtil.DEFAULT_MILLISECONDS;
            mConnTimeOut = mConnTimeOut > 0 ? mConnTimeOut : OkHttpUtil.DEFAULT_MILLISECONDS;

            mOkHttpClient = OkHttpUtil.getInstance().getOkHttpClient().newBuilder().connectTimeout(mConnTimeOut,
                    TimeUnit.MILLISECONDS)
                    .readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(mWritTimeOut, TimeUnit.MILLISECONDS).build();

            mCall = mOkHttpClient.newCall(mRequest);
        } else {
            mCall = OkHttpUtil.getInstance().getOkHttpClient().newCall(mRequest);
        }
        return mCall;
    }
}
