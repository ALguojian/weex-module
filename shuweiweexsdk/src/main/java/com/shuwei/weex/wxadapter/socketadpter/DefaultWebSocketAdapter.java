package com.shuwei.weex.wxadapter.socketadpter;

import android.support.annotation.Nullable;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;
import com.taobao.weex.appfram.websocket.IWebSocketAdapter;
import com.taobao.weex.appfram.websocket.WebSocketCloseCodes;
import com.taobao.weex.http.Status;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import okio.BufferedSource;

/**
 * weex默认的socket适配器
 *
 * @author alguojian
 * @date 2018.03.03
 */

public class DefaultWebSocketAdapter implements IWebSocketAdapter {

    private WebSocket ws;
    private EventListener eventListener;
    private SocketEventReporter wsEventReporter;

    @Override
    public void connect(String url, @Nullable final String protocol, EventListener listener) {
        this.eventListener = listener;
        this.wsEventReporter = SocketEventReporter.newInstance();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);

        Request.Builder builder = new Request.Builder();

        if (protocol != null) {
            builder.addHeader(HEADER_SEC_WEBSOCKET_PROTOCOL, protocol);
        }

        builder.url(url);
        wsEventReporter.created(url);

        Request wsRequest = builder.build();
        WebSocketCall webSocketCall = WebSocketCall.create(okHttpClient, wsRequest);

        try {
            Field field = WebSocketCall.class.getDeclaredField("request");
            field.setAccessible(true);
            Request realRequest = (Request) field.get(webSocketCall);
            Headers wsHeaders = realRequest.headers();
            Map<String, String> headers = new HashMap<>();
            for (String name : wsHeaders.names()) {
                headers.put(name, wsHeaders.values(name).toString());
            }
            wsEventReporter.willSendHandshakeRequest(headers, null);

        } catch (Exception e) {
            e.printStackTrace();
        }


        webSocketCall.enqueue(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Request request, Response response) throws IOException {
                ws = webSocket;
                eventListener.onOpen();
                Headers wsHeaders = response.headers();
                Map<String, String> headers = new HashMap<>();
                for (String name : wsHeaders.names()) {
                    headers.put(name, wsHeaders.values(name).toString());
                }
                wsEventReporter.handshakeResponseReceived(response.code(),
                        Status.getStatusText(String.valueOf(response.code())),
                        headers);
            }

            @Override
            public void onMessage(BufferedSource payload, WebSocket.PayloadType type) throws IOException {
                if (type == WebSocket.PayloadType.BINARY) {
                    wsEventReporter.frameReceived(payload.readByteArray());
                } else {
                    String message = payload.readUtf8();
                    eventListener.onMessage(message);
                    wsEventReporter.frameReceived(message);
                }
                payload.close();
            }

            @Override
            public void onPong(Buffer payload) {

            }

            @Override
            public void onClose(int code, String reason) {
                eventListener.onClose(code, reason, true);
                wsEventReporter.closed();
            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                if (e instanceof EOFException) {
                    eventListener.onClose(WebSocketCloseCodes.CLOSE_NORMAL.getCode(), WebSocketCloseCodes.CLOSE_NORMAL.name(), true);
                    wsEventReporter.closed();
                } else {
                    eventListener.onError(e.getMessage());
                    wsEventReporter.frameError(e.getMessage());
                }
            }
        });
    }

    @Override
    public void send(String data) {
        if (ws != null) {
            try {
                Buffer buffer = new Buffer().writeUtf8(data);
                ws.sendMessage(WebSocket.PayloadType.TEXT, buffer.buffer());
                buffer.flush();
                buffer.close();

                wsEventReporter.frameSent(data);
            } catch (Exception e) {
                e.printStackTrace();
                reportError(e.getMessage());
                wsEventReporter.frameError(e.getMessage());
            }
        } else {
            reportError("WebSocket is not ready");
        }
    }

    @Override
    public void close(int code, String reason) {
        if (ws != null) {
            try {
                ws.close(code, reason);
            } catch (Exception e) {
                e.printStackTrace();
                reportError(e.getMessage());
            }
        }
    }

    @Override
    public void destroy() {
        if (ws != null) {
            try {
                ws.close(WebSocketCloseCodes.CLOSE_GOING_AWAY.getCode(), WebSocketCloseCodes.CLOSE_GOING_AWAY.name());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reportError(String message) {
        if (eventListener != null) {
            eventListener.onError(message);
        }
    }
}
