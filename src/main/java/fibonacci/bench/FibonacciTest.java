package fibonacci.bench;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 3:19 PM
 */
public class FibonacciTest {

    private final BarrierTimer timer;
    private final long nThread;
    private final long trialsPerThread;
    private final ExecutorService pool;
    private final CyclicBarrier barrier;
    private final FibonacciProducer producer;

    public FibonacciTest(BarrierTimer timer, long nThread, long trialsPerThread, ExecutorService pool, CyclicBarrier barrier, FibonacciProducer producer) {
        this.timer = timer;
        this.nThread = nThread;
        this.trialsPerThread = trialsPerThread;
        this.pool = pool;
        this.barrier = barrier;
        this.producer = producer;
    }

    public void test() {
        try {
            timer.clear();
            for (int i = 0; i < nThread; i++) {
                pool.execute(producer);
            }
            barrier.await();
            barrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public long getThroughput() {
        return timer.getTime() / (nThread * trialsPerThread);
    }
}
