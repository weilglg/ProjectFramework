package com.example.admin.textdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by wll on 2015/11/3.
 */
public class TextDemo extends LinearLayout {
    public TextDemo(Context context) {
        super(context, null);
    }

    public TextDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextDemo);
        int size = typedArray.getDimensionPixelSize(R.styleable.TextDemo_size, 1);
        typedArray.recycle();
        TextView tv = new TextView(context);
        tv.setText("测试字体");
       tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,14);
        addView(tv);
    }

}
