package com.wll.viewer.view;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.wll.viewer.ImageViewer;
import com.wll.viewer.R;
import com.wll.viewer.config.ViewerConfig;
import com.wll.viewer.data.ViewData;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wll on 2018/1/29.
 * <p>
 * 图片查看类
 */

public class ImagePreviewActivity extends FragmentActivity {

    private String TAG = this.getClass().getSimpleName();

    private View backBgView;
    private ViewPager viewPager;
    private ImageView previewShow;
    private TextView previewIndex;
    /**
     * 点击的图片对应的item的位置以及大小信息
     */
    private ArrayList<ViewData> mViewDataList;
    /**
     * 图片数据源
     */
    private ArrayList<Object> mImageSrcList;
    /**
     * 点击的图片的Position
     */
    private int mBeginIndex;
    /**
     * 图片数量显示的位置
     */
    private int mIndexPos;
    /**
     * 是否显示进度条
     */
    private boolean isShowProgress;
    /**
     * 当前显示的图片信息
     */
    private ViewData mCurViewData;

    private boolean isBeginLoaded;
    /**
     * 是否已经显示缩放的图片
     */
    private boolean isShowLoaded;

    private ArrayList<View> mViewList;
    /**
     * 图片Bitmap的缓存
     */
    private HashMap<Integer, SoftReference<Bitmap>> mImageCache;
    /**
     * 图片加载失败的下标
     */
    private LinkedList<String> mLoadFailArray;

    private Point mScreenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initViews();
        initListener();
        handleIntent(getIntent());
    }

    private void initViews() {
        backBgView = findViewById(R.id.v_preview_bg);
        viewPager = findViewById(R.id.vp_preview);
        previewShow = findViewById(R.id.iv_preview_show);
        previewIndex = findViewById(R.id.tv_preview_index);

        //设置为全透明
        backBgView.setAlpha(0);
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //初始化数据
    private void handleIntent(Intent intent) {
        if (intent == null) {
            Log.w(this.getClass().getSimpleName(), "The intent is null");
            return;
        }
        mViewDataList = (ArrayList<ViewData>) intent.getSerializableExtra(ViewerConfig.VIEW_ARRAY);
        mImageSrcList = (ArrayList<Object>) intent.getSerializableExtra(ViewerConfig.IMAGE_ARRAY);
        mBeginIndex = intent.getIntExtra(ViewerConfig.BEGIN_INDEX, 0);
        mIndexPos = intent.getIntExtra(ViewerConfig.INDEX_GRAVITY, Gravity.TOP);
        isShowProgress = intent.getBooleanExtra(ViewerConfig.SHOW_PROGRESS, true);


        isBeginLoaded = false;
        isShowLoaded = false;
        mScreenSize = getScreenSize(this);
        //获得点击的图片的信息
        mCurViewData = mViewDataList.get(mBeginIndex);
        mImageCache = new HashMap<>();
        mLoadFailArray = new LinkedList<>();
        mViewList = new ArrayList<>();


        //给ViewPager设置数据
        initViewPager();

        //初始化页标信息
        initIndexView();

        showZoomView();

    }

    /**
     * 初始化页面打开时显示的缩放控件
     */
    private void showZoomView() {
        previewShow.setLayoutParams(new FrameLayout.LayoutParams((int) mCurViewData.width, (int) mCurViewData.height));
        previewShow.setX(mCurViewData.x);
        previewShow.setY(mCurViewData.y);
        previewShow.setVisibility(View.GONE);
        if (mImageCache.get(mBeginIndex) != null && mImageCache.get(mBeginIndex).get() != null) {
            previewShow.setImageBitmap(mImageCache.get(mBeginIndex).get());
            isShowLoaded = true;
            fullScreen();
        } else {
            loadImage(mBeginIndex, mImageSrcList.get(mBeginIndex), previewShow, true);
        }
    }

    /**
     * 初始化显示的数量信息
     */
    private void initIndexView() {
        String indexStr = (mBeginIndex + 1) + "/" + mImageSrcList.size();
        previewIndex.setText(indexStr);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) previewIndex.getLayoutParams();
        lp.gravity = mIndexPos;
        previewIndex.setLayoutParams(lp);
    }

    /**
     * 初始化ViewPager的数据
     */
    private void initViewPager() {
        for (int i = 0; i < mImageSrcList.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.item_image_viewer, null, false);
            PhotoView photoView = view.findViewById(R.id.photoVi_item_imgViewer);
            if (mImageCache.get(i) != null && mImageCache.get(i).get() != null) {
                photoView.setImageBitmap(mImageCache.get(i).get());
            } else {
                loadImage(i, mImageSrcList.get(i), photoView, false);
            }
            photoView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    restoreImage();
                }
            });
            mViewList.add(view);
        }

        viewPager.setAdapter(new SimpleAdapter(mViewList));
        viewPager.setCurrentItem(mBeginIndex);
        viewPager.setVisibility(View.GONE);
    }

    private void loadImage(final int index, final Object src, final ImageView view, final boolean isShow) {
        final RequestBuilder builder = Glide.with(this).asBitmap().load(src).transition(BitmapTransitionOptions.withCrossFade());
        if (ImageViewer.getOptions() != null) {
            if (index == mBeginIndex && isShow) {
                builder.apply(ImageViewer.getOptions().priority(Priority.IMMEDIATE));
            } else {
                builder.apply(ImageViewer.getOptions().priority(Priority.NORMAL));
            }
            builder.apply(ImageViewer.getOptions());
        }
        builder.into(new ImageViewTarget<Bitmap>(view) {

            @Override
            protected void setResource(@Nullable Bitmap resource) {

            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                //开始加载图片时判断是否需要显示进度条，如果需要则显示进度条控件
                ProgressBar progressBar = (ProgressBar) mViewList.get(index).findViewById(R.id.proBar_item_imgViewer);
                if (isShowProgress) {
                    if (ImageViewer.getProgressDrawable() != null) {
                        progressBar.setIndeterminateDrawable(ImageViewer.getProgressDrawable());
                    }
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                //加载失败的时，显示错误图片
                mImageCache.put(index, new SoftReference<Bitmap>(drawableToBitmap(errorDrawable)));
                //隐藏加载动画布局
                mViewList.get(index).findViewById(R.id.proBar_item_imgViewer).setVisibility(View.GONE);

                if (index == mBeginIndex) {
                    if (isShow) {
                        isShowLoaded = true;
                        if (!isBeginLoaded) {
                            PhotoView photoView = (PhotoView) mViewList.get(index).findViewById(R.id.photoVi_item_imgViewer);
                            Glide.with(ImagePreviewActivity.this).clear(photoView);
                            photoView.setImageDrawable(errorDrawable);
                        }
                        fullScreen();
                    } else {
                        isBeginLoaded = true;
                    }
                }
                mLoadFailArray.add(index + "");
            }

            @Override
            public void onResourceReady(Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);

                Log.e(TAG, "==============url===============" + src.toString());
                Log.e(TAG, "==============onResourceReady===============" + resource.getWidth());
                Log.e(TAG, "==============onResourceReady===============" + resource.getHeight());
                //加载成功对图片进行缓存
                mImageCache.put(index, new SoftReference<Bitmap>(resource));
                mViewList.get(index).findViewById(R.id.proBar_item_imgViewer).setVisibility(View.GONE);

                if (index == mBeginIndex) {
                    if (isShow) {
                        isShowLoaded = true;
                        if (!isBeginLoaded) {
                            PhotoView photoView = (PhotoView) mViewList.get(index).findViewById(R.id.photoVi_item_imgViewer);
                            Glide.with(ImagePreviewActivity.this).clear(photoView);
                            photoView.setImageBitmap(resource);
                        }
                        fullScreen();
                    } else {
                        isBeginLoaded = true;
                    }
                }
                if (mLoadFailArray.contains(index + "")) {
                    mLoadFailArray.remove(index + "");
                }
            }
        });
    }

    /**
     * 显示放大效果
     */
    private void fullScreen() {
        //原图像的宽度
        float ori_w = 1280;
        float ori_h = 720;
        // 获取原有Drawable
        Drawable drawable = previewShow.getDrawable();
        if (drawable != null) {//当Drawable不为空时获取图片的宽高
            ori_w = drawable.getIntrinsicWidth();
            ori_h = drawable.getIntrinsicHeight();
        } else if (mImageCache.get(mBeginIndex) != null && mImageCache.get(mBeginIndex).get() != null) {//缩放控件的Drawable为空时查看是否存在缓存
            //有缓存则给缩放控件设置图片
            previewShow.setImageBitmap(mImageCache.get(mBeginIndex).get());
            drawable = previewShow.getDrawable();
            ori_w = drawable.getIntrinsicWidth();
            ori_h = drawable.getIntrinsicHeight();
        } else {
            Log.w(TAG, "The width and length of the image were not obtained");
        }
        //计算原图跟手机屏幕的宽高比例并以最小的为主
        float scale = Math.min((mScreenSize.x / ori_w), (mScreenSize.y / ori_h));
        //获取缩略图的宽高
        final float cur_w = mCurViewData.width;
        final float cur_h = mCurViewData.height;
        //计算放大后的图片的宽高
        final float img_w = ori_w * scale;
        final float img_h = ori_h * scale;
        //设置缩放动画的起始位置
        final float from_x = mCurViewData.x;
        final float from_y = mCurViewData.y;
        //计算缩放动画的结束位置
        final float to_x = (mScreenSize.x - img_w) / 2;
        final float to_y = (mScreenSize.y - img_h) / 2;


        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator floatEvaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                float fraction = animatedValue / 100f;
                Log.e(TAG, "--->>>fraction = " + fraction);
                if (fraction == 0) {
                    previewShow.setScaleType(ImageView.ScaleType.FIT_XY);
                    previewShow.setVisibility(View.VISIBLE);
                }
                float width = floatEvaluator.evaluate(fraction, cur_w, img_w);
                float height = floatEvaluator.evaluate(fraction, cur_h, img_h);
                float x = floatEvaluator.evaluate(fraction, from_x, to_x);
                float y = floatEvaluator.evaluate(fraction, from_y, to_y);

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) previewShow.getLayoutParams();
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                previewShow.setLayoutParams(layoutParams);
                previewShow.setX(x);
                previewShow.setY(y);
                backBgView.setAlpha(fraction);

            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG, "===================分割线=======================");
                previewShow.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setVisibility(View.VISIBLE);
                        previewIndex.setVisibility(View.VISIBLE);
                        previewShow.setVisibility(View.GONE);
                    }
                }, 100);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(500);
        animator.start();
    }

    /**
     * 显示缩小效果
     */
    private void restoreImage() {

    }

    private Point getScreenSize(Context context) {
        return new Point(getScreenWidth(this), getScreenHeight(this));
    }

    /**
     * 获取手机屏幕的宽度
     */
    private int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获得手机屏幕的高度
     */
    private int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 通过Drawable创建Bitmap
     */
    private Bitmap drawableToBitmap(Drawable errorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                errorDrawable.getIntrinsicWidth(),
                errorDrawable.getIntrinsicHeight(),
                errorDrawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        errorDrawable.setBounds(0, 0, errorDrawable.getIntrinsicWidth(), errorDrawable.getIntrinsicHeight());
        errorDrawable.draw(canvas);
        return bitmap;
    }

}
