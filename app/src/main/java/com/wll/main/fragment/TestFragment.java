package com.wll.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wll.main.R;
import com.wll.main.base.BaseFragment;

/**
 * Created by wll on 2015/10/28.
 */
public class TestFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void loaderData() {

    }

    @Override
    protected boolean isLoadBannerLayout() {
        return true;
    }

    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.linearLayout;
    }

    @Override
    public int getViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(View v) {

    }

    @Override
    protected void initListener() {

    }
}
