package faceduck.actors;

import faceduck.ai.HunterAI;
import faceduck.custom.Actionable;
import faceduck.custom.interfaces.Hunter;
import faceduck.custom.util.Action;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.custom.util.Utility;
import faceduck.skeleton.interfaces.Edible;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

import static java.lang.Math.max;
import static java.lang.Math.pow;

public class HunterImpl extends Actionable implements Hunter, Edible {
    private static final int HUNTER_MAX_ENERGY = 150;
    private static final int HUNTER_VIEW_RANGE = 6;
    private static final int HUNTER_BREED_LIMIT = HUNTER_MAX_ENERGY * 1 / 2;
    public static final int HUNTER_ENERGY_VALUE = 100;
    private static final int HUNTER_COOL_DOWN = 4;
    private static final int HUNTER_INITIAL_ENERGY = HUNTER_MAX_ENERGY * 1 / 3;
    private static final double HUNTER_FORGETFULNESS = 1.f;

    public HunterImpl() {
        super(new HunterAI(), HUNTER_INITIAL_ENERGY);
        addEdible(BearImpl.class);
        addEdible(Fox.class);
        addEdible(Rabbit.class);
    }

    @Override
    public int getMaxEnergy() {
        return HUNTER_MAX_ENERGY;
    }

    @Override
    public int getBreedLimit() {
        return HUNTER_BREED_LIMIT;
    }

    @Override
    public int getViewRange() {
        return HUNTER_VIEW_RANGE;
    }

    @Override
    public int getCoolDown() {
        return HUNTER_COOL_DOWN;
    }

    @Override
    public int getEnergyValue() {
        return HUNTER_ENERGY_VALUE;
    }

    @Override
    protected double getForgetfulness() {
        return HUNTER_FORGETFULNESS;
    }

    @Override
    protected double judge(Actors actor) {
        switch (actor) {
            case RABBIT:
                return RabbitImpl.RABBIT_ENERGY_VALUE
                        - (getEnergy() >= getBreedLimit() ? Recognizable.EDIBLE.getValue() / 2 : 0);
            case FOX:
                return FoxImpl.FOX_ENERGY_VALUE
                        - (getEnergy() >= getBreedLimit() ? Recognizable.EDIBLE.getValue() / 2 : 0);
            case BEAR:
                return (getEnergy() <= getMaxEnergy() / 2) ? BearImpl.BEAR_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? BearImpl.BEAR_ENERGY_VALUE * 5 : 0) :
                        Recognizable.NEMESIS.getValue() * pow(1.149f, max(0, getEnergy() - getBreedLimit()));
            case HUNTER:
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
