package cse4006.utility;

import org.junit.Test;

import static org.junit.Assert.*;

public class QueueTest {
    Queue<Integer> q;

    @Test
    public void fromArrayTest() throws Exception {
        Integer array[] = new Integer[] {1, 2, 3, 4 ,5};
        q = new Queue<>(array);

        for (Integer e : array) {
            assertEquals((int) e, (int) q.pop());
        }
    }

    @Test
    public void isEmptyTest() throws Exception {
        q = new Queue<>();
        assertTrue(q.isEmpty());
    }

    @Test
    public void isFullTest() throws Exception {
        int cap = 4;
        q = new Queue<>(cap);

        for (int i = 0; i < cap; i++) {
            q.add(i);
        }

        assertTrue(q.isFull());
    }

    @Test
    public void addTest() throws Exception {
        q = new Queue<>();
        q.add(2);
        assertEquals(1, q.getSize());
        q.add(3);
        assertEquals(2, q.getSize());

        assertEquals(2, (int) q.pop());
        assertEquals(3, (int) q.pop());
    }

    @Test
    public void popTest() throws Exception {
        Integer array[] = new Integer[] {1, 2, 3, 4 ,5};
        q = new Queue<>();

        for (int e : array) {
            q.add(e);
        }

        for (Integer e : array) {
            assertEquals((int) e, (int) q.pop());
        }
    }

}