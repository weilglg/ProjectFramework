package com.wll.main.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.wll.main.R;
import com.wll.main.base.BaseActivity;
import com.wll.main.widget.DiscernCodeWebView;

public class WebViewActivity extends BaseActivity {

    private DiscernCodeWebView webView;
    private WebSettings mWebSettings;


    @Override
    protected int getMainViewResId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setLeftLayoutDrawable(R.drawable.icon_return);
        setMainTitleContent("二维码识别");
        View v = findViewById(R.id.layout_root);
        webView = (DiscernCodeWebView) findViewById(R.id.webView);
        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        //mWebSettings.setUseWideViewPort(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://weixin.qq.com/cgi-bin/readtemplate?uin=&stype=&promote=&fr=www.baidu" +
                ".com&lang=zh_CN&ADTAG=&check=false&nav=download&t=weixin_download_list&loc=readtemplate,weixin,body," +
                "3");

    }

    @Override
    protected void initListener() {
        super.initListener();

    }
}
