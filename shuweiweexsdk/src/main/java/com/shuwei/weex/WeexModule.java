package com.shuwei.weex;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.shuwei.weex.weexutils.DebugUtils;
import com.shuwei.weex.wxadapter.JsExceptionAdapter;
import com.shuwei.weex.wxadapter.WxImageAdapter;
import com.shuwei.weex.wxadapter.httpadapter.WxOkHttpAdapter;
import com.shuwei.weex.wxadapter.socketadpter.DefaultWebSocketAdapterFactory;
import com.shuwei.weex.wxcomponent.WebViewComponent;
import com.shuwei.weex.wxmodule.PhotoBrowserModule;
import com.shuwei.weex.wxmodule.ProgressHud;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.bridge.WXBridgeManager;
import com.tencent.smtt.sdk.QbSdk;

/**
 * 初始化工作
 *
 * @author alguojian
 * @date 2018/6/8
 */
public class WeexModule {

    /**
     * 初始化
     *
     * @param context 必须是application的对象
     * @param flag    是否开启weex调试
     */
    public static void init(@NonNull Context context, boolean flag) {

        QbSdk.initX5Environment(context, null);
        initWeex(context, flag);
    }


    /**
     * 初始化Weex,添加组件
     * 添加更多扩展Module只需要：WXSDKEngine.registerModule("_photoBrowser", PhotoBrowserModule.class);
     * 添加更多Component需要： WXSDKEngine.registerComponent("web", WebViewComponent.class);
     *
     * @param context
     * @param flag
     */
    private static void initWeex(Context context, boolean flag) {
        try {

            InitConfig build = new InitConfig.Builder()
                .setImgAdapter(new WxImageAdapter())
                .setJSExceptionAdapter(new JsExceptionAdapter())
                .setHttpAdapter(new WxOkHttpAdapter())
                .setWebSocketAdapterFactory(new DefaultWebSocketAdapterFactory())
                .build();
            WXSDKEngine.initialize((Application) context, build);

            WXEnvironment.setOpenDebugLog(flag);
            WXEnvironment.setApkDebugable(flag);
            WXBridgeManager.updateGlobalConfig("wson_on");

            WXSDKEngine.registerModule("_photoBrowser", PhotoBrowserModule.class);
            WXSDKEngine.registerModule("_hud", ProgressHud.class);

            WXSDKEngine.registerComponent("web", WebViewComponent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableRemoteDebug(Context context) {
        WXEnvironment.sDebugServerConnectable = true;
        WXEnvironment.sRemoteDebugMode = false;
        WXEnvironment.sRemoteDebugProxyUrl = "ws://" + DebugUtils.getDebugServer(context) + "/debugProxy/native";
        WXSDKEngine.reload();
    }
}
