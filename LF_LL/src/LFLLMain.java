import collections.concurrent.lockfree.LinkedList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LFLLMain {
    private static collections.interfaces.List<Integer> list;
    private static concurrent.Pool pool;

    private static final int[] threadSizes = new int[]{ 1, 2, 4, 8 };
    private static final int[] searchRatio = new int[]{ 1, 4, 9 };
    private static final int testSize = 10000;
    private static List<Integer> numbers;

    public static void main(String[] args) {
        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);

        for (int threadSize : threadSizes) {
            list = new LinkedList<>();
            pool = new concurrent.Pool(threadSize);

            System.out.println(String.format("Test Start with Thread %d", threadSize));
            try {
                testA();
                testB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static long executeWithTime(Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        return System.currentTimeMillis() - start;
    }

    private static boolean assertRatio(int t, int f, int v) {
        return (v % (t + f)) < t;
    }

    static void testA() throws Exception {
        System.out.println(String.format("TestA: Insert %d numbers", numbers.size()));
        long time = executeWithTime(() -> {
            numbers.forEach((e) -> pool.push(() -> list.add(e)));
            pool.join();
        });
        System.out.println(String.format("\tInserting %d numbers takes %dms", numbers.size(), time));
    }


    static void testB() throws Exception {
        long insertTime = executeWithTime(() -> {
            numbers.forEach((e) -> pool.push(() -> list.add(e)));
            pool.join();
        });
        System.out.println(String.format("TestB: Insert and Search %d with ratio", numbers.size()));
        for (int ratio : searchRatio) {
            long time = executeWithTime(() -> {
                numbers.forEach((e) -> {
                    if (assertRatio(1, ratio, e)) pool.push(() -> list.add(e));
                    else pool.push(() -> list.contains(e));
                });
                pool.join();
            });
            System.out.println(String.format("\tInsert and Search ratio 1:%d, %d numbers takes %dms", ratio, numbers.size(), time));
        }
    }
}
