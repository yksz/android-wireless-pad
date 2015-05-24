package jp.android.wirelesspad.client;

import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class WirelessPadClient extends WebSocketClient {
    private static final String TAG = WirelessPadClient.class.getSimpleName();

    static {
        if ("google_sdk".equals(Build.PRODUCT)) {
            System.setProperty("java.net.preferIPv6Addresses", "false");
            System.setProperty("java.net.preferIPv4Stack", "true");
        }
    }

    public WirelessPadClient(URI serverURI) {
        super(serverURI, new Draft_17());
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Log.d(TAG, "onOpen");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose");
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError", ex);
    }

    public URI getURI() {
        return uri;
    }
}
