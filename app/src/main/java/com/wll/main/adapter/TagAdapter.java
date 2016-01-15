package com.wll.main.adapter;

import android.view.View;

import com.wll.main.widget.flowTag.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class TagAdapter<T> {

    /**
     * 数据源
     */
    private List<T> mTagDatas;
    /**
     * 数据变化时的监听
     */
    private OnDataChangedListener onDataChangedListener;

    /**
     * 被选中的Tag的集合
     */
    private HashSet<Integer> mCheckedPositionList = new HashSet<Integer>();

    public TagAdapter(List<T> mTagDatas) {
        this.mTagDatas = mTagDatas;
    }

    public TagAdapter(T[] mTagDatas) {
        if (mTagDatas != null) {
            this.mTagDatas = new ArrayList<T>(Arrays.asList(mTagDatas));
        }
    }

    /**
     * 设置变化监听器
     */
    public void setOnDataChangedListener(OnDataChangedListener changedListener) {
        this.onDataChangedListener = changedListener;
    }

    /**
     * 设置选中的Tag
     *
     * @param positions Tag的下标
     */
    public void setSelectedTagList(int... positions) {
        for (int i = 0; i < positions.length; i++) {
            if (!mCheckedPositionList.contains(positions[i]))
                mCheckedPositionList.add(positions[i]);
        }
        notifyDataChange();
    }

    /**
     * 获取被选中的Tag的下标
     */
    public HashSet<Integer> getPreCheckedTagPositionList() {
        return mCheckedPositionList;
    }

    /**
     * 获得Tag的总数量
     */
    public int getTagCount() {
        return mTagDatas == null ? 0 : mTagDatas.size();
    }

    /**
     * 调用数据变化的监听
     */
    public void notifyDataChange() {
        if (onDataChangedListener != null)
            onDataChangedListener.onChange();
    }

    /**
     * 数据变化时的回调函数
     */
   public static interface OnDataChangedListener {
        void onChange();
    }

    /**
     * 获得下标是position的Tag
     *
     * @param position Tag的下标
     */
    public T getItem(int position) {
        return mTagDatas.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

}
