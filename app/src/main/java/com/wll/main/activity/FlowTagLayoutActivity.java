package com.wll.main.activity;

import android.os.Bundle;

import com.wll.main.R;
import com.wll.main.base.BaseActivity;
import com.wll.main.widget.WaterfallWithDeleteView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WLL on 2016/2/23.
 */
public class FlowTagLayoutActivity extends BaseActivity implements WaterfallWithDeleteView.OnTagClickListener {

    private String[] mVals = new String[]
            {"Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                    "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                    "Android", "Weclome Hello", "Button Text", "TextView"};

    private WaterfallWithDeleteView flowTagLayoutView;

    private List<WaterfallWithDeleteView.Tag> mTags;

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_flow_tag_layout;
    }

    private void initData() {
        mTags = new ArrayList<>();
        for (int i = 0; i < mVals.length; i++) {
            WaterfallWithDeleteView.Tag tag = new WaterfallWithDeleteView.Tag(mVals[i], i);
            mTags.add(tag);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        setMainTitleContent("WaterfallWithDeleteView");
        setLeftLayoutDrawable(R.drawable.icon_return);
        flowTagLayoutView = (WaterfallWithDeleteView) findViewById(R.id.flowTagLayoutView);
        initData();
        flowTagLayoutView.setTags(mTags);

    }

    @Override
    protected void initListener() {
        super.initListener();
        flowTagLayoutView.setOnTagClickListener(this);
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.flow_tag_layout_root;
    }

    @Override
    public void onTagClick(WaterfallWithDeleteView.Tag tag) {
        showToast(tag.title);
    }

    @Override
    public void onTagDelete(WaterfallWithDeleteView.Tag tag) {
        flowTagLayoutView.removeTag(tag);
    }
}
