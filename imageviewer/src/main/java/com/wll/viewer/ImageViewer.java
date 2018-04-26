package com.wll.viewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.wll.viewer.config.ViewerConfig;
import com.wll.viewer.data.ViewData;
import com.wll.viewer.view.ImagePreviewActivity;

import java.util.ArrayList;

/**
 * Created by Admin on 2018/1/29.
 */

public class ImageViewer {
    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<ViewData> mViewDatas;
    private ArrayList<String> mImageDatas;
    private int mBeginIndex;
    private int mIndexPos;
    private boolean isShowProgress;
    private static RequestOptions mOptions;
    private static Bitmap mBeginImage;
    private static Drawable mProgressDrawable;

    private ImageViewer() {
        this.mBeginIndex = 0;
        this.mIndexPos = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.isShowProgress = true;
        this.mOptions = new RequestOptions()
                .placeholder(R.drawable.img_viewer_placeholder)
                .error(R.drawable.img_viewer_error);
        this.mBeginImage = null;
        this.mProgressDrawable = null;
    }

    public static ImageViewer newInstance() {
        return new ImageViewer();
    }

    public ImageViewer beginIndex(@NonNull int index) {
        this.mBeginIndex = index;
        return this;
    }
    public ImageViewer beginView(ImageView view) {
        view.buildDrawingCache(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        view.setDrawingCacheEnabled(false);
        this.mBeginImage = bitmap;
        return this;
    }

    public ImageViewer viewData(@NonNull ArrayList<ViewData> viewDatas) {
        this.mViewDatas = viewDatas;
        return this;
    }

    public ImageViewer imageData(@NonNull ArrayList<String> imageData) {
        this.mImageDatas = imageData;
        return this;
    }

    public ImageViewer indexPos(int pos) {
        this.mIndexPos = pos;
        return this;
    }

    public ImageViewer options(RequestOptions options) {
        this.mOptions = options;
        return this;
    }

    public ImageViewer showProgress(boolean isShow) {
        this.isShowProgress = isShow;
        return this;
    }

    public ImageViewer progressDrawable(@NonNull Drawable drawable) {
        this.mProgressDrawable = drawable;
        return this;
    }

    public void show(@NonNull Context context) {
        if (mImageDatas == null || mImageDatas.size() == 0 || mViewDatas == null || mViewDatas.size() == 0) {
            Log.w(TAG, "ImageDatas or ViewDatas is null or length 0");
            return;
        }
        if (mViewDatas.size() < mImageDatas.size()) {
            Log.w(TAG, "ViewDatas is less than ImageDatas in length");
            return;
        }
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(ViewerConfig.BEGIN_INDEX, mBeginIndex);
        intent.putExtra(ViewerConfig.VIEW_ARRAY, mViewDatas);
        intent.putExtra(ViewerConfig.IMAGE_ARRAY, mImageDatas);
        intent.putExtra(ViewerConfig.INDEX_GRAVITY, mIndexPos);
        intent.putExtra(ViewerConfig.SHOW_PROGRESS, isShowProgress);
        context.startActivity(intent);
    }

    public static RequestOptions getOptions() {
        return mOptions;
    }

    public static Bitmap getBeginImage() {
        return mBeginImage;
    }

    public static void setBeginImage(Bitmap beginImage) {
        mBeginImage = beginImage;
    }

    public static Drawable getProgressDrawable() {
        return mProgressDrawable;
    }
}
