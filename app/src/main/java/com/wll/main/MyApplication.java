package com.wll.main;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.wll.main.util.FileUtils;
import com.wll.main.util.LogUtils;
import com.wll.main.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wll on 2015/10/29.
 */
public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {

    private static MyApplication myApplication;
    /**
     * "directory"
     */
    private final String KEY_DIRECTORY_NAME = "directory";
    /**
     * SD卡文件夹名称
     */
    public static String sDirectoryName;
    /**
     * 默认SdD卡文件夹名称
     */
    public static String DEFAULT_DIRECTORY_NAME = "Cache";

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        initImageLoaderConfigs();
    }

    private void initImageLoaderConfigs() {
        getDirectory();
        /**
         * ImageLoader全局参数设置
         */
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.banner_load_img)  //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.banner_load_img) //设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.banner_load_img) //设置图片加载/解码过程中错误时候显示的图片
                .resetViewBeforeLoading(false) // //设置图片在下载前是否重置，复位
                .delayBeforeLoading(1000)//设置图片下载前的延迟
                .cacheInMemory(true)  //设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)  //设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的解码类型
//                .displayer(new RoundedBitmapDisplayer(0))//是否设置为圆角，弧度为多少
                .build();

        final String imageCacheDir = sDirectoryName + "/cache/images";
        final int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 6);

        File cacheDir = new File(FileUtils.getSDCardPath() + imageCacheDir);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCacheExtraOptions(480, 800)// 设置最大的宽度和高度
                .threadPoolSize(3)// 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)// 降低线程的优先级保证主UI线程不受太大影响
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(memoryCacheSize)// 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)// sd卡(本地)缓存的最大值
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())// 将保存的时候的URI名称用HASHCODE加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(100) // 缓存的文件数量
                .diskCache(new UnlimitedDiskCache(cacheDir)) //设置文件缓存路径
                .defaultDisplayImageOptions(options)
                .imageDownloader(
                        new BaseImageDownloader(getApplicationContext(),
                                5 * 1000, 30 * 1000)) // 设置加载网络图片和读取缓存图片的超时时间
                .writeDebugLogs() // 打印debug log
                .build();
        ImageLoader.getInstance().init(config);


    }

    private void getDirectory() {
        sDirectoryName = ResourceUtils.getApplicationMetaData(this, KEY_DIRECTORY_NAME);
        if (TextUtils.isEmpty(sDirectoryName)) {
            sDirectoryName = DEFAULT_DIRECTORY_NAME;
        }
    }

    public static MyApplication getMyApplication() {
        return myApplication;
    }

    // 异常退出
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 写入异常日志文件仅供调试使用.
        writeExceptionLogFile(ex);
    }

    /**
     * @author Hank
     * @date 2015-08-19 写入异常日志文件，仅供调试使用，正式发布时需注释一下代码
     */
    private void writeExceptionLogFile(final Throwable ex) {
        Log.e("Exception print", "error log", ex);
        if ("true".equals(LogUtils.debugMode)) {
            String exPrint = getStackTraceString(ex);
            String filePath = myApplication.getPackageName() + File.separator + "trace";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            filePath = filePath + "/" + format.format(new Date()) + ".log";
            try {
                FileUtils.createFileByFileName(filePath);
                FileUtils.writeSDFromByte(filePath, exPrint.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 立即退出
        System.exit(0);
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
