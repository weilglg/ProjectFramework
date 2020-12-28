package com.wll.main.util.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by WLL on 2016/2/18.
 * 文件缓存类
 */
public class ImageFileCache {
    private static final String CACHE_DIR = "ImageCache";
    private static final String WHOLESALE_CONV = ".cache";

    private static final int MB = 1024 * 1024;
    /**
     * 规定缓存文件的总大小，单位是MB
     */
    private static final int CACHE_SIZE = 10;
    /**
     * 规定的sdcard的剩余中间大小，单位是MB
     */
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

    public ImageFileCache() {
        // 清理文件缓存
        removeCache(getDirectory());
    }

    /**
     * 根据url从文件缓存中获取图片
     * @param url
     * @return
     */
    public Bitmap getBitmap(final String url) {
        final String path = getSDPath() + File.separator + convertUrlToFileName(url);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                file.delete();
            } else {
                updateFileTime(path);
                return bitmap;
            }
        }
        return null;
    }

    public void saveBitmap(Bitmap bitmap , String url){
        if (bitmap == null) {
            return;
        }
        // 判断sdcard上的空间大小，当空间不足时不进行存储
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSdcard()) {
            return;
        }
        String fileName = convertUrlToFileName(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dirFile, fileName);
        try {
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        }  catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        }
    }

    /**
     * 修改文件的最后修改时间
     *
     * @param path
     */
    private void updateFileTime(String path) {
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /**
     * 将url转化成文件名
     *
     * @param url
     * @return
     */
    private String convertUrlToFileName(String url) {
        String[] strs = url.split(File.separator);
        return strs[strs.length - 1] + WHOLESALE_CONV;
    }

    /**
     * 计算存储目录下的文件大小<p/>
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE那么删除40%最近没有被使用的文件
     *
     * @param dirPath
     * @return
     */
    private boolean removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        int dirSize = 0;
        for (File file : files) {
            if (file.getName().contains(WHOLESALE_CONV)) {
                dirSize += file.length();
            }
        }

        if (dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSdcard()) {
            int removeFactor = (int) (0.4 * files.length + 1);
            Arrays.sort(files, new FileLastModifSort());
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
                }
            }
        }

        if (freeSpaceOnSdcard() <= CACHE_SIZE) {
            return false;
        }
        return true;
    }


    /**
     * 计算sdcard上剩余空间大小
     *
     * @return
     */
    private int freeSpaceOnSdcard() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB;
        if (android.os.Build.VERSION.SDK_INT > 18) {
            sdFreeMB = (double) statFs.getAvailableBlocksLong() * (double) statFs.getBlockSizeLong();
        } else {
            sdFreeMB = (double) statFs.getAvailableBlocks() * (double) statFs.getBlockSize();
        }
        return (int) sdFreeMB;
    }

    /**
     * 获得缓存目录
     *
     * @return
     */
    private String getDirectory() {
        String dir = getSDPath() + File.separator + CACHE_DIR;
        return dir;
    }

    /**
     * 获得SD卡跟路径
     * @return
     */
    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断SD卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获得SD卡根目录
        }
        if (sdDir != null) {
            return sdDir.getAbsolutePath();
        }
        return "";
    }

    /**
     * 根据文件的最后修改时间进行排序
     */
    private class FileLastModifSort implements java.util.Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() > rhs.lastModified()) {
                return 1;
            } else if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            } else {
                return 0;
            }
        }
    }
}
