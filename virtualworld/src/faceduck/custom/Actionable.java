package faceduck.custom;

import faceduck.ai.AbstractAI;
import faceduck.commands.EatCommand;
import faceduck.custom.util.*;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.max;

public abstract class Actionable implements Animal {
    private static final int MAX_PROB_SIZE = 5;

    private Probability probability = new Probability();
    private World world;
    private double[][] memory;
    private boolean initialized = false;
    private Location prevLoc;
    private Location nowLoc;
    private AI ai;

    private int maxDistance;
    private int width;
    private int height;

    public Actionable(AI ai) {
        this.ai = ai;
    }

    /**
     * first time init memory space,
     * @param world
     *          The world you belong.
     */
    protected void init(World world) {
        if (initialized) return;

        width = world.getWidth();
        height = world.getHeight();
        maxDistance = new Location(0, 0).distanceTo(new Location(width, height));

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
     * judges what you seen and return preference. must Override for judge objects.
     * @param actor
     * @return negative numbers for enemy, positive numbers to edible.
     */
    protected abstract double judge(Actors actor);

    /**
     * Evaluate an area, can Override for custom evaluate value.
     * @param from  now Location
     * @param to    Location to be evaluated
     * @return score of Location
     */
    protected double getScore(Location from, Location to) {
        if (!Utility.isValidLocation(to.getX(), to.getY(), width, height)) return 0;

        double value = 0;
        for (Heading head : Heading.values()) {
            value += Utility.getValue(memory,
                    to.getX() + head.getValue().getFirst(), to.getY() + head.getValue().getSecond());
        }

        return value * (maxDistance - from.distanceTo(to));
    }

    /**
     * behold world, save what you see in memory.
     * @param world
     */
    private void behold(World world) {
        init(world);
        this.world = world;

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
        prevLoc = nowLoc;
    }

    /**
     * propagation memory, predict the following situation based on memory.
     */
    private void propagation() {
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

    /**
     * predict where to go, there is an advantage over the distance.
     * @param preChoices prevent duplicate choice
     * @return maximum value in memory(where you want to go)
     */
    private Location predict(List<Location> preChoices) {
        Location result = new Location(Utility.rand.nextInt(width), Utility.rand.nextInt(height));
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                // if Location support equals operator and Comparable, it will be shorter
                Location next = new Location(i, j);
                if (Utility.contain(preChoices, next, (final Location lhs, final Location rhs) ->
                    lhs.getX() == rhs.getX() && lhs.getY() == rhs.getY()
                )) continue;
                if (getScore(nowLoc, next) > getScore(nowLoc, result))
                    result = next;
            }
        }

        return result;
    }

    /**
     *
     */
    protected Command nextCommand() {
        Pair<Action, Direction> nextAct = nextAction();
        return nextAct.getFirst().command(nextAct.getSecond());
    }

    /**
     * return Action
     * @return
     */
    protected Pair<Action, Direction> nextAction() {
        List<Location> choices = new ArrayList<>();
        Location loc;

        do {
            loc = predict(choices);
            if (Utility.isAdjacent(nowLoc, loc)) {
                Object obj = world.getThing(loc);

            } else {
                for (Direction to : Utility.workload(nowLoc.dirTo(loc))) {
                    Object obj = world.getThing(Utility.destination(nowLoc, to));
                    if (obj == null)
                        return new Pair<>(Action.MOVE, to);
                }
                return new Pair<>(Action.WAIT, null);
            }
        } while (true);
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
