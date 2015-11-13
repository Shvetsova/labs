package lab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ParallelCalculator implements IterativeParallelism {
    @Override
    public <T> T minimum(int threads, final List<T> list, Comparator<T> comparator) {
        if (list == null || list.isEmpty()) return null;
        if (threads < 1 || comparator == null) throw new IllegalArgumentException();

        BiFunction<T, T, T> bf = (arg1, arg2) -> comparator.compare(arg1, arg2) < 0 ? arg1 : arg2;
        final List<T> results = new ArrayList<>(threads);

        for (int i = 0; i < threads; i++){
            int startPosition = i * list.size() / threads;
            int endPosition = ((i + 1) * list.size() / threads) - 1;
            results.add(null);
            new Thread(new Worker<T, T>(i, results, list, startPosition, endPosition, bf, list.get(startPosition))).start();
        }
        int processedThreads = 0;
        while (processedThreads != threads) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processedThreads = 0;
            for (T r : results){
                if (r != null) processedThreads++;
            }
        }
        T min = results.get(0);
        for (T r : results) {
            min = bf.apply(min, r);
        }

        return min;
    }

    @Override
    public <T> T maximum(int threads, final List<T> list, Comparator<T> comparator) {
        return minimum(threads, list, comparator.reversed());
    }

    @Override
    public <T> boolean all(int threads, List<T> list, Predicate<T> predicate) {
        return checkIfExist(threads,list,predicate, (arg1, arg2) -> arg1 && arg2, true);
    }

    @Override
    public <T> boolean any(int threads, List<T> list, Predicate<T> predicate) {
        return checkIfExist(threads,list,predicate, (arg1, arg2) -> arg1 || arg2, false);
    }

    private <T> boolean checkIfExist(int threads, List<T> list, Predicate<T> predicate, BiFunction<Boolean, Boolean, Boolean> condition, boolean startValue) {
        if (threads < 1 || predicate == null) throw new IllegalArgumentException();

        BiFunction<Boolean, T, Boolean> bf = (arg1, arg2) -> condition.apply(predicate.test(arg2), arg1);
        final List<Boolean> results = new ArrayList<>(threads);

        for (int i = 0; i < threads; i++){
            int startPosition = i * list.size() / threads;
            int endPosition = ((i + 1) * list.size() / threads) - 1;
            results.add(null);
            new Thread(new Worker<T,Boolean>(i, results, list, startPosition, endPosition, bf, startValue)).start();
        }
        int processedThreads = 0;
        while (processedThreads != threads) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processedThreads = 0;
            for (Boolean r : results){
                if (r != null) processedThreads++;
            }
        }
        Boolean result = results.get(0);
        for (Boolean r : results) {
            result = condition.apply(result, r);
        }

        return result;
    }

    @Override
    public <T> List<T> filter(int threads, List<T> list, Predicate<T> predicate) {
        if (threads < 1 || predicate == null) throw new IllegalArgumentException();

        BiFunction<List<Boolean>, T, List<Boolean>> bf = (arg1, arg2) -> {
            arg1.add(predicate.test(arg2));
            return arg1;
        };

        final List<List<Boolean>> results = new ArrayList<>(threads);

        for (int i = 0; i < threads; i++){
            int startPosition = i * list.size() / threads;
            int endPosition = ((i + 1) * list.size() / threads) - 1;
            List<Boolean> startValue = new ArrayList<>();
            results.add(startValue);
            new Thread(new Worker<T,List<Boolean>>(i, results, list, startPosition, endPosition, bf, startValue)).start();
        }
        int processedElements = 0;
        while (processedElements != list.size()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processedElements = 0;
            for (List r : results){
                if (r != null) processedElements += r.size();
            }
        }
        List<Boolean> checkResult = new ArrayList<>(list.size());
        for (List<Boolean> r : results) {
            checkResult.addAll(r);
        }
        List<T> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (checkResult.get(i)) result.add(list.get(i));
        }

        return result;
    }

    @Override
    public <T, R> List<R> map(int threads, List<T> list, Function<T, R> function) {
        if (threads < 1 || function == null) throw new IllegalArgumentException();

        BiFunction<List<R>, T, List<R>> bf = (arg1, arg2) -> {
            arg1.add(function.apply(arg2));
            return arg1;
        };
        final List<List<R>> results = new ArrayList<>(threads);

        for (int i = 0; i < threads; i++){
            int startPosition = i * list.size() / threads;
            int endPosition = ((i + 1) * list.size() / threads) - 1;
            List<R> startValue = new ArrayList<>();
            results.add(startValue);
            new Thread(new Worker<T,List<R>>(i, results, list, startPosition, endPosition, bf, startValue)).start();
        }
        int processedElements = 0;
        while (processedElements != list.size()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processedElements = 0;
            for (List r : results){
                if (r != null) processedElements += r.size();
            }
        }
        List<R> result = new ArrayList<>();
        for (List<R> r : results) {
            result.addAll(r);
        }

        return result;
    }

    private static class Worker<T, R> implements Runnable {
        final int number;
        final List<R> results;
        final List<T>  data;
        final int startPosition;
        final int endPosition;
        final BiFunction<R, T, R>  bf;
        final R startValue;

        private Worker(int number, List<R> results, List<T> data, int startPosition, int endPosition, BiFunction<R, T, R> bf, R startValue) {
            this.number = number;
            this.results = results;
            this.data = data;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.bf = bf;
            this.startValue = startValue;
        }

        @Override
        public void run() {
            R result = startValue;
            for (int i = startPosition; i <= endPosition; i++){
                result = bf.apply(result, data.get(i));
            }
            results.set(number, result);
        }
    }

}
