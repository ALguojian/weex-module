package com.alguojian.example;

import android.app.Application;

import com.shuwei.weex.WeexModule;

/**
 *
 * @author alguojian
 * @date 2018/6/8
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WeexModule.init(this,true);
    }
}
