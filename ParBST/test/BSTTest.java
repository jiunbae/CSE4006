import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BSTTest {
    static int[] tests = { 2, 4, 1, 7, 9, 8};
    static BST bst;

    @BeforeClass
    public static void makeInstance() throws Exception {
        bst = new BST<Integer>();
    }

    @Test
    public void insert() throws Exception {
        for (int i : tests) {
            bst.insert(i);
            assertTrue(bst.search(i));
        }
    }

    @Test
    public void findMin() throws Exception {
        for (int i : tests) {
            bst.insert(i);
        }

        assertEquals(bst.findMin(), Arrays.stream(tests).min().getAsInt());
    }

    @Test
    public void search() throws Exception {
        for (int i : tests) {
            bst.insert(i);
            assertTrue(bst.search(i));
        }
    }

    @Test
    public void delete() throws Exception {
        for (int i : tests) {
            bst.insert(i);
        }

        for (int i : tests) {
            bst.delete(i);
            assertFalse(bst.search(i));
        }
    }

    @Test
    public void preOrderTraversal() throws Exception {
    }

    @Test
    public void inOrderTraversal() throws Exception {
    }

}