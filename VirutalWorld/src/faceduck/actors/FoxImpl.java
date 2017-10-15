package faceduck.actors;

import faceduck.ai.FoxAI;
import faceduck.custom.Actionable;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.*;

import static java.lang.Math.max;
import static java.lang.Math.pow;

public class FoxImpl extends Actionable implements Fox, Edible {
	private static final int FOX_MAX_ENERGY = 120;
	private static final int FOX_VIEW_RANGE = 5;
	private static final int FOX_BREED_LIMIT = FOX_MAX_ENERGY * 3 / 5;
    protected static final int FOX_ENERGY_VALUE = 80;
	private static final int FOX_COOL_DOWN = 3;
	private static final int FOX_INITIAL_ENERGY = FOX_MAX_ENERGY / 2;
	private static final double FOX_FORGETFULNESS = .6f;

	public FoxImpl() {
		super(new FoxAI(), FOX_INITIAL_ENERGY);
		addEdible(Rabbit.class);
	}

	@Override
	public int getMaxEnergy() {
		return FOX_MAX_ENERGY;
	}

    /**
     * Fox breed sometimes
     *
     * @return breedLimit
     */
	@Override
	public int getBreedLimit() {
		return FOX_BREED_LIMIT;
	}

    /**
     * Fox can be seen far away.
     *
     * @return viewRange()
     */
	@Override
	public int getViewRange() {
		return FOX_VIEW_RANGE;
	}

    /**
     * Fox is fast.
     * @return coolDown
     */
	@Override
	public int getCoolDown() {
		return FOX_COOL_DOWN;
	}

    @Override
    public int getEnergyValue() {
        return FOX_ENERGY_VALUE;
    }

	/**
	 * It represents the extent to which the fox can not remember.
     * Bigger the value, better the memory.
     *
	 * @return value of forgetfulness
	 */
	@Override
	protected double getForgetfulness() {
		return FOX_FORGETFULNESS;
	}

    /**
     * Judges what you seen and return preference.<br><br>
     *
     * - Fox like Rabbit because Fox can eat. and as the hungry. <br>
     * - Fox doesn't like other Fox because have to divide the number of Rabbits. <br>
     * - Fox hate a little of others, because staying still is not good for a changing world. <br>
     *
     * Fox have satiety, when energy above breedLimit, like Rabbits less(half). <br>
     *
     * @param actor to judge
     * @return value of preference
     */
	@Override
	protected double judge(Actors actor) {
		switch (actor) {
            case RABBIT:
                return RabbitImpl.RABBIT_ENERGY_VALUE * 10
                        - (getEnergy() >= getBreedLimit() ? RabbitImpl.RABBIT_ENERGY_VALUE * 5 : 0);
            case FOX:
                return Recognizable.COGNATION.getValue();
            case BEAR:
            case HUNTER:
                return Recognizable.NEMESIS.getValue() * pow(1.149f, max(0, getEnergy() - getBreedLimit()));
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
