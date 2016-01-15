package com.wll.main.util.http.request.get;

import android.text.TextUtils;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wll.main.util.http.OkHttpManager;
import com.wll.main.util.http.callback.ResultCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 下载文件的网络请求
 * Created by wll on 2015/11/18.
 */
public class OkHttpDownloadFileRequest extends OkHttpGetRequest {
    /**
     * 下载的文件的名称
     */
    private String saveFileName;
    /**
     * 保存下载文件的路径
     */
    private final String saveFileDir;

    public OkHttpDownloadFileRequest(String url, String tag, HashMap<String, String> params, HashMap<String, String> headers, String saveFileDir, String saveFileName) {
        super(url, tag, params, headers);
        this.saveFileDir = saveFileDir;
        this.saveFileName = saveFileName;
    }

    /**
     * 异步下载的方法，带有回调函数，可以在回调函数中更新UI
     */
    @Override
    protected void invokeAsyn(final ResultCallBack callBack) {
        prepareInvoked(callBack);
        Call call = mOkHttpClient.newCall(mRequest);
        //将当前请求加入请求队列中
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                OkHttpManager.getInstance().sendFailureResultCallBack(request, e, callBack);
            }

            @Override
            public void onResponse(Response response) {
                try {
                    //保存后台原地的数据
                    String filePath = saveFile(response, callBack);
                    //下载成功后调用成功回调函数
                    if (!TextUtils.isEmpty(filePath)) {
                        OkHttpManager.getInstance().sendSuccessResultCallBack(filePath, callBack);
                    } else {
                        OkHttpManager.getInstance().sendFailureResultCallBack(response.request(), new NullPointerException("download file is null ."), callBack);
                    }
                } catch (Exception e) {
                    OkHttpManager.getInstance().sendFailureResultCallBack(response.request(), e, callBack);
                }
            }
        });
    }

    /**
     * 同步下载的方法，没有回调函数
     */
    @Override
    public <T> T invoke(Class<T> clazz) throws IOException {
        final Call call = mOkHttpClient.newCall(mRequest);
        Response response = call.execute();
        return (T) saveFile(response, null);
    }

    /**
     * 完成下载操作，将文件能保存到指定的位置
     */
    private String saveFile(Response response, final ResultCallBack callBack) {
        InputStream inputStream = null;
        byte[] bytes = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;
        try {
            //获得输入流
            inputStream = response.body().byteStream();
            //获得下载内容的长度
            final long total = response.body().contentLength();
            long sum = 0;
            //判断保存文件的目录是否存在，不存在则创建
            File dir = new File(saveFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //创建文件
            if (TextUtils.isEmpty(saveFileName)) {
                saveFileName = getFileName(url);
            }
            File file = new File(dir, saveFileName);
            //创建输出流
            fileOutputStream = new FileOutputStream(file);
            if ((len = inputStream.read(bytes)) != -1) {
                //已经写入的文件的长度
                sum += len;
                //将读取到的字节写入文件
                fileOutputStream.write(bytes, 0, len);
                //判断回调函数是否为空，不为空的情况下将进度传给回调函数
                if (callBack != null) {
                    final long finalSum = sum;
                    mOkHttpManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //计算下载的进度，并传入回调函数
                            callBack.inProgress(finalSum * 1.0f / total);
                        }
                    });
                }
            }
            fileOutputStream.flush();
            //将下载的文件的保存路径返回
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭输入流
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
            try {
                //关闭输出流
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

}
