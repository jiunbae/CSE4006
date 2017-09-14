package cse4006;

import org.junit.Test;

import static org.junit.Assert.*;

public class FriendGraphTest {
    FriendGraph graph;

    @Test
    public void isPerson() throws Exception {
    }

    @Test
    public void isRelation() throws Exception {
    }

    @Test
    public void getNetwork() throws Exception {
        int size = 32;
        graph = new FriendGraph(size);
        int network[][] = graph.getNetwork();
        assertEquals(size, network.length);

        for (int connection[] : network) {
            assertEquals(size + 1, connection.length);
        }
    }

    @Test
    public void addPerson() throws Exception {
        final String names[] = {"John", "Merry", "Mike", "Steve"};

        graph = new FriendGraph();
        for (String name : names) {
            System.out.println(name);
            graph.addPerson(new Person(name));
        }

        for (String name : names) {
            assertTrue(graph.isPerson(name));
        }

        for (String name : names) {
            assertTrue(graph.isPerson(new Person(name)));
        }
    }

    @Test
    public void addFriendship() throws Exception {
    }

    @Test
    public void getDistance() throws Exception {
    }

}