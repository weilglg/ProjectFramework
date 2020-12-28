package com.wll.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.wll.main.R;
import com.wll.main.adapter.PreviewImageAdapter;
import com.wll.main.base.BaseActivity;

import java.util.List;

/**
 * Created by WLL on 2016/2/25.
 */
public class SeePictureActivity extends BaseActivity {
    public static final int REQUEST_CODE = 0x001;
    public static final int RESULT_CODE = 0x002;

    private ViewPager mViewPager;

    private List<String> mImagePaths;

    private PreviewImageAdapter mAdapter;
    private Button btn;

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_see_picture;
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected boolean isLoadBannerLayout() {
        return false;
    }

    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.a_root_layout;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mViewPager = (ViewPager) findViewById(R.id.zoom_image_view_activity_viewpager);
        btn = (Button) findViewById(R.id.id_btn);
        setMainTitleContent("浏览图片");
        setLeftLayoutDrawable(R.mipmap.icon_return);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btn.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && RESULT_CODE == resultCode) {
            mImagePaths = (List<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            mAdapter = new PreviewImageAdapter(this);
            mAdapter.setData(mImagePaths);
            mViewPager.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn:
                toActivityForResult(this, SelectPictureActivity.class, REQUEST_CODE);
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
