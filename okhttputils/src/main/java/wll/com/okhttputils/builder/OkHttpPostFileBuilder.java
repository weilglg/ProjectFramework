package wll.com.okhttputils.builder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import wll.com.okhttputils.request.OkHttpPostFileRequest;
import wll.com.okhttputils.request.RequestCall;

/**
 * Created by WLL on 2016/2/22.
 */
public class OkHttpPostFileBuilder extends OkHttpRequestBuilder {

    private File file;
    private MediaType mediaType;

    public OkHttpPostFileBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public OkHttpRequestBuilder file(File file) {
        this.file = file;
        return this;
    }


    @Override
    public OkHttpPostFileBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public OkHttpPostFileBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public OkHttpPostFileBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public OkHttpPostFileBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public OkHttpPostFileBuilder addParams(String key, String value) {
        if (this.params == null) {
            this.params = new LinkedHashMap<String, String>();
        }
        this.params.put(key, value);
        return this;
    }

    @Override
    public OkHttpPostFileBuilder addHeaders(String key, String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<String, String>();
        }
        this.headers.put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        return new OkHttpPostFileRequest(url, tag, params, headers, file, mediaType).build();
    }
}
