package faceduck.custom.util;

public enum Recognizable {
    EDIBLE(100), COGNATION(-15), IRRELEVANT(-5), NEMESIS(-100), UNKNOWN(0);

    private final int value;

    Recognizable(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
