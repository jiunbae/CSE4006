package faceduck.custom.util;

public enum Action {
    WAIT(0), EAT(1), MOVE(2), BREED(3);

    private final int value;
    Action(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
