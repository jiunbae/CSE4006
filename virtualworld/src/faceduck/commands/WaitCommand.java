package faceduck.commands;

import faceduck.custom.Actionable;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;

public class WaitCommand implements Command {

    private Direction dir;

    /**
     * Instantiates a wait command to be executed in the given direction or null.
     *
     * @param dir or null
     *            The direction in which the command will be performed.
     *
     * @throws NullPointerException
     *             If direction is null.
     */
    public WaitCommand(Object dir) {
        if (dir == null) {
            dir = null;
        }
        this.dir = (Direction) dir;
    }

    @Override
    public void execute(World world, Actor actor) {
    }
}
