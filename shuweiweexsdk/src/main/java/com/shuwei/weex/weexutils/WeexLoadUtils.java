package com.shuwei.weex.weexutils;

import android.content.Context;
import android.text.TextUtils;

/**
 * weex加载过程
 *
 * @author alguojian
 * @date 2018/6/8
 */
public class WeexLoadUtils {

    public static String getFinalUrl(Context context, String url) {
        if (DebugUtils.getIsServer(context)) {
            return "http://" + getIp(context) + "/dist/" + url;
        } else {
            return url;
        }

    }

    private static String getIp(Context context) {

        String stringData = DebugUtils.getDebugServer(context);
        if (TextUtils.isEmpty(stringData)) {
            return "192.168.6.2:8081";
        } else {
            return stringData;
        }

    }

}
