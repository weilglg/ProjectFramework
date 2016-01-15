package com.wll.main.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.wll.main.R;


public class NoScrollGridView extends GridView {

	private boolean isDispatchDraw = true;

	/** 设置是否需要分割线 */
	public void setDispatchDraw(boolean isDispatchDraw) {
		this.isDispatchDraw = isDispatchDraw;
	}

	public NoScrollGridView(Context context) {
		super(context);

	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!isDispatchDraw) {
			return;
		}
		View localView1 = getChildAt(0);
		int column = 0;
		if (localView1 != null) {
			column = getWidth() / localView1.getWidth();
		}
		int childCount = getChildCount();
		Paint localPaint;
		localPaint = new Paint();
		localPaint.setStyle(Paint.Style.STROKE);
		localPaint.setColor(getContext().getResources().getColor(
				R.color.base_home_tab_onTouchColor));
		for (int i = 0; i < childCount; i++) {
			View cellView = getChildAt(i);
			if ((i + 1) % column == 0) {
				canvas.drawLine(cellView.getLeft(), cellView.getBottom(),
						cellView.getRight(), cellView.getBottom(), localPaint);
			} else if ((i + 1) > (childCount - (childCount % column))) {
				canvas.drawLine(cellView.getRight(), cellView.getTop(),
						cellView.getRight(), cellView.getBottom(), localPaint);
			} else {
				canvas.drawLine(cellView.getRight(), cellView.getTop(),
						cellView.getRight(), cellView.getBottom(), localPaint);
				canvas.drawLine(cellView.getLeft(), cellView.getBottom(),
						cellView.getRight(), cellView.getBottom(), localPaint);
			}
		}
		if (column != 0 && childCount % column != 0) {
			for (int j = 0; j < (column - childCount % column); j++) {
				View lastView = getChildAt(childCount - 1);
				canvas.drawLine(lastView.getRight() + lastView.getWidth() * j,
						lastView.getTop(),
						lastView.getRight() + lastView.getWidth() * j,
						lastView.getBottom(), localPaint);
			}
		}
	}

}
