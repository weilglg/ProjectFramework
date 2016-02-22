package com.wll.main.util.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * Created by WLL on 2016/2/18.
 * <p>从内存中读取数据速度是最快的，为了更大限度的使用内存，这里使用了两层缓存。</p>
 * 硬引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
 */
public class ImageMemoryCache {

    /**
     * 软引用缓存容量
     */
    private static final int SOFT_CACHE_SIZE = 15;

    /**
     * 硬引用缓存
     */
    private static LruCache<String, Bitmap> mLruCache;

    /**
     * 软引用
     */
    private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache;

    public ImageMemoryCache(Context context) {
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 4; // 硬引用缓存容量，位系统可用内存的 1/4

        // 创建软引用
        mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {
            private static final long serialVersionUID = 6040103833179403725L;

            @Override
            protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
                if (size() > SOFT_CACHE_SIZE) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        // 创建硬引用
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value != null) {
                    return value.getRowBytes() * value.getHeight();
                } else {
                    return 0;
                }
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != null) {
                    // 硬引用缓存容量满的时候，会根据LRU算法把最近没有使用的图片转入软引用缓存
                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
                }
            }
        };
    }

    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap;
        //先从硬引用缓存中获取
        synchronized (mLruCache) {
            bitmap = mLruCache.get(url);
            if (bitmap != null) {
                //如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除的
                mLruCache.remove(url);
                mLruCache.put(url, bitmap);
                return bitmap;
            }
        }

        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    //将图片移回硬引用中
                    mLruCache.put(url, bitmap);
                    //将图片从软引用缓存中删除
                    mSoftCache.remove(url);
                    return bitmap;
                } else {
                    mSoftCache.remove(url);
                }
            }
        }
        return null;
    }

    /**
     * 将图片添加到硬引用缓存中
     *
     * @param url
     * @param bitmap
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mLruCache) {
                mLruCache.put(url, bitmap);
            }
        }
    }

    public void clearCache() {
        mSoftCache.clear();
        mLruCache = null;
        mSoftCache = null;
    }
}
