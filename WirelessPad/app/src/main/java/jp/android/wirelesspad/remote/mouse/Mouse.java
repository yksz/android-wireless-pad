package jp.android.wirelesspad.remote.mouse;

import java.io.IOException;

public interface Mouse {
    enum ClickType {
        LEFT_CLICK,
        RIGHT_CLICK,
        DOUBLE_CLICK
    }

    boolean connect();
    boolean disconnect();
    boolean isConnected();
    boolean move(int x, int y);
    boolean scroll(int amount);
    boolean click(ClickType type);
}
