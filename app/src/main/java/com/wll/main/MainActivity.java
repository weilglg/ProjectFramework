package com.wll.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wll.main.activity.BaseHomeActivity;
import com.wll.main.base.BaseActivity;
import com.wll.main.widget.ImageCycleView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMainTitleContent("测试标题栏");
        setRightLayoutDrawable(R.drawable.icon_menu);
        setLeftLayoutString("返回");
        findViewById(R.id.id_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseHomeActivity.class);
                startActivity(intent);
            }
        });

        list.add("http://b.hiphotos.baidu.com/image/pic/item/77094b36acaf2edd0aaabbc38a1001e939019345.jpg");
        list.add("http://img2.3lian.com/2014/f5/158/d/86.jpg");
        list.add("http://ww1.sinaimg.cn/mw600/bce7ca57gw1e4rg0coeqqj20dw099myu.jpg");
    }

    @Override
    protected boolean isUsedBaseTitleLayout() {
        return true;
    }

    @Override
    protected boolean isLoadBannerLayout() {
        return true;
    }

    @Override
    public boolean isLoadNetworkLayout() {
        return true;
    }

    @Override
    protected int getAdIndicatorPosition() {
        return 1;
    }

    @Override
    protected int getAdIndicatorType() {
        return 1;
    }

    @Override
    protected ArrayList<String> getBannerUrlList() {
        return list;
    }

    @Override
    protected ImageCycleView.ImageCycleViewListener getImageCycleViewListener() {
        return new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                ImageLoader.getInstance().displayImage(imageURL, imageView);
            }

            @Override
            public void onImageClick(int position, View imageView) {
                showToast("position : " + position);
            }
        };
    }

}
