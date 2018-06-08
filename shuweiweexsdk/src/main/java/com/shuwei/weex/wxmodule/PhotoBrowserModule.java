package com.shuwei.weex.wxmodule;

import com.alguojian.imagegesture.ImageGesture;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * 点击图片双击放大缩小，多图可以滑动，长按下载
 *
 * @author alguojian
 * @date 2018.04.30
 */

public class PhotoBrowserModule extends BaseWXModule {

    /**
     * 获得缓存
     *
     * @param jsCallback
     */
    @JSMethod(uiThread = true)
    public void showImages(String[] imageUrls, int index, JSCallback jsCallback) {

        if (!checkContext(jsCallback)) {
            return;
        }

        List<String> list = new ArrayList<>(Arrays.asList(imageUrls));

        ImageGesture.setDate(mWXSDKInstance.getContext(), index, list);

        jsCallBack(jsCallback, MSG_SUCCESS);

        jsCallBack(jsCallback,new TreeMap<String, String>());

    }

}
