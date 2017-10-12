package faceduck.custom.util;

public enum Recognizable {
    UNKNOWN(0), EMPTY(0), EDIBLE(100), IRRELEVANT(-10), ENEMY(-100);

    private final int value;
    Recognizable(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
