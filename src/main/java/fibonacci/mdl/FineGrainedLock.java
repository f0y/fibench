package fibonacci.mdl;

import fibonacci.mdl.interfaces.CurrentValueSupplier;
import fibonacci.mdl.interfaces.StatefulGenerator;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:47 PM
 */
public class FineGrainedLock implements
        StatefulGenerator<BigInteger>,
        CurrentValueSupplier<BigInteger> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private BigInteger curr = BigInteger.ONE;
    private BigInteger next = BigInteger.ONE;

    @Override
    public BigInteger next() {
        BigInteger result;
        writeLock.lock();
        try {
            result = curr;
            curr = next;
            next = result.add(next);
            return result;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            curr = BigInteger.ONE;
            next = BigInteger.ONE;
        } finally {
            writeLock.lock();
        }
    }

    @Override
    public BigInteger val() {
        readLock.lock();
        try {
            return curr;
        } finally {
            readLock.unlock();
        }
    }
}
