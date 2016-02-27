package wll.com.okhttputils.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import wll.com.okhttputils.request.OkHttpPostFormRequest;
import wll.com.okhttputils.request.RequestCall;

/**
 * Created by WLL on 2016/2/19.
 */
public class OkHttpPostFormBuilder extends OkHttpRequestBuilder {

    private List<FileInput> files = new ArrayList<FileInput>();

    public OkHttpPostFormBuilder addFile(String key, String fileName, File file) {
        this.files.add(new FileInput(key, fileName, file));
        return this;
    }

    @Override
    public OkHttpPostFormBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public OkHttpPostFormBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public OkHttpPostFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public OkHttpPostFormBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public OkHttpPostFormBuilder addParams(String key, String value) {
        if (this.params == null) {
            this.params = new LinkedHashMap<String, String>();
        }
        this.params.put(key, value);
        return this;
    }

    @Override
    public OkHttpPostFormBuilder addHeaders(String key, String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<String, String>();
        }
        this.headers.put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        return new OkHttpPostFormRequest(url, tag, params, headers, files).build();
    }

    public static class FileInput {
        public String key;
        public String fileName;
        public File file;

        public FileInput(String key, String fileName, File file) {
            this.key = key;
            this.fileName = fileName;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
