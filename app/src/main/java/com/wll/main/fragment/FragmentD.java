package com.wll.main.fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.wll.main.R;
import com.wll.main.base.BaseFragment;
import com.wll.main.widget.HorizontalProgressBarWithProgress;
import com.wll.main.widget.RevealTextView;
import com.wll.main.widget.SwitchView;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentD extends BaseFragment implements SwitchView.OnStateChangedListener, View.OnClickListener {

    private SwitchView mSwitchView;
    private RevealTextView mRevealTextView;
    private HorizontalProgressBarWithProgress progressBar;

    private static final int MSG_UPDATE = 0x100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if (msg.what == MSG_UPDATE) {
                int progress = progressBar.getProgress();
                progressBar.setProgress(++progress);
                if (progress >= 100) {
                    mHandler.removeMessages(MSG_UPDATE);
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 100);
//            }
        }
    };

    @Override
    protected int getViewResId() {
        return R.layout.fragment_d;
    }

    @Override
    protected void initViews(View v) {
        mSwitchView = (SwitchView) v.findViewById(R.id.fragment_d_switchView);
        mRevealTextView = (RevealTextView) v.findViewById(R.id.fragment_d_revealTextView);
        progressBar = (HorizontalProgressBarWithProgress) v.findViewById(R.id.fragment_d_progressBar);
        mHandler.sendEmptyMessage(100);
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
        switch (v.getId()) {
            case R.id.fragment_d_revealTextView:
                mRevealTextView.replayAnimation();
                break;
        }
    }
}
