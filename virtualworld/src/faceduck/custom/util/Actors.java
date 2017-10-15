package faceduck.custom.util;

import faceduck.custom.interfaces.*;
import faceduck.skeleton.interfaces.*;

public enum Actors {
    EMPTY("Empty"),
    // Original Classes
    GRASS("Grass"), GNAT("Gnat"), RABBIT("Rabbit"), FOX("Fox"), GARDENER("Gardener"),
    // Custom Classes
    BEAR("BearImpl"), HUNTER("HunterImpl");

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
            if (obj instanceof Bear) {
                return Actors.BEAR;
            } else if (obj instanceof Hunter) {
                return Actors.HUNTER;
            } else if (obj instanceof Fox) {
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