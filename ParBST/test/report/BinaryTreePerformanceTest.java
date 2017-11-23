package report;

import collections.concurrent.BinaryTree;
import collections.interfaces.Tree;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BinaryTreePerformanceTest {
    private Tree<Integer> tree;
    private thread.Pool pool;

    private int threadSize;
    private int[] searchRatio;
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
        pool = new thread.Pool(threadSize);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1}, {2}, {4}, {8}, {16}
        });
    }

    public BinaryTreePerformanceTest(int threadSize) {
        this.threadSize = threadSize;
        this.searchRatio = new int[]{ 1, 4, 9 };
    }

    private static long executeWithTime(Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        return System.currentTimeMillis() - start;
    }

    private static boolean assertRatio(int t, int f, int v) {
        return (v % (t + f)) < t;
    }

    @Test
    public void testA() throws Exception {
        long time = executeWithTime(() -> {
            numbers.forEach((e) -> pool.push(() -> tree.insert(e)));
            pool.join();
        });

        assertEquals(numbers.size(), tree.size());
    }

    @Test
    public void testB() throws Exception {
        long insertTime = executeWithTime(() -> {
            numbers.forEach((e) -> pool.push(() -> tree.insert(e)));
            pool.join();
        });

        assertEquals(numbers.size(), tree.size());

        long[] result = new long[this.searchRatio.length];
        for (int i = 0; i < searchRatio.length; ++i) {
            final int ratio = searchRatio[i];
            result[i] = executeWithTime(() -> {
                numbers.forEach((e) -> {
                    if (assertRatio(1, ratio, e)) pool.push(() -> tree.insert(e));
                    else pool.push(() -> tree.search(e));
                });
                pool.join();
            });
            System.out.println(result[i]);
        }
    }
}
