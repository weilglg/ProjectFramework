package com.wll.main.util.http.request.get;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wll.main.util.ImageUtils;
import com.wll.main.util.http.callback.ResultCallBack;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 用于下载图片的网络请求
 * Created by wll on 2015/11/19.
 */
public class OkHttpDisplayImgRequest extends OkHttpGetRequest {


    private ImageView mImageView;
    private int errorImgResId;

    public OkHttpDisplayImgRequest(String url, String tag, HashMap<String, String> params, HashMap<String, String> headers, ImageView imageView, int errorImgResId) {
        super(url, tag, params, headers);
        this.mImageView = imageView;
        this.errorImgResId = errorImgResId;
    }

    @Override
    protected void invokeAsyn(final ResultCallBack callBack) {
        prepareInvoked(callBack);

        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                setErrorImageResId();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream inputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    //获取下载的图片的宽高
                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(inputStream);
                    //获得控件的宽高
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(mImageView);
                    //计算压缩比率
                    int sampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try {
                        inputStream.reset();
                    } catch (IOException e) {
                        response = getInputStream();
                        inputStream = response.body().byteStream();
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = sampleSize;

                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                    mOkHttpManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                    mOkHttpManager.
                            sendSuccessResultCallBack(mRequest, callBack);
                } catch (IOException e) {
                    mOkHttpManager.sendFailureResultCallBack(mRequest, e, callBack);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        });
    }

    private void setErrorImageResId() {
        mOkHttpManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageResource(errorImgResId);
            }
        });
    }

    @Override
    protected <T> T invoke(Class<T> clazz) throws IOException {
        return null;
    }

    private Response getInputStream() throws IOException {
        Call call = mOkHttpClient.newCall(mRequest);
        return call.execute();
    }
}
