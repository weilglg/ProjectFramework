package com.wll.main.base;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wll.main.R;
import com.wll.main.adapter.IndicatorTabPageAdapter;
import com.wll.main.widget.ViewPagerIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wll on 2015/11/9.
 * 有选项卡的Fragment的基类
 */
public abstract class BaseIndicatorFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public static final String EXTRA_TAB = "tab";

    /**
     * 选中的选项卡的下标
     */
    public int mCurrentTab = 0;

    protected boolean isInitTabs;

    // 存放选项卡信息的列表
    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    // ViewPager adapter
    protected IndicatorTabPageAdapter myAdapter = null;

    // ViewPager
    protected ViewPager mPager;
    private ViewPagerIndicatorView mIndicator;

    @Override
    public void onDestroy() {
        if (mTabs != null) {
            mTabs.clear();
        }
        if (myAdapter != null) {
            myAdapter = null;
        }
        if (mPager != null) {
            mPager = null;
        }
        mIndicator = null;
        super.onDestroy();
    }

    @Override
    protected void initViews(View v) {
        // 这里初始化界面
        if (!isInitTabs) {
            initTabsInfo(mTabs);
            isInitTabs = true;
            Bundle bundle = getArguments();
            if (bundle != null) {
                int tabIndex = bundle.getInt(EXTRA_TAB, mCurrentTab);
                if (tabIndex >= 0 && tabIndex < mTabs.size()) {
                    mCurrentTab = tabIndex;
                }
            }
            myAdapter = new IndicatorTabPageAdapter(getActivity(), getChildFragmentManager(), mTabs);

            mPager = (ViewPager) v.findViewById(getViewPagerId());
            mPager.setAdapter(myAdapter);
            mPager.addOnPageChangeListener(this);
            mPager.setOffscreenPageLimit(1);
            // 设置ViewPager内部页面之间的间距
            mPager.setPageMargin(getResources().getDimensionPixelSize(
                    R.dimen.viewpager_margin_width));
            // 设置ViewPager内部页面间距的Drawable
            mPager.setPageMarginDrawable(R.color.viewpager_margin_color);

            mIndicator = (ViewPagerIndicatorView) v.findViewById(getViewPagerIndicatorViewId());
            mIndicator.init(mCurrentTab, mTabs, mPager);
            mPager.setCurrentItem(mCurrentTab, false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        mIndicator.onScrolled((mPager.getWidth() + mPager.getPageMargin())
                * position + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        mIndicator.onSwitched(position);
        mCurrentTab = position;
        onViewPagerSwitch(mCurrentTab, mTabs.get(mCurrentTab).getFragment());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    protected TabInfo getFragmentById(int tabId) {
        if (mTabs == null)
            return null;
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            TabInfo tab = mTabs.get(index);
            if (tab.getId() == tabId) {
                return tab;
            }
        }
        return null;
    }

    /**
     * 跳转到任意选项卡
     *
     * @param tabId 选项卡下标
     */
    public void navigate(int tabId) {
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            if (mTabs.get(index).getId() == tabId) {
                mPager.setCurrentItem(index, false);
            }
        }
    }

    /**
     * 在这里提供要显示的选项卡数据
     */
    protected abstract void initTabsInfo(List<TabInfo> tabs);

    /**
     * 在这里提供初始化后的Fragment的特性初始化操作
     */
    protected abstract void onInitFragmentEnd(int index, Fragment fragment);

    /**
     * 切换选项卡监听器
     */
    protected abstract void onViewPagerSwitch(int index, Fragment fragment);

    /**
     * 返回ViewPager的控件ID
     */
    protected abstract int getViewPagerId();

    /**
     * 返回ViewPager指示器的控件资源ID
     */
    protected abstract int getViewPagerIndicatorViewId();

    public class TabInfo extends com.wll.main.model.TabInfo{

        public TabInfo(int id, String name, Class<? extends Fragment> clazz) {
            super(id, name, clazz);
        }

        public TabInfo(int id, String name, int iconId, Class<? extends Fragment> clazz) {
            super(id, name, iconId, clazz);
        }

        public TabInfo(int id, String name, int normalIcon, int selectedIcon, Class<? extends Fragment> clazz) {
            super(id, name, normalIcon, selectedIcon, clazz);
        }

        public TabInfo(Parcel p) {
            super(p);
        }

        @Override
        public Fragment createFragment(int pos) {
            Fragment fragment = super.createFragment(pos);
            onInitFragmentEnd(pos, fragment);
            return fragment;
        }

        public final Creator<TabInfo> CREATOR = new Creator<TabInfo>() {
            public TabInfo createFromParcel(Parcel p) {
                return new TabInfo(p);
            }

            public TabInfo[] newArray(int size) {
                return new TabInfo[size];
            }
        };
    }

}
