package com.wll.main.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.wll.main.R;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import static com.wll.main.util.GlideUtil.GlideEnum.HEAD_IMAGE;


/**
 * Created by heym on 16/9/22.
 */
public class GlideUtil {

    /**
     * 加载图片
     */
    public static void loadImage(final Context mContext, String url, final ImageView imageView, GlideEnum glideEnum) {

        loadImage(mContext, url, imageView, glideEnum, true);
    }

    /**
     * 加载图片
     */
    public static void loadImage(final Context mContext, String url, final ImageView imageView, GlideEnum glideEnum, boolean isCompress) {
        loadImage(mContext, url, imageView, glideEnum, null, isCompress);
    }

    //    _100x50.jpg
    public static void loadImage(final Context mContext, String url, final ImageView imageView, final GlideEnum glideEnum, final ImageLoadListener
            imageLoadListener, boolean isCompress) {
        int imageId = getImgIdByEnum(glideEnum);
//        int width = imageView.getWidth();
//        int height = imageView.getHeight();
//        if (isCompress && !StringUtils.isEmpty(url) && width != 0 && height != 0) {//获取文件后缀
//            int lastIndexOf = url.lastIndexOf(".");
//            if (lastIndexOf > -1) {
//                String urlPostfix = url.substring(lastIndexOf);
//                url = url + "_" + width + "x" + height + urlPostfix;
//            }
//        }
        RequestOptions options = new RequestOptions();
        options.error(imageId).placeholder(imageId);
        RequestBuilder<Bitmap> requestBuilder = Glide.with(mContext).asBitmap().load(url)
                .transition(BitmapTransitionOptions.withCrossFade());
        if (glideEnum == GlideEnum.RADIUS_IMAGE) {
            options.transform(new GlideRoundTransform(mContext, 10));
            requestBuilder.apply(options).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    imageView.setImageBitmap(resource);
                    if (imageLoadListener != null) {
                        imageLoadListener.imageSuccess(resource);
                    }
                }
            });
        } else {
            requestBuilder.apply(options).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    if (HEAD_IMAGE == glideEnum) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    } else {
                        imageView.setImageBitmap(resource);
                    }
                    if (imageLoadListener != null) {
                        imageLoadListener.imageSuccess(resource);
                    }
                }
            });
        }


    }

    private static int getImgIdByEnum(GlideEnum glideEnum) {
        switch (glideEnum) {
            case BIG_IMAGE:
                return R.mipmap.icon_big_default;
            case SMALL_IMAGE:
                return R.mipmap.icon_small_default;
            case HEAD_IMAGE:
                return R.mipmap.user_header_img_default;
            default:
                return R.mipmap.icon_small_default;
        }
    }


    public interface ImageLoadListener {
        void imageSuccess(Bitmap bitmap);
    }

    public enum GlideEnum {
        BIG_IMAGE,//默认大图片
        SMALL_IMAGE,//默认小图片
        HEAD_IMAGE,  //头像图片
        RADIUS_IMAGE,  //有圆角的图片
    }


    /**
     * 清除缓存
     **/
    public static void clearCache(final Context mContext) {
        //内部缓存的路径
        File cacheDir = new File(mContext.getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR);
        if (cacheDir.list() != null) {
            deleteFolderFile(cacheDir.getAbsolutePath(), true);
        }
        //外部缓存的路径
        File externalCacheDir = new File(mContext.getExternalCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR);
        deleteFolderFile(externalCacheDir.getAbsolutePath(), true);
    }

    /**
     * 获取缓存文件的大小
     **/
    public static long getCacheSize(final Context mContext) {
        long cacheSize = 0;
        //内部缓存的路径
        File cacheDirectory = mContext.getCacheDir();
        File cacheDir = new File(cacheDirectory, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR);
        if (cacheDir.list() != null) {
            try {
                cacheSize += getFolderSize(cacheDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //外部缓存的路径
        File externalCacheDirectory = mContext.getExternalCacheDir();
        File externalCacheDir = new File(externalCacheDirectory, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR);
        if (externalCacheDir.list() != null) {
            try {
                cacheSize += getFolderSize(externalCacheDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cacheSize;
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class GlideRoundTransform extends BitmapTransformation {
        private static float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }
    }

}
