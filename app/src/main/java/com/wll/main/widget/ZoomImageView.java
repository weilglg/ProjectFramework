package com.wll.main.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by wll on 2015/10/16.
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    /**
     * 是否进行第一次加载
     */
    private boolean mOnce;

    /**
     * 初始化时缩放的比率
     */
    private float mInitScale;
    /**
     * 双击后放大的比率,此值是初始化比率的两倍
     */
    private float mMidScale;
    /**
     * 放大的最大比率,此值是初始化比率的四倍
     */
    private float mMaxScale;

    /**
     * 捕获用户多点触控时缩放的比率，判断是放大还是缩小
     */
    private ScaleGestureDetector mScaleGestureDetector;

    /**
     * 控制缩放和平移
     */
    private Matrix mScaleMatrix;

    //----------------自由移动
    /**
     * 记录上一次多点触控的触点数量
     */
    private int mLastPointerCount;

    /**
     * 中心点的X轴坐标
     */
    private float mLastX;
    /**
     * 中心点的Y轴坐标
     */
    private float mLastY;

    /**
     * 是否是移动操作的标准值
     */
    private int mTouchSlop;
    /**
     * 是否可以移动
     */
    private boolean isCanDrag;

    /**
     * 是否进行左右边界检测
     */
    private boolean isCheckLeftAndRight;
    /**
     * 是否进行上下边界检测
     */
    private boolean isCheckTopAndBottom;

    //-------------------双击放大与缩小
    private GestureDetector mGestureDetector;

    /**
     * 是否正在进行放大缩小的操作
     */
    private boolean isAutoScale;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        setOnTouchListener(this);

        //双击操作事件的实现
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();

                if (isAutoScale) {
                    return true;
                }

                if (getScale() < mMidScale) {
//                    mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                    setImageMatrix(mScaleMatrix);

                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                    isAutoScale = true;
                } else {
//                    mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                    isAutoScale = true;
                }

                return true;
            }
        });
    }

    private class AutoScaleRunnable implements Runnable {
        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        //缩放的中心点
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        private float tmpScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            //进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();
            //判断放大缩小的操作是否合法
            if ((tmpScale > 1.0f && currentScale < mTargetScale) || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {//达到目标值
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    //当ImageView显示到屏幕上时
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    //当ImageView从屏幕上消失时
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //为了兼容低版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    /**
     * 获取ImageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {

        if (!mOnce) {
            //获得控件的宽和高
            int width = getWidth();
            int height = getHeight();
            //得到图片以及宽和高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }

            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            //图片缩放的比率
            float scale = 0.1f;
            if (drawableWidth > width && drawableHeight < height) { //图片的宽度大于控件的宽度，高度小于控件的高度;将其缩小
                scale = width * 1.0f / drawableWidth;
            }

            if (drawableHeight > height && drawableWidth < width) {//图片的高度大于控件的高度，宽度小于控件的宽度;将其缩小
                scale = height * 1.0f / drawableHeight;
            }

            if (drawableWidth > width && drawableHeight > height) {//图片的宽度和高度都大于控件的宽度和高度，将其缩小
                scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight);
            }

            if (drawableWidth < width && drawableHeight < height) {//图片的宽度和高度都小于控件的宽度和高度，将其放大
                scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight);
            }

            //得到初始化时缩放的比率
            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;

            //将图片移动到当前控件的中心
            //图片需要移动的X轴的距离是：控件的宽度一半 - 图片的宽度的一半
            int dx = getWidth() / 2 - drawableWidth / 2;
            //图片需要移动的Y轴的距离是：控件的高度一半 - 图片的高度的一半
            int dy = getHeight() / 2 - drawableHeight / 2;

            //平移
            mScaleMatrix.postTranslate(dx, dy);
            //缩放
            mScaleMatrix.postScale(mInitScale, mInitScale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            mOnce = true;
        }
    }

    /**
     * 获取当前缩放的比率
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }


    //缩放区间：initScale maxScale
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        //从前一个伸缩事件至当前伸缩事件的伸缩比率
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();
        if (getDrawable() == null) {
            return true;
        }
        //缩放范围的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {

            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            //缩放
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());

            checkBorderAndCenterWhenScale();

            setImageMatrix(mScaleMatrix);
        }


        return false;
    }

    /**
     * 在缩放的时候进行以及位置的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rectF = getMatrixRectF();

        //差值
        float deltaX = 0;
        float deltaY = 0;

        //获得屏幕的宽高
        int width = getWidth();
        int height = getHeight();

        //缩放时进行边界检测，放置出现白边
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }

            if (rectF.right <= width) {
                deltaX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }

            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }

        //如果宽度或者高度小于控件的宽度或者高度，让其居中
        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2f;
        }
        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2f;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 获得图片放大缩小以后的宽和高，以及Left、Right、Top、Bottom
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //将操作传递出去
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        mScaleGestureDetector.onTouchEvent(event);


        float x = 0;
        float y = 0;

        //拿到当前多点触控的数量
        int pointerCount = event.getPointerCount();
        //遍历所有的触点坐标
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        //获得中心点
        x /= pointerCount;
        y /= pointerCount;

        //触摸过程中触点数量变化
        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ((rectF.width() > getWidth() + 0.01f || rectF.height() > getHeight() + 0.01f) && getParent() instanceof ViewPager) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if ((rectF.width() > getWidth() + 0.01f || rectF.height() > getHeight() + 0.01f) && getParent() instanceof ViewPager) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                //获取X、Y轴的偏移量
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        //如果图片的宽度小于控件的宽度，不允许横向移动
                        if (rectF.width() <= getWidth() + 0.01f) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        //如果图片的高度小于控件的高度，不允许纵向移动
                        if (rectF.height() <= getHeight() + 0.01f) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                //记录上一次移动的X、Y
                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }


        return true;
    }

    /**
     * 移动时的边界检查
     */
    private void checkBorderAndCenterWhenTranslate() {

        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (getDrawable() != null) {

            if (rectF.top > 0 && isCheckTopAndBottom) {
                deltaY = -rectF.top;
            }

            if (rectF.bottom < height && isCheckTopAndBottom) {
                deltaY = height - rectF.bottom;
            }

            if (rectF.left > 0 && isCheckLeftAndRight) {
                deltaX = -rectF.left;
            }

            if (rectF.right < width && isCheckLeftAndRight) {
                deltaX = width - rectF.right;
            }

            mScaleMatrix.postTranslate(deltaX, deltaY);
        }
    }

    /**
     * 判断是否是Move
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
