package faceduck.ai;

import faceduck.actors.Grass;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.commands.WaitCommand;
import faceduck.custom.Actionable;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class RabbitAI extends AbstractAI implements AI {

	/**
	 * constructor for RabbitAI
	 */
	public RabbitAI() {
	}

	@Override
	public Command act(World world, Actor actor) {
		if (!(actor instanceof Actionable)) return new WaitCommand(null);

		Actionable actionable = (Actionable) actor;
		return actionable.decide();
	}
}
