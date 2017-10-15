package faceduck.actors;

import faceduck.ai.HunterAI;
import faceduck.custom.Actionable;
import faceduck.custom.interfaces.Hunter;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.Edible;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;

import static java.lang.Math.max;
import static java.lang.Math.pow;

public class HunterImpl extends Actionable implements Hunter, Edible {
    private static final int HUNTER_MAX_ENERGY = 150;
    private static final int HUNTER_VIEW_RANGE = 6;
    private static final int HUNTER_BREED_LIMIT = HUNTER_MAX_ENERGY * 2;
    protected static final int HUNTER_ENERGY_VALUE = 100;
    private static final int HUNTER_COOL_DOWN = 4;
    private static final int HUNTER_INITIAL_ENERGY = HUNTER_MAX_ENERGY / 3;
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

    /**
     * Hunter can't breed
     *
     * @return breedLimit (but never breed)
     */
    @Override
    public int getBreedLimit() {
        return HUNTER_BREED_LIMIT;
    }

    /**
     * Hunter have very long view range
     *
     * @return viewRange
     */
    @Override
    public int getViewRange() {
        return HUNTER_VIEW_RANGE;
    }

    /**
     * Hunter is cautious, so act slowly
     *
     * @return coolDown
     */
    @Override
    public int getCoolDown() {
        return HUNTER_COOL_DOWN;
    }

    @Override
    public int getEnergyValue() {
        return HUNTER_ENERGY_VALUE;
    }

    /**
     * It represents the extent to which the rabbit can not remember.
     * Bigger the value, better the memory.
     *
     * @return value of forgetfulness
     */
    @Override
    protected double getForgetfulness() {
        return HUNTER_FORGETFULNESS;
    }

    /**
     * Judges what you seen and return preference.<br><br>
     *
     * - Hunter like Rabbit, Fox, Bear because Hunter can eat and as the hungry. <br>
     * - But Bear is dangerous, so Hunter does not like Bears unless they are hungry.
     * - Hunter doesn't like other Hunter because have to divide the number of Grasses. <br>
     * - Hunter hate a little of others, because staying still is not good for a changing world. <br>
     *
     * The Hunter focuses on avoiding Bears when they are not hungry. <br>
     *      1.0093f is value satisfying x ^ (75: maxEnergy - breedLimit) = (2: 2 times more afraid) <br>
     *
     * @param actor to judge
     * @return value of preference
     */
    @Override
    protected double judge(Actors actor) {
        switch (actor) {
            case RABBIT:
                return RabbitImpl.RABBIT_ENERGY_VALUE * 10;
            case FOX:
                return FoxImpl.FOX_ENERGY_VALUE * 10;
            case BEAR:
                return (getEnergy() <= getMaxEnergy() / 2) ? BearImpl.BEAR_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? BearImpl.BEAR_ENERGY_VALUE * 5 : 0) :
                        Recognizable.NEMESIS.getValue() * pow(1.0093f, max(0, getEnergy() - getBreedLimit()));
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
