package fibonacci.async;

import fibonacci.async.interfaces.FactorialSolver;
import fibonacci.async.interfaces.Interval;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/15/14
 * Time: 1:27 PM
 */
public class Pool implements AutoCloseable, FactorialSolver {

    private final int thresholdSize;
    private final ExecutorService exec;


    public Pool(int thresholdSize) {
        this.thresholdSize = thresholdSize;
        this.exec = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() + 1);

    }

    @Override
    public Future<BigInteger> factorial(int num) {
        LinkedList<Future<BigInteger>> tasks = new LinkedList<>();
        LinkedList<Interval> intervals = new LinkedList<>();
        Interval.buildIntervals(1, num, intervals, thresholdSize);
        for (Interval interval : intervals) {
            tasks.add(exec.submit(new Worker(interval.from, interval.to)));
        }
        return exec.submit(() -> {
            BigInteger result = BigInteger.ONE;
            for (Future<BigInteger> future : tasks) {
                result = result.multiply(future.get());
            }
            return result;
        });

    }

    @Override
    public void close() throws Exception {
        exec.awaitTermination(1, TimeUnit.SECONDS);
        exec.shutdown();
    }

    public static class Worker implements Callable<BigInteger> {
        final int from, to;

        public Worker(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public BigInteger call() throws Exception {
            return FactorialSolver.computeDirectly(from, to);
        }
    }
}
