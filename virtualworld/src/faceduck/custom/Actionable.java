package faceduck.custom;

import faceduck.custom.util.*;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Location;

import java.util.List;

import static java.lang.Math.max;

public abstract class Actionable implements Animal {
    private static final int MAX_PROB_SIZE = 5;

    private List<Pair<Integer, Integer>> choices;
    private Probability probability = new Probability();
    private double[][] memory;
    private boolean initialized = false;
    private Location prevLoc;
    private Location nowLoc;

    private int width;
    private int height;

    public Actionable() { }

    /**
     * first time init memory space,
     * @param world
     *          The world you belong.
     */
    protected void init(World world) {
        if (initialized) return;

        width = world.getWidth();
        height = world.getHeight();
        memory = new double[width][height];
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                memory[i][j] = 0;

        prevLoc = world.getLocation(this);

        initialized = true;
    }

    /**
     * must super before act, You must see and judge before you act on something.
     * @param world
     *            The world that the actor is currently in.
     */
    @Override
    public void act(World world) {
        behold(world);
    }

    /**
     * judges what you seen and return preference.
     * @param actor
     * @return negative numbers for enemy, positive numbers to edible.
     */
    protected abstract double judge(Actors actor);

    /**
     * behold world, save what you see in memory.
     * @param world
     */
    protected void behold(World world) {
        init(world);

        nowLoc = world.getLocation(this);
        Location nextLoc;

        for (int i = 0; i < getViewRange() * 2 + 1; ++i) {
            for (int j = 0; j < getViewRange() * 2 +1; ++j) {
                nextLoc = new Location(nowLoc.getX() + i - getViewRange(), nowLoc.getY() + j - getViewRange());
                if (i == 0 && j == 0) continue;
                if (!world.isValidLocation(nextLoc)) continue;

                Object thing = world.getThing(nextLoc);
                Actors recognized = Utility.recognize(thing);
                memory[nowLoc.getX() + i - getViewRange()][nowLoc.getY() + j - getViewRange()] = judge(recognized);
            }
        }

        propagation();

        choices.clear();
        for (int i = 0; i < MAX_PROB_SIZE; ++i) {
            choices.add(predict());
        }

        prevLoc = nowLoc;
    }

    /**
     * propagation memory, predict the following situation based on memory.
     */
    protected void propagation() {
        double[][] newMemory = new double[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (memory[i][j] == 0) continue;

                for (Heading head : Heading.values()) {
                    int x = head.getValue().getFirst() + i;
                    int y = head.getValue().getSecond() + j;
                    if (!Utility.isValidLocation(x, y, width, height)) continue;

                    newMemory[x][y] += memory[i][j] / Heading.values().length;
                }
            }
        }

        memory = newMemory;
    }

    protected Pair<Integer, Integer> predict() {
        Pair<Integer, Integer> result = new Pair<>(0, 0);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Location next = new Location(i, j);
                if (choices.contains(new Pair<>(i, j))) continue;
                if (memory[result.getFirst()][result.getSecond()] + nowLoc.distanceTo(Utility.localization(result))
                        < memory[i][j] + nowLoc.distanceTo(next))
                    result = new Pair<>(i, j);
            }
        }

        return result;
    }

    protected Action nextAction(World world) {
        for (Action act : probability.best())
            if (canAction(act, world))
                return act;
        return Action.WAIT;
    }

    /**
     * Determine if can take action.
     * so, always can WAIT
     * @param act
     * @param world
     * @return true if can take action
     */
    protected boolean canAction(Action act, World world) {
        switch(act) {
            case WAIT:
                return true;
            case MOVE:
                return !Utility.isClosed(world, this);
            case EAT:
                return false;
            case BREED:
                return false;
            default:
                return false;
        }
    }
}
