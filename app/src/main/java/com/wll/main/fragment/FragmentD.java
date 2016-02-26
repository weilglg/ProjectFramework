package com.wll.main.fragment;

import android.util.Log;
import android.view.View;

import com.wll.main.R;
import com.wll.main.base.BaseFragment;
import com.wll.main.widget.RevealTextView;
import com.wll.main.widget.SwitchView;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentD extends BaseFragment implements SwitchView.OnStateChangedListener, View.OnClickListener {

    private SwitchView mSwitchView;
    private RevealTextView mRevealTextView;
    @Override
    protected int getViewResId() {
        return R.layout.fragment_d;
    }

    @Override
    protected void initViews(View v) {
        mSwitchView = (SwitchView) v.findViewById(R.id.fragment_d_switchView);
        mRevealTextView = (RevealTextView) v.findViewById(R.id.fragment_d_revealTextView);
    }

    @Override
    protected void initListener() {
        mSwitchView.setOnStateChangedListener(this);
        mRevealTextView.setOnClickListener(this);
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

    @Override
    public void toggleToOn() {
        mSwitchView.toggleSwitch(true);
    }

    @Override
    public void toggleToOff() {
        mSwitchView.toggleSwitch(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_d_revealTextView:
                mRevealTextView.replayAnimation();
                break;
        }
    }
}
