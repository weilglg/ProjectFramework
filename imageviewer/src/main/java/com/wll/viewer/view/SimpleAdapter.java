package com.wll.viewer.view;


import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.wll.viewer.R;

import java.util.ArrayList;


public class SimpleAdapter extends PagerAdapter {
    private ArrayList<View> mViews;

    public SimpleAdapter(ArrayList<View> views) {
        this.mViews = views;
    }

    @Override
    public int getCount() {
        return mViews != null ? mViews.size() : 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        if (mViews != null) {
            container.addView(mViews.get(position));
           final PhotoView photoView = mViews.get(position).findViewById(R.id
                    .photoVi_item_imgViewer);
            photoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = photoView.getDrawable();
                    Log.e("TAG" ,"===========drawable.getIntrinsicWidth()===============" + drawable.getIntrinsicWidth());
                    Log.e("TAG" ,"===========drawable.getIntrinsicHeight()===============" +
                            drawable.getIntrinsicHeight());
                }
            }, 300);
            return mViews.get(position);
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
