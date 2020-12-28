package com.wll.main.fragment;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wll.main.R;
import com.wll.main.activity.SeePictureActivity;
import com.wll.main.activity.WebViewActivity;
import com.wll.main.base.BaseFragment;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentC extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private String[] array;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_c;
    }

    @Override
    protected void initViews(View v) {
        mListView = (ListView) v.findViewById(R.id.fragment_c_listView);
    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void loaderData() {
        if (!mHasLoadedOnce) {
            Log.e("", "-->> 加载数据 ： Fragment C");
            array = getResources().getStringArray(R.array.function_name);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout
                    .simple_list_item_1, array);
            mListView.setAdapter(adapter);
            mHasLoadedOnce = true;
        } else {
            Log.e("", "-->> 已经加载过数据 ： Fragment C");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                toActivity(SeePictureActivity.class);
                break;
            case 1:
                toActivity(WebViewActivity.class);
                break;
        }
    }
}
