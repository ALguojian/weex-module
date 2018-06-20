package com.shuwei.weex.wxadapter;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

/**
 * weex图片加载适配器,使用picasso
 *
 * @author alguojian
 * @date 2018.05.26
 */

public class WxImageAdapter implements IWXImgLoaderAdapter {

    private static final String CUSTOM_LOCAL_DRAWABLE = "http://local/";

    @Override
    public void setImage(final String url, final ImageView view, WXImageQuality quality, final WXImageStrategy strategy) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (view.getLayoutParams() == null) {
                    return;
                }

                if (TextUtils.isEmpty(url)) {
                    view.setImageBitmap(null);
                    return;
                }

                if (view.getLayoutParams().width <= 0 || view.getLayoutParams().height <= 0) {
                    return;
                }

                if (!TextUtils.isEmpty(strategy.placeHolder)) {
                    Picasso.Builder builder = new Picasso.Builder(WXEnvironment.getApplication());
                    Picasso picasso = builder.build();
                    picasso.load(Uri.parse(strategy.placeHolder)).into(view);

                    view.setTag(strategy.placeHolder.hashCode(), picasso);
                }

                if (url.startsWith(CUSTOM_LOCAL_DRAWABLE)) {
                    String imageName = url.replace(CUSTOM_LOCAL_DRAWABLE, "")
                            .replaceAll("-", "_")
                            .replaceAll(" ", "_")
                            .toLowerCase();

                    int imageResourceId = getImageResourceId(view.getContext(), imageName);

                    try {
                        Picasso.with(WXEnvironment.getApplication())
                                .load(imageResourceId)
                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        if (strategy.getImageListener() != null) {
                                            strategy.getImageListener().onImageFinish(url, view, true, null);
                                        }

                                        if (!TextUtils.isEmpty(strategy.placeHolder)) {
                                            ((Picasso) view.getTag(strategy.placeHolder.hashCode())).cancelRequest(view);
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        if (strategy.getImageListener() != null) {
                                            strategy.getImageListener().onImageFinish(url, view, false, null);
                                        }
                                    }
                                });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        Picasso.with(WXEnvironment.getApplication())
                                .load(url)
                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        if (strategy.getImageListener() != null) {
                                            strategy.getImageListener().onImageFinish(url, view, true, null);
                                        }

                                        if (!TextUtils.isEmpty(strategy.placeHolder)) {
                                            ((Picasso) view.getTag(strategy.placeHolder.hashCode())).cancelRequest(view);
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        if (strategy.getImageListener() != null) {
                                            strategy.getImageListener().onImageFinish(url, view, false, null);
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            WXSDKManager.getInstance().postOnUiThread(runnable, 0);
        }

    }

    /**
     * @param name : 图片名称(不带后缀)
     * @return ：drawable id
     */
    private int getImageResourceId(Context context, String name) {
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(name, "drawable", packageName);
    }
}
