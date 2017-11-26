package collections.concurrent.lockfree;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LinkedListTest {
    private static collections.interfaces.List<Integer> list;
    private static concurrent.Pool pool;

    private static final int testSize = 10000;
    private static List<Integer> numbers;

    @BeforeClass
    public static void init() throws Exception {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);

        pool = new concurrent.Pool(4);
    }

    @Before
    public void makeInstance() throws Exception {
        list = new LinkedList<>();
    }

    @Test
    public void add() throws Exception {
        numbers.forEach((e) -> pool.push(() -> list.add(e)));
        pool.join();
        numbers.forEach((e) -> assertTrue(list.contains(e)));
    }

    @Test
    public void remove() throws Exception {
        numbers.forEach((e) -> list.add(e));
        Collections.shuffle(numbers);
        numbers.forEach((e) -> pool.push(() -> list.remove((Integer) e)));
        pool.join();
        numbers.forEach((e) -> assertFalse(list.contains(e)));
    }

    @Test
    public void contains() throws Exception {
        numbers.forEach((e) -> list.add(e));
        Collections.shuffle(numbers);
        numbers.forEach((e) -> pool.push(() -> assertTrue(list.contains(e))));
        pool.join();
    }
}