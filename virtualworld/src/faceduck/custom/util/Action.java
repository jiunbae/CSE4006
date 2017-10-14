package faceduck.custom.util;

import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.commands.WaitCommand;
import faceduck.custom.Actionable;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.util.Direction;

public enum Action {
    BREED(0), EAT(1), MOVE(2), WAIT(3);

    private final int value;
    Action(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public Command command(Direction dir) {
        switch (this) {
            case BREED:
                return new BreedCommand(dir);
            case EAT:
                return new EatCommand(dir);
            case MOVE:
                return new MoveCommand(dir);
            case WAIT:
                return new WaitCommand(dir);
        }
        return null;
    }
}
