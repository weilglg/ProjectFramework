package com.wll.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class NoScrollListView extends ListView {

	private boolean isDispatachTouchEvent = true;

	/** 设置是否响应滑动事件  false代表不响应*/
	public void setDispatachTouchEvent(boolean isDispatachTouchEvent) {
		this.isDispatachTouchEvent = isDispatachTouchEvent;
	}

	public NoScrollListView(Context context) {
		super(context);
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

	/**
	 * 设置为不响应滑动事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE && !isDispatachTouchEvent) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}
}