package jp.android.wirelesspad.remote.mouse;

import java.io.IOException;

public interface Mouse {
    enum ClickType {
        LEFT_CLICK,
        RIGHT_CLICK,
        DOUBLE_CLICK
    }

    boolean connect(String host);
    boolean disconnect();
    boolean isConnected();
    boolean checkConnection(String host);
    boolean move(int x, int y);
    boolean scroll(int amount);
    boolean click(ClickType type);
}
