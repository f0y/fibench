package fibonacci;

import fibonacci.bench.BarrierTimer;
import fibonacci.bench.FibonacciProducer;
import fibonacci.bench.FibonacciTest;
import fibonacci.mdl.ExplicitLocking;
import fibonacci.mdl.FibonacciGenerator;
import fibonacci.mdl.IntrinsicLocking;
import fibonacci.mdl.LockFree;
import fibonacci.mdl.STM;

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
                add(IntrinsicLocking.class);
                add(ExplicitLocking.class);
                add(STM.class);
                add(LockFree.class);
            }};


    public static void main(String arguments[]) {
        int totalInvocations = 100000;
        System.out.println("Use <executable> <total_invocations>");
        System.out.println("Default value is " + totalInvocations);

        if (arguments.length > 1) {
            System.err.println("Incorrect number of arguments!");
        } else if (arguments.length == 1) {
            totalInvocations = Integer.valueOf(arguments[0]);
        }
        int maxThreads = Runtime.getRuntime().availableProcessors() * 2;
        ArrayList<Integer> threadsAmount = new ArrayList<>();
        for (int nThread = 1; nThread < maxThreads + 1; nThread *= 2)  {
            threadsAmount.add(nThread);
        }
        makeTableHeader(threadsAmount);
        for (Class<? extends FibonacciGenerator> aClass : generatorsUnderTest) {
            try {
                FibonacciGenerator fibonacciGenerator = aClass.newInstance();
                testWith(fibonacciGenerator, totalInvocations, threadsAmount);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void makeTableHeader(ArrayList<Integer> threadsAmount) {
        System.out.println("");
        System.out.print(String.format("%-17s |", "Impl/Threads"));
        for (Integer nThread : threadsAmount) {
            System.out.print(String.format(" %-6s |", nThread));
        }
        System.out.println("");
        System.out.print(String.format("%-17s |", "---"));
        for (Integer _ : threadsAmount) {
            System.out.print(String.format(" %-6s |", "---"));
        }
    }

    public static void testWith(final FibonacciGenerator generator, int totalInvocations, ArrayList<Integer> threadsAmount) {
            System.out.println("");
            System.out.print(String.format("%-17s |", generator.getClass().getSimpleName()));
            for (Integer nThread : threadsAmount) {
                generator.clear();
                int trialsPerThread = totalInvocations / nThread;
                ExecutorService producerPool = Executors.newFixedThreadPool(nThread);
                BarrierTimer barrierTimer = new BarrierTimer();
                CyclicBarrier cyclicBarrier = new CyclicBarrier(nThread + 1, barrierTimer);
                FibonacciProducer fibonacciProducer = new FibonacciProducer(generator, cyclicBarrier, trialsPerThread);
                FibonacciTest fibonacciTest = new FibonacciTest(
                        barrierTimer, nThread, trialsPerThread, producerPool, cyclicBarrier, fibonacciProducer);
                fibonacciTest.test();
                System.out.print(String.format(" %-6s |", fibonacciTest.getThroughput()));
                producerPool.shutdown();
            }

    }


}
