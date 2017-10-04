package faceduck.actors;

import faceduck.ai.GnatAI;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

/**
 * This is a simple implementation of a Gnat. It never loses energy and moves in
 * random directions.
 */
public class Gnat implements Animal {
	private static final int MAX_ENERGY = 10;
	private static final int VIEW_RANGE = 1;
	private static final int BREED_LIMIT = 0;
	private static final int COOL_DOWN = 0;

	public Gnat(int size) {

    }

	@Override
	public int getEnergy() {
		return 0;
	}

	@Override
	public int getMaxEnergy() {
		return 0;
	}

	@Override
	public int getBreedLimit() {
		return 0;
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
		move(world, Util.randomDir());
	}

	@Override
	public int getViewRange() {
		return 0;
	}

	@Override
	public int getCoolDown() {
		return 0;
	}
}
