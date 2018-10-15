package com.shuwei.weex.weexutils;

import java.util.HashMap;
import java.util.Map;

/**
 * weex页面管理
 *
 * @author alguojian
 * @date 2018.03.23
 */

public class WeexActivityManager {
    private Map<String, Integer> weexActivityMap = new HashMap<>();

    private WeexActivityManager() {

    }

    public synchronized static WeexActivityManager instance() {
        return InstanceHolder.instance;
    }

    public void putWeexActivity(String url, int index) {
        weexActivityMap.put(url, index);
    }

    public Integer getIndexWithUrl(String url) {
        return weexActivityMap.get(url);
    }

    private static class InstanceHolder {
        private static final WeexActivityManager instance = new WeexActivityManager();
    }
}
