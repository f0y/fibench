package fibonacci.mdl;

import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:47 PM
 */
public class ExplicitLocking implements FibonacciGenerator {

    private final ReentrantLock lock = new ReentrantLock();

    private BigInteger curr = BigInteger.ONE;
    private BigInteger next = BigInteger.ONE;

    @Override
    public BigInteger next() {
        BigInteger result;
        lock.lock();
        try {
            result = curr;
            curr = next;
            next = result.add(next);
            return result;
        } finally {
            lock.unlock();
        }
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
