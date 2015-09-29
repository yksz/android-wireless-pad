package jp.android.wirelesspad.remote.mouse;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPMouse implements Mouse {
    private static final String TAG = TCPMouse.class.getSimpleName();
    private static final int PORT = 7681;
    private static final int MAX_THREAD_NUM = 3;
    private static final int TIMEOUT = 2000; // 2 sec

    private Socket mSocket;
    private final ExecutorService mThreadPool;

    public TCPMouse() {
        mThreadPool = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }

    @Override
    public boolean connect(String host) {
        if (host == null)
            throw new NullPointerException("host must not be null");
        if (isConnecting()) {
            Log.d(TAG, "Already connected");
            return true;
        }

        try {
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(host, PORT), TIMEOUT);
            Log.d(TAG, "connect: host=" + host);
            return true;
        } catch (IOException e) {
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
            mSocket.close();
            Log.d(TAG, "disconnect");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "disconnect", e);
            return false;
        }
    }

    @Override
    public boolean isConnecting() {
        if (mSocket == null) {
            return false;
        }
        return mSocket.isConnected() && !mSocket.isClosed();
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
    public boolean move(final int x, final int y) {
        if (!isConnecting())
            return false;

        mThreadPool.execute(new Runnable() {
            public void run() {
                send(Command.MOVE + Command.DELIMITER + x + Command.DELIMITER + y);
            }
        });
        return true;
    }

    @Override
    public boolean scroll(final int amount) {
        if (!isConnecting())
            return false;

        mThreadPool.execute(new Runnable() {
            public void run() {
                send(Command.SCROLL + Command.DELIMITER + amount);
            }
        });
        return true;
    }

    @Override
    public boolean click(final ClickType type) {
        if (!isConnecting())
            return false;

        mThreadPool.execute(new Runnable() {
            public void run() {
                switch (type) {
                    case LEFT_CLICK:
                        send(Command.LEFT_CLICK);
                        return;
                    case RIGHT_CLICK:
                        send(Command.RIGHT_CLICK);
                        return;
                    case DOUBLE_CLICK:
                        send(Command.DOUBLE_CLICK);
                        return;
                    default:
                        throw new AssertionError("Unknown type: " + type);
                }
            }
        });
        return true;
    }

    private boolean send(String message) {
        byte[] data = message.getBytes();
        try {
            OutputStream out = mSocket.getOutputStream();
            out.write(data);
            out.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "send: message=" + message, e);
            return false;
        }
    }
}
