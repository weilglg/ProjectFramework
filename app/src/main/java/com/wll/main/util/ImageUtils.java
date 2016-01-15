package com.wll.main.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by zhy on 15/8/11.
 */
public class ImageUtils {
    /**
     * 根据InputStream获取图片实际的宽度和高度
     *
     * @param imageStream
     * @return
     */
    public static ImageSize getImageSize(InputStream imageStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }

    public static class ImageSize {
        int width;
        int height;

        public ImageSize() {
        }

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "ImageSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    /**
     * 根据原图片的宽高和实际图片的宽高计算缩放比率
     * @param srcSize
     * @param targetSize
     * @return
     */
    public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize) {
        // 源图片的宽度
        int width = srcSize.width;
        int height = srcSize.height;
        int inSampleSize = 1;

        int reqWidth = targetSize.width;
        int reqHeight = targetSize.height;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 计算合适的inSampleSize
     */
    public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ImageView imageView) {
        final int srcWidth = srcSize.width;
        final int srcHeight = srcSize.height;
        final int targetWidth = targetSize.width;
        final int targetHeight = targetSize.height;

        int scale = 1;

        if (imageView == null) {
            scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
        } else {
            switch (imageView.getScaleType()) {
                case FIT_CENTER:
                case FIT_XY:
                case FIT_START:
                case FIT_END:
                case CENTER_INSIDE:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                    break;
                case CENTER:
                case CENTER_CROP:
                case MATRIX:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // min
                    break;
                default:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                    break;
            }
        }

        if (scale < 1) {
            scale = 1;
        }

        return scale;
    }

    /**
     * 根据ImageView获适当的压缩的宽和高
     *
     * @param view
     * @return
     */
    public static ImageSize getImageViewSize(View view) {

        ImageSize imageSize = new ImageSize();

        imageSize.width = getExpectWidth(view);
        imageSize.height = getExpectHeight(view);

        return imageSize;
    }

    /**
     * 根据view获得期望的高度
     *
     * @param view
     * @return
     */
    private static int getExpectHeight(View view) {

        int height = 0;
        if (view == null) return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        //如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = view.getWidth(); // 获得实际的宽度
        }
        if (height <= 0 && params != null) {
            height = params.height; // 获得布局文件中的声明的宽度
        }

        if (height <= 0) {
            height = getImageViewFieldValue(view, "mMaxHeight");// 获得设置的最大的宽度
        }

        //如果宽度还是没有获取到，憋大招，使用屏幕的宽度
        if (height <= 0) {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            height = displayMetrics.heightPixels;
        }

        return height;
    }

    /**
     * 根据view获得期望的宽度
     *
     * @param view
     * @return
     */
    private static int getExpectWidth(View view) {
        int width = 0;
        if (view == null) return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        //如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = view.getWidth(); // 获得实际的宽度
        }
        if (width <= 0 && params != null) {
            width = params.width; // 获得布局文件中的声明的宽度
        }

        if (width <= 0)

        {
            width = getImageViewFieldValue(view, "mMaxWidth");// 获得设置的最大的宽度
        }
        //如果宽度还是没有获取到，憋大招，使用屏幕的宽度
        if (width <= 0)

        {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            width = displayMetrics.widthPixels;
        }

        return width;
    }

    /**
     * 通过反射获取imageview的某个属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;

    }


    /**
     * 根据指定的角度顺时针旋转图片。
     *
     * @param src
     * @param degrees 顺时针旋转的角度
     * @return 若产生异常返回 null
     */
    public static Bitmap rotateBitmap(Bitmap src, float degrees) {
        if (src == null) {
            return null;
        }
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            Bitmap bm = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
            recycleBitmap(src);
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取圆角 Bitmap。
     *
     * @param bm
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bm, float roundPx) {
        return convertRoundedCornerBitmap(bm, roundPx);
    }

    /**
     * 获取圆角 Bitmap。
     *
     * @param bm
     * @param roundPx 圆角的半径
     * @return
     */
    public static Bitmap convertRoundedCornerBitmap(Bitmap bm, float roundPx) {
        if (bm == null) {
            return bm;
        }
        Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.TRANSPARENT;
        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm, rect, rect, paint);

        return output;
    }

    /**
     * 把图片转换成圆形图片。
     *
     * @param bitmap
     * @return
     */
    public static Bitmap convertRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dstLeft, dstTop, dstRight, dstBottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dstLeft = 0;
            dstTop = 0;
            dstRight = width;
            dstBottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dstLeft = 0;
            dstTop = 0;
            dstRight = height;
            dstBottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dstLeft, (int) dstTop, (int) dstRight, (int) dstBottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 回收 Bitmap 资源。<br>
     * <p/>
     * <b>该方法调用了 {@link System#gc()}，当同时大量执行时会影响性能。</b>
     *
     * @param bm
     */
    public static void recycleBitmap(Bitmap bm) {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
            System.gc();
        }
    }

    /**
     * 判断 Bitmap 是否可用（不为 null，并且没有被回收）。
     *
     * @param bm
     * @return
     */
    public static boolean isEnable(Bitmap bm) {
        if (bm != null && !bm.isRecycled()) {
            return true;
        }
        return false;
    }

    /**
     * 把 Bitmap 保存到指定路径的文件中。<br>
     * <p/>
     * 使用 JPEG 格式保存图片。
     *
     * @param bm
     * @param filePath 文件路径
     * @return 保存成功返回 true
     */
    public static boolean convertBitmapToFile(Bitmap bm, String filePath) {
        return convertBitmapToFile(bm, Bitmap.CompressFormat.JPEG, 100, filePath);
    }

    /**
     * 把 Bitmap 保存到指定路径的文件中。<br>
     * <p/>
     * 使用 PNG 格式保存图片。
     *
     * @param bm
     * @param filePath 文件路径
     * @return 保存成功返回 true
     */
    public static boolean convertBitmapToPNGFile(Bitmap bm, String filePath) {
        return convertBitmapToFile(bm, Bitmap.CompressFormat.PNG, 100, filePath);
    }

    /**
     * 把 Bitmap 保存到指定路径的文件中。
     *
     * @param bm
     * @param format   图片保存的格式
     * @param quality  图片的质量
     * @param filePath 文件路径
     * @return 保存成功返回true。
     */
    public static boolean convertBitmapToFile(Bitmap bm, Bitmap.CompressFormat format, int quality, String filePath) {
        if (bm == null || TextUtils.isEmpty(filePath)) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            file.createNewFile();
            fos = new FileOutputStream(file);
            bm.compress(format, quality, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(fos);
        }
        return false;
    }


    /**
     * 把给定的 Bitmap 保存到 /data/data/<package name>/files/ 文件中。
     *
     * @Title convertBitmap2CacheDir
     * @Description 把给定的 Bitmap 保存到 /data/data/<package name>/files/ 文件中。
     *
     * @param context
     * @param bm
     * @param targetFileName
     *            目标文件的文件名（位于 /data/data/<package name>/files 文件夹中）
     * @return 目标文件的路径。若未成功保存则返回 null。
     */
    public static String convertBitmap2CacheDir(Context context, Bitmap bm, String targetFileName) {
        String cachePath = null;
        File cacheFile = new File(context.getCacheDir(), targetFileName);
        try {
            cacheFile.createNewFile();
            cachePath = cacheFile.getAbsolutePath();
            boolean success = ImageUtils.convertBitmapToPNGFile(bm, cachePath);
            if (!success) {
                cachePath = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            cachePath = null;
        }

        return cachePath;
    }
}
