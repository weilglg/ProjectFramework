package com.wll.main.widget.nineGridLayout;

import android.view.View;
import android.view.ViewGroup;
import com.wll.main.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wll on 2018/1/4/004.
 */

public abstract class NineBaseAdapter<T> {
    protected List<T> list;
    private DataChangeListener dataChangeListener;

    public NineBaseAdapter(List<T> list) {
        this.list = list;
    }

    void setDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public int getCount() {
        return list == null ? 0 : list.size();
    }

    public T getItem(int position) {
        return list != null && list.size() > position ? list.get(position) : null;
    }

    public long getItemId(int position) {
        return position;
    }

    public abstract View getView(int i, View view, ViewGroup parent);

    void removeItem(int itemPosition) {
        if (itemPosition >= list.size()) {
            return;
        }
        list.remove(itemPosition);
        if (dataChangeListener != null) {
            dataChangeListener.onItemRangeRemove(itemPosition);
        }
    }

    public void addItem(T t) {
        if (list == null || t == null) {
            return;
        }
        int oldSize = list.size();
        this.list.add(t);
        if (dataChangeListener != null) {
            dataChangeListener.onItemRangeAdd(oldSize);
        }
    }

    public void addItemAll(List<T> addList) {
        if (list == null) {
            return;
        }
        if (CollectionUtils.isEmpty(addList)) {
            return;
        }
        int oldSize = list.size();
        this.list.addAll(addList);
        if (dataChangeListener != null) {
            dataChangeListener.onItemRangeAdd(oldSize);
        }
    }

    public void removeItem(T t) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (list.contains(t)) {
            int index = list.indexOf(t);
            if (index > -1) {
                list.remove(index);
                if (dataChangeListener != null) {
                    dataChangeListener.onItemRangeRemove(index);
                }
            }
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list == null ? new ArrayList<T>() : new ArrayList<T>(list);
        if (dataChangeListener != null) {
            dataChangeListener.notifyDataSetChanged();
        }
    }

    protected interface DataChangeListener {

        void onItemRangeAdd(int oldSize);

        void onItemRangeRemove(int itemPosition);

        void notifyDataSetChanged();
    }
}
