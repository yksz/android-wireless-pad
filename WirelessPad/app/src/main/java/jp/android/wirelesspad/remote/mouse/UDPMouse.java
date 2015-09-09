package jp.android.wirelesspad.remote.mouse;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPMouse implements Mouse {
    private static final String TAG = UDPMouse.class.getSimpleName();
    private static final int PORT = 7681;
    private static final int MAX_THREAD_NUM = 3;

    private DatagramSocket mSocket;
    private final SocketAddress mAddress;
    private final ExecutorService mThreadPool;

    public UDPMouse(String host) {
        if (host == null)
            throw new NullPointerException("host must not be null");
        mAddress = new InetSocketAddress(host, PORT);
        mThreadPool = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }

    @Override
    public boolean connect() {
        try {
            mSocket = new DatagramSocket(mAddress);
            return true;
        } catch (SocketException e) {
            Log.e(TAG, "Connect", e);
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        if (mSocket != null) {
            mSocket.close();
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        if (mSocket != null) {
            return mSocket.isConnected();
        }
        return false;
    }

    @Override
    public boolean move(int x, int y) {
        if (!isConnected())
            throw new IllegalStateException("Not connected");

        mThreadPool.execute(new Runnable() {
            public void run() {
                send(Command.MOVE + Command.DELIMITER + x + Command.DELIMITER + y);
            }
        });
        return true;
    }

    @Override
    public boolean scroll(int amount) {
        if (!isConnected())
            throw new IllegalStateException("Not connected");

        mThreadPool.execute(new Runnable() {
            public void run() {
                send(Command.SCROLL + Command.DELIMITER + amount);
            }
        });
        return true;
    }

    @Override
    public boolean click(ClickType type) {
        if (!isConnected())
            throw new IllegalStateException("Not connected");

        mThreadPool.execute(new Runnable() {
            public void run() {
                switch (type) {
                    case LEFT_CLICK:
                        send(Command.LEFT_CLICK);
                    case RIGHT_CLICK:
                        send(Command.RIGHT_CLICK);
                    case DOUBLE_CLICK:
                        send(Command.DOUBLE_CLICK);
                    default:
                        throw new AssertionError("Unknown type: " + type);
                }
            }
        });
        return true;
    }

    private boolean send(String message) {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            mSocket.send(packet);
        } catch (IOException e) {
            Log.e(TAG, "Send message: " + message, e);
            return false;
        }
        return true;
    }
}
