package com.shuwei.weex.weexutils;

import com.taobao.weex.appfram.navigator.IActivityNavBarSetter;

/**
 * @author alguojian
 * @date 2018.03.24
 */

public class BaseIActivityNavBarSetter implements IActivityNavBarSetter {
    @Override
    public boolean push(String param) {
        return false;
    }

    @Override
    public boolean pop(String param) {
        return false;
    }

    @Override
    public boolean setNavBarRightItem(String param) {
        return false;
    }

    @Override
    public boolean clearNavBarRightItem(String param) {
        return false;
    }

    @Override
    public boolean setNavBarLeftItem(String param) {
        return false;
    }

    @Override
    public boolean clearNavBarLeftItem(String param) {
        return false;
    }

    @Override
    public boolean setNavBarMoreItem(String param) {
        return false;
    }

    @Override
    public boolean clearNavBarMoreItem(String param) {
        return false;
    }

    @Override
    public boolean setNavBarTitle(String param) {
        return false;
    }
}
