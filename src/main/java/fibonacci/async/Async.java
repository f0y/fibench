package fibonacci.async;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 10:34 PM
 */
public class Async {

    Future<BigInteger> factorial(int num) {
        return Executors.newSingleThreadExecutor().submit(
                () -> Interval.to(num).multiply());
    }

    static void processSequentially(List<Integer> nums)
            throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<BigInteger>> results = new LinkedList<>();
        for (Integer num : nums) {
            results.add(executor.submit(() -> Interval.to(num).multiply()));
        }
        for (Future<BigInteger> result : results) {
            // Результаты выводятся в порядке занесения на исполнение
            System.out.println(result.get());
        }

    }

    static void processAsSoonAsReady(List<Integer> nums)
            throws InterruptedException, ExecutionException {
        CompletionService<BigInteger> service = new ExecutorCompletionService<>(
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        for (Integer num : nums) {
            service.submit(() -> Interval.to(num).multiply());
        }
        for (Integer ignored : nums) {
            // Результаты выводятся в порядке завершения
            System.out.println(service.take().get());
        }
    }

}
