package com.wll.main.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wll.main.callback.DataChangeListener;
import com.wll.main.callback.MultiItemTypeSupport;

import java.util.ArrayList;
import java.util.List;

import static com.wll.main.adapter.base.ViewHolder.getViewHolder;


public abstract class BaseCommonAdapterNoDisplay<T> extends BaseAdapter {

    protected Context context;
    protected List<T> data;
    protected int layoutItemId;
    protected DataChangeListener mDataChangeListener;

    public void setDataChangeListener(DataChangeListener mDataChangeListener) {
        this.mDataChangeListener = mDataChangeListener;
    }

    /**
     * Create a Adapter
     *
     * @param context       The context
     * @param layoutItemRes The layout resource id of each item
     */
    public BaseCommonAdapterNoDisplay(Context context, int layoutItemRes) {
        this(context, null, layoutItemRes);
    }

    /**
     * Create a Adapter
     *
     * @param context      The context
     * @param data         A new list is created out of the one to avoid mutable list
     * @param layoutItemId The layout resource id of each item
     */
    public BaseCommonAdapterNoDisplay(Context context, List<T> data, int layoutItemId) {
        this.context = context;
        this.data = data == null ? new ArrayList<T>() : new
                ArrayList<T>(data);
//		this.data = data;
        this.layoutItemId = layoutItemId;
    }

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    protected void setMultiItemTypeSupport(
            MultiItemTypeSupport<T> itemTypeSupport) {
        this.mMultiItemTypeSupport = itemTypeSupport;
    }

    public BaseCommonAdapterNoDisplay(Context context, List<T> data,
                                      MultiItemTypeSupport<T> mMultiItemTypeSupport) {
        this.mMultiItemTypeSupport = mMultiItemTypeSupport;
        this.data = data == null ? new ArrayList<T>() : new
                ArrayList<T>(data);
//        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {

        return data == null ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        if (data == null || position >= data.size()) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemTypeSupport != null) {
            return mMultiItemTypeSupport.getItemViewTypeCount();
        }
        return super.getViewTypeCount();
    }

    /**
     * Starting from 1
     */
    @Override
    public int getItemViewType(int position) {
        if (data == null) {
            return 0;
        }
        if (mMultiItemTypeSupport != null) {
            return mMultiItemTypeSupport.getItemViewType(position,
                    data.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = getViewHolderManager(position, convertView, parent);
        T item = getItem(position);
        holder.setAssociatedObject(item);
        convertView(holder, data.get(position));
        return holder.getConvertView();
    }

    /**
     * You can override this method to use a custom ViewHolder in order to fit
     * your needs
     */
    protected ViewHolder getViewHolderManager(int position, View convertView,
                                              ViewGroup parent) {
        if (mMultiItemTypeSupport != null) {
            return getViewHolder(
                    context,
                    convertView,
                    parent,
                    mMultiItemTypeSupport.getLayoutItemRes(position,
                            data.get(position)), position);
        } else {
            return getViewHolder(context, convertView, parent, layoutItemId,
                    position);
        }
    }

    /**
     * Implement this method and use the helper to adapt the view to the given
     * item
     */
    public abstract void convertView(ViewHolder holder, T t);

    @Override
    public boolean isEnabled(int position) {
        return position < data.size();
    }

    /**
     * Add data to the collection
     */
    public void add(T elem) {
        if (data == null)
            data = new ArrayList<T>();
        data.add(elem);
        notifyDataSetChanged();
    }

    /**
     * Add list to the collection
     */
    public void addAll(List<T> elems) {
        if (data == null)
            data = new ArrayList<T>();
        data.addAll(elems);
        notifyDataSetChanged();
    }

    public void set(T oldElem, T newElem) {
        set(data.indexOf(oldElem), newElem);
    }

    public void set(int indexOf, T newElem) {
        if (data == null)
            return;
        data.set(indexOf, newElem);
        notifyDataSetChanged();
    }

    /**
     * Data from new settings
     */
    public void setNewList(List<T> datas) {
        this.data = datas == null ? new ArrayList<T>()
                : new ArrayList<T>(datas);
        notifyDataSetChanged();
    }

    /**
     * Delete the corresponding object in the collection
     */
    public void removerElemByObject(T elem) {
        if (data == null)
            return;
        data.remove(elem);
        notifyDataSetChanged();
    }

    /**
     * Delete the corresponding element according to the index
     */
    public void removerElemByIndex(int index) {
        if (data == null)
            return;
        data.remove(index);
        notifyDataSetChanged();
    }

    /**
     * Replace the original data
     */
    public void replaceAll(List<T> elems) {
        if (data == null)
            return;
        data.clear();
        data.addAll(elems);
        notifyDataSetChanged();
    }

    /**
     * Judging whether the current data collection contains an element
     */
    public boolean contains(T elem) {
        if (data == null)
            return false;
        return data.contains(elem);
    }

    /**
     * Empty the original data
     */
    public void clear() {
        if (data == null)
            return;
        data.clear();
        notifyDataSetChanged();
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mDataChangeListener != null) {
            mDataChangeListener.dataChange();
        }
    }

}
