package faceduck.custom.util;

import faceduck.skeleton.util.Direction;

public enum Heading {
    STAY(0, 0), UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int x;
    private final int y;
    Heading(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Pair<Integer, Integer> getValue() {
        return new Pair<>(x, y);
    }
}
