package com.wll.main.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wll.main.R;
import com.wll.main.activity.TagFlowLayoutActivity;
import com.wll.main.base.BaseFragment;
import com.wll.main.widget.ImageCycleView;
import com.wll.main.widget.NoScrollListView;

import java.util.ArrayList;

/**
 * Created by wll on 2015/10/29.
 */
public class FragmentA extends BaseFragment implements AdapterView.OnItemClickListener {

    private NoScrollListView mListView;
    private String[] array;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_a;
    }

    @Override
    protected void initViews(View v) {
        mListView = (NoScrollListView) v.findViewById(R.id.fragment_a_listview);
    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected boolean isLoadBannerLayout() {
        return true;
    }


    @Override
    protected int getLoadNetLayoutOrAdLayoutId() {
        return R.id.a_root_layout;
    }


    @Override
    protected int getAdIndicatorPosition() {
        return 2;
    }

    @Override
    protected ArrayList<String> getBannerUrlList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("http://g.hiphotos.baidu.com/image/pic/item/6a600c338744ebf874a88a36dbf9d72a6159a7da.jpg");
        list.add("http://g.hiphotos.baidu.com/image/pic/item/f9198618367adab48cb3685b89d4b31c8701e443.jpg");
        list.add("http://a.hiphotos.baidu.com/image/pic/item/d058ccbf6c81800a995f9571b33533fa828b4712.jpg");
        return list;
    }

    @Override
    protected ImageCycleView.ImageCycleViewListener getImageCycleViewListener() {
        return new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
//                使用ImageLoader加载图片并进行设置
                ImageLoader.getInstance().displayImage(imageURL, imageView);
            }

            @Override
            public void onImageClick(int position, View imageView) {
                showToast("position : " + position);
            }
        };
    }

    @Override
    protected void loaderData() {
        if (!mHasLoadedOnce) {
            array = getResources().getStringArray(R.array.views_name);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout
                    .simple_list_item_1, array);
            mListView.setAdapter(adapter);
            mHasLoadedOnce = true;
        } else {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showActivity(TagFlowLayoutActivity.class);
                break;
        }
    }
}
