package cse4006;

import org.junit.Test;

import static org.junit.Assert.*;

public class FriendGraphTest {
    FriendGraph graph;

    @Test
    public void isPerson() throws Exception {
        String name = "Mike";
        graph = new FriendGraph();
        graph.addPerson(new Person(name));
        assertTrue(graph.isPerson(name));
    }

    @Test
    public void isRelation() throws Exception {
        final String names[] = {"John", "Merry", "Mike", "Steve"};
        graph = new FriendGraph();

        for (String name : names) {
            graph.addPerson(new Person(name));
        }

        graph.addFriendship("John", "Merry");
        assertTrue(graph.isRelation("John", "Merry"));

        graph.addFriendship("John", "Mike");
        assertTrue(graph.isRelation("John", "Mike"));

        graph.addFriendship("Merry", "Steve");
        assertTrue(graph.isRelation("Merry", "Steve"));
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
        String mike = "Mike";
        String merry = "Merry";
        String john = "John";
        String kate = "Kate";

        graph = new FriendGraph();
        graph.addPerson(new Person(mike));
        graph.addPerson(new Person(merry));
        graph.addPerson(new Person(john));

        graph.addFriendship(new Person(mike), new Person(merry));
        assertTrue(graph.isRelation(mike, merry));
        assertFalse(graph.isRelation(mike, john));
        assertFalse(graph.isRelation(mike, kate));
    }

    @Test
    public void getDistance() throws Exception {
        final String names[] = {"John", "Merry", "Mike", "Steve"};
        graph = new FriendGraph();

        for (String name : names) {
            graph.addPerson(new Person(name));
        }

        graph.addFriendship("John", "Merry");
        graph.addFriendship("John", "Mike");
        graph.addFriendship("Merry", "Steve");

        assertEquals(2, graph.getDistance("John", "Steve"));
    }

}

// J->Merry->Steve
// J->Mike