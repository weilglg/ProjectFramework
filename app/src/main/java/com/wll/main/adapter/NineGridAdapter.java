package com.wll.main.adapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wll.main.util.GlideUtil;
import com.wll.main.widget.nineGridLayout.NineBaseAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wll on 2018/1/6/006.
 */

public class NineGridAdapter extends NineBaseAdapter<String> {

    public NineGridAdapter() {
        super(new ArrayList<String>());
    }

    public NineGridAdapter(List<String> list) {
        super(list);
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ImageView iv = null;
        if (view != null && view instanceof ImageView) {
            iv = (ImageView) view;
        } else {
            iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setBackgroundColor(parent.getContext().getResources().getColor((android.R.color.transparent)));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        String url = list.get(i);
        GlideUtil.loadImage(parent.getContext(), url, iv, GlideUtil.GlideEnum.RADIUS_IMAGE);
        return iv;
    }
}
