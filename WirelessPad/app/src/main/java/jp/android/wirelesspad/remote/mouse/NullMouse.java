package jp.android.wirelesspad.remote.mouse;

public class NullMouse implements Mouse {
    @Override
    public boolean connect(String host) {
        return false;
    }

    @Override
    public boolean disconnect() {
        return false;
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public boolean checkConnection(String host) {
        return false;
    }

    @Override
    public boolean move(int x, int y) {
        return false;
    }

    @Override
    public boolean scroll(int amount) {
        return false;
    }

    @Override
    public boolean click(ClickType type) {
        return false;
    }
}
