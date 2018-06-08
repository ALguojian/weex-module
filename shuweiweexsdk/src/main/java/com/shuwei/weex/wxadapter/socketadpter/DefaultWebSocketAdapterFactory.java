package com.shuwei.weex.wxadapter.socketadpter;

import com.taobao.weex.appfram.websocket.IWebSocketAdapter;
import com.taobao.weex.appfram.websocket.IWebSocketAdapterFactory;

/**
 * @author alguojian
 * @date 2018.05.23
 */

public class DefaultWebSocketAdapterFactory implements IWebSocketAdapterFactory {
    @Override
    public IWebSocketAdapter createWebSocketAdapter() {
        return new DefaultWebSocketAdapter();
    }
}
