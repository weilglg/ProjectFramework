package com.wll.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.wll.main.R;

/**
 * Created by wll on 2016/7/1.
 */
public class RoundProgressBarWithProgress extends HorizontalProgressBarWithProgress {

    private int mRadius = dp2px(30);

    /**
     * 圆环的最大宽度
     */
    private int mMaxPaintWidth;

    public RoundProgressBarWithProgress(Context context) {
        this(context, null);
    }

    public RoundProgressBarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (mReachHeight == mUnReachHeight) {
            mReachHeight = (int) (mUnReachHeight * 2.5f);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBarWithProgress);
        mRadius = (int) typedArray.getDimension(R.styleable.RoundProgressBarWithProgress_radius, mRadius);
        typedArray.recycle();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxPaintWidth = Math.max(mReachHeight, mUnReachHeight);
        //默认四个padding是一致
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);

        int readWidth = Math.min(width, height);

        mRadius = (readWidth - getPaddingRight() - getPaddingLeft() - mMaxPaintWidth) / 2;
        setMeasuredDimension(readWidth, readWidth);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress() + "%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
        canvas.save();

        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop());
        mPaint.setStyle(Paint.Style.STROKE);

        //draw unReach bar
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mUnReachHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        // draw reach bar
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius* 2 , mRadius * 2), 0, sweepAngle, false, mPaint);

        //draw text
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight , mPaint );

        canvas.restore();
    }
}
