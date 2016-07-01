package com.wll.main.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wll.main.R;
import com.wll.main.base.BaseIndicatorActivity;
import com.wll.main.fragment.FragmentA;
import com.wll.main.fragment.FragmentB;
import com.wll.main.fragment.FragmentC;
import com.wll.main.fragment.FragmentD;

import java.util.List;

public class BaseHomeActivity extends BaseIndicatorActivity {


    @Override
    protected int getViewPagerId() {
        return R.id.base_home_viewpager;
    }

    @Override
    protected int getTagIndicatorViewId() {
        return R.id.base_home_tab_indicator;
    }

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_base_home;
    }

    @Override
    protected void initTabsInfo(List<TabInfo> tabs) {
        tabs.add(new TabInfo(0, "演示", R.mipmap.icon_huangye_hui,
                R.mipmap.icon_huangye, FragmentA.class));
        tabs.add(new TabInfo(1, "嵌套", R.mipmap.icon_up, R.mipmap.icon_down,
                FragmentB.class));
        tabs.add(new TabInfo(2, "功能", R.mipmap.icon_wode_hui,
                R.mipmap.icon_wode, FragmentC.class));
        tabs.add(new TabInfo(3, "学习", R.mipmap.icon_tongxunlu_hui,
                R.mipmap.icon_tongxunlu, FragmentD.class));
    }

    @Override
    protected boolean isUsedBaseTitleLayout() {
        return true;
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected void onInitFragmentEnd(int index, Fragment fragment) {
        if (index == 1 && fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(FragmentB.EXTRA_TAB, 0);
            fragment.setArguments(bundle);
        }
    }

    @Override
    protected void onViewPagerSwitch(int index, Fragment fragment) {
        String mainTitle = "";
        switch (index) {
            case 0:
                mainTitle = "控件";
                break;
            case 1:
                mainTitle = "嵌套";
                break;
            case 2:
                mainTitle = "功能";
                break;
            case 3:
                mainTitle = "学习";
                break;
        }
        setMainTitleContent(mainTitle);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setMainTitleContent("荒野");
    }
}
