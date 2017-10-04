package faceduck.actors;

import faceduck.ai.GnatAI;
import faceduck.custom.util.Utility;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

/**
 * This is a simple implementation of a Gnat. It never loses energy and moves in
 * random directions.
 */
public class Gnat implements Animal {
	private static final int MAX_ENERGY = 10;
	private static final int VIEW_RANGE = 1;
	private static final int BREED_LIMIT = 0;
	private static final int COOL_DOWN = 0;

	public Gnat(int size) { }

	@Override
	public int getEnergy() {
		return 0;
	}

	@Override
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	@Override
	public int getBreedLimit() {
		return BREED_LIMIT;
	}

	@Override
	public void eat(World world, Direction dir) {
	}

	@Override
	public void move(World world, Direction dir) {
	    Location prevLoc = world.getLocation(this);
	    Location nextLoc = new Location(prevLoc, dir);

	    world.remove(this);
	    world.add(this, nextLoc);
	}

	@Override
	public void breed(World world, Direction dir) {

	}

	@Override
	public void act(World world) {
        if (Utility.isClosed(world, this)) return;

        Location prevLoc = world.getLocation(this);
	    Location nextLoc;
	    Direction dir;
	    do {
	        dir = Util.randomDir();
            nextLoc = new Location(prevLoc, dir);
        } while (!(world.isValidLocation(nextLoc) && world.getThing(nextLoc) == null));
	    move(world, dir);
	}

	@Override
	public int getViewRange() {
		return VIEW_RANGE;
	}

	@Override
	public int getCoolDown() {
		return COOL_DOWN;
	}
}
