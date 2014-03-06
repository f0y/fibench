package fibonacci.bench;

import fibonacci.mdl.FibonacciGenerator;

import java.math.BigInteger;
import java.util.concurrent.CyclicBarrier;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:55 PM
 */
public class FibonacciProducer implements Runnable {

    private final FibonacciGenerator generator;
    private final CyclicBarrier barrier;
    private final long trialsPerThread;

    public FibonacciProducer(FibonacciGenerator generator, CyclicBarrier barrier, long trialsPerThread) {
        this.generator = generator;
        this.barrier = barrier;
        this.trialsPerThread = trialsPerThread;
    }

    @Override
    public void run() {
        try {
            barrier.await();
            for (int i = 0; i < trialsPerThread; i++) {
                BigInteger next = generator.next();

                // Prevent dead code elimination
                if (next.hashCode() == System.nanoTime()) {
                    System.out.print("");
                }
            }
            barrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
