package jp.android.wirelesspad.remote.mouse;

import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketMouse implements Mouse {
    private static final String TAG = WebSocketMouse.class.getSimpleName();

    static {
        if ("google_sdk".equals(Build.PRODUCT)) {
            System.setProperty("java.net.preferIPv6Addresses", "false");
            System.setProperty("java.net.preferIPv4Stack", "true");
        }
    }

    private WebSocketClient mClient;

    @Override
    public boolean connect(String host) {
        if (host == null)
            throw new NullPointerException("host must not be null");
        if (isConnecting()) {
            Log.d(TAG, "Already connected");
            return true;
        }

        try {
            mClient = new WebSocketClientImpl(host);
            mClient.connectBlocking();
            Log.d(TAG, "connect: host=" + host);
            return true;
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            Log.e(TAG, "connect: host=" + host, e);
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        if (!isConnecting()) {
            Log.d(TAG, "Already disconnected");
            return true;
        }

        try {
            mClient.close();
            mClient = null;
            Log.d(TAG, "disconnect");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "disconnect", e);
            return false;
        }
    }

    @Override
    public boolean isConnecting() {
        if (mClient == null) {
            return false;
        }
        return mClient.getConnection().isOpen();
    }

    @Override
    public boolean checkConnection(String host) {
        try {
            connect(host);
            return send("test");
        } finally {
            disconnect();
        }
    }

    @Override
    public boolean move(int x, int y) {
        if (!isConnecting())
            return false;

        return send(Command.createMoveCommand(x, y));
    }

    @Override
    public boolean scroll(int amount) {
        if (!isConnecting())
            return false;

        return send(Command.createScrollCommand(amount));
    }

    @Override
    public boolean click(ClickType type) {
        if (!isConnecting())
            return false;

        switch (type) {
            case LEFT_CLICK:
                return send(Command.createLeftClickCommand());
            case RIGHT_CLICK:
                return send(Command.createRightClickCommand());
            case DOUBLE_CLICK:
                return send(Command.createDoubleClickCommand());
            default:
                throw new AssertionError("Unknown type: " + type);
        }
    }

    private boolean send(String message) {
        try {
            mClient.send(message);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "send: message=" + message, e);
            return false;
        }
    }

    private static class WebSocketClientImpl extends WebSocketClient {
        private static final String PROTOCOL = "ws";
        private static final int PORT = 7681;

        public WebSocketClientImpl(String host) throws URISyntaxException {
            super(new URI(PROTOCOL + "://" + host + ":" + PORT), new Draft_17());
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
    }
}
