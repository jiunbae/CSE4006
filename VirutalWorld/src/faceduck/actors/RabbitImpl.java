package faceduck.actors;

import faceduck.ai.RabbitAI;
import faceduck.custom.Actionable;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.Edible;
import faceduck.skeleton.interfaces.Rabbit;

import static java.lang.Math.*;

public class RabbitImpl extends Actionable implements Rabbit, Edible {
	private static final int RABBIT_MAX_ENERGY = 20;
    private static final int RABBIT_VIEW_RANGE = 3;
    private static final int RABBIT_BREED_LIMIT = RABBIT_MAX_ENERGY * 5 / 6;
    protected static final int RABBIT_ENERGY_VALUE = 20;
    private static final int RABBIT_COOL_DOWN = 2;
    private static final int RABBIT_INITIAL_ENERGY = RABBIT_MAX_ENERGY / 2;
    private static final double RABBIT_FORGETFULNESS = .8f;

	public RabbitImpl() {
        super(new RabbitAI(), RABBIT_INITIAL_ENERGY);
        addEdible(Grass.class);
	}

    @Override
    public int getMaxEnergy() {
        return RABBIT_MAX_ENERGY;
    }

    /**
     * Rabbit breed often.
     *
     * @return breedLimit (but never breed)
     */
    @Override
    public int getBreedLimit() {
        return RABBIT_BREED_LIMIT;
    }

    /**
     * Rabbit can not see far because small.
     *
     * @return viewRange
     */
    @Override
    public int getViewRange() {
        return RABBIT_VIEW_RANGE;
    }

    /**
     * Rabbit is fast
     *
     * @return coolDown
     */
    @Override
    public int getCoolDown() {
        return RABBIT_COOL_DOWN;
    }

    @Override
    public int getEnergyValue() {
        return RABBIT_ENERGY_VALUE;
    }

    /**
     * It represents the extent to which the rabbit can not remember.
     * Bigger the value, better the memory.
     *
     * @return value of forgetfulness
     */
    @Override
    protected double getForgetfulness() {
	    return RABBIT_FORGETFULNESS;
    }

    /**
     * Judges what you seen and return preference.<br><br>
     *
     * - Rabbit like Grass because Rabbit can eat and as the hungry. <br>
     * - Rabbit doesn't like other Rabbit because have to divide the number of Grasses. <br>
     * - Rabbit hate a little of others, because staying still is not good for a changing world. <br>
     *
     * The Rabbit is 10 times more hungry at energy maximum than at energy 0. <br>
     *      1.122f is value satisfying x ^ (20: maxEnergy) = (10: 10 times more hungry) <br>
     * The Rabbit focuses on avoiding foxes when they are not hungry. <br>
     *      1.149f is value satisfying x ^ (5: maxEnergy - breedLimit) = (2: 2 times more afraid) <br>
     *
     * @param actor to judge
     * @return value of preference
     */
    @Override
    protected double judge(Actors actor) {
	    switch (actor) {
            case GRASS:
                return Grass.GRASS_ENERGY_VALUE * 10 * pow(1.122f, getMaxEnergy() - getEnergy());
            case RABBIT:
                return Recognizable.COGNATION.getValue();
            case FOX:
            case BEAR:
            case HUNTER:
                return Recognizable.NEMESIS.getValue() * pow(1.149f, max(0, getEnergy() - getBreedLimit()));
            case GNAT:
                return Recognizable.IRRELEVANT.getValue();
            case GARDENER:
                return Recognizable.IRRELEVANT.getValue();
            default:
                return Recognizable.IRRELEVANT.getValue();
        }
    }
}
