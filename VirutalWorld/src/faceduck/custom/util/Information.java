package faceduck.custom.util;

public class Information {
    private int born;
    private int age;
    private int eat;
    private int move;
    private int child;
    private int energy;

    public Information(int born) {
        this.born = born;
        age = eat = move = child = 0;
    }

    public void older(int gen) {
        age += gen;
    }

    public void writeMove() {
        move += 1;
    }

    public void writeBreed() {
        child += 1;
    }

    public void writeEat() {
        eat += 1;
    }

    public void writeEnergy(int value) {
        energy = value;
    }

    public int getGeneration() {
        return born + age;
    }

    public static final String[] getLabels() {
        return new String[]{"born", "age", "eat", "move", "child", "energy"};
    }

    public int[] getValues() {
        return new int[]{born, age, eat, move, child, energy};
    }
}
