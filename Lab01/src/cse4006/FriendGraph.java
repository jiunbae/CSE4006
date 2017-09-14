package cse4006;

import cse4006.utility.Queue;

public class FriendGraph {
    //private static final int DEFAULT_SIZE = 16; // use *static* cause using in constructor.
    private int size = -1;
    private int count = -1;
    private Person persons[];
    private int network[][];

    public FriendGraph() {
        size = 16;                                //@Improving: use *static* keyword if there is no constraint
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

    /**
     * recall newConnection(default size of graph(=16))
     * @return newConnection(array list)
     */
    private final int[] newConnection() {
        return newConnection(size);
    }

    /**
     * @param size: new array list size(capacity)
     * @return newConnection(array list) with input size
     */
    private final int[] newConnection(final int size) {
        int array[] = new int[size + 1];
        array[0] = 0;
        return array;
    }

    /**
     * @param size: new array list size(capacity)
     * @param connection: array list
     * @return newConnection from input connection, with input size
     */
    private final int[] newConnection(final int size, final int[] connection) {
        int array[] = new int[Math.min(connection[0], size + 1)];
        array[0] = Math.min(connection[0], size);
        for (int i = 0; i < array[0]; i++) {
            array[i + 1] = connection[i + 1];
        }
        return array;
    }

    /**
     * network resize by input size
     * @param size: size to reform
     */
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

    /**
     * expand network size
     * call adjust(2 * now size)
     */
    private void expand() {
        adjust(this.size * 2);
    }

    /**
     * recall isPerson(Person.getName())
     * @param person: person to check
     * @return is in network or not
     */
    public final boolean isPerson(Person person) {
        return isPerson(person.getName());
    }

    /**
     * @param name: check persons have *name*
     * @return is in network or not
     */
    public final boolean isPerson(String name) {
        for (int i = 0; i <= count; i++) {
            if (persons[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * recall isRelation(target.getName(), object.getName());
     * @param target: relation from
     * @param object: relation to
     * @return check there is relation
     */
    public final boolean isRelation(Person target, Person object) {
        return isRelation(target.getName(), object.getName());
    }

    /**
     * @param target: relation from
     * @param object: relation to
     * @return check there is relation
     */
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

    /**
     * @param connection: array list
     * @return check is connection(array list) is full
     */
    private final boolean isFullConnection(int[] connection) {
        return connection[0] == connection.length - 1;
    }

    /**
     * @param name: to find in network
     * @return index of named person in network (or not -1)
     */
    private int getIndex(String name) {
        for (int i = 0; i <= count; i++) {
            if (persons[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * for test, protected
     * !Do not use directly
     * @return copy of network
     */
    protected int[][] getNetwork() {
        return network;
    }

    /**
     * add person to network
     * @param person: to add
     */
    public void addPerson(Person person) {
        if (isPerson(person))
            return;

        count += 1;
        if (count > size) {
            expand();
        }

        persons[count] = person;
    }

    /**
     * make relation between persons
     * add object to target's network
     * @param target: person
     * @param object: person
     */
    private void addConnection(int target, int object) {
        int connection[] = network[target];
        if (isFullConnection(connection)) {
            connection = newConnection(connection.length * 2, connection);
        }

        connection[0] += 1;
        connection[connection[0]] = object;

        network[target] = connection;
    }

    /**
     * recall addFriendship(target.getName(), object.getName())
     * @param target: person
     * @param object: person
     */
    public void addFriendship(Person target, Person object) {
        addFriendship(target.getName(), object.getName());
    }

    /**
     * make relation between persons
     * no differ in target and object
     * @param target: person to add network
     * @param object: person to add network
     */
    public void addFriendship(String target, String object) {
        int i = getIndex(target);
        int j = getIndex(object);

        addConnection(i, j);
        addConnection(j, i);
    }

    /**
     * find shortest path from target to object using BFS
     * @param target: from
     * @param object: to
     * @return shortest distance from target to object(target == object than 0, if target or object not in network -1)
     */
    public int getDistance(String target, String object) {
        int i = getIndex(target);
        int j = getIndex(object);

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
            int v = q.pop();

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
