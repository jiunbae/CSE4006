package faceduck.custom;

import faceduck.custom.util.*;
import faceduck.skeleton.interfaces.*;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Actionable is how can take action implements {@link Animal}
 *
 * You must execute {@link #act(World)} as super.act(world) before do something in act.
 * This method will propagation {@link Actionable}'s memory to judge where is best place to next.
 *
 * {@link #decide()} return best command to do next in view of memory.
 *
 * Actionable replace all of AI Class, because, each class does not need a separate AI.
 * It always chooses the best place to act.
 *
 * All you have to do is just override {@link #judge(Actors)} to evaluate objects.
 * Furthermore, you should override {@link #evaluate(Location, Location)} to evaluate area.
 * Then Actionable move the best area evaluated.
 */
public abstract class Actionable implements Animal, Cloneable {
    private HashSet<Class<?>> edible = new HashSet<>();
    private boolean initialized = false;
    private double[][] memory;
    private World world;

    private Location prevLoc;
    private Location nowLoc;

    private int maxDistance;
    private int width;
    private int height;
    private int energy;

    public Actionable() {
        energy = getInitialEnergy();
    }

    /**
     * Remove this from world, when die
     */
    @Override
    public void finalize() {
        world.remove(this);
    }

    /**
     * First time init memory space,
     *
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
     * Must super before act, You must see and judge before you act on something.
     *
     * @param world actor is currently in.
     */
    @Override
    public void act(World world) {
        if (world == null)
            throw new NullPointerException("World must not be null.");

        // propagation coolDown times, cuz think other moves during cool down.
        for (int i = 0; i <= getCoolDown(); i++)
            propagation();

        // update memory, see world and arrange memory to right predict.
        behold(world);

        // get best choice
        Command cmd = decide();
        cmd.execute(world, this);

        // do something(even if wait) is take energy.
        energy -= 1;
        energy = max(0, energy);

        // die if energy under 0
        if (energy <= 0 && energy != getMaxEnergy())
            this.finalize();
    }

    /**
     * Add actor class to check edible
     *
     * @param animal can it
     */
    protected void addEdible(Class<?> animal) {
        edible.add(animal);
    }

    /**
     * Judges what you seen and return preference. Must override for judge objects.
     *
     * @param actor to judge
     * @return negative numbers for enemy, positive numbers to edible.
     */
    protected abstract double judge(Actors actor);

    protected abstract int getInitialEnergy();

    /**
     * Evaluate an area, can override for custom evaluate function.
     * Default value is sum of the places next to {@link Location} and multiple reciprocal of distance.
     *
     * @param from  now Location
     * @param to    Location to be evaluated
     * @return score of Location
     */
    protected double evaluate(Location from, Location to) {
        if (!Utility.isValidLocation(to.getX(), to.getY(), width, height)) return 0;

        double value = 0;
        for (Heading head : Heading.values()) {
            value += Utility.getValue(memory,
                    to.getX() + head.getValue().getFirst(), to.getY() + head.getValue().getSecond());
        }

        return value * (maxDistance - from.distanceTo(to));
    }

    /**
     * Behold world, save what you see in memory.
     * Can see only as much as your {@link #getViewRange()}
     *
     * @param world to seen
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

        prevLoc = nowLoc;
    }

    /**
     * propagation memory, predict the following situation based on memory.
     * Basically, everything moves 25% each {@link Heading}.
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
     * Best location where to go, evaluate with the {@link #evaluate(Location, Location)}.
     * You can override {@link #evaluate(Location, Location)} to make custom evaluate function.
     *
     * @param preChoices prevent duplicate choice
     * @return maximum value in memory(where you want to go)
     */
    private Location bestLocation(List<Location> preChoices) {
        Location result = new Location(Utility.rand.nextInt(width), Utility.rand.nextInt(height));
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Location next = new Location(i, j);
                if (Utility.contain(preChoices, next, (final Location lhs, final Location rhs) ->
                    lhs.getX() == rhs.getX() && lhs.getY() == rhs.getY()
                )) continue;
                if (evaluate(nowLoc, next) > evaluate(nowLoc, result))
                    result = next;
            }
        }

        preChoices.add(result);
        return result;
    }


    /**
     * Return best choice to next move.
     *
     * @return the best command
     */
    protected Command decide() {
        List<Location> choices = new ArrayList<>();
        Location loc;

        do {
            loc = bestLocation(choices);
            Direction dir = nowLoc.dirTo(loc);

            if (canAction(Action.BREED, nowLoc)) {
                dir = Utility.randomAdjacent(t ->
                        world.isValidLocation(Utility.destination(nowLoc, t)) &&
                        world.getThing(Utility.destination(nowLoc, t)) == null);
                if (dir != null) return Action.BREED.command(dir);
            }

            if (canAction(Action.EAT, Utility.destination(nowLoc, dir))) {
                return Action.EAT.command(dir);
            } else if (canAction(Action.MOVE, Utility.destination(nowLoc, dir))) {
                return Action.MOVE.command(dir);
            } else {
                for (Direction to : Utility.workload(dir))
                    if (canAction(Action.MOVE, Utility.destination(nowLoc, to)))
                        return Action.MOVE.command(to);
            }

            if (Utility.isClosed(world, nowLoc)) {
                return Action.WAIT.command(null);
            }
        } while (true);
    }

    /**
     * Determine if can take action.
     * so, always can WAIT
     *
     * @param act
     * @return true if can take action
     */
    protected boolean canAction(Action act, Location loc) {
        Object obj = world.getThing(loc);
        switch(act) {
            case WAIT:
                return true;
            case MOVE:
                return obj == null;
            case EAT:
                return obj instanceof Edible && edible.contains(obj.getClass());
            case BREED:
                return energy > this.getBreedLimit() && !Utility.isClosed(world, loc);
            default:
                return false;
        }
    }

    /**
     * Earn energy value, call when take edible
     *
     * @param value to earn
     */
    private void earnEnergy(int value) {
        energy += value;
        energy = min(energy, getMaxEnergy());
    }

    /**
     * Make clone, override Cloneable. Child objects take half of energy.
     * Not able to use the constructor method because forced to not use <code>java.reflect</code> in the manual.
     *
     * @return clone of this instance.
     */
    @Override
    public Actionable clone() {
        energy = (int) floor(energy / 2);
        final Actionable clone;
        try {
            clone = (Actionable) super.clone();
            clone.energy = this.energy;
            clone.initialized = false;
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Eat something in the world
     * The command returned from {@link #act(World)} ensures that this action can be taken.
     *
     * @param world
     *            The world containing this actor.
     * @param dir
     *            The direction of the Actor to eat.
     *
     */
    @Override
    public void eat(World world, Direction dir) {
        Location prevLoc = world.getLocation(this);
        Location targetLoc = new Location(prevLoc, dir);

        Edible target = (Edible) world.getThing(targetLoc);
        earnEnergy(target.getEnergyValue());
        world.remove(target);
    }

    /**
     * Move to somewhere.
     * The command returned from {@link #act(World)} ensures that this action can be taken.
     *
     * @param world
     *            The world containing this actor.
     * @param dir
     *            The direction to move in.
     *
     */
    @Override
    public void move(World world, Direction dir) {
        Location prevLoc = world.getLocation(this);
        Location nextLoc = new Location(prevLoc, dir);

        world.remove(this);
        world.add(this, nextLoc);
    }

    /**
     * Breed child to someplace.
     * The command returned from {@link #act(World)} ensures that this action can be taken.
     *
     * @param world
     *            The world containing this actor.
     * @param dir
     *            The direction in which the new Animal will spawn.
     *
     */
    @Override
    public void breed(World world, Direction dir) {
        Location prevLoc = world.getLocation(this);
        Location nextLoc = new Location(prevLoc, dir);

        try {
            world.add(this.clone(), nextLoc);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
