package com.wll.main.util;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 集合的工具类。
 * 
 */
public final class CollectionUtils {

	/**
	 * 获取集合中元素的数量。
	 * 
	 * @param collection
	 * @return
	 */
	public static int size(Collection<?> collection) {
		if (collection == null) {
			return 0;
		}
		return collection.size();
	}

	/**
	 * 判断集合是否为空（null 或者 size=0）。
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		if (size(collection) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 把目标集合中的元素全部添加到源集合中。<br>
	 * 
	 * 封装 {@link Collection#addAll(Collection)} 方法，避免空指针。
	 * 
	 * @param origin
	 * @param target
	 * @return
	 */
	public static <T> boolean addAll(Collection<T> origin, Collection<T> target) {
		if (origin == null || target == null) {
			return false;
		}
		return origin.addAll(target);
	}

	/**
	 * 把目标集合中的元素全部添加到源集合中，并忽略掉源集合中已经包含的元素。
	 * 
	 * @param origin
	 * @param target
	 * @return
	 */
	public static <T> Collection<T> addAllIgnoreContains(Collection<T> origin, Collection<T> target) {
		if (origin == null) {
			return null;
		}

		List<T> temp = new ArrayList<T>();
		if (!isEmpty(target)) {
			Iterator<T> it = target.iterator();
			while (it.hasNext()) {
				T object = it.next();
				if (!origin.contains(object)) {
					temp.add(object);
				}
			}
		}

		addAll(origin, temp);
		return origin;
	}

	/**
	 * 根据源 List 中的元素过滤掉目标 List 中重复的元素。
	 * 
	 * @param origin
	 * @param target
	 * @return
	 */
	public static <T> List<T> removeRepeatData(List<T> origin, List<T> target) {
		if (origin == null || target == null) {
			return target;
		}

		List<T> temp = new ArrayList<T>();
		if (!isEmpty(origin)) {
			for (T object : target) {
				if (!origin.contains(object)) {
					temp.add(object);
				}
			}
			return temp;
		}
		return target;
	}

	/**
	 * 根据给定的排序规则对集合进行排序。
	 * 
	 * @param list
	 * @param comparator
	 * @return
	 */
	public static <T> boolean sort(List<T> list, Comparator<? super T> comparator) {
		if (list == null) {
			return false;
		}
		Collections.sort(list, comparator);
		return true;
	}

}
