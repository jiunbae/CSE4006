package faceduck.ai;

import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public abstract class AbstractAI implements AI {

	/**
	 * constructor for AbstractAI
	 */
	public AbstractAI() {

	}


	/**
	 * abstract act command
	 */
	public abstract Command act(World world, Actor actor);
}
