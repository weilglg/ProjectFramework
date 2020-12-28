package com.wll.main.callback;


/**
 * Many layout support interface
 */
public interface MultiItemTypeSupport<T> {
	int getLayoutItemRes(int position, T t);

	int getItemViewTypeCount();

	int getItemViewType(int position, T t);

}
