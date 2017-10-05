package faceduck.custom;

import faceduck.custom.util.*;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.max;

public abstract class Actionable implements Animal {
    private static final int MAX_PROB_SIZE = 5;

    private List<Location> choices = new ArrayList<>();
    private Probability probability = new Probability();
    private double[][] memory;
    private boolean initialized = false;
    private Location prevLoc;
    private Location nowLoc;

    private int maxDistance;
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

    /**
     * predict where to go, there is an advantage over the distance.
     * @return maximum value in memory(where you want to go)
     */
    protected Location predict() {
        Location result = new Location(Utility.rand.nextInt(width), Utility.rand.nextInt(height));
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                // if Location support, equals operator, it will be shorter
                Location next = new Location(i, j);
                if (Utility.contain(choices, next, (final Location lhs, final Location rhs) ->
                    lhs.getX() == rhs.getX() && lhs.getY() == rhs.getY()
                )) continue;
                if (getScore(nowLoc, next) > getScore(nowLoc, result))
                    result = next;
            }
        }

        return result;
    }

    protected double getScore(Location from, Location to) {
        if (!Utility.isValidLocation(to.getX(), to.getY(), width, height)) return 0;

        double value = 0;
        for (Heading head : Heading.values()) {
            value += Utility.getValue(memory,
                    to.getX() + head.getValue().getFirst(), to.getY() + head.getValue().getSecond());
        }

        return value * (maxDistance - from.distanceTo(to));
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
