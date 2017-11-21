package collections;

import collections.interfaces.List;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class LinkedListTest {
    static int[] tests = { 2, 4, 1, 7, 9, 8};
    static List<Integer> list;

    @BeforeClass
    public static void makeInstance() throws Exception {
        list = new LinkedList<>();
    }

    @Test
    public void add() throws Exception {
        for (int i : tests) {
            list.add(i);
        }

        assertEquals(list.size(), tests.length);
        Object[] array = list.toArray();
        for (int i = 0; i < tests.length; ++i) {
            assertEquals(tests[i], (int) array[i]);
        }
    }

    @Test
    public void add1() throws Exception {
    }

    @Test
    public void addFirst() throws Exception {
    }

    @Test
    public void get() throws Exception {
    }

    @Test
    public void getFirst() throws Exception {
    }

    @Test
    public void getLast() throws Exception {
    }

    @Test
    public void indexOf() throws Exception {
    }

    @Test
    public void remove() throws Exception {
    }

    @Test
    public void remove1() throws Exception {
    }

    @Test
    public void size() throws Exception {
    }

}