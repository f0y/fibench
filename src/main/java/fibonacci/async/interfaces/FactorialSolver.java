package fibonacci.async.interfaces;

import java.math.BigInteger;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/15/14
 * Time: 2:54 PM
 */
public interface FactorialSolver {

    Future<BigInteger> factorial(int num);

    static BigInteger computeDirectly(int from, int to) {
        BigInteger result = BigInteger.ONE;
        for (int i = from; i <= to; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

}
