package faceduck.actors;

import faceduck.custom.Actionable;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;

import java.util.Arrays;

public class RabbitImpl extends Actionable implements Rabbit {
	private static final int RABBIT_MAX_ENERGY = 20;
	private static final int RABBIT_VIEW_RANGE = 3;
	private static final int RABBIT_BREED_LIMIT = RABBIT_MAX_ENERGY * 2 / 4;
	private static final int RABBIT_ENERGY_VALUE = 20;
	private static final int RABBIT_COOL_DOWN = 4;
	private static final int RABBIT_INITAL_ENERGY = RABBIT_MAX_ENERGY * 1 / 2;

	private int energy = 0;
	private Recognizable[][] mWorld = null;

	public RabbitImpl() {
	    energy = RABBIT_INITAL_ENERGY;
	}

    @Override
    public int getEnergy() {
        return energy;
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
    public void eat(World world, Direction dir) {

    }

    @Override
    public void move(World world, Direction dir) {

    }

    @Override
    public void breed(World world, Direction dir) {

    }

    @Override
    public void act(World world) {
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
                return Recognizable.EDIBLE.getValue();
            case GNAT:
                return Recognizable.IRRELEVANT.getValue();
            case RABBIT:
                return Recognizable.IRRELEVANT.getValue();
            case FOX:
                return Recognizable.ENEMY.getValue();
            case GARDENER:
                return Recognizable.IRRELEVANT.getValue();
        }
        return Recognizable.EMPTY.getValue();
    }
}
