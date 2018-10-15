package com.shuwei.weex.wxadapter.httpadapter;

/**
 * @author alguojian
 * @date 2018.05.21
 */
public interface RequestListener {

    void onRequest(long consumed, long total, boolean done);
}
