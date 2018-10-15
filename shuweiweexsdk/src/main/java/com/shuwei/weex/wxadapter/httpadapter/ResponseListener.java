package com.shuwei.weex.wxadapter.httpadapter;

/**
 * @author alguojian
 * @date 2018.05.21
 */
public interface ResponseListener {

    void onResponse(long consumed, long total, boolean done);
}
