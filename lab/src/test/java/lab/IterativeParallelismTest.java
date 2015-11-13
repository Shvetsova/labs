package lab;

import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class IterativeParallelismTest {
    private static int THREADS_NUMBER = 4;
    private static int LIST_SIZE = 1000;
    private List<Integer> testData;

    @Before
    public void setUp() throws Exception {
        Random random = new Random();
        testData = random.ints(LIST_SIZE, 4, 10).boxed().collect(Collectors.toList());
    }

    @Test
    public void testMinimum() throws Exception {
        Integer expected = Integer.MIN_VALUE;
        testData.set(LIST_SIZE / 3, expected);
        IterativeParallelism ip = new ParallelCalculator();
        assertEquals(expected, ip.minimum(THREADS_NUMBER, testData, Comparator.<Integer>naturalOrder()));
    }

    @Test
    public void testMaximum() throws Exception {
        Integer expected = Integer.MAX_VALUE;
        testData.set(LIST_SIZE / 3, expected);
        IterativeParallelism ip = new ParallelCalculator();
        assertEquals(expected, ip.maximum(THREADS_NUMBER, testData, Comparator.<Integer>naturalOrder()));
    }

    @Test
    public void testAll() throws Exception {
        IterativeParallelism ip = new ParallelCalculator();
        Predicate predicate = arg -> arg instanceof Number;
        assertEquals(true, ip.all(THREADS_NUMBER, testData, predicate));
    }

    @Test
    public void testAny() throws Exception {
        Integer maxValue = Integer.MAX_VALUE;
        testData.set(LIST_SIZE / 3, maxValue);
        IterativeParallelism ip = new ParallelCalculator();
        Predicate<Integer> isMaxValue = arg -> maxValue.equals(arg);
        assertEquals(true, ip.any(THREADS_NUMBER, testData, isMaxValue));
        Integer unexistedValue = 7777;
        Predicate<Integer> isUnexistedValue = arg -> unexistedValue.equals(arg);
        assertEquals(false, ip.any(THREADS_NUMBER, testData, isUnexistedValue));
    }

    @Test
    public void testFilter() throws Exception {
        IterativeParallelism ip = new ParallelCalculator();
        Predicate<Integer> predicate = arg -> arg.equals(5) || arg.equals(6);
        List<Integer> expected = testData.stream().filter(predicate).collect(Collectors.toList());
        assertEquals(expected, ip.filter(THREADS_NUMBER, testData, predicate));
    }

    @Test
    public void testMap() throws Exception {
        IterativeParallelism ip = new ParallelCalculator();
        Function<Integer, String> mapToString = arg -> arg.toString();
        List<String> expected = testData.stream().map(mapToString).collect(Collectors.toList());
        assertEquals(expected, ip.map(THREADS_NUMBER, testData, mapToString));
    }
}