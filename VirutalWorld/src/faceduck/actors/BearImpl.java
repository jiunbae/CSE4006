package faceduck.actors;

import faceduck.ai.BearAI;
import faceduck.custom.Actionable;
import faceduck.custom.interfaces.Bear;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.Edible;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.pow;

public class BearImpl extends Actionable implements Bear, Edible {
    private static final int BEAR_MAX_ENERGY = 250;
    private static final int BEAR_VIEW_RANGE = 7;
    private static final int BEAR_BREED_LIMIT = BEAR_MAX_ENERGY * 6 / 7;
    protected static final int BEAR_ENERGY_VALUE = 200;
    private static final int BEAR_COOL_DOWN = 5;
    private static final int BEAR_INITIAL_ENERGY = BEAR_MAX_ENERGY / 3;
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

    /**
     * Bear can see very large range, because Bear is huge
     *
     * @return viewRange
     */
    @Override
    public int getViewRange() {
        return BEAR_VIEW_RANGE;
    }

    /**
     * Bear is very slow
     *
     * @return coolDown
     */
    @Override
    public int getCoolDown() {
        return BEAR_COOL_DOWN;
    }

    @Override
    public int getEnergyValue() {
        return BEAR_ENERGY_VALUE;
    }

    /**
     * It represents the extent to which the rabbit can not remember.
     * Bigger the value, better the memory.
     *
     * @return value of forgetfulness
     */
    @Override
    protected double getForgetfulness() {
        return BEAR_FORGETFULNESS;
    }

    /**
     * Judges what you seen and return preference.<br><br>
     *
     * - Bear like Rabbit, Fox, Hunter because Bear can eat and as the hungry. <br>
     * - But Hunter is dangerous, so Bear does not like Hunters unless they are hungry.
     * - Bear doesn't like other Bear because have to divide the number of Grasses. <br>
     * - Bear hate a little of others, because staying still is not good for a changing world. <br>
     *
     * The Bear focuses on avoiding Hunters when they are not hungry. <br>
     *      1.0037f is value satisfying x ^ (187.5: maxEnergy - breedLimit) = (2: 2 times more afraid) <br>
     *      2.71828f is value of e,
     *
     * @param actor to judge
     * @return value of preference
     */
    @Override
    protected double judge(Actors actor) {
        switch (actor) {
            case RABBIT:
                return RabbitImpl.RABBIT_ENERGY_VALUE * 5 * ((getEnergy() < BEAR_INITIAL_ENERGY)
                        ? pow(1.0196, BEAR_INITIAL_ENERGY - getEnergy())
                        : 1 / log(2.71828f + getEnergy() - BEAR_INITIAL_ENERGY));
            case FOX:
                return FoxImpl.FOX_ENERGY_VALUE * 10 * ((getEnergy() < BEAR_INITIAL_ENERGY)
                        ? pow(1.0196, BEAR_INITIAL_ENERGY - getEnergy())
                        : 1 / log(2.71828f + getEnergy() - BEAR_INITIAL_ENERGY));
            case HUNTER:
                return (getEnergy() <= getMaxEnergy() / 2) ? HunterImpl.HUNTER_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? HunterImpl.HUNTER_ENERGY_VALUE * 5 : 0) :
                        Recognizable.NEMESIS.getValue() * pow(1.0037f, max(0, getEnergy() - getBreedLimit()));
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
