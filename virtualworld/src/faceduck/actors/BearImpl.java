package faceduck.actors;

import faceduck.ai.BearAI;
import faceduck.custom.Actionable;
import faceduck.custom.interfaces.Bear;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.Edible;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;

import static java.lang.Math.max;
import static java.lang.Math.pow;

public class BearImpl extends Actionable implements Bear, Edible {
    private static final int BEAR_MAX_ENERGY = 250;
    private static final int BEAR_VIEW_RANGE = 7;
    private static final int BEAR_BREED_LIMIT = BEAR_MAX_ENERGY * 2 / 4;
    public static final int BEAR_ENERGY_VALUE = 200;
    private static final int BEAR_COOL_DOWN = 5;
    private static final int BEAR_INITIAL_ENERGY = BEAR_MAX_ENERGY * 1 / 3;
    private static final double BEAR_FORGETFULNESS = .3f;

    public BearImpl() {
        super(new BearAI(), BEAR_INITIAL_ENERGY);
        addEdible(Fox.class);
        addEdible(Rabbit.class);
        addEdible(HunterImpl.class);
    }

    @Override
    public int getMaxEnergy() {
        return BEAR_MAX_ENERGY;
    }

    @Override
    public int getBreedLimit() {
        return BEAR_BREED_LIMIT;
    }

    @Override
    public int getViewRange() {
        return BEAR_VIEW_RANGE;
    }

    @Override
    public int getCoolDown() {
        return BEAR_COOL_DOWN;
    }

    @Override
    public int getEnergyValue() {
        return BEAR_ENERGY_VALUE;
    }

    @Override
    protected double getForgetfulness() {
        return BEAR_FORGETFULNESS;
    }

    @Override
    protected double judge(Actors actor) {
        switch (actor) {
            case RABBIT:
                return RabbitImpl.RABBIT_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? RabbitImpl.RABBIT_ENERGY_VALUE * 5 : 0);
            case FOX:
                return FoxImpl.FOX_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? FoxImpl.FOX_ENERGY_VALUE * 5 : 0);
            case HUNTER:
                return (getEnergy() <= getMaxEnergy() / 2) ? HunterImpl.HUNTER_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? HunterImpl.HUNTER_ENERGY_VALUE * 5 : 0) :
                        Recognizable.NEMESIS.getValue() * pow(1.149f, max(0, getEnergy() - getBreedLimit()));
            case BEAR:
                return Recognizable.COGNATION.getValue();
            case GRASS:
                return Recognizable.IRRELEVANT.getValue();
            case GNAT:
                return Recognizable.IRRELEVANT.getValue();
            case GARDENER:
                return Recognizable.IRRELEVANT.getValue();
            default:
                return Recognizable.IRRELEVANT.getValue();
        }
    }
}
