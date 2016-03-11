package com.wll.main.activity;

import android.os.Bundle;

import com.wll.main.R;
import com.wll.main.base.BaseActivity;

public class ZoomImageViewActivity extends BaseActivity {

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_zoom_image_view;
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.zoom_image_view_activity_root;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setMainTitleContent("ZoomImageView");
        setLeftLayoutDrawable(R.drawable.icon_return);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }
}
