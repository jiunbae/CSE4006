import collections.concurrent.lockfree.LinkedList;
import collections.interfaces.List;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {


    private static List<Integer> list;
    private static concurrent.Pool pool;

    private static final int testSize = 10000;
    private static java.util.List<Integer> numbers;

    public static void main(String[] args) {
        list = new LinkedList<>();

        numbers = IntStream.range(0, testSize).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);

        pool = new concurrent.Pool(4);

        numbers.forEach((e) -> pool.push(() -> list.add(e)));
        pool.join();
        System.out.println("Done");
    }

}
