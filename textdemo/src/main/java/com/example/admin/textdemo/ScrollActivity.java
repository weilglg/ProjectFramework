package com.example.admin.textdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ScrollActivity extends AppCompatActivity {

    private ObservableScrollView scrollView;
    private ListView mListView;
    private int mAlpha;
    private LinearLayout title_bar;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            title_bar.getBackground().mutate().setAlpha(msg.what);
        }
    };
    private int data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        scrollView = (ObservableScrollView) findViewById(R.id.id_scrollview);
        mListView = (ListView) findViewById(R.id.id_listview);
        title_bar = (LinearLayout) findViewById(R.id.title_bar);

        //设置ListView失去焦点
        mListView.setFocusable(false);
        //绑定数据
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData());
        mListView.setAdapter(mAdapter);
        //重新设置ListView的高度
        setListViewHeight(mListView);

        scrollView.setUpTitleBar(mListView, title_bar);

//        handler.sendEmptyMessage(0);

//
//        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
//            @Override
//            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//
//                int viewTop = mListView.getTop();
//                if (y+10 >= viewTop) {
//                    if (mAlpha == 255)
//                        return;
//                    handler.sendEmptyMessage(255);
//                } else {
//                    mAlpha = 0;
//                    float ratio = y * 1f / viewTop * 1f;
//                    int mAlpha = (int) (255 * ratio);
//                    if (mAlpha <= 2) {
//                        handler.sendEmptyMessage(0);
//                    } else {
//                        handler.sendEmptyMessage(mAlpha);
//                    }
//                }
//            }
//        });
    }

    /**
     * 动态计算ListView的高度，并重新设置ListView的高度
     *
     * @param mListView 需要进行设置的ListView
     */
    private void setListViewHeight(ListView mListView) {
        if (mListView == null) {
            return;
        }
        ListAdapter adapter = mListView.getAdapter();
        if (adapter == null) {
            return;
        }
        int count = adapter.getCount();
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View listItemView = adapter.getView(i, null, mListView);
            listItemView.measure(0, 0);
            totalHeight += listItemView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (count - 1));
        mListView.setLayoutParams(params);
    }

    private List<String> getData() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            list.add("测试数据" + i);
        }
        return list;
    }
}
