package com.wll.main.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wll.main.R;

import java.util.ArrayList;


/**
 * 广告轮播
 */

/**
 * 尽量避免在onCreate 里加载图片 避免程序无响应
 * <p/>
 * 游标是圆形还是长条，要是设置为1是长条，要是0就是圆形 默认是圆形 imageCycle.setImageResources(list,
 * listener,1); 实现ImageCycleViewListener 在displayImage 里 用imageload加载图片
 * ImageLoader.getInstance().displayImage(imageURL, imageView);
 *
 * @Override public void onPause() { super.onPause();
 * imageCycle.pushImageCycle(); }
 * @Override public void onDestroy() { super.onDestroy();
 * imageCycle.pushImageCycle(); }
 */

public class ImageCycleView extends RelativeLayout {
    public int stype = 1;
    /**
     * 图片轮播视图
     */
    private ViewPager mAdvPager = null;

    /**
     * 滚动图片视图适配器
     */
    private ImageCycleAdapter mAdvAdapter;

    /**
     * 图片轮播指示器控件
     */
    private LinearLayout mGroup;

    /**
     * 滚动图片指示器-视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mCurrentPagerPosition = 1;
    /**
     * 是否变化
     */
    private boolean mChanged;
    /**
     * 轮播图的数量
     */
    private int pageCount;
    /**
     * 轮播时间间隔
     */
    private static final int TIMER = 3000;

    private int timerTask = TIMER;

    private int indicatorItemPadding = 10;

    private int indicatorPaddingBottom = 5;


    public ImageCycleView(Context context) {
        this(context, null);
    }

    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initAdView();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageCycleView);
        indicatorItemPadding = (int) typedArray.getDimension(R.styleable.ImageCycleView_indicatorItemPadding, 0);
        indicatorPaddingBottom = (int) typedArray.getDimension(R.styleable.ImageCycleView_indicatorPaddingBottom, 0);
        timerTask = typedArray.getInt(R.styleable.ImageCycleView_timerTask, TIMER);
        typedArray.recycle();
    }

    private void initAdView() {
        //创建ViewPager
        mAdvPager = new ViewPager(getContext());
        addView(mAdvPager, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mAdvPager.addOnPageChangeListener(new GuidePageChangeListener());
        mAdvPager.setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视图
        mGroup = new LinearLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        mGroup.setOrientation(LinearLayout.HORIZONTAL);
        mGroup.setGravity(Gravity.CENTER);
        addView(mGroup, layoutParams);
    }

    /**
     * 装填图片数据
     */
    public void setImageResources(ArrayList<String> imageUrlList,
                                  ImageCycleViewListener imageCycleViewListener, int stype, int IndicatorPosition) {
        this.stype = stype;
        // 清除所有子视图
        mGroup.removeAllViews();
        if (imageUrlList == null) {
            return;
        }
        // 图片广告数量
        pageCount = imageUrlList.size();
        setIndicator(IndicatorPosition);
        mAdvAdapter = new ImageCycleAdapter(getContext(), imageUrlList,
                imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);
        mAdvPager.setCurrentItem(1);
        startImageTimerTask();
    }

    /**
     * 设置指示器图片
     */
    private void setIndicator(int IndicatorPosition) {
        if (pageCount <= 1) {
            return;
        }
        mImageViews = new ImageView[pageCount];
        //创建单个指示器图片
        for (int i = 0; i < pageCount; i++) {
            ImageView mImageView = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(indicatorItemPadding, indicatorItemPadding, indicatorItemPadding, indicatorPaddingBottom);
            mImageView.setScaleType(ScaleType.FIT_XY);
            mImageView.setLayoutParams(params);
            mImageViews[i] = mImageView;
            if (i == 0) {
                if (this.stype == 1)
                    mImageViews[i]
                            .setImageResource(R.mipmap.banner_dian_focus);
                else
                    mImageViews[i]
                            .setImageResource(R.mipmap.cicle_banner_dian_focus);
            } else {
                if (this.stype == 1)
                    mImageViews[i]
                            .setImageResource(R.mipmap.banner_dian_blur);
                else
                    mImageViews[i]
                            .setImageResource(R.mipmap.cicle_banner_dian_blur);
            }
            mGroup.addView(mImageViews[i]);

            //设置指示器的位置
            if (IndicatorPosition == 0) {
                mGroup.setGravity(Gravity.START);
                mGroup.setGravity(Gravity.LEFT);
                mGroup.setPadding(indicatorItemPadding, 0, 0, 0);
            } else if (IndicatorPosition == 2) {
                mGroup.setGravity(Gravity.RIGHT);
                mGroup.setGravity(Gravity.END);
                mGroup.setPadding(0, 0, indicatorItemPadding, 0);
            }

        }
    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 开始图片滚动任务
     */
    private void startImageTimerTask() {
        if (pageCount > 1) {
            stopImageTimerTask();
            // 图片每3秒滚动一次
            mHandler.postDelayed(mImageTimerTask, timerTask);
        }
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        if (pageCount > 1) {
            mHandler.removeCallbacks(mImageTimerTask);
        }
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (mImageViews != null) {
                mCurrentPagerPosition++;
                mAdvPager.setCurrentItem(mCurrentPagerPosition);
            }
        }
    };

    /**
     * 轮播图片状态监听器
     */
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (mChanged) {
                    mChanged = false;
                    mAdvPager.setCurrentItem(mCurrentPagerPosition, false);
                    // 开始图片滚动
                    startImageTimerTask();
                }
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mAdvAdapter.getCount() - 2 > 1) { // 当页数大于1时，才可以进行无限滑动的操作
                mChanged = true;
                if (position < 1) { // 首位之前，跳转到末尾（N）
                    mCurrentPagerPosition = mAdvAdapter.getPageCount();
                } else if (position > mAdvAdapter.getPageCount()) { // 末位之后，跳转到首位（1）
                    mCurrentPagerPosition = 1;
                } else {
                    mCurrentPagerPosition = position;
                }
                setImageBackground(mCurrentPagerPosition - 1);
            }
        }
    }

    /**
     * 设置指示器的图片
     */
    public void setImageBackground(int selectorPosition) {
        if (selectorPosition == -1 || mImageViews == null) {
            return;
        }
        if (stype == 1)
            mImageViews[selectorPosition]
                    .setImageResource(R.mipmap.banner_dian_focus);
        else
            mImageViews[selectorPosition]
                    .setImageResource(R.mipmap.cicle_banner_dian_focus);
        for (int i = 0; i < mImageViews.length; i++) {
            if (selectorPosition != i) {
                if (stype == 1)
                    mImageViews[i]
                            .setImageResource(R.mipmap.banner_dian_blur);
                else
                    mImageViews[i]
                            .setImageResource(R.mipmap.cicle_banner_dian_blur);
            }
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {

        private int pageCount;

        /**
         * 图片资源列表
         */
        private ArrayList<String> mAdList = new ArrayList<String>();

        /**
         * 广告图片点击监听器
         */
        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;

        public ImageCycleAdapter(Context context, ArrayList<String> adList,
                                 ImageCycleViewListener imageCycleViewListener) {
            mContext = context;
            if (adList != null) {
                pageCount = adList.size();
                mAdList.addAll(adList);
                if (adList.size() > 1) {
                    String firstUrl = adList.get(0);
                    String endUrl = adList.get(adList.size() - 1);
                    mAdList.add(0, endUrl);
                    mAdList.add(firstUrl);
                }
            }
            mImageCycleViewListener = imageCycleViewListener;
        }

        @Override
        public int getCount() {
            return mAdList.size();
        }

        public int getPageCount() {
            return pageCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            String imageUrl = mAdList.get(position);
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ScaleType.FIT_XY);
            // 设置图片点击监听
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (position > 0 && position <= pageCount && pageCount > 1) {
                        mImageCycleViewListener.onImageClick(position - 1, v);
                    } else if (pageCount == 1) {
                        mImageCycleViewListener.onImageClick(position, v);
                    }

                }
            });
            container.addView(imageView);
            mImageCycleViewListener.displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
        }

    }

    /**
     * 轮播控件的监听事件
     */
    public interface ImageCycleViewListener {

        /**
         * 加载图片资源
         */
        void displayImage(String imageURL, ImageView imageView);

        /**
         * 单击图片事件
         */
        void onImageClick(int position, View imageView);
    }

}
