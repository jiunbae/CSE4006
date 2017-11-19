import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BinaryTreeTest {
    static int[] tests = { 2, 4, 1, 7, 9, 8};
    static BinaryTree binaryTree;

    @BeforeClass
    public static void makeInstance() throws Exception {
        binaryTree = new BinaryTree<Integer>();
    }

    @Test
    public void insert() throws Exception {
        for (int i : tests) {
            binaryTree.insert(i);
            assertTrue(binaryTree.search(i));
        }
    }

    @Test
    public void findMin() throws Exception {
        for (int i : tests) {
            binaryTree.insert(i);
        }

        assertEquals(binaryTree.findMin(), Arrays.stream(tests).min().getAsInt());
    }

    @Test
    public void search() throws Exception {
        for (int i : tests) {
            binaryTree.insert(i);
            assertTrue(binaryTree.search(i));
        }
    }

    @Test
    public void delete() throws Exception {
        for (int i : tests) {
            binaryTree.insert(i);
        }

        for (int i : tests) {
            binaryTree.delete(i);
            assertFalse(binaryTree.search(i));
        }
    }

    @Test
    public void preOrderTraversal() throws Exception {
    }

    @Test
    public void inOrderTraversal() throws Exception {
    }

}