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
    protected int getViewPagerIndicatorViewId() {
        return R.id.base_home_tab_indicator;
    }

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_base_home;
    }

    @Override
    protected void initTabsInfo(List<TabInfo> tabs) {
        tabs.add(new TabInfo(0, "荒野", R.drawable.icon_huangye_hui,
                R.drawable.icon_huangye, FragmentA.class));
        tabs.add(new TabInfo(1, "跛豪", R.drawable.icon_up, R.drawable.icon_down,
                FragmentB.class));
        tabs.add(new TabInfo(2, "我的", R.drawable.icon_wode_hui,
                R.drawable.icon_wode, FragmentC.class));
        tabs.add(new TabInfo(3, "通宵", R.drawable.icon_tongxunlu_hui,
                R.drawable.icon_tongxunlu, FragmentD.class));
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
            bundle.putInt(FragmentB.EXTRA_TAB, 2);
            fragment.setArguments(bundle);
        }
    }

    @Override
    protected void onViewPagerSwitch(int index, Fragment fragment) {
        String mainTitle = "";
        switch (index) {
            case 0:
                mainTitle = "荒野";
                break;
            case 1:
                mainTitle = "跛豪";
                break;
            case 2:
                mainTitle = "我的";
                break;
            case 3:
                mainTitle = "通宵";
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
