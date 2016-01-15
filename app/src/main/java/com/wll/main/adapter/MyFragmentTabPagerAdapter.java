package com.wll.main.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wll.main.base.BaseFragment;

import java.util.ArrayList;

public class MyFragmentTabPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<BaseFragment> list;
    public MyFragmentTabPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> list){
        super(fm);
        this.list=list;
    }
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
