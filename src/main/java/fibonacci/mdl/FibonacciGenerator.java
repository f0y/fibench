package fibonacci.mdl;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:54 PM
 */
public interface FibonacciGenerator {
    BigInteger next();

    void clear();
}
