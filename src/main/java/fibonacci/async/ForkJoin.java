package fibonacci.async;

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
class ForkJoin {

    Future<BigInteger> factorial(int num) {
        return new ForkJoinPool().submit(new ForkJoinTask(Interval.to(num)));
    }

    static class ForkJoinTask extends RecursiveTask<BigInteger> {

        final Interval interval;

        ForkJoinTask(Interval interval) {
            this.interval = interval;
        }

        @Override
        protected BigInteger compute() {
            if (interval.isTooSmall()) {
                return interval.multiply();
            } else {
                ForkJoinTask left = new ForkJoinTask(new Interval(interval.from, interval.mid()));
                ForkJoinTask right = new ForkJoinTask(new Interval(interval.mid() + 1, interval.to));
                left.fork();
                BigInteger rightAns = right.compute();
                BigInteger leftAns = left.join();
                return leftAns.multiply(rightAns);
            }
        }
    }


}
