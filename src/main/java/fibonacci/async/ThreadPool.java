package fibonacci.async;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/15/14
 * Time: 1:27 PM
 */
class ThreadPool {

    Future<BigInteger> factorial(int num) {
        CompletionService<BigInteger> completionService = new ExecutorCompletionService<>(
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1));
        List<Interval> intervals = Interval.to(num).split();
        for (Interval interval : intervals) {
            completionService.submit(interval::multiply);
        }
        return Executors.newSingleThreadExecutor().submit(() -> {
            BigInteger result = BigInteger.ONE;
            for (Interval ignored : intervals) {
                result = result.multiply(completionService.take().get());
            }
            return result;
        });

    }

}
