package com.shuwei.weex.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shuwei.weex.weexutils.AppManager;
import com.shuwei.weex.weexutils.BaseIActivityNavBarSetter;
import com.shuwei.weex.weexutils.WXAnalyzerDelegate;
import com.shuwei.weex.weexutils.WeexActivityManager;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.RenderContainer;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXFileUtils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 用于管理weex页面
 *
 * @author alguojian
 * @date 2018/6/20
 */
public class WeexPagerViewModule implements IWXRenderListener, WXSDKInstance.NestedInstanceInterceptor {

    private static final String TTAG = "asdfghjkl";
    private static final String EXCEPTION_FILE_NOT_FOUND = "-200001";
    private WXSDKInstance wxSdkInstance;
    private WXAnalyzerDelegate mWxAnalyzerDelegate;
    private HashMap<String, Object> mConfigMap = new HashMap<>();
    private Uri uri = null;
    private boolean onRenderSuccess;
    private String bundleUrl = null;
    private Context mContext;
    private FrameLayout flContent;
    private LinearLayout errorView;
    private RelativeLayout loading;

    public WeexPagerViewModule(@NonNull Context context) {
        this.mContext = context;
    }

    public WeexPagerViewModule setUrl(String url) {
        this.bundleUrl = url;
        return this;
    }

    public WeexPagerViewModule setView(FrameLayout flContent, LinearLayout errorView, RelativeLayout loading) {
        this.flContent = flContent;
        this.errorView = errorView;
        this.loading = loading;
        return this;
    }


    public WXSDKInstance getWxSdkInstance() {
        return wxSdkInstance;
    }

    /**
     * 加载js过程try-catch--防止加载闪退情况发生
     */
    public void initData() {
        try {
            renderJs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 开始加载js
     */
    private void renderJs() {
        if (null == uri) {
            ((Activity) mContext).finish();
            return;
        }
        loading.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        onRenderSuccess = false;
        boolean isService = TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme());

        destroyWXSdkInstance();
        initWXData(isService ? uri.toString() : null);

        if (isService) {
            loadWXFromService(uri.toString());
        } else {
            loadWXFromLocal();
        }
    }

    /**
     * 解绑weex
     */
    public void destroyWXSdkInstance() {
        if (wxSdkInstance != null) {
            wxSdkInstance.destroy();
            wxSdkInstance = null;
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onDestroy();
            mWxAnalyzerDelegate = null;
        }
    }

    /**
     * 初始化Weex
     */
    private void initWXData(String url) {
        RenderContainer renderContainer = new RenderContainer(mContext);
        wxSdkInstance = new WXSDKInstance(mContext);
        wxSdkInstance.setRenderContainer(renderContainer);
        wxSdkInstance.registerRenderListener(this);
        wxSdkInstance.setNestedInstanceInterceptor(this);
        if (!TextUtils.isEmpty(url)) {
            wxSdkInstance.setBundleUrl(url);
        }
        wxSdkInstance.setTrackComponent(true);
    }

    /**
     * 加载远程JS
     */
    private void loadWXFromService(final String url) {
        mConfigMap.put("bundleUrl", url);
        wxSdkInstance.renderByUrl(TTAG, url, mConfigMap, null, WXRenderStrategy.APPEND_ONCE);
        createActivity();
    }

    /**
     * 加载本地JS
     */
    @SuppressLint("CheckResult")
    private void loadWXFromLocal() {

        mConfigMap.put("bundleUrl", uri.toString());
        final String path = assembleFilePath(uri);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext(WXFileUtils.loadFileOrAsset(path, mContext));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {

                        if (TextUtils.isEmpty(s)) {
                            onException(wxSdkInstance, EXCEPTION_FILE_NOT_FOUND, "local file not found !");
                            return;
                        }
                        wxSdkInstance.render(TTAG, s, mConfigMap, null, WXRenderStrategy.APPEND_ASYNC);
                        createActivity();
                    }
                });
    }

    private void createActivity() {
        mWxAnalyzerDelegate = new WXAnalyzerDelegate(mContext);
        mWxAnalyzerDelegate.onCreate();

        if (wxSdkInstance != null) {
            wxSdkInstance.onActivityCreate();
        }
    }

    /**
     * 裁剪本地地址
     */
    private String assembleFilePath(Uri uri) {
        String path = "";
        if (uri != null) {
            if ("file".equals(uri.getScheme()) && uri.getPath() != null) {
                path = uri.getPath();
            } else {
                path = uri.toString();
            }
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
        }
        return path;
    }

    /**
     * 获得weex加载路径
     */
    public void initInternalWXData() {

        WXSDKEngine.setActivityNavBarSetter(new BaseIActivityNavBarSetter());
        mConfigMap.put("bundleUrl", bundleUrl);
        uri = Uri.parse(bundleUrl);
        String url = mConfigMap.get("bundleUrl") + "";
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        WeexActivityManager.instance().putWeexActivity(url, AppManager.getAppManager().getStackIndex((((BaseWeexActivity) mContext).getActivityWeakReference())));
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {

        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeAllViews();
        }
        flContent.removeAllViews();
        flContent.addView(view);
    }

    /**
     * 加载完成
     */
    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        onRenderSuccess = true;
        loading.setVisibility(View.GONE);
    }

    /**
     * 刷新完成
     */
    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
        loading.setVisibility(View.GONE);
    }

    /**
     * 加载出错，调用空视图
     */
    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        loading.setVisibility(View.GONE);
        if (!onRenderSuccess) {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {

    }

    public Uri getUri() {
        return uri;
    }
}
