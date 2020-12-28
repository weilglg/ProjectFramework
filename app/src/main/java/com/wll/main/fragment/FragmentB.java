package com.wll.main.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.wll.main.R;
import com.wll.main.base.BaseIndicatorFragment;

import java.util.List;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentB extends BaseIndicatorFragment {
    @Override
    protected int getViewResId() {
        return R.layout.fragment_b;
    }

    @Override
    protected void initViews(View v) {
        super.initViews(v);
    }

    @Override
    protected void initTabsInfo(List<TabInfo> tabs) {
        tabs.add(new TabInfo(10, "电脑", SubFragmentA.class));
        tabs.add(new TabInfo(11, "手机", SubFragmentA.class));
        tabs.add(new TabInfo(12, "手机2", SubFragmentA.class));
        tabs.add(new TabInfo(13, "手机3", SubFragmentA.class));
        tabs.add(new TabInfo(14, "手机4", SubFragmentA.class));
//        tabs.add(new TabInfo(12, "书本", SubFragmentC.class));
    }

    @Override
    protected void onInitFragmentEnd(int index, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", index);
        fragment.setArguments(bundle);
    }

    @Override
    protected void onViewPagerSwitch(int index, Fragment fragment) {

    }

    @Override
    protected int getViewPagerId() {
        return R.id.fragment_b_viewpager;
    }

    @Override
    protected int getTabIndicatorViewId() {
        return R.id.fragment_b_indicator;
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void loaderData() {
        if (!mHasLoadedOnce) {
            Log.e("", "-->> 加载数据 ： Fragment B");
            mHasLoadedOnce = true;
        } else {
            Log.e("", "-->> 已经加载过数据 ： Fragment B");
        }
    }
}
