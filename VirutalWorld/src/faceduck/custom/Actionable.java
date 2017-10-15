package faceduck.custom;

import faceduck.custom.util.*;
import faceduck.skeleton.interfaces.*;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

import static java.lang.Math.*;

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
    private AI ai;

    private Location nowLoc;
    private Location preLoc;

    protected Information info;
    private int maxDistance;
    private int width;
    private int height;
    private int energy;

    public Actionable() {

    }

    public Actionable(AI ai, int energy) {
        this.energy = energy;
        this.ai = ai;
    }

    /**
     * Remove this from world, when die
     */
    @Override
    public void finalize() {
        try {
            world.remove(this);
        } catch (Exception e) {
            // this can raise if erase from world after erased.
            // e.printStackTrace();
        }
    }

    /**
     * First time init memory space,
     *
     * @param world
     *          The world you belong.
     */
    private void init(World world) {
        if (initialized) return;

        width = world.getWidth();
        height = world.getHeight();
        maxDistance = new Location(0, 0).distanceTo(new Location(width, height));
        preLoc = world.getLocation(this);

        memory = new double[width][height];
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                memory[i][j] = 0;

        if (info == null) info = new Information(0);
        initialized = true;
    }

    /**
     * Must super before act, You must see and judge before you act on something.
     *
     * 1. First of all, {@link #init(World)} if not initialized
     * 2. {@link #propagation()} coolDown / 2 times
     * 3. {@link #behold(World)} to update memory
     * 4. Find out what to do next, by {@link #decide()}, it re-called in {@link AI}
     * 5. Update {@link #energy}
     * 6. Done
     *
     * @param world actor is currently in.
     */
    @Override
    public void act(World world) {
        if (world == null)
            throw new NullPointerException("World must not be null.");

        init(world);

        nowLoc = world.getLocation(this);
        // propagation coolDown times, cuz think other moves during cool down.
        for (int i = 0; i <= getCoolDown() / 2; i++)
            propagation();

        // update memory, see world and arrange memory to right predict.
        behold(world);

        // @TODO anti pattern, It's cleaner not to use AI
        // get best choice
        Command cmd = ai.act(world, this);
        cmd.execute(world, this);
        preLoc = nowLoc;

        // do something(even if wait) is take energy.
        energy -= 1;
        energy = max(0, energy);
        info.older(getCoolDown());
        info.writeEnergy(energy);

        // die if energy under 0
        if (energy <= 0 && energy != getMaxEnergy())
            world.remove(this);
    }

    /**
     * propagation memory, predict the following situation based on memory.
     * Basically, everything moves 25% each {@link Direction}.
     */
    private void propagation() {
        double[][] newMemory = new double[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (memory[i][j] == 0) continue;

                for (Direction dir : Direction.values()) {
                    int x = dir.getValue().getFirst() + i;
                    int y = dir.getValue().getSecond() + j;
                    if (!Utility.isValidLocation(x, y, width, height)) continue;

                    newMemory[x][y] += memory[i][j] / Direction.values().length * getForgetfulness();
                }
            }
        }

        // Avoid going past way little
        newMemory[preLoc.getX()][preLoc.getY()] -= abs(newMemory[preLoc.getX()][preLoc.getY()] / 2);
        memory = newMemory;
    }

    /**
     * Behold world, save what you see in memory.
     * Can see only as much as your {@link #getViewRange()}
     *
     * @param world to seen
     */
    private void behold(World world) {
        this.world = world;
        Location nextLoc;

        for (int i = 0; i < getViewRange() * 2 + 1; ++i) {
            for (int j = 0; j < getViewRange() * 2 +1; ++j) {
                nextLoc = new Location(nowLoc.getX() + i - getViewRange(), nowLoc.getY() + j - getViewRange());
                if ((i == 0 && j == 0) || !world.isValidLocation(nextLoc)) continue;

                memory[nowLoc.getX() + i - getViewRange()][nowLoc.getY() + j - getViewRange()]
                        = judge(Actors.recognize(world.getThing(nextLoc)));
            }
        }
    }

    /**
     * Return best choice to next move.
     *
     * @return the best command
     */
    public Command decide() {
        List<Location> choices = new ArrayList<>();
        Location loc;

        do {
            loc = destination(choices);
            Direction dir = nowLoc.dirTo(loc);
            Location dest = Utility.destination(nowLoc, dir);
            if (!world.isValidLocation(loc) || !world.isValidLocation(dest)) continue;

            // First of all, check you can breed.
            // Breed is most a intense instinct

            // Breed require 3 thing belows.
            // 1. Energy > breedLimit
            // 2. At least one empty space beside
            // 3. Probability of breeding increases with the amount of energy (limit: 25% to max: 100%)
            double breedProbability = (100 - (getMaxEnergy() - getEnergy()) * (25.f / getEnergy())) / 100.f;
            if (isPossible(Action.BREED, nowLoc) && Util.getRandom().nextFloat() < breedProbability) {
                dir = Utility.randomAdjacent(t ->
                        world.isValidLocation(Utility.destination(nowLoc, t)) &&
                                world.getThing(Utility.destination(nowLoc, t)) == null);
                if (dir != null) return Action.BREED.command(dir);
            }

            if (isPossible(Action.EAT, dest)) {
                return Action.EAT.command(dir);
            } else if (isPossible(Action.MOVE, dest)) {
                return Action.MOVE.command(dir);
            } else {
                for (Direction to : Utility.workload(dir)) {
                    Location d = Utility.destination(nowLoc, to);
                    if (!world.isValidLocation(d)) continue;
                    if (isPossible(Action.MOVE, d))
                        return Action.MOVE.command(to);
                }
            }

            if (Utility.isClosed(world, nowLoc)) {
                return Action.WAIT.command(null);
            }
        } while (true);
    }

    /**
     * Best location where to go, evaluate with the {@link #evaluate(Location, Location)}.
     * You can override {@link #evaluate(Location, Location)} to make custom evaluate function.
     *
     * @param preChoices prevent duplicate choice
     * @return maximum value in memory(where you want to go)
     */
    private Location destination(List<Location> preChoices) {
        Location result = new Location(Util.getRandom().nextInt(width), Util.getRandom().nextInt(height));
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
     * Evaluate an area, can override for custom evaluate function.
     * Default value is sum of the places next to {@link Location} and multiple reciprocal of distance.
     *
     * @param from  now Location
     * @param to    Location to be evaluated
     * @return score of Location
     */
    public double evaluate(Location from, Location to) {
        if (!Utility.isValidLocation(to.getX(), to.getY(), width, height)) return 0;
        return evaluate(from, to, getViewRange() / 2) * log((maxDistance - from.distanceTo(to)) * 16) / 5;
    }
    private double evaluate(Location from, Location to, int depth) {
        if (!Utility.isValidLocation(to.getX(), to.getY(), width, height)) return 0;

        if (depth == 0) {
            if (!Utility.isValidLocation(to.getX(), to.getY(), width, height)) return 0;
            return memory[to.getX()][to.getY()];
        } else {
            double sum = 0;
            for (Direction dir : Direction.values()) {
                sum += evaluate(from, Utility.destination(to, dir), depth - 1);
            }
            return sum / Direction.values().length * (maxDistance - from.distanceTo(to)) / maxDistance;
        }
    }


    /**
     * Add actor class to check edible
     *
     * @param e can it
     */
    protected void addEdible(Class<?> e) {
        edible.add(e);
    }

    /**
     * Judges what you seen and return preference. Must override for judge objects.
     *
     * @param actor to judge
     * @return negative numbers for enemy, positive numbers to edible.
     */
    protected abstract double judge(Actors actor);

    /**
     * The degree of confidence in what is seen is increasingly faint.
     * The lower the value, the more think about what is visible.
     *
     * @return getForgetfulness ratio
     */
    protected abstract double getForgetfulness();

    /**
     * Support forEach loop to iterate memory
     *
     * @param f as Consumer with Double as value, Location as position
     */
    public void forEachMemory(BiConsumer<Double, Location> f) {
        if (memory == null) return;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                f.accept(memory[i][j], new Location(i, j));
            }
        }
    }

    /**
     * return {@link Information} of {@link Actionable}
     * @return
     */
    public int[] getInformation() {
        if (info == null) return null;
        return info.getValues();
    }

    /**
     * check the action is possible
     * @param act to take
     * @param loc to do action
     * @return take action in location is possible
     */
    protected boolean isPossible(Action act, Location loc) {
        Object obj = world.getThing(loc);
        switch(act) {
            case WAIT:
                return true;
            case MOVE:
                return obj == null;
            case EAT:
                return obj instanceof Edible && edible.stream().anyMatch(e -> e.isInstance(obj));
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
    protected void earnEnergy(int value) {
        energy += value;
        energy = min(energy, getMaxEnergy());
    }

    @Override
    public int getEnergy() {
        return energy;
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
        info.writeEat();
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
        info.writeMove();
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
        info.writeBreed();
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
            clone.info = new Information(info.getGeneration());
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
