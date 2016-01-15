package com.wll.main.fragment;

import android.util.Log;
import android.view.View;

import com.wll.main.R;
import com.wll.main.base.BaseFragment;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentD extends BaseFragment {
    @Override
    protected int getViewResId() {
        return R.layout.fragment_d;
    }

    @Override
    protected void initViews(View v) {

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
            Log.e("", "-->> 加载数据 ： Fragment D");
            mHasLoadedOnce = true;
        } else {
            Log.e("", "-->> 已经加载过数据 ： Fragment D");
        }
    }
}
