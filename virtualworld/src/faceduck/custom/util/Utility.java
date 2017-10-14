package faceduck.custom.util;

import faceduck.skeleton.interfaces.*;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

import java.util.*;
import java.util.function.*;

public class Utility {
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
        List<Direction> ret = Arrays.asList(Direction.values());

        return ret;
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
        return new Location(loc.getX() + dir.getValue().getFirst(), loc.getY() + dir.getValue().getSecond());
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
}
