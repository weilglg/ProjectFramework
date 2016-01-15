package com.wll.main.util.http.request.post;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.wll.main.util.http.body.CountingRequestBody;
import com.wll.main.util.http.callback.ResultCallBack;
import com.wll.main.util.http.request.OkHttpRequest;

import java.io.File;
import java.util.HashMap;

/**
 * Post方式请求网络
 * Created by wll on 2015/11/17.
 */
public class OkHttpPostRequest extends OkHttpRequest {
    /**
     * 文本内容
     */
    private String content;
    /**
     * 请求参数的类型
     */
    private MediaType mediaType;
    /**
     * 字节数组
     */
    private byte[] bytes;
    /**
     * 文件
     */
    private File file;

    private int type = 0;
    private static final int TYPE_PARAMS = 1;
    private static final int TYPE_STRING = 2;
    private static final int TYPE_BYTES = 3;
    private static final int TYPE_FILE = 4;

    private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");
    //默认字符串类型请求
    private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");


    public OkHttpPostRequest(String url, String tag, HashMap<String, String> params, HashMap<String, String> headers, String content, MediaType mediaType, File file, byte[] bytes) {
        super(url, tag, params, headers);
        this.content = content;
        this.mediaType = mediaType;
        this.file = file;
        this.bytes = bytes;
    }

    /**
     * 根据传入的参数判断请求参数类型（键值对、字符串）
     */
    protected void validParams() {
        int count = 0;
        if (params != null && !params.isEmpty()) {
            type = TYPE_PARAMS;
            count++;
        }
        if (content != null) {
            type = TYPE_STRING;
            count++;
        }
        if (bytes != null) {
            type = TYPE_BYTES;
            count++;
        }
        if (file != null) {
            type = TYPE_FILE;
            count++;
        }
        if (count <= 0 || count > 1) {
            throw new IllegalArgumentException("the params , content , file , bytes must has one and only one .");
        }
    }

    /**
     * 向请求中添加请求数据
     */
    private void addParams(FormEncodingBuilder builder, HashMap<String, String> params) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not be null .");
        }
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can not be empty!");
        }
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder, headers);
        builder.url(url)//设置连接地址
                .tag(tag)//给当前请求链接设置标签
                .post(requestBody);//设置请求体
        return builder.build();
    }


    /**
     * 根据不同的请求类型创建不同的请求体
     */
    @Override
    protected RequestBody buildRequestBody() {
        validParams();
        RequestBody requestBody = null;
        //根据不同的请求类型设置不同的请求体
        switch (type) {
            case TYPE_PARAMS:
                FormEncodingBuilder builder = new FormEncodingBuilder();
                addParams(builder, params);
                requestBody = builder.build();
                break;
            case TYPE_BYTES:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STREAM, bytes);
                break;
            case TYPE_FILE:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STREAM, file);
                break;
            case TYPE_STRING:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STRING, content);
                break;
        }
        return requestBody;
    }

    /**
     * 根据请求体计算进度
     */
    @Override
    protected RequestBody wrapRequestBody(RequestBody mRequestBody, final ResultCallBack callBack) {
        CountingRequestBody countingRequestBody = new CountingRequestBody(mRequestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                mOkHttpManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.inProgress((int) (bytesWritten * 1.0f / contentLength));
                    }
                });
            }
        });
        return countingRequestBody;
    }
}
