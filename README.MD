# weex封装组件

> [weex官方文档](https://weex.apache.org/cn/)

> [weex源码地址](https://github.com/apache/incubator-weex)

使用如下：

### 1. 初始化
```

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        **
        * 初始化
        *
        * @param context    application的对象
        * @param flag           是否开启weex调试
        */
        ShuWeiWeexSdk.init(this,true);
    }
}


```

### 2. 添加weex页面

```

    // 跳转到weex--activity
    WeexPagerActivity.start(this,"home/home.js");

    //添加weex--fragment
    WeexPagerFragment.newInstance("home/home.js");
```

### 3. 添加module例如下

```
public class ProgressHud extends BaseWXModule {

    //运行在主线程
    @JSMethod(uiThread = true)
    public void show(JSCallback jsCallback) {

        //检测jscallback是否为null，为null直接回复fail
        if (!checkContext(jsCallback)) {
            return;
        }

        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mWXSDKInstance.getContext(), 2);
            mLoadingDialog.setDissmissByOutside(false);
            mLoadingDialog.setDissmissByBack(true);
        }
        mLoadingDialog.show();

        //传递成功的消息，只包含result一个字段
        jsCallBack(jsCallback, MSG_SUCCESS);

        //如果需要传递多个字段建议如下：
        // jsCallBack(jsCallback,new TreeMap<String, String>());

    }
}

```

### 4. 添加Component

更多建议参考weex文档：[android使用weex扩展组件](https://weex.apache.org/cn/guide/extend-android.html)

```

public class RichText extends WXComponent<TextView> {

    public RichText(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
    }

    @Override
    protected TextView initComponentHostView(@NonNull Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    @WXComponentProp(name = "tel")
    public void setTel(String telNumber) {
        getHostView().setText("tel: " + telNumber);
    }
}

```

### 5. 目前扩展的组件有：

Module:

- _hud 加载动画
- _photoBrowser 图片浏览器

Component

- web 使用tencent x5 内核的webView

