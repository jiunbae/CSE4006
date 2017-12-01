package collections.concurrent.lockfree;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LinkedListTest {
    private static collections.interfaces.List<Integer> list;
    private static concurrent.Pool pool;

    private static final int testSize = 100000;
    private static List<Integer> numbers;

    @BeforeClass
    public static void init() throws Exception {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
    }

    @Before
    public void makeInstance() throws Exception {
        list = new LinkedList<>();

        pool = new concurrent.Pool(4);
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

    @Test
    public void complex() throws Exception {
        Set<Integer> verifier = new HashSet<>();
        Random random = new Random(0);
        numbers.forEach((e) -> {
            list.add(e);
            verifier.add(e);
        });
        Collections.shuffle(numbers);
        for (int i = 0; i < testSize; ++i) {
            int finalI = i;
            switch (random.nextInt(3)) {
                case 0:
                    pool.push(() -> list.add(numbers.get(finalI)));
                    verifier.add(numbers.get(finalI));
                    break;
                case 1:
                    pool.push(() -> list.remove(numbers.get(finalI)));
                    verifier.remove(numbers.get(finalI));
                    break;
                case 2:
                    pool.push(() -> list.contains(numbers.get(finalI)));
                    break;
            }
        }
        pool.join();
        numbers.forEach((e) -> assertEquals(verifier.contains(e), list.contains(e)));
    }
}