package faceduck.ai;

import faceduck.commands.BreedCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Util;

/**
 * The AI for a Gnat. This AI will pick a random direction and then return a
 * command which moves in that direction.
 *
 * This class serves as a simple example for how other AIs should be
 * implemented.
 */
public class GnatAI implements AI {

	/*
	 * Your AI implementation must provide a public default constructor so that
	 * the it can be initialized outside of the package.
	 */
	public GnatAI() {
	}

	/*
	 * GnatAI is dumb. It disregards its surroundings and simply tells the Gnat
	 * to move in a random direction.
	 */
	@Override
	public Command act(World world, Actor actor) {
	    return new MoveCommand(Util.randomDir());
    }
}
