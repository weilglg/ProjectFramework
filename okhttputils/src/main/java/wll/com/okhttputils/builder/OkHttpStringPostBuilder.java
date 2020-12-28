package wll.com.okhttputils.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import wll.com.okhttputils.request.OkHttpPostStringRequest;
import wll.com.okhttputils.request.RequestCall;

/**
 * Created by WLL on 2016/2/19.
 */
public class OkHttpStringPostBuilder extends OkHttpRequestBuilder {
    private String content;
    private MediaType mediaType;

    /**
     * 请求体
     *
     * @param content
     * @return
     */
    public OkHttpStringPostBuilder content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 请求编码格式
     *
     * @param mediaType
     * @return
     */
    public OkHttpStringPostBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public OkHttpStringPostBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public OkHttpStringPostBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public OkHttpStringPostBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public OkHttpStringPostBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public OkHttpStringPostBuilder addParams(String key, String value) {
        if (this.params == null) {
            this.params = new LinkedHashMap<String, String>();
        }
        this.params.put(key, value);
        return this;
    }

    @Override
    public OkHttpStringPostBuilder addHeaders(String key, String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<String, String>();
        }
        this.headers.put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        return new OkHttpPostStringRequest(url, tag, params, headers, content, mediaType).build();
    }
}
