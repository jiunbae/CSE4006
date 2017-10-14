package faceduck.custom.util;

import faceduck.skeleton.interfaces.*;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

import java.util.*;
import java.util.function.*;

public class Utility {

    // It's same seed Random object skeleton.util.Util.rand, cuz private;
    public static final Random rand = new Random(2013);

    /**
     * check if can move
     * @param world
     * @param loc
     * @return {@link Actor} can move in {@link World}
     */
    public static boolean isClosed(World world, Location loc) {
        Location next;
        for (Direction dir : Direction.values()) {
            next = new Location(loc, dir);
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
     * return random adjacent from location
     * @return
     */
    public static Direction randomAdjacent(Function<Direction, Boolean> f) {
        List<Direction> deck = Arrays.asList(Direction.values());
        Collections.shuffle(deck);
        for (Direction dir : deck) {
            if (f.apply(dir))
                return dir;
        }
        return null;
    }

    /**
     * workload return clock side directions
     * @param dir
     * @return list of Direction
     */
    public static List<Direction> workload(Direction dir) {
        List<Direction> ret = new ArrayList<>();

        ret.add(dir);
        ret.add(leftside(dir));
        ret.add(rightside(dir));
        ret.add(opside(dir));

        return ret;
    }

    public static Direction leftside(Direction dir) {
        switch (dir) {
            case NORTH:
                return Direction.EAST;
            case SOUTH:
                return Direction.WEST;
            case EAST:
                return Direction.NORTH;
            case WEST:
                return Direction.SOUTH;
        }
        return null;
    }

    public static Direction rightside(Direction dir) {
        switch (dir) {
            case NORTH:
                return Direction.WEST;
            case SOUTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case WEST:
                return Direction.NORTH;
        }
        return null;
    }

    public static Direction opside(Direction dir) {
        switch (dir) {
            case NORTH:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.NORTH;
            case EAST:
                return Direction.WEST;
            case WEST:
                return Direction.EAST;
        }
        return null;
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

    public static Location destination(Location loc, Direction dir) {
        Heading to = toHeading(dir);
        return new Location(loc.getX() + to.getValue().getFirst(), loc.getY() + to.getValue().getSecond());
    }

    public static <T, U, R> Function<U, R> bind(BiFunction<T, U, R> f, T t) {
        return u -> f.apply(t, u);
    }

    public static <T> boolean contain(List<T> list, T item, BiFunction<T, T, Boolean> lambda) {
        for (final T i : list)
            if (lambda.apply(i, item)) return true;
        return false;
    }

    /**
     * get value from 2d array, if valid position
     * @param map
     * @param x
     * @param y
     * @return return value of position if valid
     */
    public static double getValue(double[][] map, int x, int y) {
        int width = map.length;
        int height = map[0].length;
        if (!Utility.isValidLocation(x, y, width, height)) return 0;
        return map[x][y];
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

    /**
     * change pair to Location
     * @param pair
     * @return Location
     */
    public static Location toLocation(Pair<Integer, Integer> pair) {
        return new Location(pair.getFirst(), pair.getSecond());
    }

    public static Pair<Integer, Integer> toPair(Location loc) {
        return new Pair<>(loc.getX(), loc.getY());
    }

    public static Heading toHeading(Direction dir) {
        switch (dir) {
            case NORTH:
                return Heading.UP;
            case SOUTH:
                return Heading.DOWN;
            case EAST:
                return Heading.RIGHT;
            case WEST:
                return Heading.LEFT;
        }
        return Heading.STAY;
    }
}
