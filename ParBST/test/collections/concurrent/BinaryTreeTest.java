package collections.concurrent;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class BinaryTreeTest {
    static BinaryTree<Integer> tree;
    static Random random;
    static Set<Integer> numbers;
    static final int testSize = 100000;

    static thread.Pool pool;

    @BeforeClass
    public static void makeInstance() throws Exception {
        tree = new BinaryTree<>();
        random = new Random();
        numbers = new HashSet<>();
        while (numbers.size() < testSize) {
            numbers.add(random.nextInt(testSize * 10));
        }

        pool = new thread.Pool(8);
    }

    @Test
    public void insert() throws Exception {
        for (int i : numbers) {
            tree.insert(i);
        }

        for (int i : numbers) {
            assertTrue(tree.search(i));
        }
    }

    @Test
    public void insertParallel() throws Exception {
        for (int i : numbers) {
            pool.push(() -> tree.insert(i));
        }

        pool.join();

        for (int i : numbers) {
            assertTrue(tree.search(i));
        }
    }

    @Test
    public void delete() throws Exception {
        for (int i : numbers) {
            tree.insert(i);
        }

        for (int i : numbers) {
            assertTrue(tree.search(i));
            tree.delete(i);
            assertFalse(tree.search(i));
        }
    }

    @Test
    public void deleteParallel() throws Exception {
        for (int i : numbers) {
            tree.insert(i);
        }

        for (int i : numbers) {
            pool.push(() -> tree.delete(i));
        }

        for (int i : numbers) {
            assertFalse(tree.search(i));
        }
    }
}