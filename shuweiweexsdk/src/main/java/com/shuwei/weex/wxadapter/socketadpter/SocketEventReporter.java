package com.shuwei.weex.wxadapter.socketadpter;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.taobao.weex.devtools.inspector.network.NetworkEventReporter;
import com.taobao.weex.devtools.inspector.network.NetworkEventReporterManager;
import com.taobao.weex.devtools.inspector.network.SimpleBinaryInspectorWebSocketFrame;
import com.taobao.weex.devtools.inspector.network.SimpleTextInspectorWebSocketFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author alguojian
 * @date 2018.03.03
 */
public class SocketEventReporter {

    private String requestId;
    private NetworkEventReporter reporter;
    private WSRequest request;

    private SocketEventReporter() {
        requestId = String.valueOf(RequestIdGenerator.nextRequestId());
        reporter = NetworkEventReporterManager.get();
    }

    public static SocketEventReporter newInstance() {
        return new SocketEventReporter();
    }

    public void created(String url) {
        if (reporter != null) {
            reporter.webSocketCreated(requestId, url);
        }
    }

    public void closed() {
        if (reporter != null) {
            reporter.webSocketClosed(requestId);
        }
    }

    public void willSendHandshakeRequest(Map<String, String> headers, @Nullable String friendlyName) {
        if (reporter != null) {
            request = new WSRequest(requestId, friendlyName, headers);
            reporter.webSocketWillSendHandshakeRequest(request);
        }
    }

    public void handshakeResponseReceived(int code, String phrase, Map<String, String> responseHeaders) {
        if (reporter != null) {
            reporter.webSocketHandshakeResponseReceived(new WSResponse(requestId, code, phrase, responseHeaders, request));
        }
    }

    public void frameSent(String frame) {
        if (reporter != null) {
            reporter.webSocketFrameSent(new SimpleTextInspectorWebSocketFrame(requestId, frame));
        }
    }

    public void frameSent(byte[] frame) {
        if (reporter != null) {
            reporter.webSocketFrameSent(new SimpleBinaryInspectorWebSocketFrame(requestId, frame));
        }
    }

    public void frameReceived(String frame) {
        if (reporter != null) {
            reporter.webSocketFrameReceived(new SimpleTextInspectorWebSocketFrame(requestId, frame));
        }
    }

    public void frameReceived(byte[] frame) {
        if (reporter != null) {
            reporter.webSocketFrameReceived(new SimpleBinaryInspectorWebSocketFrame(requestId, frame));
        }
    }

    public void frameError(String msg) {
        if (reporter != null) {
            reporter.webSocketFrameError(requestId, msg);
        }
    }

    private static class WSRequest extends WSHeaderCommon implements NetworkEventReporter.InspectorWebSocketRequest {

        private String id;
        private String name;

        public WSRequest(String id, String name, Map<String, String> headers) {
            attachHeaders(headers);
            this.id = id;
            this.name = name;
            if (this.name == null) {
                this.name = "WS Connection " + id;
            }
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String friendlyName() {
            return name;
        }
    }

    private static class WSResponse extends WSHeaderCommon implements NetworkEventReporter.InspectorWebSocketResponse {

        private String id;
        private int code;
        private String phrase;
        private WSHeaderCommon headers;

        public WSResponse(String id, int code, String phrase, Map<String, String> responseHeaders, WSHeaderCommon headers) {
            attachHeaders(responseHeaders);
            this.id = id;
            this.code = code;
            this.phrase = phrase;
            this.headers = headers;
        }

        @Override
        public String requestId() {
            return id;
        }

        @Override
        public int statusCode() {
            return code;
        }

        @Override
        public String reasonPhrase() {
            return phrase;
        }

        @Nullable
        @Override
        public NetworkEventReporter.InspectorHeaders requestHeaders() {
            return headers;
        }
    }

    private static class WSHeaderCommon implements NetworkEventReporter.InspectorHeaders {

        private List<Pair<String, String>> headerList = new ArrayList<>();

        public void attachHeaders(Map<String, String> headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerList.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }

        public void addHeader(String key, String value) {
            headerList.add(new Pair<>(key, value));
        }


        @Override
        public int headerCount() {
            return headerList.size();
        }

        @Override
        public String headerName(int index) {
            return headerList.get(index).first;
        }

        @Override
        public String headerValue(int index) {
            return headerList.get(index).second;
        }

        @Nullable
        @Override
        public String firstHeaderValue(String name) {
            for (Pair<String, String> pair : headerList) {
                if (pair.first.equals(name)) {
                    return pair.second;
                }
            }
            return null;
        }
    }
}
