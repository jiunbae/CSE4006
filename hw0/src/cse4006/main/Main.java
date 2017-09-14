package cse4006.main;

import cse4006.FriendGraph;
import cse4006.Person;

/**
 * project for Homework #1: A Social Network
 *
 * @see tests
 *
 * There is no limit(especially hadware-dependent) it's work as dynamic
 */
public class Main {

    /**
     * Main implement for show case
     *
     * Graph may be,
     * John -> Tom -> Jane
     *
     * @param args
     */
    public static void main(String[] args) {
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

        System.out.println(graph.getDistance("John", "Tom")); // should print 1
        System.out.println(graph.getDistance("John", "Jane")); // should print 2
        System.out.println(graph.getDistance("John", "John")); // should print 0
        System.out.println(graph.getDistance("Marry", "Marry")); // should print -1
    }
}
