package faceduck.ai;

import faceduck.commands.WaitCommand;
import faceduck.custom.Actionable;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;

public class HunterAI extends AbstractAI implements AI {

    public HunterAI() {

    }

    @Override
    public Command act(World world, Actor actor) {
        if (!(actor instanceof Actionable)) return new WaitCommand(null);

        Actionable actionable = (Actionable) actor;
        return actionable.decide();
    }
}
