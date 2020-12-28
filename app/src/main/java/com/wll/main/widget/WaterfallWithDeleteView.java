package com.wll.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wll.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 带删除按钮的瀑布流标签
 */

public class WaterfallWithDeleteView extends ViewGroup implements OnClickListener,
        OnLongClickListener {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int horizontalSpacing = 0;
    private int verticalSpacing = 0;
    private int orientation = 0;
    private boolean debugDraw = false;
    private List<Tag> mTags = new ArrayList<Tag>();
    private OnTagClickListener onTagClickListener;

    public WaterfallWithDeleteView(Context context) {
        super(context);

        this.readStyleParameters(context, null);
    }

    public WaterfallWithDeleteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.readStyleParameters(context, attributeSet);
    }

    public WaterfallWithDeleteView(Context context, AttributeSet attributeSet,
                                   int defStyle) {
        super(context, attributeSet, defStyle);

        this.readStyleParameters(context, attributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
                - this.getPaddingRight() - this.getPaddingLeft();
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
                - this.getPaddingTop() - this.getPaddingBottom();

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int size;
        int mode;

        if (orientation == HORIZONTAL) {
            size = sizeWidth;
            mode = modeWidth;
        } else {
            size = sizeHeight;
            mode = modeHeight;
        }

        int lineThicknessWithSpacing = 0;
        int lineThickness = 0;
        int lineLengthWithSpacing = 0;
        int lineLength;

        int prevLinePosition = 0;

        int controlMaxLength = 0;
        int controlMaxThickness = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, this.getPaddingLeft()
                            + this.getPaddingRight(), lp.width),
                    getChildMeasureSpec(heightMeasureSpec, this.getPaddingTop()
                            + this.getPaddingBottom(), lp.height));

            int hSpacing = this.getHorizontalSpacing(lp);
            int vSpacing = this.getVerticalSpacing(lp);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int childLength;
            int childThickness;
            int spacingLength;
            int spacingThickness;

            if (orientation == HORIZONTAL) {
                childLength = childWidth;
                childThickness = childHeight;
                spacingLength = hSpacing;
                spacingThickness = vSpacing;
            } else {
                childLength = childHeight;
                childThickness = childWidth;
                spacingLength = vSpacing;
                spacingThickness = hSpacing;
            }

            lineLength = lineLengthWithSpacing + childLength;
            lineLengthWithSpacing = lineLength + spacingLength;

            boolean newLine = lp.newLine
                    || (mode != MeasureSpec.UNSPECIFIED && lineLength > size);
            if (newLine) {
                prevLinePosition = prevLinePosition + lineThicknessWithSpacing;

                lineThickness = childThickness;
                lineLength = childLength;
                lineThicknessWithSpacing = childThickness + spacingThickness;
                lineLengthWithSpacing = lineLength + spacingLength;
            }

            lineThicknessWithSpacing = Math.max(lineThicknessWithSpacing,
                    childThickness + spacingThickness);
            lineThickness = Math.max(lineThickness, childThickness);

            int posX;
            int posY;
            if (orientation == HORIZONTAL) {
                posX = getPaddingLeft() + lineLength - childLength;
                posY = getPaddingTop() + prevLinePosition;
            } else {
                posX = getPaddingLeft() + prevLinePosition;
                posY = getPaddingTop() + lineLength - childHeight;
            }
            lp.setPosition(posX, posY);

            controlMaxLength = Math.max(controlMaxLength, lineLength);
            controlMaxThickness = prevLinePosition + lineThickness;
        }

		/* need to take paddings into account */
        if (orientation == HORIZONTAL) {
            controlMaxLength += getPaddingLeft() + getPaddingRight();
            controlMaxThickness += getPaddingBottom() + getPaddingTop();
        } else {
            controlMaxLength += getPaddingBottom() + getPaddingTop();
            controlMaxThickness += getPaddingLeft() + getPaddingRight();
        }

        if (orientation == HORIZONTAL) {
            this.setMeasuredDimension(
                    resolveSize(controlMaxLength, widthMeasureSpec),
                    resolveSize(controlMaxThickness, heightMeasureSpec));
        } else {
            this.setMeasuredDimension(
                    resolveSize(controlMaxThickness, widthMeasureSpec),
                    resolveSize(controlMaxLength, heightMeasureSpec));
        }
    }

    private int getVerticalSpacing(LayoutParams lp) {
        int vSpacing;
        if (lp.verticalSpacingSpecified()) {
            vSpacing = lp.verticalSpacing;
        } else {
            vSpacing = this.verticalSpacing;
        }
        return vSpacing;
    }

    private int getHorizontalSpacing(LayoutParams lp) {
        int hSpacing;
        if (lp.horizontalSpacingSpecified()) {
            hSpacing = lp.horizontalSpacing;
        } else {
            hSpacing = this.horizontalSpacing;
        }
        return hSpacing;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y
                    + child.getMeasuredHeight());
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        this.drawDebugInfo(canvas, child);
        return more;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet,
                R.styleable.FlowLayout);
        try {
            horizontalSpacing = a.getDimensionPixelSize(
                    R.styleable.FlowLayout_horizontalSpacing, 0);
            verticalSpacing = a.getDimensionPixelSize(
                    R.styleable.FlowLayout_verticalSpacing, 0);
            orientation = a.getInteger(R.styleable.FlowLayout_flow_orientation,
                    HORIZONTAL);
            debugDraw = a.getBoolean(R.styleable.FlowLayout_debugDraw, false);
        } finally {
            a.recycle();
        }
    }

    private void drawDebugInfo(Canvas canvas, View child) {
        if (!debugDraw) {
            return;
        }

        Paint childPaint = this.createPaint(0xffffff00);
        Paint layoutPaint = this.createPaint(0xff00ff00);
        Paint newLinePaint = this.createPaint(0xffff0000);

        LayoutParams lp = (LayoutParams) child.getLayoutParams();

        if (lp.horizontalSpacing > 0) {
            float x = child.getRight();
            float y = child.getTop() + child.getHeight() / 2.0f;
            canvas.drawLine(x, y, x + lp.horizontalSpacing, y, childPaint);
            canvas.drawLine(x + lp.horizontalSpacing - 4.0f, y - 4.0f, x
                    + lp.horizontalSpacing, y, childPaint);
            canvas.drawLine(x + lp.horizontalSpacing - 4.0f, y + 4.0f, x
                    + lp.horizontalSpacing, y, childPaint);
        } else if (this.horizontalSpacing > 0) {
            float x = child.getRight();
            float y = child.getTop() + child.getHeight() / 2.0f;
            canvas.drawLine(x, y, x + this.horizontalSpacing, y, layoutPaint);
            canvas.drawLine(x + this.horizontalSpacing - 4.0f, y - 4.0f, x
                    + this.horizontalSpacing, y, layoutPaint);
            canvas.drawLine(x + this.horizontalSpacing - 4.0f, y + 4.0f, x
                    + this.horizontalSpacing, y, layoutPaint);
        }

        if (lp.verticalSpacing > 0) {
            float x = child.getLeft() + child.getWidth() / 2.0f;
            float y = child.getBottom();
            canvas.drawLine(x, y, x, y + lp.verticalSpacing, childPaint);
            canvas.drawLine(x - 4.0f, y + lp.verticalSpacing - 4.0f, x, y
                    + lp.verticalSpacing, childPaint);
            canvas.drawLine(x + 4.0f, y + lp.verticalSpacing - 4.0f, x, y
                    + lp.verticalSpacing, childPaint);
        } else if (this.verticalSpacing > 0) {
            float x = child.getLeft() + child.getWidth() / 2.0f;
            float y = child.getBottom();
            canvas.drawLine(x, y, x, y + this.verticalSpacing, layoutPaint);
            canvas.drawLine(x - 4.0f, y + this.verticalSpacing - 4.0f, x, y
                    + this.verticalSpacing, layoutPaint);
            canvas.drawLine(x + 4.0f, y + this.verticalSpacing - 4.0f, x, y
                    + this.verticalSpacing, layoutPaint);
        }

        if (lp.newLine) {
            if (orientation == HORIZONTAL) {
                float x = child.getLeft();
                float y = child.getTop() + child.getHeight() / 2.0f;
                canvas.drawLine(x, y - 6.0f, x, y + 6.0f, newLinePaint);
            } else {
                float x = child.getLeft() + child.getWidth() / 2.0f;
                float y = child.getTop();
                canvas.drawLine(x - 6.0f, y, x + 6.0f, y, newLinePaint);
            }
        }
    }

    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(2.0f);
        return paint;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        private static int NO_SPACING = -1;
        private int x;
        private int y;
        private int horizontalSpacing = NO_SPACING;
        private int verticalSpacing = NO_SPACING;
        private boolean newLine = false;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.readStyleParameters(context, attributeSet);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public boolean horizontalSpacingSpecified() {
            return horizontalSpacing != NO_SPACING;
        }

        public boolean verticalSpacingSpecified() {
            return verticalSpacing != NO_SPACING;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private void readStyleParameters(Context context,
                                         AttributeSet attributeSet) {
            TypedArray a = context.obtainStyledAttributes(attributeSet,
                    R.styleable.FlowLayout_LayoutParams);
            try {
                horizontalSpacing = a
                        .getDimensionPixelSize(
                                R.styleable.FlowLayout_LayoutParams_layout_horizontalSpacing,
                                NO_SPACING);
                verticalSpacing = a
                        .getDimensionPixelSize(
                                R.styleable.FlowLayout_LayoutParams_layout_verticalSpacing,
                                NO_SPACING);
                newLine = a.getBoolean(
                        R.styleable.FlowLayout_LayoutParams_layout_newLine,
                        false);
            } finally {
                a.recycle();
            }
        }
    }

    public OnTagClickListener getOnTagClickListener() {
        return onTagClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    public interface OnTagClickListener {
        public abstract void onTagClick(Tag tag);

        public abstract void onTagDelete(Tag tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flowtag_item_delete_img:
                if ((v.getParent() instanceof LinearLayout)) {
                    Tag localTag = (Tag) ((View) v.getParent()).getTag();
                    if (this.onTagClickListener != null) {
                        this.onTagClickListener.onTagDelete(localTag);
                    }
                }
                break;
            case R.id.flowtag_item_linear:
                if ((v instanceof LinearLayout)) {
                    Tag localTag = (Tag) v.getTag();
                    if (this.onTagClickListener != null
                            && !localTag.isDeletedOperation) {
                        this.onTagClickListener.onTagClick(localTag);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        v.setEnabled(false);
        if (v instanceof LinearLayout) {
            for (Tag tag : mTags) {
                LinearLayout linear = (LinearLayout) this.getViewByTag(tag);
                ImageView img = (ImageView) linear
                        .findViewById(R.id.flowtag_item_delete_img);
                if (tag.id != 0 && img.getVisibility() != View.VISIBLE) {
                    img.setVisibility(View.VISIBLE);
                    tag.isDeletedOperation = true;
                } else {
                    img.setVisibility(View.GONE);
                    tag.isDeletedOperation = false;
                }
            }
            new CountDownTimer(1000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    v.setEnabled(true);
                }
            }.start();
        }
        return false;
    }

    /**
     * 添加标签集合
     */
    public void setTags(List<? extends Tag> lists) {
        removeAllViews();
        mTags.clear();
        invalidate();
        for (int i = 0; i < lists.size(); i++) {
            addTag((Tag) lists.get(i));
        }

    }

    /**
     * 获取标签集合
     */
    public List<Tag> getTagList() {
        return mTags;
    }

    /**
     * 单个添加标签
     */
    public void addTag(String title, int id) {
        addTag(new Tag(title, id));
    }

    /**
     * 单个添加标签
     */
    public void addTag(Tag tag) {
        if (!mTags.contains(tag)) {
            mTags.add(tag);
            inflateTagView(tag);
        }
    }

    /**
     * 获取当前标签的布局
     */
    public View getViewByTag(Tag tag) {
        return findViewWithTag(tag);
    }

    /**
     * 删除某一标签布局
     */
    public void removeTag(Tag tag) {
        mTags.remove(tag);
        removeView(getViewByTag(tag));
    }

    private void inflateTagView(Tag t) {

        LinearLayout linear = (LinearLayout) View.inflate(getContext(),
                R.layout.flowlayout_item, null);
        TextView localTagView = (TextView) linear
                .findViewById(R.id.flowtag_item_tv);
        ImageView deleteImg = (ImageView) linear
                .findViewById(R.id.flowtag_item_delete_img);
        deleteImg.setVisibility(View.GONE);
        localTagView.setText(t.title);
        linear.setTag(t);
        deleteImg.setOnClickListener(this);
        linear.setOnLongClickListener(this);
        linear.setOnClickListener(this);
        addView(linear);
    }

    public static class Tag {
        public Tag() {
        }

        public Tag(String title, int id) {
            this.id = id;
            this.title = title;
        }

        public int id;
        /**
         * 标签的文字内容
         */
        public String title;
        /**
         * 是否正在进行删除操作
         */
        public boolean isDeletedOperation = false;

        @Override
        public boolean equals(Object o) {
            if ((o instanceof Tag) && this.title.equals(((Tag) o).title)) {
                return true;
            }
            return super.equals(o);
        }
    }

}
