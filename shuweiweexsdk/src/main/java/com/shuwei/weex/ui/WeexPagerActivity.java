package com.shuwei.weex.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shuwei.weex.R;

/**
 * 使用weex加载的activity
 *
 * @author alguojian
 * @date 2018.06.08
 */
public class WeexPagerActivity extends BaseWeexActivity {

    public static final String BUNDLE_URL = "bundleUrl";

    protected WeexPagerViewModule mWeexPagerViewModule;

    /**
     * 跳转到weex页面
     *
     * @param context 上下文
     * @param url     页面url
     */
    public static void start(@NonNull Context context, @NonNull String url) {
        Intent starter = new Intent(context, WeexPagerActivity.class);
        starter.putExtra(BUNDLE_URL, url);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weex_pager);
        FrameLayout flContent = findViewById(R.id.flContent);
        final LinearLayout errorView = findViewById(R.id.errorView);
        RelativeLayout loading = findViewById(R.id.loading);
        errorView.setVerticalGravity(View.GONE);
        mWeexPagerViewModule = new WeexPagerViewModule(this);
        mWeexPagerViewModule.setView(flContent, errorView, loading).setUrl(getIntent().getStringExtra(BUNDLE_URL));

        errorView.setOnClickListener(v -> {
            errorView.setVerticalGravity(View.GONE);
            mWeexPagerViewModule.initData();
        });
        mWeexPagerViewModule.initInternalWXData();
        mWeexPagerViewModule.initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeexPagerViewModule.destroyWXSdkInstance();
    }

}
