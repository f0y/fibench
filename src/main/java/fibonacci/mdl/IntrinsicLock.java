package fibonacci.mdl;

import fibonacci.mdl.interfaces.FibonacciGenerator;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/6/14
 * Time: 2:21 PM
 */
public class IntrinsicLock implements FibonacciGenerator<BigInteger> {

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
    public synchronized BigInteger val() {
        return curr;
    }

}
