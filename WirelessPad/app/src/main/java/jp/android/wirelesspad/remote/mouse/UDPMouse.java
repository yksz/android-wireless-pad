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

    private final DatagramSocket mSocket;
    private final SocketAddress mAddress;
    private final ExecutorService mThreadPool;

    public UDPMouse(String host) throws SocketException {
        if (host == null)
            throw new NullPointerException("host must not be null");
        mSocket = new DatagramSocket();
        mAddress = new InetSocketAddress(host, PORT);
        mThreadPool = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }

    @Override
    public boolean connect() {
        try {
            mSocket.connect(mAddress);
            return true;
        } catch (SocketException e) {
            Log.e(TAG, "Connect", e);
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        mSocket.disconnect();
        return true;
    }

    @Override
    public boolean isConnected() {
        return mSocket.isConnected();
    }

    @Override
    public boolean move(final int x, final int y) {
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
    public boolean scroll(final int amount) {
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
    public boolean click(final ClickType type) {
        if (!isConnected())
            throw new IllegalStateException("Not connected");

        mThreadPool.execute(new Runnable() {
            public void run() {
                switch (type) {
                    case LEFT_CLICK:
                        send(Command.LEFT_CLICK);
                        break;
                    case RIGHT_CLICK:
                        send(Command.RIGHT_CLICK);
                        break;
                    case DOUBLE_CLICK:
                        send(Command.DOUBLE_CLICK);
                        break;
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
            DatagramPacket packet = new DatagramPacket(data, data.length, mAddress);
            mSocket.send(packet);
        } catch (IOException e) {
            Log.e(TAG, "Send message: " + message, e);
            return false;
        }
        return true;
    }
}
