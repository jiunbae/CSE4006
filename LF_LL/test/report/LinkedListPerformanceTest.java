package report;

import collections.concurrent.lockfree.LinkedList;
import collections.interfaces.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class LinkedListPerformanceTest {
    private List<Integer> list;
    private concurrent.Pool pool;

    private int threadSize;
    private int[] searchRatio;
    private static final int testSize = 100000;
    private static java.util.List<Integer> numbers;

    @BeforeClass
    public static void init() throws Exception {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
    }

    @Before
    public void makeInstance() throws Exception {
        list = new LinkedList<>();

        pool = new concurrent.Pool(threadSize);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1}, {2}, {4}, {8}
        });
    }

    public LinkedListPerformanceTest(int threadSize) {
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
            numbers.forEach((e) -> pool.push(() -> list.add(e)));
            pool.join();
        });
        System.out.println(String.format("Inserting %d numbers takes %dms", numbers.size(), time));
    }

    @Test
    public void testB() throws Exception {
        long insertTime = executeWithTime(() -> {
            numbers.forEach((e) -> pool.push(() -> list.add(e)));
            pool.join();
        });

        System.out.println(String.format("Inserting %d numbers takes %dms", numbers.size(), insertTime));

        Random random = new Random();
        java.util.List<Integer> insertNumbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        for (int i = 0; i < searchRatio.length; ++i) {
            final int ratio = searchRatio[i];
            int finalI = i;
            long time = executeWithTime(() -> {
                numbers.forEach((e) -> {
                    if (assertRatio(1, ratio, random.nextInt(ratio + 1)))
                        pool.push(() -> list.add(insertNumbers.get(finalI)));
                    else pool.push(() -> list.contains(e));
                });
                pool.join();
            });
            System.out.println(String.format("Insert and Search ratio 1:%d, %d numbers takes %dms", searchRatio[i], numbers.size(), time));
        }
    }
}
