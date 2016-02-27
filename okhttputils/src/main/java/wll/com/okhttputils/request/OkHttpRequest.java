package wll.com.okhttputils.request;

import android.text.TextUtils;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import wll.com.okhttputils.callback.Callback;

/**
 * Created by WLL on 2016/2/19.<p/>
 * 该类完成请求的初始化操作
 */
public abstract class OkHttpRequest {
    /**
     * 请求地址
     */
    protected String url;
    /**
     * 当前请求的标志
     */
    protected Object tag;
    /**
     * 参数
     */
    protected Map<String, String> params;
    /**
     * 请求头信息
     */
    protected Map<String, String> headers;

    Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        if (TextUtils.isEmpty(url)) {
            new IllegalArgumentException("url can not be null");
        }
    }

    /**
     * 拼接请求体
     */
    protected abstract RequestBody buildRequestBody();

    /**
     * 网络请求的初始化
     *
     * @param builder
     * @param requestBody
     * @return
     */
    protected abstract Request buildRequest(Request.Builder builder, RequestBody requestBody);

    /**
     * 将请求跟自定义的回调函数关联
     *
     * @param requestBody
     * @param callback
     * @return
     */
    protected RequestBody warpRequestBody(RequestBody requestBody, final Callback callback) {
        return requestBody;
    }

    /**
     * 获得RequestCall实例
     *
     * @return
     */
    public RequestCall build() {
        return new RequestCall(this);
    }

    /**
     * 生成请求
     *
     * @param callback
     * @return
     */
    public Request generateRequest(Callback callback) {
        // 将请求过程跟回调函数进行绑定
        RequestBody requestBody = warpRequestBody(buildRequestBody(), callback);
        // 设置请求地址、标志、请求头信息
        prepareBuilder();
        return buildRequest(builder, requestBody);
    }

    /**
     * 完成请求的准备工作（设置请求地址、请求标志、请求头）
     */
    protected void prepareBuilder() {
        //设置请求地址、标志
        builder.url(url).tag(tag);
        //设置请求头信息
        appendHeaders();
    }

    /**
     * 拼接请求头信息
     */
    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    @Override
    public String toString() {
        return "OkHttpRequest{" +
                "url='" + url + '\'' +
                ", tag=" + tag +
                ", params=" + params +
                ", headers=" + headers +
                '}';
    }
}
