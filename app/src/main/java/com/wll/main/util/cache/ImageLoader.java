package com.wll.main.util.cache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by WLL on 2016/2/18.
 */
public class ImageLoader {
    private static ImageLoader mImageLoader;

    private ImageMemoryCache imageMemoryCache;
    private ImageFileCache imageFileCache;

    public static ImageLoader getInstance(Context context) {
        if (mImageLoader == null) {
            synchronized (Object.class) {
                if (mImageLoader == null) {
                    mImageLoader = new ImageLoader(context);
                }
            }
        }
        return mImageLoader;
    }

    private ImageLoader(Context context) {
        imageMemoryCache = new ImageMemoryCache(context);
        imageFileCache = new ImageFileCache();
    }

    public Bitmap getBitmap(String url) {
        //从内存缓存中获取图片
        Bitmap bitmap = imageMemoryCache.getBitmapFromCache(url);
        if (bitmap == null) {
            //从文件缓存中获取图片
            bitmap = imageFileCache.getBitmap(url);
            if (bitmap == null) {
                //从网络获取图片
                bitmap = ImageGetFromHttp.downloadBitmapFromUrl(url);
                if (bitmap != null) {
                    imageFileCache.saveBitmap(bitmap, url);
                    imageMemoryCache.addBitmapToCache(url, bitmap);
                }
            } else {
                imageMemoryCache.addBitmapToCache(url, bitmap);
            }
        }
        return bitmap;
    }
}
