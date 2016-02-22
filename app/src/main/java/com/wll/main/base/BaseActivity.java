package com.wll.main.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wll.main.R;
import com.wll.main.receiver.NetWorkReceiver;
import com.wll.main.util.CommUtils;
import com.wll.main.util.SystemStatusManager;
import com.wll.main.widget.ImageCycleView;

import java.util.ArrayList;

public class BaseActivity extends FragmentActivity implements View.OnClickListener {


    /**
     * 左侧按钮
     */
    private View leftLayout;
    private TextView leftTv;
    private ImageView leftImg;
    /**
     * 右侧按钮
     */
    private View rightLayout;
    private TextView rightTv;
    private ImageView rightImg;
    /**
     * 主标题
     */
    protected TextView mainTitle;
    /**
     * 副标题
     */
    protected TextView subTitle;

    private ImageCycleView bannerLayout;
    /**
     * 网络布局
     */
    public View networkLayout;

    /**
     * 网络变化广播监听器
     */
    private NetWorkReceiver netWorkReceiver;

    private LayoutInflater mInflater;
    /**
     * 整个title布局
     */
    private View titleLayout;

    private boolean isFirstLoadBanner;

    public android.os.Handler baseHandler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NetWorkReceiver.NETWORK_CONNECTED) {
                if (null != networkLayout) {
                    networkLayout.setVisibility(View.GONE);
                }
            } else if (msg.what == NetWorkReceiver.NETWORK_DISCONNECTED) {
                if (null != networkLayout) {
                    networkLayout.setVisibility(View.VISIBLE);
                } else {
                    showToast("请检查您的网络设置");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(this);
    }


    @Override
    public void setContentView(int layoutResID) {
        View contentView = null;
        boolean isLoadSuccess = false;
        if (layoutResID == R.layout.base_title_layout || !isUsedBaseTitleLayout()) { //
            // 当布局的resId跟根布局文件的Id相同或者需要装载统一的子布局时做一些共有的操作
            contentView = inflateView(layoutResID);
            //获取需要组装广告布局或者网络提示布局的控件
            LinearLayout bodyLayout = null;
            if (!isUsedBaseTitleLayout()) {//当不使用统一的标题基布局时，获取要装载广告布局或者网络布局的根布局。也就是要往哪个布局下面装载
                //获得自定义布局中要拼装布局的控件ID（该控件必须是LinearLayout）
                int bodyLayoutId = getLoadNetLayoutOrAdLayoutId();
                if (bodyLayoutId != 0) {
                    View view = contentView.findViewById(bodyLayoutId);
                    if (view instanceof LinearLayout) {
                        bodyLayout = (LinearLayout) view;
                    }
                }
            } else {//当使用统一的标题基布局时，装载到默认的位置
                bodyLayout = (LinearLayout) contentView.findViewById(R.id.base_title_body_layout);
            }
            if (bodyLayout != null) {
                //获得要装载布局的位置
                int addViewIndex = getAddViewIndex();
                if (isLoadNetworkLayout()) {//是否需要装载网络布局
                    View view = inflateView(R.layout.base_title_network_layout);
                    bodyLayout.addView(view, addViewIndex);
                    addViewIndex++;
                }
                if (isLoadBannerLayout()) {//是否装载广告布局
                    View view = inflateView(R.layout.base_title_banner_layout);
                    bodyLayout.addView(view, addViewIndex);
                    addViewIndex++;
                }
            }
            super.setContentView(contentView);
            //当使用了自己定义的布局时
            if (!isUsedBaseTitleLayout()) {
                isLoadSuccess = true;
            }
        } else {// 传入的布局文件的resID不是根布局的resID时,先加载根布局再将子布局加入到根布局中
            setContentView(R.layout.base_title_layout);
            //获取bodyLayout中装载子布局的控件FrameLayout
            FrameLayout mContainer = (FrameLayout) findViewById(R.id.base_title_container);
            //加载需要装载的布局文件
            contentView = inflateView(layoutResID);
            //将子布局文件装载到FrameLayout中
            mContainer.addView(contentView);
            isLoadSuccess = true;
        }
        if (isLoadSuccess) {
            //初始化控件
            initViews();
            initListener();
        }
    }

    /**
     * 设置app不需要根据系统字体的大小来改变
     **/
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册广播
        if (isLoadNetworkLayout()) {
            if (null != netWorkReceiver) {
                registerNetWorkReceiver();
            } else {
                netWorkReceiver = new NetWorkReceiver(baseHandler);
                registerNetWorkReceiver();
            }
        }
        if (isLoadBannerLayout() && bannerLayout != null) {
            if (!isFirstLoadBanner) {
                ArrayList<String> list = getBannerUrlList();
                ImageCycleView.ImageCycleViewListener imageCycleViewListener = getImageCycleViewListener();
                bannerLayout.setImageResources(list, imageCycleViewListener, getAdIndicatorType(),
                        getAdIndicatorPosition());
                isFirstLoadBanner = true;
            } else {
                bannerLayout.startImageCycle();
            }
        }
    }

    /**
     * 设置广告指示器的形状（1代表方框，2代表圆点）
     */
    protected int getAdIndicatorType() {
        return 2;
    }

    /**
     * 返回广告图片的URL
     */
    protected ArrayList<String> getBannerUrlList() {
        return null;
    }

    /**
     * 广告栏中每个Item的点击事件监听
     */
    protected ImageCycleView.ImageCycleViewListener getImageCycleViewListener() {
        return null;
    }

    /**
     * 广告栏中指示器的位置（0表示在左边、1表示在中间、2表示在右边）
     */
    protected int getAdIndicatorPosition() {
        return 1;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bannerLayout != null)
            bannerLayout.startImageCycle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bannerLayout != null)
            bannerLayout.pushImageCycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        if (isLoadNetworkLayout() && null != netWorkReceiver) {
            unRegisterNetWorkReceiver();
        }
    }

    /**
     * 是否需要加载广告布局，子类需要加载时重写该方法返回true
     *
     * @return 如果需要加载网络布局就返回true，不需要就返回false
     */
    protected boolean isLoadBannerLayout() {
        return false;
    }

    /**
     * 是否需要加载网络布局，子类重写即可
     *
     * @return 如果需要加载网络布局就返回true，不需要就返回false
     */
    public boolean isLoadNetworkLayout() {
        return false;
    }

    /**
     * 要给自定义的布局装载广告布局或者网络布局时，需要重写此方法。<p/>
     * 如果需要装载广告布局需要重写{@link #isLoadBannerLayout()}，同时需要重写{@link #getAddViewIndex()}<p/>
     * 如果需要装载网络布局需要重写{@link #isLoadNetworkLayout()}
     */
    protected int getLoadNetLayoutOrAdLayoutId() {
        return 0;
    }

    /**
     * 根据指定的布局文件ID获取视图
     */
    protected View inflateView(int layoutResID) {
        if (mInflater != null)
            return mInflater.inflate(layoutResID, null);
        return null;
    }

    /**
     * 根据指定的布局文件ID获取视图
     */
    protected View inflateView(int layoutResID, ViewGroup root) {
        if (mInflater != null)
            return mInflater.inflate(layoutResID, root, false);
        return null;
    }


    /**
     * 子类如果不适用统一的标题栏时，需要重写这个方法
     *
     * @return true表示使用统一标题
     */
    protected boolean isUsedBaseTitleLayout() {
        return true;
    }

    /**
     * 设置装载广告布局或者网络布局的位置，默认为0
     */
    protected int getAddViewIndex() {
        return 0;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.base_title_left_layout) {
            onLeftLayoutClick(v);
        } else if (id == R.id.base_title_right_layout) {
            onRightLayoutClick(v);
        } else if (id == R.id.network_root_layout) {
            Intent intent = new Intent((Settings.ACTION_SETTINGS));
            startActivity(intent);
        }
    }

    /**
     * 右侧布局的点击事件
     */
    protected void onRightLayoutClick(View v) {

    }

    /**
     * 左侧布局的点击事件
     */
    protected void onLeftLayoutClick(View v) {
        super.onBackPressed();
    }


    /**
     * 设置主标题内容
     * <p>
     * 标题可以是资源ID或者字符串
     * </p>
     */
    protected void setMainTitleContent(String mainTitleContent) {
        if (mainTitle != null) {
            mainTitle.setText(mainTitleContent);
        }
    }

    protected void setMainTitleContent(int mainTitleContentResId) {
        if (mainTitle != null) {
            mainTitle.setText(mainTitleContentResId);
        }
    }

    /**
     * 设置副标题内容
     * <p>
     * 标题可以是资源ID或者字符串
     * </p>
     */
    protected void setSubTitleContent(String subTitleContent) {
        if (subTitleContent != null) {
            subTitle.setText(subTitleContent);
        }
    }

    protected void setSubTitleContent(int subTitleContentResId) {
        if (subTitle != null) {
            subTitle.setText(subTitleContentResId);
        }
    }

    /**
     * 设置左侧按钮文本内容
     * <p>可以是字符串也可以是字符串资源ID</p>
     */
    protected void setLeftLayoutString(String content) {
        if (leftTv != null) {
            leftTv.setText(content);
            if (leftImg != null && leftImg.getDrawable() != null) {
                leftTv.setPadding((int) getResources().getDimension(R.dimen.dp_ten), 0, 0, 0);
            }
        }
    }

    protected void setLeftLayoutString(int contentResId) {
        if (leftTv != null) {
            leftTv.setText(contentResId);
            if (leftImg != null && leftImg.getDrawable() != null) {
                leftTv.setPadding((int) getResources().getDimension(R.dimen.dp_ten), 0, 0, 0);
            }
        }
    }

    /**
     * 设置标题栏左侧按钮图片
     * <p>可以是Drawable或者图片资源ID</p>
     */
    protected void setLeftLayoutDrawable(Drawable drawable) {
        if (leftImg != null) {
            leftImg.setImageDrawable(drawable);
        }
    }

    protected void setLeftLayoutDrawable(int drawableResId) {
        if (leftImg != null) {
            leftImg.setImageResource(drawableResId);
        }
    }


    /**
     * 设置右侧按钮文本内容
     * <p>可以是字符串也可以是字符串资源ID</p>
     */
    protected void setRightLayoutString(String content) {
        if (rightTv != null) {
            rightTv.setText(content);
            if (rightImg != null && leftImg.getDrawable() != null) {
                rightTv.setPadding(0, 0, (int) getResources().getDimension(R.dimen.dp_ten), 0);
            }
        }
    }

    protected void setRightLayoutString(int contentResId) {
        if (rightTv != null) {
            rightTv.setText(contentResId);
            if (rightImg != null && leftImg.getDrawable() != null) {
                rightTv.setPadding(0, 0, (int) getResources().getDimension(R.dimen.dp_ten), 0);
            }
        }
    }

    /**
     * 设置标题栏右侧按钮图片
     * <p>可以是Drawable或者图片资源ID</p>
     */
    protected void setRightLayoutDrawable(Drawable drawable) {
        if (rightImg != null) {
            rightImg.setImageDrawable(drawable);
        }
    }

    protected void setRightLayoutDrawable(int drawableResId) {
        if (rightImg != null) {
            rightImg.setImageResource(drawableResId);
        }
    }

    /**
     * 设置标题是否隐藏
     */
    public void setVisibilityTitleLayout(boolean isV) {
        if (isV) {
            titleLayout.setVisibility(View.VISIBLE);
        } else {
            titleLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 提示
     */
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转Activity
     */
    public void showActivity(Class<? extends BaseActivity> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
    }

    public void showActivity(Class<? extends BaseActivity> clazz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftKeyboard() {
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //初始化一些基本控件
    protected void initListener() {
        if (leftLayout != null) {
            leftLayout.setOnClickListener(this);
        }
        if (rightLayout != null) {
            rightLayout.setOnClickListener(this);
        }
        if (networkLayout != null) {
            networkLayout.setOnClickListener(this);
        }

    }

    /**
     * 初始化控件
     */
    protected void initViews() {

        //根布局
        View view = findViewById(R.id.base_main_layout);
        //设置手机自身的状态栏状态
        setTranslucentStatus(view, R.color.color_title_background);

        //左侧按钮布局
        leftLayout = findViewById(R.id.base_title_left_layout);
        leftTv = (TextView) findViewById(R.id.base_title_left_tv);
        leftImg = (ImageView) findViewById(R.id.base_title_left_img);
        //右侧按钮布局
        rightLayout = findViewById(R.id.base_title_right_layout);
        rightTv = (TextView) findViewById(R.id.base_title_right_tv);
        rightImg = (ImageView) findViewById(R.id.base_title_right_img);

        titleLayout = findViewById(R.id.base_title_main_layout);
        mainTitle = (TextView) findViewById(R.id.base_title_mainTitle);
        subTitle = (TextView) findViewById(R.id.base_title_subTitle);
        networkLayout = findViewById(R.id.network_root_layout);
        initBannerUI();
    }

    /**
     * 初始化广告布局中的控件
     */
    private void initBannerUI() {
        if (isLoadBannerLayout()) {
            bannerLayout = (ImageCycleView) findViewById(R.id.banner_AbSlidingPlayView);
        }
    }


    /**
     * 注册自身
     */
    private void registerNetWorkReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            filter.addAction("android.net.wifi.STATE_CHANGE");
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addCategory("android.intent.category.DEFAULT");
            registerReceiver(netWorkReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销自身
     */
    private void unRegisterNetWorkReceiver() {
        try {
            unregisterReceiver(netWorkReceiver);
            netWorkReceiver = null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 设置系统状态栏
    @SuppressLint("InlinedApi")
    protected void setTranslucentStatus(View view, int resId) {
        // 判断版本是4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(this);
            // 打开系统状态栏控制
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(resId);// 设置背景
            SystemStatusManager.SystemBarConfig config = tintManager.getConfig();
            // 设置系统栏需要的内偏移
            view.setPadding(0, CommUtils.getStatusHeight(this), 0, config.getPixelInsetBottom());
        }
    }


}
