package com.wll.main.util.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by WLL on 2016/2/18.
 * 从网络上获取图片
 */
public class ImageGetFromHttp {
    private static final String LOG_TAG = "ImageGetFromHttp";
    /**
     * 超时时间
     */
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 5000;


    public static Bitmap downloadBitmapFromUrl(String bitmapUrl) {

        try {
            // 根据地址创建URL对象(网络访问的url)
            URL url = new URL(bitmapUrl);
            // 打开网络连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //设置访问方式
            connection.setReadTimeout(READ_TIMEOUT); // 设置超时时间
            connection.setConnectTimeout(CONNECTION_TIMEOUT); // 设置连接超时时间
            // 设置输入和输出流
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 获取响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = null;
                FilterInputStream fis = null;
                try {
                    //获得响应输入流对象
                    inputStream = connection.getInputStream();
                    fis = new FlushedInputStream(inputStream);
                    //转化bitmap
                    return BitmapFactory.decodeStream(fis);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    if (fis != null) {
                        fis.close();
                        fis = null;
                    }
                }
            }
            //关闭连接
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
