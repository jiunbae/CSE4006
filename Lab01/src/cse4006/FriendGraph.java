package cse4006;

import cse4006.utility.Queue;

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
        if (size > 0 && size < 512) {
            this.size = size;
        } else {
            throw new Exception("input valid size");
        }
        adjust(size);
    }

    private final int[] newConnection() {
        return newConnection(size);
    }

    private final int[] newConnection(final int size) {
        int array[] = new int[size + 1];
        array[0] = 0;
        return array;
    }

    private final int[] newConnection(final int size, int[] connection) {
        int array[] = new int[size + 1];
        array[0] = connection[0];
        for (int i = 0; i < connection[0]; i++) {
            array[i + 1] = connection[i + 1];
        }
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
                latest[i] = network[i];
            }

            for (int i = 0; i < Math.min(gen.length, persons.length); i++) {
                gen[i] = persons[i];
            }
        }

        network = latest;
        persons = gen;
        this.size = size;
    }

    private void expand() {
        adjust(this.size * 2);
    }

    public final boolean isPerson(Person person) {
        return isPerson(person.getName());
    }

    public final boolean isPerson(String name) {
        for (int i = 0; i <= count; i++) {
            if (persons[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isRelation(Person target, Person object) {
        return isRelation(target.getName(), object.getName());
    }

    public final boolean isRelation(String target, String object) {
        int i = getIndex(target);
        int j = getIndex(object);

        for (int k = 0; k < network[i][0]; k++) {
            if (network[i][k + 1] == j) {
                return true;
            }
        }
        return false;
    }

    private final boolean isFullConnection(int[] connection) {
        return connection[0] == connection.length - 1;
    }

    private int getIndex(String name) {
        for (int i = 0; i <= count; i++) {
            if (persons[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    protected int[][] getNetwork() {
        return network;
    }

    public void addPerson(Person person) {
        if (isPerson(person))
            return;

        count += 1;
        if (count > size) {
            expand();
        }

        persons[count] = person;
    }

    private void addConnection(int target, int object) {
        int connection[] = network[target];
        if (isFullConnection(connection)) {
            connection = newConnection(connection.length * 2, connection);
        }

        connection[0] += 1;
        connection[connection[0]] = object;

        network[target] = connection;
    }

    public void addFriendship(Person target, Person object) {
        addFriendship(target.getName(), object.getName());
    }

    public void addFriendship(String target, String object) {
        int i = getIndex(target);
        int j = getIndex(object);

        addConnection(i, j);
        addConnection(j, i);
    }

    public int getDistance(String target, String object) {
        System.out.print(target);
        System.out.println(object);
        int i = getIndex(target);
        int j = getIndex(object);
        System.out.print(i);
        System.out.println(j);

        if (i == -1 || j == -1) {
            return -1;
        } else if (i == j) {
            return 0;
        }

        int last = 1;
        int[] visit = new int[count + 1];
        for (int k = 0; k < visit.length; k++) {
            visit[k] = -1;
        }

        Queue<Integer> q = new Queue<>();
        q.add(i);
        visit[i] = 0;

        while (!q.isEmpty()) {
            int v = 0;
            v = q.pop();

            for (int k = 0; k < network[v][0]; k++) {
                if (visit[network[v][k + 1]] == -1) {
                    visit[network[v][k + 1]] = last;
                    if (network[v][k + 1] == j) {
                        return visit[network[v][k + 1]];
                    }
                    q.add(network[v][k + 1]);
                }
            }
            last += 1;
        }

        return -1;
    }
}
