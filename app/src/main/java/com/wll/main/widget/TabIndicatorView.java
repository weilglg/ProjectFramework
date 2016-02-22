package com.wll.main.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wll.main.R;
import com.wll.main.model.TabInfo;

import java.util.List;


/**
 * <b>选项卡式控件</b><br/>
 * 当绑定ViewPager后可以实现下滑线跟随ViewPager的滑动而滑动<br/>
 * 当不需要绑定ViewPager时，{@link #init(int, List, ViewPager)}第三个置为空即可。此时可以重写
 * {@link #setOnTabChangeListener(OnTabChangeListener)}方法实现对选项卡切换的监听
 */
public class TabIndicatorView extends LinearLayout implements
		View.OnClickListener, OnFocusChangeListener, OnTouchListener {

	private static final float FOOTER_LINE_HEIGHT = 4.0f;

	private static final int FOOTER_COLOR = 0xFFFFC445;

	private static final int UNDERLINE_COLOR = 0x1A000000;

	private static final int TRANSPARENT_COLOR = Color.parseColor("#00000000");

	private static final int BLACK_COLOR = Color.parseColor("#000000");

	/**
	 * 记录当前滑动的距离(以X轴左边为参照)
	 */
	private int mCurrentScroll = 0;

	/**
	 * 选项卡列表
	 */
	private List<? extends TabInfo> mTabs;

	/**
	 * 选项卡所依赖的ViewPager
	 */
	private ViewPager mViewPager;

	/**
	 * 选项卡普通状态下的字体颜色
	 */
	private int mTextColorNormal;

	/**
	 * 选项卡被选中状态下的字体颜色
	 */
	private int mTextColorSelected;

	/**
	 * 普通状态和选中状态下的字体大小
	 */
	private int mTextSizeNormal;
	private int mTextSizeSelected;

	private Paint mPaintFooterLine;

	private Paint mPaintTabtip;

	/**
	 * 滚动条的高度
	 */
	private float mFooterLineHeight;

	/**
	 * 滚动条所占的Tab宽度的比值
	 */
	private int mFooterLineWidthRatio = 1;

	/**
	 * 是否显示滑动条
	 */
	private boolean mIsShowFooterLine = false;

	/**
	 * 是否显示下划线
	 */
	private boolean mIsShowUnderline = false;

	/**
	 * 滑动条的颜色
	 */
	private int footerColor = TRANSPARENT_COLOR;

	/**
	 * 滑动条的颜色
	 */
	private int underlineColor = TRANSPARENT_COLOR;

	/**
	 * 点击时Tab的背景颜色
	 */
	private int mOnTouchBackgroundColor = -1;

	/**
	 * 是否显示tab之间的竖线
	 */
	private boolean mIsShowVerticalLine;

	/**
	 * Tab之间的数显的宽度
	 */
	private float mVerticalLineWidth = 0;

	/**
	 * Tab之间竖线的高度占总布局高度的比值
	 */
	private int mVerticalLineHeightRatio = 1;

	/**
	 * Tab之间竖线的颜色
	 */
	private int mVerticalLineColor;

	/**
	 * 当前选项卡的下标，从0开始
	 */
	private int mSelectedTab = 0;

	private Context mContext;

	private final int BASE_ID = 0xffff00;

	private int mCurrID = 0;

	/**
	 * 表示选项卡总共有几个
	 */
	private int mTotal = 1;

	private LayoutInflater mInflater;

	private int mPaddingBottom;

	private int mPaddingTop;

	private int mTabSelectedColor;

	/**
	 * 选项卡切换时的监听(当不绑定ViewPager时添加该监听)
	 */
	public interface OnTabChangeListener {
		/**
		 * 被选中的选项卡
		 */
		void onTabChange(int index);
	}

	private OnTabChangeListener mChangeListener;

	public OnTabChangeListener getOnTabChangeListener() {
		return mChangeListener;
	}

	public void setOnTabChangeListener(OnTabChangeListener listener) {
		mChangeListener = listener;
	}

	public TabIndicatorView(Context context) {
		this(context, null);
	}

	public TabIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setOnFocusChangeListener(this);
		initAttrs(context, attrs);
		initDraw(mFooterLineHeight, footerColor);

	}

	private void initAttrs(Context context, AttributeSet attrs) {
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabIndicator);

		// 字体颜色
		mTextColorNormal = a
				.getColor(R.styleable.TabIndicator_indicator_textColorNormal,
						BLACK_COLOR);
		mTextColorSelected = a.getColor(
				R.styleable.TabIndicator_indicator_textColorSelected,
				BLACK_COLOR);

		// 获得滑动条的颜色
		footerColor = a.getColor(
				R.styleable.TabIndicator_indicator_footerColor, FOOTER_COLOR);

		// 获得下划线的颜色
		underlineColor = a
				.getColor(R.styleable.TabIndicator_indicator_underlineColor,
						UNDERLINE_COLOR);

		// 是否显示下划线
		mIsShowUnderline = a.getBoolean(
				R.styleable.TabIndicator_indicator_underlineVisibility, false);

		// 字体大小
		mTextSizeNormal = a.getDimensionPixelSize(
				R.styleable.TabIndicator_indicator_textSizeNormal, 14);
		mTextSizeSelected = a.getDimensionPixelSize(
				R.styleable.TabIndicator_indicator_textSizeSelected,
				mTextSizeNormal);

		// 滚动条相关
		mFooterLineHeight = a.getDimension(
				R.styleable.TabIndicator_indicator_footerLineHeight,
				FOOTER_LINE_HEIGHT);
		mFooterLineWidthRatio = a.getInt(
				R.styleable.TabIndicator_indicator_footerLineWidthRatio, 1);
		mIsShowFooterLine = a.getBoolean(
				R.styleable.TabIndicator_indicator_footerLineVisibility, false);

		// 点击时背景的颜色
		mOnTouchBackgroundColor = a.getColor(
				R.styleable.TabIndicator_indicator_onTouchBackgroundColor, 0);

		mTabSelectedColor = a.getColor(
				R.styleable.TabIndicator_indicator_tabSelectedColor, 0);

		// 竖线相关
		mVerticalLineHeightRatio = a.getInt(
				R.styleable.TabIndicator_indicator_verticalLineHeightRatio, 1);
		mVerticalLineColor = a.getColor(
				R.styleable.TabIndicator_indicator_verticalLineColor, 0);
		mVerticalLineWidth = a.getDimension(
				R.styleable.TabIndicator_indicator_verticalLineWidth, 0);
		mIsShowVerticalLine = a.getBoolean(
				R.styleable.TabIndicator_indicator_verticalLineVisibility,
				false);

		mPaddingTop = (int) a.getDimension(
				R.styleable.TabIndicator_indicator_textPaddingTop, 0);
		mPaddingBottom = (int) a.getDimension(
				R.styleable.TabIndicator_indicator_textPaddingBottom, 0);

		a.recycle();
	}

	/**
	 * Initialize draw objects
	 */
	private void initDraw(float footerLineHeight, int footerColor) {
		// 标题下面的指示线
		mPaintFooterLine = new Paint();
		// mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
		// mPaintFooterLine.setStrokeWidth(footerLineHeight);
		// mPaintFooterLine.setColor(footerColor);
		// 设置抗锯齿
		mPaintFooterLine.setAntiAlias(true);
		mPaintFooterLine.setStyle(Style.FILL);

		// Tab之间竖线
		mPaintTabtip = new Paint();
		mPaintTabtip.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintTabtip.setStrokeWidth(mVerticalLineWidth);
		mPaintTabtip.setColor(mVerticalLineColor);

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 这个是核心函数，选项卡是用canvas画出来的。所有的invalidate方法均会触发onDraw
	 * 大意是这样的：当页面滚动的时候，会有一个滚动距离，然后onDraw被触发后， 就会在新位置重新画上滚动条（其实就是画线）
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mTabs == null || mTabs.size() == 0) {
			return;
		}
		// 单个选项卡的宽度
		float mPerItemWidth = 0;
		int tabID = 0;
		float scroll_x = 0;

		if (mViewPager != null) {
			mPerItemWidth = ((getWidth() + mViewPager.getPageMargin()) * 1f)
					/ mTotal;
			tabID = mSelectedTab;
			// 下面是计算本次滑动的距离
			scroll_x = (mCurrentScroll - (tabID * (getWidth() + mViewPager
					.getPageMargin()))) / mTotal;
		} else {
			mPerItemWidth = (getWidth() * 1f) / mTotal;
			tabID = mSelectedTab;
			// 下面是计算本次滑动的距离
			scroll_x = (mCurrentScroll - (tabID * getWidth())) / mTotal;
		}

		if (mIsShowUnderline) {
			// 绘制默认的下划线
			mPaintFooterLine.setColor(underlineColor);
			canvas.drawRect(0, getHeight() - mFooterLineHeight, getWidth(),
					getHeight(), mPaintFooterLine);
		}

		// 如果需要才进行下划线的绘制
		if (mIsShowFooterLine) {
			// 下面就是如何画线了

			// 计算滑动条的宽度
			float footerLineWidth = mFooterLineWidthRatio > 0 ? mPerItemWidth
					/ mFooterLineWidthRatio : mPerItemWidth;
			// 计算滑动条离左右的间距
			float offset = (mPerItemWidth - footerLineWidth) / 2;
			float left_x = 0f;
			float right_x = 0f;
			// 根据选中的Tab的位置计算下划线绘制时的开始与结束位置
			if (mSelectedTab != 0 && mSelectedTab < mTabs.size() - 1) {
				// 当选中的Tab在中间时
				left_x = mSelectedTab * mPerItemWidth + scroll_x
						+ mVerticalLineWidth / 2 + offset;
				right_x = (mSelectedTab + 1) * mPerItemWidth + scroll_x
						- mVerticalLineWidth / 2 - offset;
			} else if (mSelectedTab == mTabs.size() - 1) {
				// 当选中的Tag在最后一个时
				left_x = mSelectedTab * mPerItemWidth + scroll_x
						+ mVerticalLineWidth + offset;
				right_x = (mSelectedTab + 1) * mPerItemWidth + scroll_x
						- offset;
			} else {
				// 当选中的Tab是第一个时，X轴绘制的终点是一个tab的长度
				left_x = mSelectedTab * mPerItemWidth + scroll_x + offset;
				right_x = (mSelectedTab + 1) * mPerItemWidth + scroll_x
						- mVerticalLineWidth / 2 - offset;
			}

			// 计算下滑线在顶部坐标
			float top_y = getHeight() - mFooterLineHeight;
			// 计算下滑线的底部坐标
			float bottom_y = getHeight();
			// 因为绘制的下滑线可以设置高度所以这边绘制一个实心矩形

			mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
			mPaintFooterLine.setColor(footerColor);

			canvas.drawRect(left_x, top_y, right_x, bottom_y, mPaintFooterLine);
		}

		// 判断是否显示Tab中间的竖线
		if (mIsShowVerticalLine) {
			// 计算绘制竖线Y轴的开始位置
			float startY = mVerticalLineHeightRatio > 0 ? (getHeight() - getHeight()
					/ mVerticalLineHeightRatio) / 2
					: 0;
			// 计算绘制竖线Y轴的结束位置
			float stopY = getHeight() - startY - mFooterLineHeight;
			for (int i = 0; i < mTabs.size(); i++) {
				int index = i + 1;
				if (index != mTabs.size()) {
					float temp = mPerItemWidth * (index) - 0.5f;
					// 计算分割线X轴的开始位置
					canvas.drawLine(temp, startY, temp, stopY, mPaintTabtip);
				}

			}
		}
	}

	/**
	 * 获取指定下标的选项卡的标题
	 */
	private String getTab(int pos) {
		// Set the default Tab
		String Tab = "Tab " + pos;
		// If the TabProvider exist
		if (mTabs != null && mTabs.size() > pos) {
			Tab = mTabs.get(pos).getName();
		}
		return Tab;
	}

	/**
	 * 获取指定下标的选项卡的图标资源id（如果设置了图标的话）
	 */
	private int getSelectedIcon(int pos) {
		int ret = 0;
		if (mTabs != null && mTabs.size() > pos) {
			ret = mTabs.get(pos).getSelectedIcon();
		}
		return ret;
	}

	/**
	 * 获取指定下标的选项卡的图标资源id（如果设置了图标的话）
	 */
	private int getNormalIcon(int pos) {
		int ret = 0;
		if (mTabs != null && mTabs.size() > pos) {
			ret = mTabs.get(pos).getNormalIcon();
		}
		return ret;
	}

	// 当页面滚动的时候，重新绘制滚动条
	public void onScrolled(int h) {
		mCurrentScroll = h;
		invalidate();
	}

	// 当页面切换的时候，重新绘制滚动条
	public synchronized void onSwitched(int position) {
		if (mSelectedTab == position) {
			return;
		}
		setCurrentTab(position);
		// invalidate();
	}

	// 初始化选项卡
	public void init(int startPos, List<? extends TabInfo> tabs,
					 ViewPager mViewPager) {
		removeAllViews();
		this.mViewPager = mViewPager;
		this.mTabs = tabs;
		if (mTabs.size() > 0) {
			this.mTotal = tabs.size();
		}
		for (int i = 0; mTotal > i; i++) {
			add(getTab(i), getNormalIcon(i));
		}
		setCurrentTab(startPos);
		// invalidate();
	}

	// 添加选项卡
	protected void add(String label, int icon) {
		/* 加载Tab布局 */
		View tabIndicator = mInflater.inflate(R.layout.tab_indicator_item,
				this, false);
		// 获得Tab的TextView控件
		final TextView tv = (TextView) tabIndicator
				.findViewById(R.id.tab_title);
		// 设置默认的字体颜色
		tv.setTextColor(mTextColorNormal);
		// 设置上下间距
		tv.setPadding(0, mPaddingTop, 0, mPaddingBottom);
		// 如果使用了自定义个字体大小属性就进行设置字体大小的设置
		if (mTextSizeNormal > 0) {
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal);
		}
		// 设置文本
		tv.setText(label);
		// 如果需要显示图片的话就进行图片的显示
		if (icon != 0) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
		}
		// 给每个Tab布局设置一个ID
		tabIndicator.setId(BASE_ID + (mCurrID++));
		// 设置Tab的点击事件
		tabIndicator.setOnClickListener(this);
		tabIndicator.setOnTouchListener(this);

		// 当有下滑线时设置下离下边距的距离，这样文字和滑动条就不会重叠
		LayoutParams lP = (LayoutParams) tabIndicator.getLayoutParams();
		lP.gravity = Gravity.CENTER_VERTICAL;
		if (mIsShowFooterLine) {
			lP.bottomMargin = (int) (mFooterLineHeight);
		}

		// 将当前Tab加入到自定义的控件中
		addView(tabIndicator);
	}

	@Override
	public void onClick(View v) {
		// 根据ID算出当前点击的Tab是第几个
		int position = v.getId() - BASE_ID;
		setCurrentTab(position);
	}

	public int getTabCount() {
		return getChildCount();
	}

	// 设置当前选中的选项卡
	public synchronized void setCurrentTab(int index) {
		if (index < 0 || index >= getTabCount()) {
			return;
		}
		// 先将上一个Tab的属性还原
		View oldTab = getChildAt(mSelectedTab);
		oldTab.setSelected(false);
		setTabTextSize(oldTab, false, mSelectedTab);

		// 将当前选中的选项卡的下标保存起来
		mSelectedTab = index;
		// 将当前选中的Tab的属性设置为选中
		View newTab = getChildAt(mSelectedTab);
		newTab.setSelected(true);
		setTabTextSize(newTab, true, index);

		if (mViewPager != null) {
			// 将跟指示器中相对应的Fragment设置为显示状态
			mViewPager.setCurrentItem(mSelectedTab, false);
		} else if (mChangeListener != null) {
			mChangeListener.onTabChange(mSelectedTab);
		}

		invalidate();
	}

	/**
	 * 根据选项卡的状态设置选项卡的属性
	 *
	 * @param tab
	 *            选项卡布局
	 * @param selected
	 *            选项卡的状态
	 * @param index
	 *            选项卡的下标
	 */
	private void setTabTextSize(View tab, boolean selected, int index) {
		TextView tv = (TextView) tab.findViewById(R.id.tab_title);
		// 根据选项卡是否被选中设置不同的属性
		if (selected) {
			// 设置选中时的图片
			tv.setCompoundDrawablesWithIntrinsicBounds(0,
					getSelectedIcon(index), 0, 0);
			// 设置选中时字体的大小
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeSelected);
			// 设置选中时的字体颜色
			tv.setTextColor(mTextColorSelected);
			// 设置选中时的背景颜色
			tab.setBackgroundColor(mTabSelectedColor);
		} else {
			// 设置未选中时的图片，如果有资源的话才进行设置
			if (getNormalIcon(index) > 0) {
				tv.setCompoundDrawablesWithIntrinsicBounds(0,
						getNormalIcon(index), 0, 0);
			}
			// 设置未选中时字体的大小，如果进行了字体大小的设置
			if (mTextSizeNormal > 0) {
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal);
			}
			// 设置未选中时字体的颜色
			tv.setTextColor(mTextColorNormal);
			// 设置未选中时背景的颜色为透明
			tab.setBackgroundColor(TRANSPARENT_COLOR);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == this && hasFocus && getTabCount() > 0) {
			getChildAt(mSelectedTab).requestFocus();
			return;
		}
		if (hasFocus) {
			int i = 0;
			int numTabs = getTabCount();
			while (i < numTabs) {
				if (getChildAt(i) == v) {
					setCurrentTab(i);
					break;
				}
				i++;
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mIsShowVerticalLine) {
			// 设置每个Tab的偏移量
			int count = getChildCount();
			for (int i = 0; i < getChildCount(); i++) {
				View view = getChildAt(i);
				LayoutParams lP = (LayoutParams) view.getLayoutParams();
				if (i == 0) {
					lP.rightMargin = (int) mVerticalLineWidth;
				} else if (i == count - 1) {
					lP.leftMargin = (int) mVerticalLineWidth;
				} else {
					lP.rightMargin = (int) mVerticalLineWidth / 2;
					lP.leftMargin = (int) mVerticalLineWidth / 2;
				}

			}
		}
		if (mViewPager != null) {
			if (mCurrentScroll == 0 && mSelectedTab != 0) {
				mCurrentScroll = (getWidth() + mViewPager.getPageMargin())
						* mSelectedTab;
			}
		} else {
			mCurrentScroll = getWidth() * mSelectedTab;
		}

	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& mOnTouchBackgroundColor != -1) {
			v.setBackgroundColor(mOnTouchBackgroundColor);
			// 更改为按下时的背景图片
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// 改为抬起时的图片
			v.setBackgroundColor(TRANSPARENT_COLOR);
		}
		return false;
	}
}
