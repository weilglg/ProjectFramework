package com.wll.main.fragment;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wll.main.R;
import com.wll.main.activity.SelectPictureActivity;
import com.wll.main.adapter.PreviewImageAdapter;
import com.wll.main.base.BaseFragment;

import java.util.List;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentC extends BaseFragment implements View.OnClickListener {

    public static final int REQUEST_CODE = 0x001;
    public static final int RESULT_CODE = 0x002;

    private ViewPager mViewPager;

    private List<String> mImagePaths;

    private PreviewImageAdapter mAdapter;

    private Button btn;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_c;
    }

    @Override
    protected void initViews(View v) {
        mViewPager = (ViewPager) v.findViewById(R.id.zoom_image_view_activity_viewpager);
        btn = (Button) v.findViewById(R.id.id_btn);
    }

    @Override
    protected void initListener() {
        btn.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    protected void loaderData() {
        if (!mHasLoadedOnce) {
            Log.e("", "-->> 加载数据 ： Fragment C");
            mHasLoadedOnce = true;
        } else {
            Log.e("", "-->> 已经加载过数据 ： Fragment C");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && RESULT_CODE == resultCode){
            mImagePaths = (List<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            mAdapter = new PreviewImageAdapter(getContext());
            mAdapter.setData(mImagePaths);
            mViewPager.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn:
                toActivityForResult(getActivity(), SelectPictureActivity.class, REQUEST_CODE);
                break;
        }
    }
}
