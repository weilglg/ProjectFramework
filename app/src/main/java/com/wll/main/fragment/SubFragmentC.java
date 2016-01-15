package com.wll.main.fragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wll.main.R;
import com.wll.main.base.BaseFragment;

import java.util.ArrayList;

/**
 * Created by wll on 2015/11/9.
 */
public class SubFragmentC extends BaseFragment {

    private ListView mListView;
    @Override
    protected void loaderData() {
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getData()));
        mHasLoadedOnce= true;
    }

    private ArrayList<String> getData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("测试加载 ： " + i);
        }
        return list;
    }

    @Override
    protected int getViewResId() {
        return R.layout.fragment_sub_c;
    }

    @Override
    protected void initViews(View v) {
        mListView= (ListView) v.findViewById(R.id.subFragmentB_listView);
    }

    @Override
    protected void initListener() {

    }
}
