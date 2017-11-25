package collections;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LinkedListTest {
    private static collections.interfaces.List<Integer> list;

    private static final int testSize = 1000000;
    private static List<Integer> numbers;

    @BeforeClass
    public static void init() throws Exception {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
    }

    @Before
    public void makeInstance() throws Exception {
        list = new collections.LinkedList<>();

        for (int i : numbers) {
            list.add(i);
        }
    }

    @Test
    public void add() throws Exception {
        assertEquals(numbers.size(), list.size());
    }

    @Test
    public void add1() throws Exception {
        int index = 3;
        list.add(index, numbers.get(index));

        assertEquals(numbers.get(index), list.get(index));
    }

    @Test
    public void addFirst() throws Exception {
        list.addFirst(numbers.get(0));
        assertEquals(numbers.get(0), list.getFirst());
    }

    @Test
    public void get() throws Exception {
        for (int i = 0; i < numbers.size() / 1000; ++i)
            assertEquals(numbers.get(i), list.get(i));
    }

    @Test
    public void getFirst() throws Exception {
        assertEquals(numbers.get(0), list.getFirst());
    }

    @Test
    public void getLast() throws Exception {
        assertEquals(numbers.get(numbers.size() - 1), list.getLast());
    }

    @Test
    public void indexOf() throws Exception {
        for (int i = 0; i < numbers.size() / 1000; ++i) {
            assertEquals(i, list.indexOf(numbers.get(i)));
        }
    }

    @Test
    public void remove() throws Exception {
        list.remove(0);
        assertEquals(numbers.size() - 1, list.size());
    }

    @Test
    public void remove1() throws Exception {
        numbers.forEach((e) -> list.remove(e));

        assertEquals(0, list.size());
    }

    @Test
    public void size() throws Exception {
        assertEquals(numbers.size(), list.size());
    }

    @Test
    public void toArray() throws Exception {
        Object[] array = list.toArray();
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(array[i], numbers.get(i));
        }
    }

    @Test
    public void iterate() throws Exception {
        int counter = 0;
        for (Iterator it = list.iterator(); it.hasNext();) {
            Integer item = (Integer) it.next();
            assertEquals(numbers.get(counter++), item);
        }
    }

    @Test
    public void forEach() throws Exception {
        int counter = 0;
        for (Integer i : list) {
            assertEquals(numbers.get(counter++), i);
        }
    }
}