package collections;

import collections.interfaces.Tree;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BinaryTreeTest {
    private static Tree<Integer> tree;

    private static final int testSize = 100000;
    private static List<Integer> numbers;

    @BeforeClass
    public static void init() throws Exception {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
    }

    @Before
    public void makeInstance() throws Exception {
        tree = new BinaryTree<>();
    }

    @Test
    public void insert() throws Exception {
        for (int i : numbers) {
            tree.insert(i);
            assertTrue(tree.search(i));
        }
    }

    @Test
    public void search() throws Exception {
        for (int i : numbers) {
            tree.insert(i);
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
    public void preOrderTraversal() throws Exception {
    }

    @Test
    public void inOrderTraversal() throws Exception {
    }

}