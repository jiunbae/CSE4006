package faceduck.custom.util;

import faceduck.skeleton.interfaces.*;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class Utility {

    /**
     * check if can move
     * @param world
     * @param actor
     * @return {@link Actor} can move in {@link World}
     */
    public static boolean isClosed(World world, Actor actor) {
        Location next;
        for (Direction dir : Direction.values()) {
            next = new Location(world.getLocation(actor), dir);
            if (world.isValidLocation(next) && world.getThing(next) == null)
                return false;
        }
        return true;
    }

    /**
     * check two object is adjacent
     * @param lhs
     * @param rhs
     * @return true if lhs adjacent rhs
     */
    public static boolean isAdjacent(Location lhs, Location rhs) {
        return lhs.distanceTo(rhs) == 1;
    }

    /**
     * check position valid
     * @param x
     * @param y
     * @param width
     * @param height
     * @return true if valid position
     */
    public static boolean isValidLocation(int x, int y, int width, int height) {
        return 0 <= x && x < width && 0 <=y && y < height;
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
            } else if (obj instanceof Edible) {
                return Actors.GRASS;
            } else if (obj instanceof Animal) {
                return Actors.GNAT;
            } else {
                return Actors.GARDENER;
            }
        }
        return Actors.EMPTY;
    }

    /**
     * change pair to Location
     * @param pair
     * @return Location
     */
    public static Location localization(Pair<Integer, Integer> pair) {
        return new Location(pair.getFirst(), pair.getSecond());
    }
}
