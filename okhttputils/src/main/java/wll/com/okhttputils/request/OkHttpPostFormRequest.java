package wll.com.okhttputils.request;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import wll.com.okhttputils.OkHttpUtil;
import wll.com.okhttputils.builder.OkHttpPostFormBuilder;
import wll.com.okhttputils.callback.Callback;

/**
 * Created by WLL on 2016/2/19.<p/>
 * 表单形式的请求
 */
public class OkHttpPostFormRequest extends OkHttpRequest {

    private List<OkHttpPostFormBuilder.FileInput> files;

    public OkHttpPostFormRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers,
                                 List<OkHttpPostFormBuilder.FileInput> files) {
        super(url, tag, params, headers);
        this.files = files;
    }

    @Override
    protected RequestBody buildRequestBody() {
        // 如果没有文件就不行进行文件参数的设置
        if (files == null || files.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        } else {

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            // 将其他类型的参数设置到请求体中
            addParams(builder);
            // 将文件参数设置到请求体中
            for (int i = 0; i < files.size(); i++) {
                OkHttpPostFormBuilder.FileInput fileInput = files.get(i);
                RequestBody requestBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.fileName)),
                        fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.fileName, requestBody);
            }
            return builder.build();
        }
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }


    @Override
    protected RequestBody warpRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback == null) return requestBody;
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody
                .Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {

                OkHttpUtil.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten * 1.0f / contentLength);
                    }
                });

            }
        });
        return countingRequestBody;
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody
                        .create(null, params.get(key)));
            }
        }
    }

    /**
     * 将参数添加到请求体中
     *
     * @param builder
     */
    private void addParams(FormBody.Builder builder) {
        if (params == null || params.isEmpty()) {
            builder.add("1", "1");
            return;
        }
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (files != null) {
            for (OkHttpPostFormBuilder.FileInput file : files) {
                sb.append(file.toString() + "  ");
            }
        }
        return sb.toString();
    }

}
