package com.wll.main.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by wll on 2015/10/23.
 */
public class ViewUtils {

    /**
     * 确认点击的返回是否在控件范围内
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y
                || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    /**
     * 重新设置ListView的高度
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    //转换dip为px
    public static int convertDip2Px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    //转换px为dip
    public static int convertPx2Dip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }
}
