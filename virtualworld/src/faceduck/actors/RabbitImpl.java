package faceduck.actors;

import faceduck.custom.Actionable;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.Rabbit;

import static java.lang.Math.log;

public class RabbitImpl extends Actionable implements Rabbit {
	private static final int RABBIT_MAX_ENERGY = 20;
	private static final int RABBIT_VIEW_RANGE = 3;
	private static final int RABBIT_BREED_LIMIT = RABBIT_MAX_ENERGY * 2 / 4;
	private static final int RABBIT_ENERGY_VALUE = 20;
	private static final int RABBIT_COOL_DOWN = 1;
	private static final int RABBIT_INITIAL_ENERGY = RABBIT_MAX_ENERGY * 1 / 2;

	public RabbitImpl() {
        super(RABBIT_INITIAL_ENERGY);
        addEdible(Grass.class);
	}

    @Override
    public int getMaxEnergy() {
        return RABBIT_MAX_ENERGY;
    }

    @Override
    public int getBreedLimit() {
        return RABBIT_BREED_LIMIT;
    }

    @Override
    public int getViewRange() {
        return RABBIT_VIEW_RANGE;
    }

    @Override
    public int getCoolDown() {
        return RABBIT_COOL_DOWN;
    }

    @Override
    public int getEnergyValue() {
        return RABBIT_ENERGY_VALUE;
    }

    @Override
    protected double judge(Actors actor) {
	    switch (actor) {
            case GRASS:
                return Recognizable.EDIBLE.getValue() * log((getMaxEnergy() - getEnergy()) * 20);
            case RABBIT:
                return Recognizable.COGNATION.getValue();
            case FOX:
                return Recognizable.NEMESIS.getValue();
            case GNAT:
                return Recognizable.IRRELEVANT.getValue();
            case GARDENER:
                return Recognizable.IRRELEVANT.getValue();
            default:
                return Recognizable.IRRELEVANT.getValue();
        }
    }
}
