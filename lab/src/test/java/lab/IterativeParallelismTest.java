package lab;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class IterativeParallelismTest {
    private static int THREADS_NUMBER = 3;
    private static int LIST_SIZE = 300;
    private List<Integer> testData;
    IterativeParallelism iterativeParallelism = new ParallelCalculator();

    @Before
    public void setUp() throws Exception {
        int[] temp = new int[LIST_SIZE];
        for (int i =0; i < THREADS_NUMBER; i++){
            temp[i * LIST_SIZE/ THREADS_NUMBER ] = 1;
            temp[(i+1) * LIST_SIZE/ THREADS_NUMBER -1] = -1;
        }

        testData = Arrays.stream(temp).boxed().collect(Collectors.toList());
//        testData.forEach(System.out::print);
    }

    @Test
    public void testMinimum() throws Exception {
        Integer expected = -1;
        Long startTime = System.currentTimeMillis();

        assertEquals(expected, iterativeParallelism.minimum(THREADS_NUMBER, testData, Comparator.<Integer>naturalOrder()));
        Long endTime = System.currentTimeMillis();
        System.out.println("Time exec min= " + (endTime - startTime) );
    }

    @Test
    public void testMaximum() throws Exception {
        Integer expected = 1;
        Long startTime = System.currentTimeMillis();

        assertEquals(expected, iterativeParallelism.maximum(THREADS_NUMBER, testData, Comparator.<Integer>naturalOrder()));
        Long endTime = System.currentTimeMillis();
        System.out.println("Time exec max = " + (endTime - startTime) );
    }

    @Test
    public void testAll() throws Exception {
        Long startTime = System.currentTimeMillis();


        assertEquals(false, iterativeParallelism.all(THREADS_NUMBER, testData, arg -> arg > 0));
        Long endTime = System.currentTimeMillis();
        System.out.println("Time exec all = " + (endTime - startTime) );
    }

    @Test
    public void testAny() throws Exception {
        Long startTime = System.currentTimeMillis();
        assertEquals(true, iterativeParallelism.any(THREADS_NUMBER, testData, arg -> arg.equals(0)));
//        assertEquals(false, iterativeParallelism.any(THREADS_NUMBER, testData, arg -> arg.equals(15)));
        Long endTime = System.currentTimeMillis();
        System.out.println("Time exec any = " + (endTime - startTime) );

    }

    @Test
    public void testFilter() throws Exception {
        Long startTime = System.currentTimeMillis();

        Predicate<Integer> predicate = arg -> arg < 0;
        List<Integer> expected = testData.stream().filter(predicate).collect(Collectors.toList());
        assertEquals(expected, iterativeParallelism.filter(THREADS_NUMBER, testData, predicate));
        Long endTime = System.currentTimeMillis();
        System.out.println("Time exec filter = " + (endTime - startTime) );
    }

    @Test
    public void testMap() throws Exception {
        Long startTime = System.currentTimeMillis();

        Function<Integer, Integer> mapToString = i -> i + 10;
        List<Integer> expected = testData.stream().map(i -> i + 10).collect(Collectors.toList());
        assertEquals(expected, iterativeParallelism.map(THREADS_NUMBER, testData, mapToString));
        Long endTime = System.currentTimeMillis();
        System.out.println("Time exec map= " + (endTime - startTime) );
    }
}
