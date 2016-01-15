package com.wll.main.util.http.request;

import android.text.TextUtils;
import android.util.Pair;
import android.widget.ImageView;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.wll.main.util.http.OkHttpManager;
import com.wll.main.util.http.callback.ResultCallBack;
import com.wll.main.util.http.request.get.OkHttpDisplayImgRequest;
import com.wll.main.util.http.request.get.OkHttpDownloadFileRequest;
import com.wll.main.util.http.request.get.OkHttpGetRequest;
import com.wll.main.util.http.request.post.OkHttpPostRequest;
import com.wll.main.util.http.request.post.OkHttpUploadFileRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wll on 2015/11/17.
 */
public abstract class OkHttpRequest {
    /**
     * 网络请求管理对象
     */
    protected OkHttpManager mOkHttpManager;
    /**
     * 网络请求
     */
    protected OkHttpClient mOkHttpClient;
    /**
     * 请求体
     */
    protected RequestBody mRequestBody;

    protected Request mRequest;

    /**
     * 网络请求的链接地址
     */
    protected String url;
    /**
     * 标识
     */
    protected String tag;
    /**
     * 请求参数键值对
     */
    protected HashMap<String, String> params;
    /**
     * 请求头信息
     */
    protected HashMap<String, String> headers;


    public OkHttpRequest(String url, String tag, HashMap<String, String> params, HashMap<String, String> headers) {
        this.mOkHttpManager = OkHttpManager.getInstance();
        this.mOkHttpClient = this.mOkHttpManager.getOkHttpClient();
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
    }


    /**
     * 根据标签取消线程
     */
    public void cancel() {
        if (!TextUtils.isEmpty(tag))
            mOkHttpManager.cancelTag(tag);
    }

    /**
     * 完成网络请求的初始化
     */
    protected abstract Request buildRequest(RequestBody mRequestBody);

    /**
     * 拼接请求体
     */
    protected abstract RequestBody buildRequestBody();


    /**
     * 代理类
     */
    public static class Builder {
        /**
         * 链接地址
         */
        private String url;
        /**
         * 标签
         */
        private String tag;
        /**
         * 请求参数
         */
        private HashMap<String, String> params;
        /**
         * 请求头
         */
        private HashMap<String, String> headers;
        /**
         * 请求内容
         */
        private String content;

        /**
         * 请求的类型（文本、图片、文件等）
         */
        private MediaType mediaType;
        /**
         * 文件
         */
        private File file;
        /**
         * 字节数组
         */
        private byte[] bytes;
        /**
         * 需要上传的文件
         */
        private Pair<String, File>[] files;
        /**
         * 保存文件的路径
         */
        private String saveFileDir;
        /**
         * 保存的文件名称
         */
        private String saveFileName;
        /**
         * 需要设置图片的控件
         */
        private ImageView mImageView;
        /**
         * 加载失败时设置的图片资源ID
         */
        private int errorImageResId;

        /**
         * 设置请求链接
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置标签
         */
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 设置请求参数集合
         */
        public Builder params(HashMap<String, String> params) {
            this.params = params;
            return this;
        }

        /**
         * 设置单个请求参数
         */
        public Builder addParam(String key, String value) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.put(key, value);
            return this;
        }

        /**
         * 设置请求头集合
         */
        public Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        /**
         * 设置单个请求头
         */
        public Builder addHeader(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }

        /**
         * 设置请求内容
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * 设置请求的类型
         */
        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder bytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        /**
         * 设置要上传的文件集合
         */
        public Builder files(Pair<String, File>... files) {
            this.files = files;
            return this;
        }

        /**
         * 设置保存到本地的文件路径
         */
        public Builder saveFileDir(String fileDir) {
            this.saveFileDir = fileDir;
            return this;
        }

        /**
         * 设置保存到本地的文件名称
         */
        public Builder saveFileName(String fileName) {
            this.saveFileName = fileName;
            return this;
        }

        /**
         * 设置保存要设置图片的控件
         */
        public Builder setImageView(ImageView imageView) {
            this.mImageView = imageView;
            return this;
        }

        /**
         * 设置加载失败时显示的图片资源
         */
        public Builder setErrorImageResId(int errorImageResId) {
            this.errorImageResId = errorImageResId;
            return this;
        }

        /**
         * get同步网络请求
         */
        public <T> T get(Class<T> clazz) throws IOException {
            OkHttpRequest request = new OkHttpGetRequest(url, tag, params, headers);
            return request.invoke(clazz);
        }

        /**
         * get异步网络请求
         */
        public OkHttpRequest get(ResultCallBack callBack) {
            OkHttpRequest request = new OkHttpGetRequest(url, tag, params, headers);
            request.invokeAsyn(callBack);
            return request;
        }

        /**
         * post同步网络请求
         */
        public <T> T post(Class<T> clazz) throws IOException {
            OkHttpRequest request = new OkHttpPostRequest(url, tag, params, headers, content, mediaType, file, bytes);
            return request.invoke(clazz);
        }

        /**
         * post异步网络请求
         */
        public OkHttpRequest post(ResultCallBack callback) {
            OkHttpRequest request = new OkHttpPostRequest(url, tag, params, headers, content, mediaType, file, bytes);
            request.invokeAsyn(callback);
            return request;
        }

        /**
         * 上传文件
         */
        public OkHttpRequest uploadFile(ResultCallBack callback) {
            OkHttpRequest request = new OkHttpUploadFileRequest(url, tag, params, headers, files);
            request.invokeAsyn(callback);
            return request;
        }

        /**
         * 下载文件
         */
        public OkHttpRequest downloadFile(ResultCallBack callback) {
            OkHttpRequest request = new OkHttpDownloadFileRequest(url, tag, params, headers, saveFileDir, saveFileName);
            request.invokeAsyn(callback);
            return request;
        }

        /**
         * 给某个控件设置网络图片
         */
        public OkHttpRequest displayImageView(ResultCallBack callback) {
            OkHttpRequest request = new OkHttpDisplayImgRequest(url, tag, params, headers, mImageView, errorImageResId);
            request.invokeAsyn(callback);
            return request;
        }

    }

    /**
     * 异步请求
     */
    protected void invokeAsyn(ResultCallBack callBack) {
        //完成请求前的准备工作
        prepareInvoked(callBack);
        //调用封装好的请求网络的方法
        mOkHttpManager.execute(mRequest, callBack);
    }

    protected <T> T invoke(Class<T> clazz) throws IOException {
        mRequestBody = buildRequestBody();
        Request request = buildRequest(mRequestBody);
        return mOkHttpManager.execute(request, clazz);
    }

    /**
     * 请求前的初始化工作
     */
    protected void prepareInvoked(ResultCallBack callBack) {
        //拼接请求体
        mRequestBody = buildRequestBody();
        //对请求体进行包装，可以在包装的时候进行进度的计算
        mRequestBody = wrapRequestBody(mRequestBody, callBack);
        //初始化请求链接，如添加链接、请求体、请求头等等
        mRequest = buildRequest(mRequestBody);
    }

    protected RequestBody wrapRequestBody(RequestBody mRequestBody, ResultCallBack callBack) {
        return mRequestBody;
    }

    /**
     * 拼接请求头
     */
    protected void appendHeaders(Request.Builder builder, HashMap<String, String> headers) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not be empty!");
        }
        //创建请求头对象
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) {
            return;
        }
        //向请求中加入头信息
        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }


}
