package com.shuwei.weex.wxcomponent;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.shuwei.weex.wxview.XdWxWebView;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.adapter.URIAdapter;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.IWebView;
import com.taobao.weex.utils.WXUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展的WebView组件
 *
 * @author alguojian
 * @data 2018/5/24
 */

@Component(lazyload = false)
public class WebViewComponent extends WXComponent {

    private static final String GO_BACK = "goBack";
    private static final String GO_FORWARD = "goForward";
    private static final String RELOAD = "reload";
    private XdWxWebView mWebView;

    @Deprecated
    public WebViewComponent(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, String instanceId, boolean isLazy) {
        this(instance, dom, parent, isLazy);
    }

    public WebViewComponent(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, boolean isLazy) {
        super(instance, dom, parent, isLazy);
        createWebView();
    }

    protected void createWebView() {
        mWebView = new XdWxWebView(getContext());
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case Constants.Name.SHOW_LOADING:
                Boolean result = WXUtils.getBoolean(param, null);
                if (result != null) {
                    setShowLoading(result);
                }
                return true;
            case Constants.Name.SRC:
                String src = WXUtils.getString(param, null);
                if (src != null) {
                    setUrl(src);
                }
                return true;
            default:
                break;
        }
        return super.setProperty(key, param);
    }

    @Override
    protected View initComponentHostView(@NonNull Context context) {
        mWebView.setOnErrorListener((type, message) -> fireEvent(type, message));
        mWebView.setOnPageListener(new IWebView.OnPageListener() {
            @Override
            public void onReceivedTitle(String title) {
                if (getDomObject().getEvents().contains(Constants.Event.RECEIVEDTITLE)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("title", title);
                    fireEvent(Constants.Event.RECEIVEDTITLE, params);
                }
            }

            @Override
            public void onPageStart(String url) {
                if (getDomObject().getEvents().contains(Constants.Event.PAGESTART)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("url", url);
                    fireEvent(Constants.Event.PAGESTART, params);
                }
            }

            @Override
            public void onPageFinish(String url, boolean canGoBack, boolean canGoForward) {
                if (getDomObject().getEvents().contains(Constants.Event.PAGEFINISH)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("url", url);
                    params.put("canGoBack", canGoBack);
                    params.put("canGoForward", canGoForward);
                    fireEvent(Constants.Event.PAGEFINISH, params);
                }
            }
        });
        return mWebView.getView();
    }

    @Override
    public void destroy() {
        super.destroy();
        getWebView().destroy();
    }

    @WXComponentProp(name = Constants.Name.SHOW_LOADING)
    public void setShowLoading(boolean showLoading) {
        getWebView().setShowLoading(showLoading);
    }

    @WXComponentProp(name = Constants.Name.SRC)
    public void setUrl(String url) {
        if (TextUtils.isEmpty(url) || getHostView() == null) {
            return;
        }
        if (!TextUtils.isEmpty(url)) {
            loadUrl(getInstance().rewriteUri(Uri.parse(url), URIAdapter.WEB).toString());
        }
    }

    private IWebView getWebView() {
        return mWebView;
    }

    private void loadUrl(String url) {
        getWebView().loadUrl(url);
    }

    private void fireEvent(String type, Object message) {
        if (getDomObject().getEvents().contains(Constants.Event.ERROR)) {
            Map<String, Object> params = new HashMap<>();
            params.put("type", type);
            params.put("errorMsg", message);
            fireEvent(Constants.Event.ERROR, params);
        }
    }

    public void setAction(String action) {
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(GO_BACK)) {
                goBack();
            } else if (action.equals(GO_FORWARD)) {
                goForward();
            } else if (action.equals(RELOAD)) {
                reload();
            }
        }
    }

    private void goBack() {
        getWebView().goBack();
    }

    private void goForward() {
        getWebView().goForward();
    }

    private void reload() {
        getWebView().reload();
    }

}
