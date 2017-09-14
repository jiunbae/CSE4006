package cse4006;

public class FriendGraph {
    private int size = -1;
    private int count = -1;
    private Person persons[];
    private int network[][];

    public FriendGraph() {
        size = 16;
        adjust(size);
    }

    public FriendGraph(int size) throws Exception {
        if (isSize(size)) {
            this.size = size;
        } else {
            throw new Exception("input valid size");
        }
        adjust(size);
    }

    private boolean isSize(int size) { //@Improving: use *static* keyword if there is no constraint
        return size > 0 && size < 512;
    }

    private final int[] newConnection() {
        return newConnection(size);
    }

    private final int[] newConnection(final int size) {
        int array[] = new int[size + 1];
        array[0] = 0;
        return array;
    }

    private void adjust(int size) {
        Person gen[] = new Person[size];
        int latest[][] = new int[size][];

        if (network == null) {
            for (int i = 0; i < latest.length; i++) {
                latest[i] = newConnection();
            }

        } else {
            for (int i = 0; i < Math.min(latest.length, network.length); i++) {
                latest[i] = newConnection(network[i].length);
                for (int j = 0; j < network[i].length; j++) {
                    latest[i][j] = network[i][j];
                }
            }

            for (int i = 0; i < Math.min(gen.length, persons.length); i++) {
                gen[i] = persons[i];
            }
        }

        network = latest;
        persons = gen;
        this.size = size;
    }

    public boolean isPerson(Person person) {
        for (Person unknown : persons) {
            if (unknown.equals(person)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPerson(String name) {
        return isPerson(new Person(name));
    }

    public boolean isRelation(Person target, Person object) {
        return false;
    }

    public boolean isRelation(String target, String object) {
        return isRelation(new Person(target), new Person(object));
    }

    private void expand() {
        adjust(this.size * 2);
    }

    protected int[][] getNetwork() {
        return network;
    }

    public void addPerson(Person person) {
        if (!isPerson(person))
            return;

        count += 1;
        if (count > size) {
            expand();
        }

        persons[count] = person;
    }

    public void addFriendship(String target, String object) {

    }

    public int getDistance(String target, String object) {
        return 0;
    }
}
