package thread.safe;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class BinaryTreeTest {
    static BinaryTree<Integer> tree;
    static Random random;
    static List<Integer> numbers;

    @BeforeClass
    public static void makeInstance() throws Exception {
        tree = new BinaryTree<>();
        random = new Random();
        numbers = new ArrayList<>();
        for (int i = 0; i < 10000; ++i) {
            numbers.add(random.nextInt(10000));
        }
    }

    @Test
    public void insert() throws Exception {
        for (int i : numbers) {
            tree.insert(i);
            assertTrue(tree.search(i));
        }
    }

    @Test
    public void insertParallel() throws Exception {
        for (int i : numbers) {
            new Thread(() -> tree.insert(i)).start();
        }

        for (int i : numbers) {
            assertTrue(tree.search(i));
        }
    }

    @Test
    public void delete() throws Exception {
    }

}