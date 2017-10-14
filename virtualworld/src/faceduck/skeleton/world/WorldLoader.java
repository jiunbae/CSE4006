package faceduck.skeleton.world;

import faceduck.actors.FoxImpl;
import faceduck.actors.Gardener;
import faceduck.actors.Gnat;
import faceduck.actors.Grass;
import faceduck.actors.RabbitImpl;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

/**
 * This class is for initializing your world. Here you get to decide how many of
 * your objects you want to add to your world in the beginning.
 */
public class WorldLoader {

	private World world;
	private int numGrass;
	private int numGnats;
	private int numRabbits;
	private int numFoxes;

	public WorldLoader(World w) {
		this.world = w;
		this.numGrass = world.getHeight() * world.getWidth() / 7;
		this.numGnats = numGrass / 4;
		this.numRabbits = numGrass / 4;
		this.numFoxes = numRabbits / 8;

//		this.numFoxes = 1;
//		this.numGnats = 0;
//		this.numRabbits = 1;
	}

	/**
	 * Populates the world with the initial {@link Actor}s.
	 */
	public void initializeWorld() {
		addGrass();
		addGnats();
		addGardener();
		addRabbit();
		addFox();
	}

	private void addGrass() {
		for (int i = 0; i < numGrass; i++) {
			Location loc = Util.randomEmptyLoc(world);
			world.add(new Grass(), loc);
		}
	}

	private void addGnats() {
		for (int i = 0; i < numGnats; i++) {
			Location loc = Util.randomEmptyLoc(world);
			world.add(new Gnat(10), loc);
		}
	}

	private void addGardener() {
		Location loc = Util.randomEmptyLoc(world);
		world.add(new Gardener(), loc);
	}

	private void addFox() {
		for (int i = 0; i < numFoxes; i++) {
			Location loc = Util.randomEmptyLoc(world);
			if (loc != null) {
				// If the world isn't full
				world.add(new FoxImpl(), loc);
			}
		}
	}

	private void addRabbit() {
		for (int i = 0; i < numRabbits; i++) {
			Location loc = Util.randomEmptyLoc(world);
			if (loc != null) {
				// If the world isn't full
				world.add(new RabbitImpl(), loc);
			}
		}
	}
}
