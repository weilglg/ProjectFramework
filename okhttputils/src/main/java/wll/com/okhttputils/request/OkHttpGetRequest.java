package wll.com.okhttputils.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by WLL on 2016/2/19.<p/>
 * GET方式的请求
 */
public class OkHttpGetRequest extends OkHttpRequest {
    public OkHttpGetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);

    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }
}
