package com.wll.main.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by WLL on 2016/2/25.
 */
public class EdgeImageView extends ImageView {
    private int mWidth, mHeight;
    private float radius1, radius2, cx, cy;
    private Paint mPaint1, mPaint2, mPaint3;
    private RectF oval1, oval2, oval3;

    public EdgeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2 = new Paint(mPaint1);
        mPaint3 = new Paint(mPaint1);
        mPaint1.setColor(Color.BLUE);
        mPaint2.setColor(Color.RED);
        mPaint3.setColor(Color.YELLOW);
        radius1 = 50;
        radius2 = 70;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        cx = mWidth / 2;
        cy = mHeight / 2;
        oval1 = new RectF(cx - radius1 -20,cy - radius1 -20, cx + radius1 + 20, cy + radius1 + 20);
        oval2 = new RectF(cx - radius1 -40,cy - radius1 -40, cx + radius1 + 40, cy + radius1 + 40);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    boolean reDraw;
    int increase = 0;

    private void reset(){
        increase = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                reDraw = true;
                reset();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                reDraw = false;
                reset();
                oval1 = new RectF(cx - radius1 -20,cy - radius1 -20, cx + radius1 + 20, cy + radius1 + 20);
                oval2 = new RectF(cx - radius1 -40,cy - radius1 -40, cx + radius1 + 40, cy + radius1 + 40);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(oval2, -90, 360, false, mPaint3);
        canvas.drawArc(oval1, -90, 360, false, mPaint2);

        canvas.drawCircle(cx, cy, radius1, mPaint1);
        if (reDraw) {
            oval1 = new RectF(cx - radius1 - increase , cy - radius1 - increase, cx + radius1 + increase, cy + radius1 + increase);
            int j = increase + 10;
            oval2 = new RectF(cx - radius1 - j , cy - radius1 - j, cx + radius1 + j, cy + radius1 + j);
            increase += 2;
            if (increase > 41) {
                reset();
            }
            invalidate();
        }
    }
}
