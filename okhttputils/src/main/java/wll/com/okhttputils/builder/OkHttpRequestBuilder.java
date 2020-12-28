package wll.com.okhttputils.builder;

import java.util.Map;

import wll.com.okhttputils.request.RequestCall;

/**
 * Created by WLL on 2016/2/19.
 */
public abstract class OkHttpRequestBuilder {
    /**
     * 网络请求地址
     */
    protected String url;
    /**
     * 标志
     */
    protected Object tag;
    /**
     * 请求头信息
     */
    protected Map<String, String> headers;
    /**
     * 请求参数
     */
    protected Map<String, String> params;

    /**
     * 设置请求地址
     *
     * @param url
     * @return
     */
    public abstract OkHttpRequestBuilder url(String url);

    /**
     * 设置请求标志
     *
     * @param tag
     * @return
     */
    public abstract OkHttpRequestBuilder tag(Object tag);

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public abstract OkHttpRequestBuilder params(Map<String, String> params);

    /**
     * 设置请求头信息
     *
     * @param headers
     * @return
     */
    public abstract OkHttpRequestBuilder headers(Map<String, String> headers);

    /**
     * 设置请求参数
     *
     * @param key
     * @param value
     * @return
     */
    public abstract OkHttpRequestBuilder addParams(String key, String value);

    /**
     * 设置请求头信息
     *
     * @param key
     * @param value
     * @return
     */
    public abstract OkHttpRequestBuilder addHeaders(String key, String value);

    public abstract RequestCall build();


}
