package com.wll.main.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wll.main.R;
import com.wll.main.adapter.TagAdapter;
import com.wll.main.base.BaseActivity;
import com.wll.main.widget.flowTag.FlowLayout;
import com.wll.main.widget.flowTag.TagFlowLayout;

import java.util.Set;

/**
 * Created by WLL on 2016/2/22.
 */
public class TagFlowLayoutActivity extends BaseActivity implements TagFlowLayout.OnTagClickListener, TagFlowLayout
        .OnSelectedItemListener {

    private String[] mTagDatas = {"add", "delete", "private", "public", "protected", "a", "one", "not null"};


    private TagFlowLayout tagFlowLayout;

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_tag_flow_layout;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setMainTitleContent("TagFlowLayout");
        setLeftLayoutDrawable(R.mipmap.icon_return);
        tagFlowLayout = (TagFlowLayout) findViewById(R.id.tagFlowLayout);
    }

    @Override
    protected void initListener() {
        super.initListener();
        tagFlowLayout.setOnTagClickListener(this);
        tagFlowLayout.setOnSelectedItemListener(this);
        tagFlowLayout.setTagAdapter(new MyTagAdapter(mTagDatas));
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.tagFlowLayout_root;
    }

    @Override
    protected boolean isLoadBannerLayout() {
        return false;
    }

    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        Log.e("TAG", "-->>onTagClick : "+ mTagDatas[position]);
        return false;
    }

    @Override
    public void onItemSelected(Integer selectedPosition) {
        Log.e("TAG","-->>onItemSelected : "+ mTagDatas[selectedPosition]);
    }

    @Override
    public void onSelectedAllPosition(Set<Integer> selectedPositionSet) {
        Log.e("TAG","-->>onSelectedAllPosition : "+ selectedPositionSet.size());
    }


    private class MyTagAdapter extends TagAdapter<String> {

        public MyTagAdapter(String[] mTagDatas) {
            super(mTagDatas);
        }

        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) inflateView(R.layout.tag_tv, parent);
            tv.setText(getItem(position));
            return tv;
        }
    }
}
