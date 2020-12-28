package com.wll.main.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.wll.main.R;
import com.wll.main.base.BaseActivity;
import com.wll.main.widget.NoScrollListView;
import com.wll.main.widget.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

public class ObservableScrollViewActivity extends BaseActivity {

    private ObservableScrollView observableScrollView;
    private NoScrollListView noScrollListView;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_observable_scroll_view);
//        initViews();
//        initListener();
//    }

    @Override
    protected int getMainViewResId() {
        return R.layout.activity_observable_scroll_view;
    }

    @Override
    protected boolean isUsedBaseTitleLayout() {
        return false;
    }

    @Override
    protected void initViews() {
        super.initViews();
        observableScrollView = (ObservableScrollView) findViewById(R.id.observableScrollView);
        noScrollListView = (NoScrollListView) findViewById(R.id.obserable_scroll_view_listView);
        setMainTitleContent("ObservableScrollView");
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData());
        noScrollListView.setFocusable(false);
        noScrollListView.setAdapter(mAdapter);
        observableScrollView.setUpTitleBar(noScrollListView, titleLayout);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.obserable_scroll_view_content_layout;
    }

    public List<String> getData() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            list.add("测试数据" + i);
        }
        return list;
    }
}
