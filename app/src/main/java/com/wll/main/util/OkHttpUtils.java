package com.wll.main.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wll.main.util.http.callback.LoaderImageListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by wll on 2015/11/4.
 * 网络请求类，修改网络上zhy提供的访问网络的工具类
 */
public class OkHttpUtils {
    private static OkHttpUtils mHttpUtil;

    private OkHttpClient mOkHttpClient;

    private Handler mDelivery;

    private Gson mGson;

    private PostDelegate mPostDelegate;

    private GetDelegate mGetDelegate;

    private DownloadDelegate mDownloadDelegate;

    private UploadDelegate mUploadDelegate;

    private DisplayImageDelegate mDisplayImageDelegate;

    public static OkHttpUtils getInstance() {
        if (mHttpUtil == null) {
            synchronized (OkHttpUtils.class) {
                if (mHttpUtil == null) {
                    mHttpUtil = new OkHttpUtils();
                }
            }
        }
        return mHttpUtil;
    }

    public OkHttpUtils() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
        mPostDelegate = new PostDelegate();
        mGetDelegate = new GetDelegate();
        mUploadDelegate = new UploadDelegate();
        mDownloadDelegate = new DownloadDelegate();
        mDisplayImageDelegate = new DisplayImageDelegate();
    }

    private PostDelegate getPostDelegate() {
        return mPostDelegate;
    }

    private GetDelegate getGetDelegate() {
        return mGetDelegate;
    }

    private UploadDelegate getUploadDelegate() {
        return mUploadDelegate;
    }

    private DownloadDelegate getDownloadDelegate() {
        return mDownloadDelegate;
    }


    private DisplayImageDelegate getDisplayImageDelegate() {
        return mDisplayImageDelegate;
    }


    //-------------------------------------------对外提供的方法方便调用-------------------------------

    /**
     * 取消某个请求
     */
    public static void cancelTag(Object tag) {
        getInstance()._cancelTag(tag);
    }


    //--------------------------------便利的Post请求的方法--------------------------------------------

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数
     */
    public static Response post(String url, Param[] params) throws IOException {
        return post(url, params, null);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数
     * @param tag    标签
     */
    public static Response post(String url, Param[] params, Object tag) throws IOException {
        return getInstance().getPostDelegate()._post(url, params, tag);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数
     * @return 字符串
     */
    public static String postString(String url, Param[] params) throws IOException {
        return postString(url, params, null);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数
     * @param tag    标签
     * @return 字符串
     */
    public static String postString(String url, Param[] params, Object tag) throws IOException {
        return getInstance().getPostDelegate()._postString(url, params, tag);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数
     */
    public static void postAsyn(String url, ResultCallback callback, Param[] params) {
        postAsyn(url, callback, params, null);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数
     * @param tag    标签
     */
    public static void postAsyn(String url, ResultCallback callback, Param[] params, Object tag) {
        getInstance().getPostDelegate()._postAsyn(url, callback, params, tag);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数(HashMap)
     */
    public static void postAsyn(String url, ResultCallback callback, HashMap<String, String> params) {
        postAsyn(url, callback, params, null);
    }

    /**
     * 同步的Post请求
     *
     * @param url    请求URL
     * @param params post的参数(HashMap)
     * @param tag    标签
     */
    public static void postAsyn(String url, ResultCallback callback, HashMap<String, String> params, Object tag) {
        Param[] paramArr = getInstance().mapToParams(params);
        postAsyn(url, callback, params, tag);
    }


    //--------------------------------便利的Get请求的方法--------------------------------------------

    /**
     * 同步的Get请求
     *
     * @param url 请求URL
     * @return 请求结果是字符串
     */
    public static String getString(String url) throws IOException {
        return getInstance().getGetDelegate().getString(url, null);
    }

    /**
     * 同步的Get请求
     *
     * @param url 请求URL
     * @param tag 标签
     * @return 请求结果是字符串
     */
    public static String getString(String url, Object tag) throws IOException {
        return getInstance().getGetDelegate().getString(url, tag);
    }

    /**
     * 异步的Get请求
     *
     * @param url      请求URL
     * @param callback 请求结果回调
     */
    public static void getAsyn(String url, final ResultCallback callback) {
        getInstance().getGetDelegate().getAsyn(url, callback, null);
    }

    /**
     * 异步的Get请求
     *
     * @param url      请求URL
     * @param callback 请求结果回调
     * @param tag      标签
     */
    public static void getAsyn(String url, final ResultCallback callback, Object tag) {
        getInstance().getGetDelegate().getAsyn(url, callback, tag);
    }


    //--------------------------------便利的调用下载的方法--------------------------------------------

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param tag         标签用于取消
     */
    public static void downloadAsyn(final String url, final String destFileDir, final ResultCallback callback, Object
            tag) {
        getInstance().getDownloadDelegate().downloadAsyn(url, destFileDir, callback, tag);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     */
    public static void downloadAsyn(final String url, final String destFileDir, final ResultCallback callback) {
        getInstance().getDownloadDelegate().downloadAsyn(url, destFileDir, callback, null);
    }


    //--------------------------------便利调用的上传的方法--------------------------------------------

    /**
     * 同步基于post的文件上传:上传单个文件
     */
    public static Response post(String url, String fileKey, File file, Object tag) throws IOException {
        return post(url, new String[]{fileKey}, new File[]{file}, null, tag);
    }

    /**
     * 同步基于post的文件上传:上传多个文件以及携带key-value对：主方法
     */
    public static Response post(String url, String[] fileKeys, File[] files, Param[] params, Object tag) throws
            IOException {
        return getInstance().getUploadDelegate().post(url, fileKeys, files, params, tag);
    }

    /**
     * 同步单文件上传
     */
    public static Response post(String url, String fileKey, File file, Param[] params, Object tag) throws IOException {
        return post(url, new String[]{fileKey}, new File[]{file}, params, tag);
    }

    /**
     * 异步基于post的文件上传:主方法
     */
    public static void postAsyn(String url, String[] fileKeys, File[] files, Param[] params, ResultCallback callback,
                                Object tag) {
        getInstance().getUploadDelegate().postAsyn(url, fileKeys, files, params, callback, tag);
    }

    /**
     * 异步基于post的文件上传:单文件不带参数上传
     */
    public static void postAsyn(String url, String fileKey, File file, ResultCallback callback, Object tag) throws
            IOException {
        postAsyn(url, new String[]{fileKey}, new File[]{file}, null, callback, tag);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     */
    public static void postAsyn(String url, String fileKey, File file, Param[] params, ResultCallback callback,
                                Object tag) {
        postAsyn(url, new String[]{fileKey}, new File[]{file}, params, callback, tag);

    }

    public static void displayImage(ImageView view, String url, int errorResId, Object tag) {
        getInstance().getDisplayImageDelegate().displayImage(view, url, errorResId, tag);
    }

    public static void displayImage(ImageView view, String url) {
        getInstance().getDisplayImageDelegate().displayImage(view, url, -1, null);
    }

    public static void displayImage(ImageView view, String url, Object tag) {
        getInstance().getDisplayImageDelegate().displayImage(view, url, -1, tag);
    }


    //------------------------------------------------GET请求--------------------------------

    public class GetDelegate {

        /**
         * 通用的Get请求操作方法
         */
        private Request buildGetRequest(String url, Object tag) {
            Request.Builder builder = new Request.Builder()
                    .url(url);

            if (tag != null) {
                builder.tag(tag);
            }

            return builder.build();
        }

        /**
         * 通用的Get请求方法
         */
        public Response get(Request request) throws IOException {
            Call call = mOkHttpClient.newCall(request);
            Response execute = call.execute();
            return execute;
        }

        /**
         * 同步的Get请求方法
         *
         * @param url 请求URL
         * @param tag 标签
         * @return Response
         * @throws IOException
         */
        public Response get(String url, Object tag) throws IOException {
            final Request request = buildGetRequest(url, tag);
            return get(request);
        }

        /**
         * 同步的Get请求
         *
         * @param url 请求URL
         * @param tag 标签
         * @return 请求结果是字符串
         * @throws IOException
         */
        public String getString(String url, Object tag) throws IOException {
            Response execute = get(url, tag);
            return execute.body().string();
        }

        /**
         * 通用的异步Get请求方法
         */
        public void getAsyn(Request request, ResultCallback callback) {
            deliveryResult(callback, request);
        }

        /**
         * 异步的Get请求
         *
         * @param url      请求URL
         * @param callback 请求结果回调
         * @param tag      标签
         */
        public void getAsyn(String url, final ResultCallback callback, Object tag) {
            final Request request = buildGetRequest(url, tag);
            getAsyn(request, callback);
        }
    }


    //--------------------------------------Post---------------------------------------------
    private class PostDelegate {

        /**
         * 同步的Post请求
         *
         * @param url    请求URL
         * @param params post的参数
         * @param tag    标签
         */
        public Response _post(String url, Param[] params, Object tag) throws IOException {
            Request request = buildPostFormRequest(url, params, tag);
            Response response = mOkHttpClient.newCall(request).execute();
            return response;
        }

        /**
         * 同步的Post请求
         *
         * @param url    请求URL
         * @param params post的参数
         * @return 字符串
         */
        private String _postString(String url, Param[] params, Object tag) throws IOException {
            Response response = _post(url, params, tag);
            return response.body().string();
        }

        /**
         * 异步的Post请求
         *
         * @param url      请求URL
         * @param callback 请求结果回调函数
         * @param params   post的参数
         * @param tag      标签
         */
        private void _postAsyn(String url, ResultCallback callback, Param[] params, Object tag) {
            Request request = buildPostFormRequest(url, params, tag);
            deliveryResult(callback, request);
        }

        /**
         * post构造Request的方法
         *
         * @param url    请求URL
         * @param params 参数集合
         * @param tag    标签
         */
        private Request buildPostFormRequest(String url, Param[] params, Object tag) {
            if (params == null) {
                params = new Param[0];
            }
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (Param param : params) {
                builder.add(param.key, param.value);
            }
            RequestBody requestBody = builder.build();

            Request.Builder reqBuilder = new Request.Builder();
            reqBuilder.url(url)
                    .post(requestBody);

            //设置标签
            if (tag != null) {
                reqBuilder.tag(tag);
            }
            return reqBuilder.build();
        }

    }

    //-----------------------------------------上传文件-----------------------------------

    private class UploadDelegate {

        /**
         * 同步基于post的文件上传:上传多个文件以及携带key-value对：主方法
         */
        public Response post(String url, String[] fileKeys, File[] files, Param[] params, Object tag) throws
                IOException {
            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * 异步基于post的文件上传:主方法
         */
        public void postAsyn(String url, String[] fileKeys, File[] files, Param[] params, ResultCallback callback,
                             Object tag) {
            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
            deliveryResult(callback, request);
        }


        /**
         * 上传文件的操作方法
         *
         * @param url      上传地址
         * @param files    File数组
         * @param fileKeys 文件相应的key
         * @param params   其他的一些参数
         * @param tag      标签
         */
        private Request buildMultipartFormRequest(String url, File[] files,
                                                  String[] fileKeys, Param[] params, Object tag) {
            if (params == null) {
                params = new Param[0];
            }

            MultipartBuilder builder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM);

            for (Param param : params) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                        RequestBody.create(null, param.value));
            }
            if (files != null) {
                RequestBody fileBody = null;
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String fileName = file.getName();
                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                    builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                }
            }
            RequestBody requestBody = builder.build();
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .tag(tag)
                    .build();
        }
    }

    //====================DisplayImageDelegate=======================

    /**
     * 加载图片相关
     */
    private class DisplayImageDelegate {
        /**
         * 加载图片
         */
        public void displayImage(final ImageView view, final String url, final int errorResId, final Object tag) {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    setErrorResId(view, errorResId);
                }

                @Override
                public void onResponse(Response response) {
                    InputStream is = null;
                    try {
                        is = response.body().byteStream();
                        ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                        ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                        int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                        try {
                            is.reset();
                        } catch (IOException e) {
                            response = mGetDelegate.get(url, tag);
                            is = response.body().byteStream();
                        }

                        BitmapFactory.Options ops = new BitmapFactory.Options();
                        ops.inJustDecodeBounds = false;
                        ops.inSampleSize = inSampleSize;
                        final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                        mDelivery.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setImageBitmap(bm);
                            }
                        });
                    } catch (Exception e) {
                        setErrorResId(view, errorResId);

                    } finally {
                        if (is != null) try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public void displayImage(String url, LoaderImageListener listener){
            displayImage(url, listener, null);
        }

        /**
         * 加载图片
         */
        public void displayImage(final String url, final LoaderImageListener listener, final Object tag) {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    listener.onError(request, e);
                }

                @Override
                public void onResponse(Response response) {
                    InputStream is = null;
                    try {
                        is = response.body().byteStream();
                        try {
                            is.reset();
                        } catch (IOException e) {
                            response = mGetDelegate.get(url, tag);
                            is = response.body().byteStream();
                        }
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        if (bm != null){
                            listener.onResponse(bm);
                        }else{
                            listener.onError(request, new NullPointerException("bitmap is null"));
                        }
                    } catch (Exception e) {
                        listener.onError(request, e);
                    } finally {
                        if (is != null) try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public void displayImage(final ImageView view, String url) {
            displayImage(view, url, -1, null);
        }

        public void displayImage(final ImageView view, String url, Object tag) {
            displayImage(view, url, -1, tag);
        }

        private void setErrorResId(final ImageView view, final int errorResId) {
            mDelivery.post(new Runnable() {
                @Override
                public void run() {
                    view.setImageResource(errorResId);
                }
            });
        }
    }


    //-------------------------------------------DownloadDelegate------------------------------------

    /**
     * 下载相关的模块
     */
    public class DownloadDelegate {
        /**
         * 异步下载文件
         *
         * @param url
         * @param destFileDir 本地文件存储的文件夹
         */
        public void downloadAsyn(final String url, final String destFileDir, final ResultCallback callback, Object
                tag) {
            final Request request = new Request.Builder()
                    .url(url)
                    .tag(tag)
                    .build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    sendFailedStringCallback(request, e, callback);
                }

                @Override
                public void onResponse(Response response) {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        is = response.body().byteStream();

                        File dir = new File(destFileDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, getFileName(url));
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        //如果下载文件成功，第一个参数为文件的绝对路径
                        sendSuccessResultCallback(file.getAbsolutePath(), callback);
                    } catch (IOException e) {
                        sendFailedStringCallback(response.request(), e, callback);
                    } finally {
                        try {
                            if (is != null) is.close();
                        } catch (IOException e) {
                        }
                        try {
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                        }
                    }

                }
            });
        }
    }


    //-------------------------------------------封装的一些共有操作方法------------------------

    //获得文件名称
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 将HashMap转化成数组形式
     */
    private Param[] mapToParams(HashMap<String, String> params) {
        if (params == null || params.size() == 0) {
            return new Param[0];
        }
        Param[] paramsArr = new Param[params.size()];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            paramsArr[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return paramsArr;
    }


    /**
     * 取消对应的网络请求
     */
    private void _cancelTag(Object tag) {
        mOkHttpClient.cancel(tag);
    }

    /**
     * 请求的处理方法
     */
    private void deliveryResult(ResultCallback callback, Request request) {

        if (callback == null) callback = DEFAULT_RESULT_CALLBACK;
        final ResultCallback resCallBack = callback;
        //UI thread
        callback.onBefore(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, resCallBack);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    final String string = response.body().string();
                    //根据设置的泛型对返回的json进行处理
                    if (resCallBack.mType == String.class) { //当传入的泛型是String时，直接将字符串返回
                        sendSuccessResultCallback(string, resCallBack);
                    } else {//当传入的泛型是自定义对象时，使用Gson工具对json进行处理
                        Object o = mGson.fromJson(string, resCallBack.mType);
                        sendSuccessResultCallback(o, resCallBack);
                    }
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, resCallBack);
                } catch (com.google.gson.JsonParseException e) {//Json解析的错误
                    sendFailedStringCallback(response.request(), e, resCallBack);
                }

            }
        });
    }

    /**
     * 请求成功时的处理
     */
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    /**
     * 请求失败时的处理
     */
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    private final ResultCallback<String> DEFAULT_RESULT_CALLBACK = new ResultCallback<String>() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String response) {

        }
    };

    /**
     * 该类封装的是请求时参数的键值对
     */
    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }


    /**
     * 请求结束后的回调函数
     *
     * @param <T> 返回数据的类型
     */
    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        /**
         * 请求开始时会调用这个方法，这个方法中可以进行弹出等待框等操作
         */
        public void onBefore(Request request) {
        }

        /**
         * 请求完成时会调用这个方法，这个方法中可以进行隐藏等待框等操作
         */
        public void onAfter() {
        }

        /**
         * 请求失败时会调用这个方法
         */
        public abstract void onError(Request request, Exception e);

        /**
         * 请求成功时会调用这个方法
         */
        public abstract void onResponse(T response);
    }


}
