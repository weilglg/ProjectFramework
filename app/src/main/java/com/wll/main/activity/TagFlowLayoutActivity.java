package com.wll.main.activity;

import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_flow_layout);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setMainTitleContent("TagFlowLayout");
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
        return false;
    }

    @Override
    public void onItemSelected(Integer selectedPosition) {

    }

    @Override
    public void onSelectedAllPosition(Set<Integer> selectedPositionSet) {

    }


    private class MyTagAdapter extends TagAdapter<String> {

        public MyTagAdapter(String[] mTagDatas) {
            super(mTagDatas);
        }


        @Override
        public View getView(FlowLayout parent, int position, String s) {
            View view = inflateView(R.layout.flowlayout_item);
            TextView tv = (TextView) view.findViewById(R.id.flowtag_item_tv);
            tv.setText(getItem(position));
            return view;
        }
    }
}
