package com.shuwei.weex.event;

import java.util.List;

public class WeexRefreshEvent {
    public static final String KEY_WEEX_REFRESH = "key_weex_refresh";
    public List<String> urls;
    public String key;

    public WeexRefreshEvent(List<String> urls, String key) {
        this.urls = urls;
        this.key = key;
    }
}
