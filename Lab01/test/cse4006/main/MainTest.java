package cse4006.main;

import cse4006.FriendGraph;
import cse4006.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void main() throws Exception {
        FriendGraph graph = new FriendGraph();
        Person john = new Person("John");
        Person tom = new Person("Tom");
        Person jane = new Person("Jane");
        Person marry = new Person("Marry");

        graph.addPerson(john);
        graph.addPerson(tom);
        graph.addPerson(jane);
        graph.addPerson(marry);
        graph.addFriendship("John", "Tom");
        graph.addFriendship("Tom", "Jane");

        assertEquals(1, graph.getDistance("John", "Tom"));
        assertEquals(2, graph.getDistance("John", "Jane"));
        assertEquals(0, graph.getDistance("John", "John"));
        assertEquals(0, graph.getDistance("Marry", "Marry"));
    }
}
