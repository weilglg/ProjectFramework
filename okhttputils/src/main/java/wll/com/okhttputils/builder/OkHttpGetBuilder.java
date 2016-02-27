package wll.com.okhttputils.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import wll.com.okhttputils.request.OkHttpGetRequest;
import wll.com.okhttputils.request.RequestCall;

/**
 * Created by WLL on 2016/2/19.
 */
public class OkHttpGetBuilder extends OkHttpRequestBuilder {

    @Override
    public OkHttpGetBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public OkHttpGetBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public OkHttpGetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public OkHttpGetBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public OkHttpGetBuilder addParams(String key, String value) {
        if (this.params == null) {
            this.params = new LinkedHashMap<String, String>();
        }
        this.params.put(key, value);
        return this;
    }

    @Override
    public OkHttpGetBuilder addHeaders(String key, String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<String, String>();
        }
        this.headers.put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        if (params != null && !params.isEmpty()) {
            url = appendParams(url, params);
        }
        return new OkHttpGetRequest(url, tag, params, headers).build();
    }

    /**
     * 拼接请求参数
     * @param url
     * @param params
     * @return
     */
    private String appendParams(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url +"?");
        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
