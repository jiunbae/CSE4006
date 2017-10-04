package faceduck.custom.util;

import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class Utility {

    public static boolean isClosed(World world, Actor actor) {
        Location next;
        for (Direction dir : Direction.values()) {
            next = new Location(world.getLocation(actor), dir);
            if (world.isValidLocation(next) && world.getThing(next) == null)
                return false;
        }
        return true;
    }
}
