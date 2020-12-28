package com.wll.main.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wll.main.R;
import com.wll.main.base.BaseFragment;

/**
 * Created by wll on 2015/11/9.
 */
public class SubFragmentA extends BaseFragment {
    @Override
    protected void loaderData() {

    }

    @Override
    protected int getViewResId() {
        return R.layout.fragment_sub_a;
    }

    @Override
    protected void initViews(View v) {
        Bundle bundle = getArguments();
        int pos = bundle.getInt("pos");
        TextView tv = v.findViewById(R.id.tv);
        tv.setText("" + pos);
    }

    @Override
    protected void initListener() {

    }
}
