package fibonacci.async;

import fibonacci.async.interfaces.FactorialSolver;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/15/14
 * Time: 10:28 AM
 */
public class ForkJoin implements FactorialSolver {

    final int thresholdSize;

    public ForkJoin(int thresholdSize) {
        this.thresholdSize = thresholdSize;
    }

    @Override
    public Future<BigInteger> factorial(int num) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask task = new ForkJoinTask(1, num, thresholdSize);
        return forkJoinPool.submit(task);
    }

    private static class ForkJoinTask extends RecursiveTask<BigInteger> {

        final int from;
        final int to;
        final int thresholdSize;

        protected ForkJoinTask(int from, int to, int thresholdSize) {
            this.thresholdSize = thresholdSize;
            this.from = from;
            this.to = to;
        }

        @Override
        protected BigInteger compute() {
            if (to - from <= thresholdSize) {
                return FactorialSolver.computeDirectly(from, to);
            } else {
                int mid = (to + from) >>> 1;
                ForkJoinTask left = new ForkJoinTask(from, mid, thresholdSize);
                ForkJoinTask right = new ForkJoinTask(mid + 1, to, thresholdSize);
                left.fork();
                BigInteger rightAns = right.compute();
                BigInteger leftAns = left.join();
                return leftAns.multiply(rightAns);
            }
        }
    }


}
