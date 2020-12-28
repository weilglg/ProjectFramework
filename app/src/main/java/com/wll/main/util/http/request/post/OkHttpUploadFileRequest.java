package com.wll.main.util.http.request.post;

import android.util.Pair;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * 上传文件的网络请求
 * Created by wll on 2015/11/18.
 */
public class OkHttpUploadFileRequest extends OkHttpPostRequest {
    /**
     * 需要上传的文件的数组
     */
    private final Pair<String, File>[] files;

    public OkHttpUploadFileRequest(String url, String tag, HashMap<String, String> params, HashMap<String, String> headers, Pair<String, File>[] files) {
        super(url, tag, params, headers, null, null, null, null);
        this.files = files;
    }

    /**
     * 拼接表单数据（模拟浏览器的表单提交）
     */
    public void addMultipartParams(MultipartBuilder builder, HashMap<String, String> params) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not be null .");
        }
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));

            }
        }
    }


    @Override
    protected void validParams() {
        if (files == null && (params == null || params.isEmpty())) {
            throw new IllegalArgumentException("params and files can't both null in upload request .");
        }
    }

    /**
     * 获取指定文件名的 mime 类型
     */
    private String guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    @Override
    protected RequestBody buildRequestBody() {
        validParams();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        addMultipartParams(builder, params);
        RequestBody requestBody = null;
        //根据要上传的文件Key和Value，创建请求体
        for (int i = 0; i < files.length; i++) {
            Pair<String, File> filePair = files[i];
            String fileKeyName = filePair.first;
            File file = filePair.second;
            String fileName = file.getName();
            requestBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
            //根据文件名设置contentType
            builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"" + fileKeyName + "\"; filename=\"" + fileName + "\""),
                    requestBody);
        }
        return builder.build();
    }


}
