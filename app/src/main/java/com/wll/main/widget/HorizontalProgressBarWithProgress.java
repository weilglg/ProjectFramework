package com.wll.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;

import com.wll.main.R;

/**
 * Created by wll on 2016/7/1.
 */
public class HorizontalProgressBarWithProgress extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 12;//sp
    private static final int DEFAULT_TEXT_COLOR = 0XFFCC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0XFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    /**
     * 字体大小
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * 文字颜色
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;

    /**
     * 进度条默认颜色
     */
    protected int mUnReachColor = DEFAULT_COLOR_UNREACH;

    /**
     * 进度条默认高度
     */
    protected int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);

    /**
     * 进度条
     */
    protected int mReachColor = DEFAULT_COLOR_REACH;

    /**
     * 进度条显示进度的高度
     */
    protected int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);

    /**
     * 文本距离左右的距离
     */
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint = new Paint();

    protected int mRealWidth;


    public HorizontalProgressBarWithProgress(Context context) {
        this(context, null);
    }

    public HorizontalProgressBarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWithProgress);
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithProgress_progress_text_size,
                mTextSize);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressBarWithProgress_progress_text_color, mTextColor);
        mTextOffset = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithProgress_progress_text_offset,
                mTextOffset);

        mReachColor = ta.getColor(R.styleable.HorizontalProgressBarWithProgress_progress_reach_color,
                mReachColor);
        mUnReachColor = ta.getColor(R.styleable.HorizontalProgressBarWithProgress_progress_unreach_color,
                mUnReachColor);

        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithProgress_progress_height,
                mReachHeight);
        mUnReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithProgress_progress_unheight,
                mUnReachHeight);
        ta.recycle();

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);

        int heightVal = measureHeight(heightMeasureSpec);

        setMeasuredDimension(widthVal, heightVal);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            //精确值
            result = size;
        } else {
            //测量自己需要的值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mReachHeight, mUnReachHeight),
                    Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) { // 跟父布局给定的值进行比较取最小值
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        //是否需要绘制默认进度条
        boolean noNeedUnReach = false;

        String text = getProgress() + "%";
        //计算文本的宽度
        int textWidth = (int) mPaint.measureText(text);

        /**
         * draw reach
         */
        // 当前进度比例
        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        // 当前进度加文本宽度超过最大宽度时，不再绘制默认进度
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnReach = true;
        }
        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        /**
         * draw text
         */
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);

        /**
         * draw unReach
         */
        if (!noNeedUnReach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0 , mRealWidth, 0 , mPaint);
        }

        canvas.restore();

    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }

    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

}
