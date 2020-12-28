package com.wll.main.util.http.request.get;

import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.wll.main.util.http.request.OkHttpRequest;

import java.util.HashMap;
import java.util.Set;

/**
 * Get方式请求网络
 * Created by wll on 2015/11/17.
 */
public class OkHttpGetRequest extends OkHttpRequest {

    private final static String TAG = OkHttpGetRequest.class.getSimpleName();

    public OkHttpGetRequest(String url, String tag, HashMap<String, String> params, HashMap<String, String> headers) {
        super(url, tag, params, headers);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can not be empty!");
        }
        //向请求链接地址后面追加参数
        url = addParams(url, params);
        Request.Builder builder = new Request.Builder();
        //拼接请求头
        appendHeaders(builder, headers);
        builder.url(url)
                .tag(tag);
        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    private String addParams(String url, HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            //获得所有的key
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
            //删除最后拼接的“&”
            sb.deleteCharAt(sb.length()  -1);
        }
        Log.e(TAG, "GET URL : " + sb.toString());
        return sb.toString();
    }
}