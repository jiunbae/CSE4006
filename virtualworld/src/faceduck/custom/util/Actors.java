package faceduck.custom.util;

import faceduck.skeleton.interfaces.*;

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

    /**
     * recognize object in world
     * @param obj
     * @return Object to Actors{FOX, RABBIT, GRASS, GNAT, GARDENER or EMPTY}
     */
    public static Actors recognize(Object obj) {
        if (obj instanceof Actor) {
            if (obj instanceof Fox) {
                return Actors.FOX;
            } else if (obj instanceof Rabbit) {
                return Actors.RABBIT;
            } else if (obj instanceof Animal) {
                return Actors.GNAT;
            } else {
                return Actors.GARDENER;
            }
        } else if (obj instanceof Edible) {
            return Actors.GRASS;
        }
        return Actors.EMPTY;
    }
}