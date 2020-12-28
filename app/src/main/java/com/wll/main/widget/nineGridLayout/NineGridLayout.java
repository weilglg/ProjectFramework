package com.wll.main.widget.nineGridLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wll.main.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by wll on 2018/1/4/004.
 * <p>
 * 九宫格
 */

public class NineGridLayout extends ViewGroup implements NineBaseAdapter.DataChangeListener {

    private static final int REMOVE_VIEW_PADDING = 5;
    /**
     * 水平方向的间距
     */
    private int horizontalSpacing;
    /**
     * 垂直方向的间距
     */
    private int verticalSpacing;

    /**
     * 展示方式
     */
    private int mode;
    /**
     * 添加按钮资源
     */
    private int addIconResId;
    /**
     * 右上角删除按钮资源
     */
    private int removeIconResId;
    /**
     * 设置显示的图片最大数
     */
    private int maxNum;
    /**
     * 列数
     */
    private int numColumns;
    /**
     * 行数
     */
    private int rowsColumns;
    /**
     * item的总数
     */
    private int itemCount;
    /**
     * 是否显示添加按钮
     */
    private boolean isDisplayAddView = false;
    /**
     * 是否显示删除按钮
     */
    private boolean isDisplayRemoveView = false;

    private List<SoftReference<View>> views;

    private NineBaseAdapter adapter;
    private int singleWidth;
    private int singleHeight;
    private OnChildItemClickListener onItemClickListener;

    public NineGridLayout(Context context) {
        this(context, null);
    }

    public NineGridLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    /**
     * 获取自定义的属性值
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridImageView);
        horizontalSpacing = typedArray.getDimensionPixelOffset(R.styleable.GridImageView_horizontal_spacing, 20);
        verticalSpacing = typedArray.getDimensionPixelOffset(R.styleable.GridImageView_vertical_spacing, 20);
        mode = typedArray.getInt(R.styleable.GridImageView_mode, 1);
        addIconResId = typedArray.getResourceId(R.styleable.GridImageView_add_icon, -1);
        removeIconResId = typedArray.getResourceId(R.styleable.GridImageView_remove_icon, -1);
        maxNum = typedArray.getInt(R.styleable.GridImageView_max_num, 6);
        numColumns = typedArray.getInt(R.styleable.GridImageView_num_columns, 6);
        typedArray.recycle();
        if (addIconResId > 0) {
            isDisplayAddView = true;
        }
        if (removeIconResId > 0) {
            isDisplayRemoveView = true;
        }
    }

    public void setOnChildItemClickListener(OnChildItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setAdapter(NineBaseAdapter adapter) {
        if (this.views != null && !this.views.isEmpty()) {
            this.views.clear();
        }
        this.adapter = adapter;
        if (adapter == null) {
            return;
        }
        this.adapter.setDataChangeListener(this);
        removeAllViews();
        //初始化布局形状
        generateChildrenLayout();
        for (int i = 0; i < itemCount; i++) {
            if (i == adapter.getCount()) {
                displayAddView();
            } else {
                addItemView(i);
            }
        }
    }

    /**
     * 设置是否显示添加按钮
     *
     * @param displayAddView
     */
    public void setDisplayAddView(boolean displayAddView) {
        isDisplayAddView = displayAddView;
        if (!displayAddView) {
            View addView = findViewById(R.id.add_image);
            generateChildrenLayout();
            removeView(addView);
        } else {
            // 重新初始化行数以及列数
            generateChildrenLayout();
            displayAddView();
        }
    }

    /**
     * 设置是否显示删除按钮
     *
     * @param displayRemoveView
     */
    public void setDisplayRemoveView(boolean displayRemoveView) {
        isDisplayRemoveView = displayRemoveView;
        if (!displayRemoveView) {
            removeAllDeleteView();
        } else {
            addAllChildDeleteView();
        }
    }

    private void addAllChildDeleteView() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView != null && childView instanceof FrameLayout) {
                ImageView deleteView = new ImageView(getContext());
                deleteView.setId(R.id.delete_image);
                deleteView.setPadding(REMOVE_VIEW_PADDING, REMOVE_VIEW_PADDING, REMOVE_VIEW_PADDING, REMOVE_VIEW_PADDING);
                deleteView.setImageResource(removeIconResId);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.RIGHT | Gravity.TOP;
                ((FrameLayout) childView).addView(deleteView, params);
            }
        }
    }

    private void removeAllDeleteView() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView != null && childView instanceof FrameLayout) {
                View removeView = childView.findViewById(R.id.delete_image);
                if (removeView != null) {
                    ((FrameLayout) childView).removeView(removeView);
                }
            }
        }
    }

    /**
     * 初始化添加按钮布局
     *
     * @return
     */
    private View getAddView() {
        ImageView addView = new ImageView(getContext());
        addView.setImageResource(addIconResId);
        addView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView.setId(R.id.add_image);
        return addView;
    }

    @NonNull
    private View getItemView(int i) {
        View itemView = adapter.getView(i, null, this);
        if (isDisplayRemoveView) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.addView(itemView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            ImageView deleteView = new ImageView(getContext());
            deleteView.setId(R.id.delete_image);
            deleteView.setPadding(REMOVE_VIEW_PADDING, REMOVE_VIEW_PADDING, REMOVE_VIEW_PADDING, REMOVE_VIEW_PADDING);
            deleteView.setImageResource(removeIconResId);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT | Gravity.TOP;
            frameLayout.addView(deleteView, params);
            return frameLayout;
        }
        if (views == null) {
            views = new ArrayList<>();
        }
        views.add(new SoftReference<View>(itemView));
        return itemView;
    }

    private void generateChildrenLayout() {
        itemCount = adapter.getCount();
        // 当给的数据的size大于设定的最大显示数，则只显示设定的数量
        if (maxNum > 0 && maxNum <= itemCount) {
            itemCount = maxNum;
            inspectDataSourceSize();
        } else if (isDisplayAddView) {
            itemCount = itemCount + 1;
        }
        //通过列数计算行数
        if (itemCount % numColumns == 0) {
            rowsColumns = itemCount / numColumns;
        } else {
            rowsColumns = itemCount / numColumns + 1;
        }
    }

    /**
     * 检查数据源长度
     */
    private void inspectDataSourceSize() {
        int adapterCount = adapter.getCount();
        if (maxNum > 0 && adapterCount > maxNum) {
            List list = adapter.getList();
            int size = list.size() - 1;
            for (int i = maxNum; i < adapterCount; i++) {
                list.remove(size);
                size = list.size() - 1;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int totalWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        if (adapter != null && (adapter.getCount() > 0 || isDisplayAddView)) {
            int measureHeight;
            int childrenCount = itemCount;
            //根据列数计算child的宽度
            if (childrenCount == 1 && mode == 2 && addIconResId <= 0) { //当模式是展示大图并且数据的size是1，则将图片按照大图展示
                singleWidth = totalWidth;
                singleHeight = singleWidth * 3 / 4 - getPaddingBottom() - getPaddingTop();
            } else {
                singleWidth = (totalWidth - horizontalSpacing * (numColumns - 1)) / numColumns;
                singleHeight = singleWidth;
            }
            measureChildren(MeasureSpec.makeMeasureSpec(singleWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(singleHeight, MeasureSpec.EXACTLY));
            measureHeight = singleHeight * rowsColumns + verticalSpacing * (rowsColumns - 1);
            setMeasuredDimension(sizeWidth, measureHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildrenView();
    }

    private void layoutChildrenView() {
        if (adapter == null || (adapter.getCount() == 0 && !isDisplayAddView)) {
            return;
        }
        int childCount = itemCount;
        for (int i = 0; i < childCount; i++) {
            int[] position = findPosition(i);
            int left = (singleWidth + horizontalSpacing) * position[1] + getPaddingLeft();
            int top = (singleHeight + verticalSpacing) * position[0] + getPaddingTop();
            int right = left + singleWidth;
            int bottom = top + singleHeight;
            View childrenView = initImageView(childCount, i);
            setChildViewClickListener(childrenView, i);
            setDeleteViewClickListener(childrenView, i);
            childrenView.layout(left, top, right, bottom);
        }
    }

    private View initImageView(int childCount, int i) {
        ImageView imageView = null;
        View childrenView = getChildAt(i);
        if (childrenView instanceof ImageView) {
            imageView = (ImageView) childrenView;
        } else if (childrenView instanceof ViewGroup) {
            imageView = getImageView((ViewGroup) childrenView);
        }
        if (imageView != null) {
            if (childCount == 1 && mode == 2 && !isDisplayAddView) {
//                    只有一张图片
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
        return childrenView;
    }

    private void setDeleteViewClickListener(View childrenView, final int itemPosition) {
        View deleteView = childrenView.findViewById(R.id.delete_image);
        if (deleteView != null) {
            deleteView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = true;
                    if (onItemClickListener != null) {
                        flag = onItemClickListener.removeItemClick(itemPosition);
                    }
                    if (!flag) {
                        return;
                    }
                    if (adapter != null) {
                        adapter.removeItem(itemPosition);
                    }
                }
            });
        }
    }

    private void setChildViewClickListener(View childrenView, final int itemPosition) {
        childrenView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    if (v.getId() == R.id.add_image) {
                        onItemClickListener.addItemClick();
                    } else {
                        onItemClickListener.onItemClick(v, itemPosition);
                    }
                }
            }
        });
    }

    @Nullable
    private ImageView getImageView(ViewGroup childrenView) {
        int count = childrenView.getChildCount();
        for (int j = 0; j < count; j++) {
            View childAt = childrenView.getChildAt(j);
            if (childAt instanceof ImageView && childAt.getId() != R.id.delete_image) {
                return (ImageView) childAt;
            }
        }
        return null;
    }

    /**
     * 显示添加按钮
     */
    private void displayAddView() {
        addView(getAddView(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 添加ItemView
     */
    private void addItemView(int i) {
        addView(getItemView(i), i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 查找childView的下标
     *
     * @param childNum
     * @return
     */
    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rowsColumns; i++) {
            for (int j = 0; j < numColumns; j++) {
                if ((i * numColumns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    public NineBaseAdapter getAdapter() {
        return this.adapter;
    }

    @Override
    public void onItemRangeAdd(int oldSize) {
        if (maxNum > 0 && adapter.getCount() >= maxNum) {
            View addView = this.findViewById(R.id.add_image);
            if (addView != null) {
                isDisplayAddView = false;
                removeView(addView);
            }
        }
        generateChildrenLayout();
        for (int i = oldSize; i < adapter.getCount(); i++) {
            if (i < maxNum) {
                addItemView(i);
            }
        }
    }

    @Override
    public void onItemRangeRemove(int itemPosition) {
        if (adapter == null) {
            return;
        }
        if (adapter.getCount() < maxNum && addIconResId > 0) {
            isDisplayAddView = true;
        }
        // 重新初始化行数以及列数
        generateChildrenLayout();
        removeViewAt(itemPosition);
        if (this.views != null && itemPosition < this.views.size()) {
            this.views.remove(itemPosition);
        }
        if (isDisplayAddView) {
            displayAddView();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        removeAllViews();
        //初始化布局形状
        generateChildrenLayout();
        for (int i = 0; i < itemCount; i++) {
            if (i == adapter.getCount() && isDisplayAddView) {
                displayAddView();
            } else {
                addItemView(i);
            }
        }
        if (this.adapter != null) {
            int itemCount = this.adapter.getCount();
            this.removeAllViews();
            if (itemCount == 0) {
                this.views.clear();
                return;
            }
            Iterator var6;
            SoftReference softReference;
            for (int i = 0; i < itemCount; ++i) {
                if (views != null && views.size() > 0) {
                    boolean isAdd = false;
                    var6 = views.iterator();
                    while (var6.hasNext()) {
                        softReference = (SoftReference) var6.next();
                        if (softReference != null && softReference.get() != null && ((View) softReference.get()).getParent() == null) {
                            this.addView(this.adapter.getView(i, (View) softReference.get(), this));
                            isAdd = true;
                            break;
                        }
                    }

                    if (!isAdd) {
                        View itemView = this.adapter.getView(i, (View) null, this);
                        views.add(new SoftReference<View>(itemView));
                        this.addView(itemView);
                    }
                } else {
                    View itemView = this.adapter.getView(i, (View) null, this);
                    if (views == null) {
                        views = new ArrayList<>();
                    }
                    views.add(new SoftReference<View>(itemView));
                    this.addView(itemView);
                }
            }
        }

        this.invalidate();
        this.requestLayout();
    }


    public static interface OnChildItemClickListener {

        void onItemClick(View view, int itemPosition);

        abstract void addItemClick();

        boolean removeItemClick(int position);

    }

    public static class DefaultChildItemClickListener implements OnChildItemClickListener{

        @Override
        public void onItemClick(View view, int itemPosition) {

        }

        @Override
        public void addItemClick() {

        }

        @Override
        public boolean removeItemClick(int position) {
            return false;
        }
    }
}
