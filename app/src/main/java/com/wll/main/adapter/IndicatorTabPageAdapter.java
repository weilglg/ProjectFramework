package com.wll.main.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.wll.main.model.TabInfo;

import java.util.ArrayList;

/**
 * Created by wll on 2015/11/9.
 */
public class IndicatorTabPageAdapter extends FragmentPagerAdapter {
    ArrayList<? extends TabInfo> tabs = null;
    Context context = null;

    public IndicatorTabPageAdapter(Context context, FragmentManager fm,
                                   ArrayList<? extends TabInfo> tabs) {
        super(fm);
        this.tabs = tabs;
        this.context = context;
    }

    @Override
    public Fragment getItem(int pos) {
        Fragment fragment = null;
        if (tabs != null && pos < tabs.size()) {
            TabInfo tab = tabs.get(pos);
            if (tab == null)
                return null;
            fragment = tab.createFragment(pos);
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (tabs != null && tabs.size() > 0)
            return tabs.size();
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TabInfo tab = tabs.get(position);
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        tab.setFragment(fragment);
        return fragment;
    }
}
