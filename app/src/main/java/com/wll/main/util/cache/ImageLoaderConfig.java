package com.wll.main.util.cache;

/**
 * Created by WLL on 2016/2/18.
 */
public class ImageLoaderConfig {

    public static class Builder {
        /**
         * 网络下载图片超时时间
         */
        public int imageDownloaderTimeout;
        /**
         * 缓存文件的目录
         */
        public String cacheDir;
    }
}
