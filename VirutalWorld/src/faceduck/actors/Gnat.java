package faceduck.actors;

import faceduck.ai.GnatAI;
import faceduck.custom.Actionable;
import faceduck.custom.util.*;

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
        super(new GnatAI(), 0);
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

    /**
     * Gnat does not have any memory, so he behaves randomly.
     * @return 0
     */
    @Override
    protected double getForgetfulness() {
        return 0;
    }

    /**
     * Judges what you seen and return preference.<br><br>
     *
     * But Gnat doesn't care anything.
     *
     * @param actor to judge
     * @return value of preference
     */
    @Override
    protected double judge(Actors actor) {
	    switch (actor) {
            case GRASS:
            case GNAT:
            case RABBIT:
            case FOX:
            case GARDENER:
            case EMPTY:
            default:
                return Recognizable.UNKNOWN.getValue();
        }
    }
}
