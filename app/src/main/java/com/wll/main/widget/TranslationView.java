package com.wll.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by WLL on 2016/1/22.
 */
public class TranslationView extends TextView {

    private int mLastY;
    private int mLastX;

    public TranslationView(Context context) {
        super(context);
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前手指的位置
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                Log.e("TAG", "move--deltaX : " + deltaX + " deltaY : " + deltaY);
                int translationX = (int) ViewHelper.getTranslationX(this) + deltaX;
                int translationY = (int) ViewHelper.getTranslationY(this) + deltaY;
                ViewHelper.setTranslationX(this, translationX);
                ViewHelper.setTranslationY(this, translationY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
}
