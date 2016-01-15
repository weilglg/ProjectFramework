package com.wll.main.widget.flowTag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * Created by wll on 2015/10/15.
 */
public class TagView extends FrameLayout implements Checkable {

    /**
     * 是否被选中
     */
    private boolean isChecked;
    /**
     * 选中状态
     */
    private static final int[] CHECK_STATE = new int[]{android.R.attr.state_checked};

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public View getTagView() {
        return getChildAt(0);
    }

    /**
     * 为当前视图生成新的可绘图区状态。这个方式当缓存的图像绘图区状态确定失效时通过视图系统调用。你可以使用getDrawableState()方法重新取得当前的状态。
     * <p>
     * 参数
     * <p>
     * extraSpace      如果为非零，这是你应该返回的数组在你可以存放你的状态的额外条目的数量。
     * <p>
     * 返回值
     * <p>
     * 返回一个记录着视图中当前绘图区状态的数组
     */
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        //加入自定义个状态判断
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }

    /**
     * 改变视图的选中状态
     *
     * @param checked 选中状态
     */
    @Override
    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            //刷新状态
            refreshDrawableState();
        }
    }

    /**
     * @return 返回当前视图的选中状态
     */
    @Override
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * 改变当前视图的状态，如原来为选中现在就将其改变为不选中
     */
    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

}
