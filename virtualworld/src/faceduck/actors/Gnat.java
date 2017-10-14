package faceduck.actors;

import faceduck.custom.Actionable;
import faceduck.custom.util.*;
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
public class Gnat extends Actionable {
	private static final int MAX_ENERGY = 0;
	private static final int VIEW_RANGE = 1;
	private static final int BREED_LIMIT = 0;
	private static final int COOL_DOWN = 0;

	public Gnat(int size) {
        super();
    }

    @Override
    public int getInitialEnergy() {
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
	public int getViewRange() {
		return VIEW_RANGE;
	}

	@Override
	public int getCoolDown() {
		return COOL_DOWN;
	}

    @Override
    protected double judge(Actors actor) {
	    if (actor == Actors.EMPTY) return Recognizable.EMPTY.getValue();
        return Recognizable.IRRELEVANT.getValue();
    }
}
