package faceduck.custom.UI;

public enum SightMode {
    NOPE(0), VIEWRANGE(1), WEIGHTS(2), EVALUATED(3);

    private final int value;
    SightMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public SightMode next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
