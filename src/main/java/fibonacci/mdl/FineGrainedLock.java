package fibonacci.mdl;

import fibonacci.mdl.interfaces.FibonacciGenerator;

import java.math.BigInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:47 PM
 */
public class FineGrainedLock implements FibonacciGenerator<BigInteger> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private BigInteger curr = BigInteger.ONE;
    private BigInteger next = BigInteger.ONE;

    @Override
    public BigInteger next() {
        BigInteger result;
        lock.writeLock().lock();
        try {
            // Вход запрещен
            result = curr;
            curr = next;
            next = result.add(next);
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public BigInteger val() {
        lock.readLock().lock();
        try {
            // При отпущенном write lock
            // Допуст`им вход множества потоков на чтение
            return curr;
        } finally {
            lock.readLock().unlock();
        }
    }
}
