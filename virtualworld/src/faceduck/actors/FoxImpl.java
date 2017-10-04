package faceduck.actors;

import faceduck.ai.FoxAI;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;

public class FoxImpl implements Fox {
	private static final int FOX_MAX_ENERGY = 160;
	private static final int FOX_VIEW_RANGE = 5;
	private static final int FOX_BREED_LIMIT = FOX_MAX_ENERGY * 3 / 4;
	private static final int FOX_COOL_DOWN = 2;
	private static final int FOX_INITAL_ENERGY = FOX_MAX_ENERGY * 1 / 2;

	@Override
	public int getEnergy() {
		return 0;
	}

	@Override
	public int getMaxEnergy() {
		return FOX_MAX_ENERGY;
	}

	@Override
	public int getBreedLimit() {
		return FOX_BREED_LIMIT;
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
		return FOX_VIEW_RANGE;
	}

	@Override
	public int getCoolDown() {
		return FOX_COOL_DOWN;
	}
}
