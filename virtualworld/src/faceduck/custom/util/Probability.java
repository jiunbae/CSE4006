package faceduck.custom.util;

import faceduck.skeleton.interfaces.World;

import java.util.*;

public class Probability {
    private double[] possibility = new double[Action.values().length];

    public Probability() { }

    public List<Action> best() {
        List<Pair<Action, Double>> ret = new ArrayList<>();

        for (int i = 0; i < possibility.length; ++i) {
            ret.add(new Pair<>(Action.values()[i], possibility[i]));
        }

        Collections.sort(ret, (Pair<Action, Double> o1, Pair<Action, Double> o2) -> {
            return -o1.getSecond().compareTo(o2.getSecond());
        });

        List<Action> actions = new ArrayList<>();
        for (Pair<Action, Double> e : ret) {
            actions.add(e.getFirst());
        }

        return actions;
    }
}
