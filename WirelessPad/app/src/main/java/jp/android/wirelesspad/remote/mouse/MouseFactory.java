package jp.android.wirelesspad.remote.mouse;

public class MouseFactory {
    private static Mouse mouse = new NullMouse();

    public static Mouse getMouse() {
        return mouse;
    }

    public static void setMouse(Mouse mouse) {
        MouseFactory.mouse = mouse;
    }
}
