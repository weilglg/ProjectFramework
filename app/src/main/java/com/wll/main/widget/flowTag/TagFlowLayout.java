package com.wll.main.widget.flowTag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wll.main.R;
import com.wll.main.adapter.TagAdapter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by WLL on 2016/2/22.
 */
public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {


    private static final String KEY_DEFAULT = "KEY_DEFAULT";
    private static final String KEY_CHOOSE_POSITION = "KEY_CHOOSE_POSITION";
    /**
     * Tag的适配器
     */
    private TagAdapter mTagAdapter;
    /**
     * 是否开启默认的选中效果，即为selector中设置的效果，默认为true；如果设置为false，则无选中效果，需要自己在回调中处理
     */
    private boolean mAutoSelectEffect = true;
    /**
     * 设置可以被选中的Tag的数量，默认不设置
     */
    private int mSelectedMax = -1;

    private static final String TAG = TagFlowLayout.class.getSimpleName();
    /**
     * 设置选中的Tag的下标
     */
    private Set<Integer> mSelectedViewPosition = new HashSet<Integer>();

    /**
     * 手势监听
     */
    private MotionEvent mMotionEvent;

    private OnSelectedItemListener onSelectedItemListener;

    private OnTagClickListener onTagClickListener;

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
        if (onTagClickListener != null) {
            setClickable(true);
        }
    }

    public void setOnSelectedItemListener(OnSelectedItemListener onSelectedItemListener) {
        this.onSelectedItemListener = onSelectedItemListener;
        if (onSelectedItemListener != null) {
            setClickable(true);
        }
    }

    /**
     * 设置可以选中的最大数值
     */
    public void setSelectedMax(int selectedMax) {
        if (mSelectedViewPosition.size() > selectedMax) {
            Log.w(TAG, "you has already select more than " + selectedMax + " views , so it will be clear .");
            mSelectedViewPosition.clear();
        }
        mSelectedMax = selectedMax;
    }

    /**
     * 获取当前所有选中的Tag的下标
     */
    public Set<Integer> getSelectedViewPosition() {
        return new HashSet<Integer>(mSelectedViewPosition);
    }

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取自定义的属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mAutoSelectEffect = typedArray.getBoolean(R.styleable.TagFlowLayout_auto_select_effect, true);
        mSelectedMax = typedArray.getInteger(R.styleable.TagFlowLayout_max_select, -1);
        typedArray.recycle();

        if (mAutoSelectEffect) {
            Log.e("TAG", "mAutoSelectEffect : " + mAutoSelectEffect);

            setClickable(true);
        }
    }

    public void setTagAdapter(TagAdapter mTagAdapter) {
        this.mTagAdapter = mTagAdapter;
        this.mTagAdapter.setOnDataChangedListener(this);
        //根据传入的适配器绘制视图
        drawingViewsAccordingToAdapter(this.mTagAdapter);
    }

    /**
     * 根据传入的适配器绘制相应的视图
     */
    private void drawingViewsAccordingToAdapter(TagAdapter mTagAdapter) {
        //清楚原有的视图
        removeAllViews();
        //放置改变原有适配器
        TagAdapter adapter = mTagAdapter;
        //用于包裹Tag视图的自定义View,用于处理选中状态
        TagView tagViewContainer = null;
        //获得要设置选中position的集合
        HashSet<Integer> preCheckedList = mTagAdapter.getPreCheckedTagPositionList();
        for (int i = 0; i < adapter.getTagCount(); i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));

            tagViewContainer = new TagView(getContext());

            //使内部类获得外部容器类的状态
            tagView.setDuplicateParentStateEnabled(true);

            tagViewContainer.setLayoutParams(tagView.getLayoutParams());

            //将Tag加入到父布局中
            tagViewContainer.addView(tagView);
            //将Tag加入当前视图中
            addView(tagViewContainer);

            //判断当前Tag中是否需要设置选中
            if (preCheckedList.contains(i)) {
                tagViewContainer.setChecked(true);
            }
        }

        mSelectedViewPosition.addAll(preCheckedList);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            TagView itemView = (TagView) getChildAt(i);
            //获得TagItemView中包裹的布局的显示状态，通过判断包裹的布局的状态设置TagItemView的显示状态
            if (itemView.getTagView().getVisibility() == View.GONE) {
                itemView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mMotionEvent = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }

    //重写点击事件
    @Override
    public boolean performClick() {

        if (mMotionEvent == null) {
            return super.performClick();
        }
        //获得手指点击的坐标
        int x = (int) mMotionEvent.getX();
        int y = (int) mMotionEvent.getY();
        mMotionEvent = null;
        TagView child = findChild(x, y);
        if (child != null) {
            int position = findPositionByView(child);
            doSelect(child, position);
            if (onTagClickListener != null) {
                return onTagClickListener.onTagClick(child.getTagView(), position, this);
            }
        }
        return super.performClick();
    }

    @Override
    public void onChange() {
        if (mTagAdapter != null)
            drawingViewsAccordingToAdapter(mTagAdapter);
    }

    //保存当前视图的状态
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        String selectPosition = "";
        if (mSelectedViewPosition.size() > 0) {
            for (Integer key : mSelectedViewPosition) {
                selectPosition += key + "|";
            }
            selectPosition = selectPosition.substring(0, selectPosition.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POSITION, selectPosition);
        return bundle;
    }

    //获取上次保存的状态，并设置当前视图中选中的Tag
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POSITION);
            if (!TextUtils.isEmpty(mSelectPos)) {
                String[] split = mSelectPos.split("\\|");
                for (String position : split) {
                    int index = Integer.parseInt(position);
                    mSelectedViewPosition.add(index);

                    TagView tagView = (TagView) getChildAt(index);
                    tagView.setChecked(true);
                }

            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 设置选中当前Tag
     */
    private void doSelect(TagView child, int position) {

        if (mAutoSelectEffect) {
            if (!child.isChecked()) {
                //处理max_select = 1的情况
                if (mSelectedMax == 1 && mSelectedViewPosition.size() == 1) {
                    Iterator<Integer> iterator = mSelectedViewPosition.iterator();
                    Integer preIndex = iterator.next();
                    TagView pre = (TagView) getChildAt(preIndex);
                    pre.setChecked(false);
                    child.setChecked(true);
                    mSelectedViewPosition.remove(preIndex);
                    mSelectedViewPosition.add(position);
                } else {//处理多选时的状态
                    //判断当前点击Tag的下标是否大于或等于设置的可选的总数量
                    if (mSelectedMax > 0 && mSelectedViewPosition.size() >= mSelectedMax) {
                        return;
                    }
                    //设置当前Tag的状态为选中
                    child.setChecked(true);
                    //将当前Tag的下标保存
                    mSelectedViewPosition.add(position);
                }
            } else {
                //设置当前Tag的状态为不选中
                child.setChecked(false);
                //将当前Tag的下标从存储集合中移除
                mSelectedViewPosition.remove(position);
            }
            //调用Tag选中监听器
            if (onSelectedItemListener != null) {
                onSelectedItemListener.onItemSelected(position);
                onSelectedItemListener.onSelectedAllPosition(new HashSet<>(mSelectedViewPosition));
            }
        }
    }

    /**
     * 获取指定的Tag所对应的下标
     */
    private int findPositionByView(View child) {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View v = getChildAt(i);
            if (v == child) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据坐标获取当前点击的Tag
     *
     * @param x X轴
     * @param y Y轴
     */
    private TagView findChild(int x, int y) {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            TagView v = (TagView) getChildAt(i);
            if (v.getVisibility() == View.GONE) {
                continue;
            }
            Rect outRect = new Rect();
            //找到控件占据的矩形区域的矩形坐标
            v.getHitRect(outRect);
            //判断用户点击的坐标点在当前Tag所占据的矩形区域中
            if (outRect.contains(x, y)) {
                return v;
            }
        }
        return null;
    }

    /**
     * 点击每一个Tag时的回调函数
     */
    public interface OnTagClickListener {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }

    /**
     * 选中Tag时的回调
     */
    public interface OnSelectedItemListener {
        void onItemSelected(Integer selectedPosition);

        void onSelectedAllPosition(Set<Integer> selectedPositionSet);
    }
}
