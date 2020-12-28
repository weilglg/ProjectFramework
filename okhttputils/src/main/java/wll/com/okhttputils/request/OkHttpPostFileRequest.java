package wll.com.okhttputils.request;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by WLL on 2016/2/22.
 */
public class OkHttpPostFileRequest extends OkHttpRequest {
    // 默认文件类型
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private MediaType mediaType;
    private File file;

    public OkHttpPostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers,
                                 File file, MediaType mediaType) {
        super(url, tag, params, headers);
        this.file = file;
        this.mediaType = mediaType;
        if (this.file == null) {
            new IllegalArgumentException("the file can not be null !");
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, file);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    @Override
    public String toString() {
        return super.toString() + ", requestBody{uploadfilePath=" + file.getAbsolutePath() + "} ";
    }

}
