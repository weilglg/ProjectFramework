package com.wll.main.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wll.main.R;

public class PullRefreshScrollView extends ScrollView {

    /**
     * 松开手指即可进行刷新
     */
    private final static int RELEASE_To_REFRESH = 0x01;

    /**
     * 正在进行下拉刷新操作
     */
    private final static int PULL_To_REFRESH = 0x11;

    /**
     * 数据正在加载中
     */
    private final static int REFRESHING = 0x111;

    /**
     * 隐藏
     */
    private final static int DONE = 0x1111;

    /**
     * ScrollView里面嵌套的根布局
     */
    private ViewGroup parentView = null;

    /**
     * 刷新动画布局
     */
    private LinearLayout headView = null;

    /**
     * 刷新动画布局的高度
     */
    private int headContentHeight;

    /**
     * 是否记录开始位置
     */
    private boolean isRecord;

    /**
     * 计算滑动距离的开始位置
     */
    private float startY;

    /**
     * 当前状态
     */
    private int state;

    /**
     * 刷新动画布局是否可见
     */
    private boolean isSeeHead;

    /**
     * 比率
     */
    private final static float RATIO = 3;

    /**
     * 刷新动画布局中的提示文字
     */
    private TextView tvContent;

    /**
     * 刷新监听
     */
    private OnPullRefreshListener refreshListener;

    /**
     * 正在加载时播放的动画
     */
    private AnimationDrawable animationDrawable;

    public PullRefreshScrollView(Context context) {
        this(context, null);
    }

    public PullRefreshScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public void initView(Context context) {
        // 加载刷新动画布局文件
        LayoutInflater inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(
                R.layout.layout_refresh_header, this, false);
        // 重新测量刷新动画布局
        measureView(headView);
        // 获得布局的高度
        headContentHeight = headView.getMeasuredHeight();
        tvContent = (TextView) headView.findViewById(R.id.refresh_header_prompt);
        // 播放动画的ImageView
        ImageView animImage = (ImageView) headView
                .findViewById(R.id.refresh_header_anim_image);
        // 获得AnimationDrawable
        animationDrawable = (AnimationDrawable) animImage.getDrawable();
        // 设置刷新动画布局为隐藏状态
        state = DONE;
        isRecord = false;
        isSeeHead = false;
    }

    /**
     * 从新对布局文件进行测量
     *
     * @param child 要重新测量的控件
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            child.setLayoutParams(p);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 获取ScrollView下的LinearLayout布局
        if (parentView == null) {
            parentView = (ViewGroup) this.getChildAt(0);
            // 设置刷新动画布局离顶部的内边距为-headContentHeight
            headView.setPadding(0, -1 * headContentHeight, 0, 0);
            // 从新绘制刷新动画布局
            headView.invalidate();
            // 将刷新动画布局加入到LinearLayout中的第一个位置
            parentView.addView(headView, 0);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.getScrollY() == 0 && !isRecord) {
                    // 记录按下时的Y轴的位置
                    startY = ev.getY();
                    isRecord = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 获得当前滑动的Y轴位置
                float tempY = ev.getY();
                // 避免上下反复滑动
                if (!isRecord && this.getScrollY() == 0) {
                    isRecord = true;
                    startY = tempY;
                }
                if (state != REFRESHING && isRecord) {
                    // 当前刷新动画布局的状态是隐藏，并且是向下滑动
                    if (state == DONE && tempY - startY >= 0) {
                        // 设置状态为松开手指可以刷新
                        state = PULL_To_REFRESH;
                        // 设置刷新动画布局是可见的
                        isSeeHead = true;
                    } else if (state != DONE && tempY - startY <= 0) { // 当前不处于隐藏状态，但是滑动的距离是负数也就是说刷新布局已不可见还在向上滑动
                        // 更新状态为隐藏状态
                        hideHeadView();
                    } else if (state == PULL_To_REFRESH
                            && (tempY - startY) / RATIO >= headContentHeight + 10) {// 当前正在进行下拉的操作，并且滑动的距离大于等于刷新动画布局的高度
                        // 更新状态为松开手指即开始刷新操作
                        state = RELEASE_To_REFRESH;
                        tvContent.setText("松开可以刷新");
                    } else if (state == RELEASE_To_REFRESH
                            && (tempY - startY) / RATIO < headContentHeight + 10) { //
                        // 当前处于松开手指即可进行刷新的状态，并且滑动的距离小于刷新动画布局的高度
                        // 更新状态为正在进行下拉操作
                        state = PULL_To_REFRESH;
                    }
                    // 动态设置刷新动画布局的内间距实现布局的缓慢显示
                    headView.setPadding(0,
                            (int) ((tempY - startY) / RATIO - headContentHeight),
                            0, 0);
                    // 强制重绘刷新动画布局
                    headView.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state == PULL_To_REFRESH) { // 当前处于下拉刷新的过程中，还没有达到固定距离不进行刷新操作
                    hideHeadView();
                }
                if (state == RELEASE_To_REFRESH) { // 当前处于松开手指即可进行刷新的操作，松开手指进行刷新操作
                    // 进行刷新操作
                    onRefresh();
                }
                isRecord = false;
                isSeeHead = false;
                break;
        }
        // 如果正在进行下拉刷新的操作，那么所有的点击时间都交给ScrollView进行处理
        if (isSeeHead) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    /**
     * 隐藏刷新动画布局
     */
    private void hideHeadView() {
        // 设置刷新动画布局的状态为隐藏
        state = DONE;
        // 通过设置paddingTop为负数达到隐藏的效果
        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        isRecord = false;
    }

    /**
     * 显示刷新动画布局，进行刷新操作
     */
    private void onRefresh() {
        // 设置状态为正在刷新
        state = REFRESHING;
        headView.setPadding(0, 0, 0, 0);
        // 如果刷新动画，调用刷新动画
        if (animationDrawable != null) {
            animationDrawable.setOneShot(false);
            animationDrawable.start();
        }
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    /**
     * 完成数据加载后调用此方法
     */
    public void onPullRefreshComplete() {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        hideHeadView();
    }

    /**
     * 设置刷新监听
     * @param refreshListener
     */
    public void setOnPullRefreshListener(OnPullRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface OnPullRefreshListener {
        public void onRefresh();
    }

}