/*
 * @author http://blog.csdn.net/singwhatiwanna
 */
package com.wll.main.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;

import com.wll.main.R;
import com.wll.main.adapter.IndicatorTabPageAdapter;
import com.wll.main.widget.TabIndicatorView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseIndicatorActivity extends BaseActivity implements OnPageChangeListener,
        TabIndicatorView.OnTabChangeListener {

    public static final String EXTRA_TAB = "tab";

    /**
     * 选中的选项卡的下标
     */
    public int mCurrentTab = 0;

    // 存放选项卡信息的列表
    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    // ViewPager adapter
    protected IndicatorTabPageAdapter myAdapter = null;

    // ViewPager
    protected ViewPager mPager;

    // 选项卡控件
    protected TabIndicatorView mIndicator;


    @Override
    protected void onDestroy() {
        mTabs.clear();
        mTabs = null;
        if (mPager != null) {
            myAdapter.notifyDataSetChanged();
            myAdapter = null;
            mPager.setAdapter(null);
            mPager = null;
        }
        mIndicator = null;
        super.onDestroy();
    }

    @Override
    protected void initViews() {
        super.initViews();
        // 这里初始化界面
        initTabsInfo(mTabs);
        Intent intent = getIntent();
        if (intent != null) {
            int tabIndex = intent.getIntExtra(EXTRA_TAB, mCurrentTab);
            if (tabIndex >= 0 && tabIndex < mTabs.size()) {
                mCurrentTab = tabIndex;
            }
        }
        // 根据ID获得选项卡对象
        mIndicator = (TabIndicatorView) findViewById(getTagIndicatorViewId());
        int layoutResId = getViewPagerId();
        if (layoutResId != 0) {
            myAdapter = new IndicatorTabPageAdapter(this, getSupportFragmentManager(), mTabs);
            mPager = (ViewPager) findViewById(getViewPagerId());
            mPager.setAdapter(myAdapter);
            mPager.setOnPageChangeListener(this);
            mPager.setOffscreenPageLimit(0);
            // 设置ViewPager内部页面之间的间距
            mPager.setPageMargin(getResources().getDimensionPixelSize(
                    R.dimen.viewpager_margin_width));
            // 设置ViewPager内部页面间距的Drawable
            mPager.setPageMarginDrawable(android.R.color.transparent);
            mPager.setCurrentItem(mCurrentTab, false);
        } else {
            mIndicator.setOnTabChangeListener(this);
        }

        // 初始化选项卡
        mIndicator.init(mCurrentTab, mTabs, mPager);
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

    protected TabInfo getTabInfoById(int tabId) {
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
                if (mPager != null) {
                    mPager.setCurrentItem(index, false);
                } else if (mIndicator != null
                        && mIndicator.getOnTabChangeListener() != null) {
                    mIndicator.getOnTabChangeListener().onTabChange(index);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 在这里提供要显示的选项卡数据
     */
    protected abstract void initTabsInfo(List<TabInfo> tabs);

    /**
     * 在这里提供初始化后的Fragment的特性初始化操作(没有ViewPager时不用重写这个方法)
     */
    protected void onInitFragmentEnd(int index, Fragment fragment) {

    }

    /**
     * 切换选项卡监听器(没有ViewPager时不用重写这个方法)
     */
    protected void onViewPagerSwitch(int index, Fragment fragment) {

    }

    /**
     * 返回ViewPager的控件ID，如果没有ViewPager不用重写
     */
    protected int getViewPagerId() {
        return 0;
    }

    /**
     * 选项卡被选中时的回调函数
     *
     * @param index 被选中的选项卡的下标
     */
    @Override
    public void onTabChange(int index) {

    }

    /**
     * 返回选项卡控件的布局ID
     */
    protected abstract int getTagIndicatorViewId();


    public class TabInfo extends com.wll.main.model.TabInfo {

        public TabInfo(int id, String name, Class<? extends Fragment> clazz) {
            super(id, name, clazz);
        }

        public TabInfo(int id, String name, int iconid, Class<? extends Fragment> clazz) {
            super(id, name, iconid, clazz);
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
