package com.shuwei.weex.wxmodule;

import android.os.Handler;

import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.TreeMap;

/**
 * @author alguojian
 * @date 2018.04.22
 */
public class BaseWXModule extends WXModule {

    public static final String MSG_SUCCESS = "success";
    public static final String MSG_FAILED = "fail";

    public static final String INSTANCE_ID = "instanceId";
    public static final String WEEX_ACTION = "com.shuwtech.weex.pager";
    public static final String TTAG = "asdfghjkl";
    public Handler handler = new Handler();

    public boolean checkContext(JSCallback jsCallback) {
        if (mWXSDKInstance == null || mWXSDKInstance.getContext() == null) {
            jsCallBack(jsCallback, MSG_FAILED);
            return false;
        }
        return true;
    }

    public void jsCallBack(JSCallback jsCallback, String msg) {
        jsCallBack(jsCallback, msg, 0);
    }

    public void jsCallBack(JSCallback jsCallback, String msg, int time) {

        TreeMap<String, String> stringStringTreeMap = new TreeMap<>();

        stringStringTreeMap.put("result", msg);
        jsCallBack(jsCallback, stringStringTreeMap, time);
    }

    public void jsCallBack(JSCallback jsCallback, TreeMap<String, String> treeMap, int time) {
        if (jsCallback != null) {
            handler.postDelayed(new RunnableHandler(jsCallback, treeMap), time);
        }
    }

    public void jsCallBack(JSCallback jsCallback, int index) {

        TreeMap<String, Integer> stringStringTreeMap = new TreeMap<>();

        stringStringTreeMap.put("result", index);

        jsCallback.invokeAndKeepAlive(stringStringTreeMap);
    }

    public void jsCallBack(JSCallback jsCallback, TreeMap<String, String> treeMap) {
        jsCallBack(jsCallback, treeMap, 0);
    }

    public static class RunnableHandler implements Runnable {

        private final JSCallback jsCallback;
        private final TreeMap<String, String> treeMap;

        public RunnableHandler(JSCallback jsCallback, TreeMap<String, String> treeMap) {
            this.jsCallback = jsCallback;
            this.treeMap = treeMap;
        }

        @Override
        public void run() {
            jsCallback.invokeAndKeepAlive(treeMap);
        }
    }


}
