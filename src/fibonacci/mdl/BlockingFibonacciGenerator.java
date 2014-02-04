package fibonacci.mdl;

import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:47 PM
 */
public class BlockingFibonacciGenerator implements FibonacciGenerator {

    private final ReentrantLock lock = new ReentrantLock();

    private BigInteger curr = BigInteger.ONE;
    private BigInteger next = BigInteger.ONE;

    @Override
    public BigInteger next() {
        BigInteger result;
        BigInteger newNext;
        lock.lock();
        try {
            result = curr;
            newNext = curr.add(next);
            curr = next;
            next = newNext;
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            curr = BigInteger.ONE;
            next = BigInteger.ONE;
        } finally {
            lock.unlock();
        }
    }
}
