package fibonacci;

import fibonacci.bench.BarrierTimer;
import fibonacci.bench.FibonacciProducer;
import fibonacci.bench.FibonacciTest;
import fibonacci.mdl.BlockingFibonacciGenerator;
import fibonacci.mdl.FibonacciGenerator;
import fibonacci.mdl.LockFreeFibonacciGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/2/14
 * Time: 3:42 PM
 */
public class FibonacciMain {

    private static List<Class<? extends FibonacciGenerator>> generatorsUnderTest =
            new ArrayList<Class<? extends FibonacciGenerator>>() {{
                add(BlockingFibonacciGenerator.class);
                add(LockFreeFibonacciGenerator.class);
            }};


    public static void main(String arguments[]) {
        int totalInvocations = 1_000_000;
        System.out.println("Use <executable> <total_invocations>");
        System.out.println("Default value is " + totalInvocations);

        if (arguments.length > 1) {
            System.err.println("Incorrect number of arguments!");
        } else if (arguments.length == 1) {
            totalInvocations = Integer.valueOf(arguments[0]);
        }
        int maxThreads = Runtime.getRuntime().availableProcessors() * 2;
        System.out.println("Computing fibonacci numbers up to " + totalInvocations + " invocations ");
        for (Class<? extends FibonacciGenerator> aClass : generatorsUnderTest) {
            try {
                FibonacciGenerator fibonacciGenerator = aClass.newInstance();
                testWith(fibonacciGenerator, totalInvocations, maxThreads);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    public static void testWith(final FibonacciGenerator generator, int totalInvocations, int maxThreads) {
            System.out.println("\nTesting " + generator.getClass().getSimpleName());
            System.out.println("thread(s) / throughput (ns/item): ");
            for (int nThread = 1; nThread < maxThreads + 1; nThread *= 2) {
                generator.clear();
                System.out.print(String.format("%2s/", nThread));
                int trialsPerThread = totalInvocations / nThread;
                ExecutorService producerPool = Executors.newFixedThreadPool(nThread);
                BarrierTimer barrierTimer = new BarrierTimer();
                CyclicBarrier cyclicBarrier = new CyclicBarrier(nThread + 1, barrierTimer);
                FibonacciProducer fibonacciProducer = new FibonacciProducer(generator, cyclicBarrier, trialsPerThread);
                FibonacciTest fibonacciTest = new FibonacciTest(
                        barrierTimer, nThread, trialsPerThread, producerPool, cyclicBarrier, fibonacciProducer);
                fibonacciTest.test();
                System.out.print(String.format("%-6s | ", fibonacciTest.getThroughput()));
                producerPool.shutdown();
            }

    }


}
