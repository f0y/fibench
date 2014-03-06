package fibonacci.mdl;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/6/14
 * Time: 2:21 PM
 */
public class IntrinsicLocking implements FibonacciGenerator {

    private BigInteger curr = BigInteger.ONE;
    private BigInteger next = BigInteger.ONE;

    @Override
    public synchronized BigInteger next() {
        BigInteger result = curr;
        curr = next;
        next = result.add(next);
        return result;
    }

    @Override
    public synchronized void clear() {
        curr = BigInteger.ONE;
        next = BigInteger.ONE;
    }
}
