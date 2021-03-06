package com.shuwei.weex.wxadapter.httpadapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;

import java.io.IOException;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * weex网络适配器
 *
 * @author alguojian
 * @data 2018/5/24
 */
public class WxOkHttpAdapter implements IWXHttpAdapter {

    private static final int REQUEST_FAILURE = -100;
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    @Override
    public void sendRequest(final WXRequest request, final OnHttpListener listener) {
        if (listener != null) {
            listener.onHttpStart();
        }

        RequestListener requestListener = new RequestListener() {
            @Override
            public void onRequest(long consumed, long total, boolean done) {
                if (Assert.checkNull(listener)) {
                    listener.onHttpUploadProgress((int) (consumed));
                }
            }
        };

        final ResponseListener responseListener =new ResponseListener() {
            @Override
            public void onResponse(long consumed, long total, boolean done) {
                if (Assert.checkNull(listener)) {
                    listener.onHttpResponseProgress((int) (consumed));
                }
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new IncrementalResponseBody(originalResponse.body(), responseListener))
                                .build();
                    }
                }).build();

        Request okHttpRequest;

        if (METHOD_GET.equalsIgnoreCase(request.method)) {
            okHttpRequest = new Request.Builder()
                    .headers(addHeaders(request))
                    .url(request.url)
                    .get()
                    .build();

        } else if (METHOD_PUT.equalsIgnoreCase(request.method)) {
            okHttpRequest = new Request.Builder()
                    .headers(addHeaders(request))
                    .url(request.url)
                    .put(new IncrementaRequestBody(RequestBody.create(MediaType.parse(request.body), request.body), requestListener))
                    .build();

        } else if (METHOD_POST.equalsIgnoreCase(request.method)) {
            okHttpRequest = new Request.Builder()
                    .headers(addHeaders(request))
                    .url(request.url)
                    .post(new IncrementaRequestBody(RequestBody.create(MediaType.parse(request.body), request.body), requestListener))
                    .build();

        } else if (METHOD_DELETE.equalsIgnoreCase(request.method)) {
            okHttpRequest = new Request.Builder()
                    .headers(addHeaders(request))
                    .url(request.url)
                    .delete()
                    .build();

        } else if (!TextUtils.isEmpty(request.method)) {
            okHttpRequest = new Request.Builder()
                    .headers(addHeaders(request))
                    .url(request.url)
                    .method(request.method, new IncrementaRequestBody(RequestBody.create(MediaType.parse(request.body), request.body), requestListener))
                    .build();
        } else {
            okHttpRequest = new Request.Builder()
                    .headers(addHeaders(request))
                    .url(request.url)
                    .get()
                    .build();
        }
        client.newCall(okHttpRequest).enqueue(commonCallBack(listener));
    }

    private Headers addHeaders(WXRequest request) {
        Headers.Builder builder = new Headers.Builder();
        if (request.paramMap != null) {
            Set<String> keySets = request.paramMap.keySet();
            for (String key : keySets) {
                builder.add(key, request.paramMap.get(key));
            }
        }
        return builder.build();
    }

    private Callback commonCallBack(final OnHttpListener listener) {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (Assert.checkNull(listener)) {
                    WXResponse wxResponse = new WXResponse();
                    wxResponse.errorCode = String.valueOf(REQUEST_FAILURE);
                    wxResponse.statusCode = String.valueOf(REQUEST_FAILURE);
                    wxResponse.errorMsg = e.getMessage();
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (Assert.checkNull(listener)) {

                    WXResponse wxResponse = new WXResponse();
                    wxResponse.statusCode = String.valueOf(response.code());
                    if (requestSuccess(Integer.parseInt(wxResponse.statusCode))) {
                        wxResponse.originalData = response.body().bytes();
                    } else {
                        wxResponse.errorCode = String.valueOf(response.code());
                        wxResponse.errorMsg = response.body().string();
                    }
                    listener.onHttpFinish(wxResponse);
                }
            }
        };
    }

    private boolean requestSuccess(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }

}
