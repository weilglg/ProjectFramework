package com.wll.main.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wll.main.R;
import com.wll.main.util.RGBLuminanceSource;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.concurrent.Executors;

/**
 * Created by WLL on 2016/2/26.
 */
public class DiscernCodeWebView extends WebView implements View.OnLongClickListener {

    private String codeAnalyticalResult;

    public DiscernCodeWebView(Context context) {
        this(context, null);
    }

    public DiscernCodeWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscernCodeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnLongClickListener(this);
    }


    @Override
    public boolean onLongClick(View v) {
        WebView webView = (WebView) v;
        HitTestResult hitTestResult = ((WebView) v).getHitTestResult();
        if (hitTestResult != null) {
            int type = hitTestResult.getType();
            if (type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                String imageUrl = hitTestResult.getExtra();
                showPopupWindow(webView.getRootView(), imageUrl);
            }
        }
        return false;
    }

    private void showPopupWindow(View parent, String imageUrl) {
        View view = View.inflate(getContext(), R.layout.popu_discern_code_operating, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams
                .WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        final TextView codeView = (TextView) view.findViewById(R.id.popuwoindow_eqCode);
        codeView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(codeAnalyticalResult)) return;
                Toast.makeText(getContext(), codeAnalyticalResult, Toast.LENGTH_LONG).show();
                codeAnalyticalResult = "";
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
        scanningImage(bitmap, new IZCodeCallBack() {
            @Override
            public void ZCodeCallBackUi(final String result) {
                DiscernCodeWebView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        codeAnalyticalResult = result;
                        if (TextUtils.isEmpty(result)) {
                            codeView.setVisibility(View.GONE);
                        } else {
                            codeView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    /**
     * 解析选择图片的二维码
     *
     * @param bitmap 传入bitmap
     * @return
     */

    public void scanningImage(final Bitmap bitmap, final IZCodeCallBack callBack) {
        if (bitmap == null) {
            return;
        }
        Executors.newCachedThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    // DecodeHintType 和EncodeHintType
                    Result result = null;
                    Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
                    hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

                    // int[] intArray = new
                    // int[scanBitmap.getWidth()*scanBitmap.getHeight()];
                    // //copy pixel data from the Bitmap into the 'intArray' array
                    // scanBitmap.getPixels(intArray, 0, scanBitmap.getWidth(), 0,
                    // 0,
                    // scanBitmap.getWidth(), scanBitmap.getHeight());

                    RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                            source));
                    QRCodeReader reader = new QRCodeReader();
                    result = reader.decode(binaryBitmap, hints);
                    callBack.ZCodeCallBackUi(recode(result.toString()));
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (ChecksumException e) {
                    e.printStackTrace();
                } catch (com.google.zxing.FormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 调用recode(result.toString) 方法进行中文乱码处理
     *
     * @param str
     * @return
     */
    public static String recode(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }


    public interface IZCodeCallBack {
        public void ZCodeCallBackUi(String result);
    }
}
