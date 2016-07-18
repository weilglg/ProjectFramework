package com.wll.main.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wll.main.R;
import com.wll.main.widget.ImageCycleView;

import java.util.ArrayList;

/**
 * Created by wll on 2015/10/28.
 */
public abstract class BaseFragment extends Fragment {

    protected static final String ACTIVITY_BUNDLE = "ACTIVITY_BUNDLE";

    /**
     * Fragment绑定的布局文件资源ID
     */
    private int layoutResID;
    /**
     * 广告栏
     */
    private ImageCycleView bannerLayout;
    /**
     * 当前的Fragment是否可见
     */
    private boolean mCurrentViewIsVisible;
    /**
     * 标志位，标志已经初始化完成
     */
    protected boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     * <p>
     * 子类在加载完成数据后要把该值修改为true
     * </p>
     */
    protected boolean mHasLoadedOnce = false;

    private View view;
    /**
     * 是否是第一次加载广告布局
     */
    private boolean isFirstLoadBanner;

    /**
     * 是否是第一次加载
     */
    private boolean isFirstLoaderView = false;

    public BaseFragment() {
        layoutResID = getViewResId();
        view = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layoutResID != 0 && view == null) {
            view = loadContentView(layoutResID);
            isPrepared = true;
            isFirstLoaderView = true;
        } else if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            isFirstLoaderView = false;
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (view != null && isFirstLoaderView) {
            initUI(view);
            initListener();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUserVisibleHint(getUserVisibleHint());
    }

    /**
     * 重写此方法是为了延迟预加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isVisibleToUser) {
            mCurrentViewIsVisible = true;
            onCurrentViewVisible();
        } else {
            mCurrentViewIsVisible = false;
            onCurrentViewNoVisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
     * 设置广告指示器的形状（1代表横线，2代表圆点）
     */
    protected int getAdIndicatorType() {
        return 2;
    }

    /**
     * 广告栏中指示器的位置（0表示在左边、1表示在中间、2表示在右边）
     */
    protected int getAdIndicatorPosition() {
        return 1;
    }

    /**
     * 广告栏中每个Item的点击事件监听
     */
    protected ImageCycleView.ImageCycleViewListener getImageCycleViewListener() {
        return null;
    }

    /**
     * 广告栏要显示的banner的URL链接
     */
    protected ArrayList<String> getBannerUrlList() {
        return null;
    }

    /**
     * 当前Fragment不可见时的操作
     */
    protected void onCurrentViewNoVisible() {

    }

    /**
     * 当前Fragment可见时的操作
     */
    private void onCurrentViewVisible() {
        if (!isPrepared || !mCurrentViewIsVisible || mHasLoadedOnce) {
            return;
        }
        loaderData();
    }

    /**
     * 加载数据
     */
    protected abstract void loaderData();

    /**
     * 是否需要加载广告布局，子类需要加载时重写该方法返回true
     *
     * @return 如果需要加载网络布局就返回true，不需要就返回false
     */
    protected boolean isLoadBannerLayout() {
        return false;
    }

    /**
     * 要给自定义的布局装载广告布局时，需要重写此方法。同时需要重写getAddViewIndex()
     */
    protected int getLoadNetLayoutOrAdLayoutId() {
        return 0;
    }

    /**
     * 根据指定的布局文件ID获取视图
     */
    protected View inflateView(int layoutResID) {
        return LayoutInflater.from(getActivity()).inflate(layoutResID, null);
    }

    /**
     * 根据指定的布局文件ID获取视图
     */
    protected View inflateView(int layoutResID, ViewGroup root) {
        return LayoutInflater.from(getActivity()).inflate(layoutResID, root, false);
    }

    /**
     * 设置装载广告布局的位置，默认为0
     */
    protected int getAddViewIndex() {
        return 0;
    }

    /**
     * 显示提示语
     */
    protected void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void toActivity(Class<?> cls) {
        toActivity(getActivity(), cls, null);
    }

    protected void toActivity(Class<?> cls, Bundle bundle) {
        toActivity(getActivity(), cls, bundle);
    }

    protected void toActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(ACTIVITY_BUNDLE, bundle);
        startActivity(intent);
    }

    /**
     * 获取回调的start
     */
    protected void toActivityForResult(Context context, Class<?> cls,
                                       int requestCode) {
        toActivityForResult(context, cls, requestCode, null);
    }

    protected void toActivityForResult(Context context, Class<?> cls,
                                       int requestCode, @Nullable Bundle options) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(ACTIVITY_BUNDLE, options);
        startActivityForResult(intent, requestCode);
    }


    /**
     * 隐藏软键盘
     */
    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 返回要加载的布局文件的ID
     */
    protected abstract int getViewResId();

    /**
     * 加载并装载布局文件
     */
    private View loadContentView(int layoutResID) {
        View contentView = inflateView(layoutResID);
        if (isLoadBannerLayout()) {
            LinearLayout bodyLayout = null;
            //获得自定义布局中要拼装布局的控件ID（该控件必须是LinearLayout）
            int bodyLayoutId = getLoadNetLayoutOrAdLayoutId();
            if (bodyLayoutId != 0) {
                View view = contentView.findViewById(bodyLayoutId);
                if (view instanceof LinearLayout) {
                    bodyLayout = (LinearLayout) view;
                }
            }
            if (bodyLayout != null) {
                //获得要装载布局的位置
                int addViewIndex = getAddViewIndex();
                if (isLoadBannerLayout()) {//是否装载广告布局
                    View view = inflateView(R.layout.base_title_banner_layout);
                    bodyLayout.addView(view, addViewIndex);
                }
            }
        }
        return contentView;
    }


    private void initUI(View v) {
        if (isLoadBannerLayout()) {
            bannerLayout = (ImageCycleView) v.findViewById(R.id.banner_AbSlidingPlayView);
        }
        initViews(v);
    }

    /**
     * 初始化控件
     */
    protected abstract void initViews(View v);


    protected abstract void initListener();


}
