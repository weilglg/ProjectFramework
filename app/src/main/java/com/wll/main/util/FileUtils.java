package com.wll.main.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wll on 2015/10/29.
 */
public class FileUtils {
    //获得SDK根目录
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 创建指定路径的文件
     */
    public static File createFileByFileName(String filePath) throws IOException {
        String path = getSDPath();
        if (!TextUtils.isEmpty(path)) {
            path = path + File.separator + filePath;
        }
        File file = new File(path);
        if (!file.exists()) {
            //判断文件目录是否存在，不存在则创建一个
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            //创建文件
            file.createNewFile();
        }
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public static void writeSDFromByte(String path, byte[] data) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        OutputStream outputStream = new FileOutputStream(file);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(data, 0, data.length);
    }

    /**
     * 获取 SD 卡的空闲空间大小（MB）。
     *
     * @return
     * @Title getFreeSpaceOnSdcard
     * @Description 获取 SD 卡的空闲空间大小（MB）。
     */
    @SuppressWarnings("deprecation")
    public static int getFreeSpaceOnSdcard() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / 1024 / 1024;
        return (int) sdFreeMB;
    }

    /**
     * 获取 SD 卡的路径。
     *
     * @return
     * @Title getSDCardPath
     * @Description 获取 SD 卡的路径。
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 根据给定的文件路径生成文件。
     *
     * @return
     * @Title createFile
     * @Description 根据给定的文件路径生成文件。
     */
    public static File createFile(String dirPath, String fileName) {
        File dir = new File(dirPath);
        dir.mkdirs();
        File file = new File(dirPath, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 拼接文件路径。<br>
     * <p/>
     * 去掉多余的 '/'。
     *
     * @param prefix
     * @param suffix
     * @return
     * @Title joinFilePath
     * @Description 拼接文件路径。
     */
    public static String joinFilePath(String prefix, String suffix) {
        int prefixLength = prefix.length();
        boolean haveSlash = (prefixLength > 0 && prefix.charAt(prefixLength - 1) == File.separatorChar);
        if (!haveSlash) {
            haveSlash = (suffix.length() > 0 && suffix.charAt(0) == File.separatorChar);
        }
        return haveSlash ? (prefix + suffix) : (prefix + File.separatorChar + suffix);
    }

    /**
     * 复制文件。
     *
     * @param from 源文件的路径。
     * @param to   目标文件的路径。
     * @return 若复制成功返回 true，否则返回 false。
     * @Title copyFile
     * @Description 复制文件。
     */
    public static boolean copyFile(String from, String to) {
        if (TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
            return false;
        }
        File sourceFile = new File(from);
        File targetFile = new File(to);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bos = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] b = new byte[1024];
            int len;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(bis);
            close(bos);
        }
        return false;
    }

    public static String saveFile(String filePath, InputStream is) {
        FileOutputStream fos = null;
        if (is != null) {
            try {
                fos = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(fos);
                close(is);
            }
        }
        return filePath;
    }

    /**
     * 把给定路径的文件拷贝到 /data/data/<package name>/files/kokozuTemp.jpg 文件夹中。
     *
     * @param context
     * @param filePath
     * @return 若拷贝成功则返回 /data/data/<package name>/files/kokozuTemp.jpg 的路径，否则返回
     * null。
     * @Title createTempImage
     * @Description 把给定路径的文件拷贝到 /data/data/<package name>/files/kokozuTemp.jpg
     * 文件夹中。
     * @deprecated 使用 {@link #copyFile2CacheDir(Context, String, String)} 方法替代该方法。
     */
    public static String createTempImage(Context context, String filePath) {
        return copyFile2CacheDir(context, filePath, "kokozuTemp.jpg");
    }

    /**
     * 把给定路径的文件拷贝到 /data/data/<package name>/files 文件夹中。
     *
     * @param context
     * @param srcFilePath    源文件的路径
     * @param targetFileName 目标文件的文件名（位于 /data/data/<package name>/files 文件夹中）
     * @return 目标文件的路径。若未成功保存则返回 null。
     * @Title copyFile2CacheDir
     * @Description 把给定路径的文件拷贝到 /data/data/<package name>/files 文件夹中。
     */
    public static String copyFile2CacheDir(Context context, String srcFilePath, String targetFileName) {
        String cachePath = null;
        File cacheFile = new File(context.getCacheDir(), targetFileName);
        try {
            cacheFile.createNewFile();
            cachePath = cacheFile.getAbsolutePath();
            copyFile(srcFilePath, cachePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cachePath;
    }


    /**
     * 把 assets 目录下的文件拷贝到 /data/data/<package name>/files 文件夹中。
     *
     * @param context
     * @param from    assets 目录下文件的名称
     * @param to      目标文件的名称
     * @return 若成功返回 true，否则返回 false
     * @Title retrieveFileFromAssets
     * @Description 把 assets 目录下的文件拷贝到 /data/data/<package name>/files 文件夹中。
     */
    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    public static boolean retrieveFileFromAssets(Context context, String from, String to) {
        boolean bRet = false;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(from);
            fos = context.openFileOutput(to, Context.MODE_WORLD_READABLE);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fos);
            close(is);
        }
        return bRet;
    }

    /**
     * 关闭资源（I/O流、数据库资源等）。
     *
     * @param closeable
     * @return 若成功关闭资源则返回 true，否则返回 false。
     * @Title close
     * @Description 关闭资源（I/O流、数据库资源等）。
     */
    public static boolean close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据给定的编码把 InputStream 转换为字符串。
     *
     * @param is
     * @param encoding
     * @return 如果发生异常返回 ""
     * @Title readStream
     * @Description 根据给定的编码把 InputStream 转换为字符串。
     */
    public static String readStream(InputStream is, String encoding) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] result = baos.toByteArray();
            return new String(result, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(baos);
            close(is);
        }
        return "";
    }
}
