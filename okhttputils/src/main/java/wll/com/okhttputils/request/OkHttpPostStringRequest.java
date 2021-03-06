package wll.com.okhttputils.request;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by WLL on 2016/2/19.
 */
public class OkHttpPostStringRequest extends OkHttpRequest {

    /**
     * 编码格式
     */
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private String content;
    private MediaType mediaType;

    public OkHttpPostStringRequest(String url, Object tag, Map<String, String> params, Map<String, String>
            headers, String content, MediaType mediaType) {
        super(url, tag, params, headers);
        this.content = content;
        this.mediaType = mediaType;
        if (this.content == null) {
            new IllegalArgumentException("the content can not be null !");
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_PLAIN;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    @Override
    public String toString() {
        return super.toString() + ", requestBody{content=" + content + "}";
    }
}
