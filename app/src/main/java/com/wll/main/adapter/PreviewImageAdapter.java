package com.wll.main.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wll.main.widget.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WLL on 2016/2/24.
 */
public class PreviewImageAdapter extends PagerAdapter {

    private List<ZoomImageView> views;

    private Context context;

    public PreviewImageAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> imagePaths) {
        views = new ArrayList<>();
        for (String imagePath : imagePaths) {
            ZoomImageView imageView = new ZoomImageView(context);
            if (imagePath.indexOf("/") == 0) {
                imagePath = imagePath.replaceFirst("/", "");
            }
            Log.e("filePath", imagePath);
            ImageLoader.getInstance().displayImage("file://" + imagePath, imageView);
            views.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (views != null) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (views != null) {
            ((ViewPager) container).removeView(views.get(position));
        } else {
            super.destroyItem(container, position, object);
        }

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
