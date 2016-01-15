package com.wll.main.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Admin on 2015/9/23.
 * 自定义ScrollView，可以实现标题栏的动态显示隐藏。需要设置在哪个控件顶部开始隐藏显示
 */
public class ObservableScrollView extends ScrollView {


    //用于获取显示隐藏的分界线
    private View view;

    //标题栏的透明度
    private int mAlpha;

    private View titleBar;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (titleBar != null && titleBar.getBackground().mutate() != null) {
                titleBar.getBackground().mutate().setAlpha(msg.what);
            }
        }
    };

    public void setUpTitleBar(View v, View titleBar) {
        this.view = v;
        this.titleBar = titleBar;
        handler.sendEmptyMessage(0);
    }


    public ObservableScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObservableScrollView(Context context) {
        this(context, null);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);

        setUpTitleBarBackground(y);
    }


    /**
     * 根据ScrollView滑动过程中具体顶部的距离判断显示还是隐藏
     *
     * @param scrollY
     */
    private void setUpTitleBarBackground(int scrollY) {
        if (view == null) {
            return;
        }
        //获得显示隐藏标题栏的分界线（也就是以Y轴上的哪个点为分界点）
        int viewTop = view.getTop();

        //判断滑动过程中时候超过分界点
        if (scrollY >= viewTop) {//超过分界点时，将标题栏设置为不透明
            if (mAlpha == 255)
                return;
            handler.sendEmptyMessage(255);
        } else {//没有超过分界点时，计算标题栏的透明度
            //根据Y轴上滑动的距离与分界点计算透明度的比值
            float ratio = scrollY * 1f / viewTop * 1f;
            //计算透明度
            mAlpha = (int) (255 * ratio);
            //因为ScrollView快速滑动到顶部时有回弹，所以这块做下特殊处理
            if (mAlpha <= 2) {
                handler.sendEmptyMessage(0);
            } else {
                handler.sendEmptyMessage(mAlpha);
            }
        }
    }

}
