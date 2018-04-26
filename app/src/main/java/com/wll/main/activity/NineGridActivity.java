package com.wll.main.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.bumptech.glide.request.RequestOptions;
import com.wll.main.R;
import com.wll.main.adapter.NineGridAdapter;
import com.wll.main.widget.nineGridLayout.NineGridLayout;
import com.wll.viewer.ImageViewer;
import com.wll.viewer.data.ViewData;

import java.util.ArrayList;
import java.util.List;

public class NineGridActivity extends AppCompatActivity {

    private NineGridLayout nine_grid;
    private ArrayList<String> mImageDatas;
    private NineGridAdapter nineGridAdapter;
    private ImageViewer imageViewer;
    private ArrayList<ViewData> mViewDatas;
    private RequestOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine_grid);
        nine_grid = (NineGridLayout) findViewById(R.id.nine_grid);
        nineGridAdapter = new NineGridAdapter();
        nine_grid.setAdapter(nineGridAdapter);
        generateData();

        mViewDatas = new ArrayList<>();
        mOptions = new RequestOptions();
        mOptions.placeholder(R.drawable.img_viewer_placeholder).error(R.drawable.img_viewer_error);
        imageViewer = ImageViewer.newInstance();
        imageViewer.indexPos(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        nineGridAdapter.setList(mImageDatas);
        nine_grid.setOnChildItemClickListener(new NineGridLayout.DefaultChildItemClickListener() {
            @Override
            public void onItemClick(View view, int itemPosition) {
                mViewDatas.clear();
                for (int j = 0; j < nine_grid.getChildCount(); j++) {
                    int[] location = new int[2];
                    // 获取在整个屏幕内的绝对坐标
                    nine_grid.getChildAt(j).getLocationOnScreen(location);
                    ViewData viewData = new ViewData();
                    viewData.x = location[0];
                    viewData.y = location[1];
                    viewData.width = nine_grid.getChildAt(j).getMeasuredWidth();
                    viewData.height = nine_grid.getChildAt(j).getMeasuredHeight();
                    mViewDatas.add(viewData);
                }
                imageViewer.beginIndex(itemPosition)
                        .viewData(mViewDatas)
                        .imageData(new ArrayList<String>(nineGridAdapter.getList()))
                        .show(NineGridActivity.this);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void generateData() {
        mImageDatas = new ArrayList<>();
        String url0 = "http://img5.duitang.com/uploads/item/201404/11/20140411214939_XswXa.jpeg";
        String url1 = "http://att.bbs.duowan.com/forum/201210/20/210446opy9p5pghu015p9u.jpg";
        String url2 = "https://b-ssl.duitang.com/uploads/item/201505/09/20150509221719_kyNrM.jpeg";
        String url3 = "https://b-ssl.duitang.com/uploads/item/201709/26/20170926131419_8YhLA.jpeg";
        String url4 = "https://b-ssl.duitang.com/uploads/item/201505/11/20150511122951_MAwVZ.jpeg";
        String url5 = "https://b-ssl.duitang.com/uploads/item/201704/23/20170423205828_BhNSv.jpeg";
        String url6 = "https://b-ssl.duitang.com/uploads/item/201706/30/20170630181644_j4mh5.jpeg";
        String url7 = "https://b-ssl.duitang.com/uploads/item/201407/22/20140722172759_iPCXv.jpeg";
        String url8 = "https://b-ssl.duitang.com/uploads/item/201511/11/20151111103149_mrRfd.jpeg";
        String url9 = "https://b-ssl.duitang.com/uploads/item/201510/14/20151014172010_RnJVz.jpeg";
        mImageDatas.add(url0);
        mImageDatas.add(url1);
        mImageDatas.add(url2);
        mImageDatas.add(url3);
        mImageDatas.add(url4);
        mImageDatas.add(url5);
        mImageDatas.add(url6);
        mImageDatas.add(url7);
        mImageDatas.add(url8);
        mImageDatas.add(url9);
    }
}
