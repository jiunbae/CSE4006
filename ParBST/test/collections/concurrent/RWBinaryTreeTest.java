package collections.concurrent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class RWBinaryTreeTest {
    private static RWBinaryTree<Integer> tree;
    private static concurrent.Pool pool;

    private static final int testSize = 10;
    private static List<Integer> numbers;

    @BeforeClass
    public static void init() throws Exception {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);

        pool = new concurrent.Pool(2);
    }

    @Before
    public void makeInstance() throws Exception {
        tree = new RWBinaryTree<>();
    }

    @Test
    public void insert() throws Exception {
        numbers.forEach((e) -> tree.insert(e));
        numbers.forEach((e) -> assertTrue(tree.search(e)));
    }

    @Test
    public void insertParallel() throws Exception {
        numbers.forEach((e) -> pool.push(() -> tree.insert(e)));
        pool.join();
        assertEquals(numbers.size(), tree.size());
        System.out.println(tree.size());
        numbers.forEach((e) -> assertTrue(tree.search(e)));
    }

    @Test
    public void delete() throws Exception {
        numbers.forEach((e) -> tree.insert(e));
        numbers.forEach((e) -> tree.delete(e));
        numbers.forEach((e) -> assertFalse(tree.search(e)));
    }

    @Test
    public void deleteParallel() throws Exception {
        numbers.forEach((e) -> tree.insert(e));
        numbers.forEach((e) -> pool.push(() -> tree.delete(e)));
        pool.join();
        assertEquals(0, tree.size());
        System.out.println(tree.size());
        numbers.forEach((e) -> assertFalse(tree.search(e)));
    }

    @Test
    public void search() throws Exception {
        numbers.forEach((e) -> tree.insert(e));
        numbers.forEach((e) -> tree.search(e));
    }

    @Test
    public void searchParallel() throws Exception {
        numbers.forEach((e) -> tree.insert(e));
        numbers.forEach((e) -> pool.push(() -> assertTrue(tree.search(e))));
        pool.join();
    }
}