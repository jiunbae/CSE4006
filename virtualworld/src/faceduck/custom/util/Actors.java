package faceduck.custom.util;

public enum Actors {
    GRASS("Grass"), GNAT("Gnat"), RABBIT("Rabbit"), FOX("Fox"), GARDENER("Gardener"), EMPTY("Empty");

    private final String name;

    Actors(String name) {
        this.name = name;
    }

    public boolean equalsName(String rhs) {
        return name.equals(rhs);
    }

    public String toString() {
        return name;
    }
}