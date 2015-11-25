package jp.android.wirelesspad.remote.mouse;

final class Command {
    private static final String DELIMITER = " ";
    private static final String MOVE = "mv";
    private static final String SCROLL = "sr";
    private static final String LEFT_CLICK = "lc";
    private static final String RIGHT_CLICK = "rc";
    private static final String DOUBLE_CLICK = "dc";

    static String createMoveCommand(int x, int y) {
        return MOVE + DELIMITER + x + DELIMITER + y;
    }

    static String createScrollCommand(int amount) {
        return SCROLL + DELIMITER + amount;
    }

    static String createLeftClickCommand() {
        return LEFT_CLICK;
    }

    static String createRightClickCommand() {
        return RIGHT_CLICK;
    }

    static String createDoubleClickCommand() {
        return DOUBLE_CLICK;
    }
}
