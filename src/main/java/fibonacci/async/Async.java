package fibonacci.async;

import fibonacci.async.interfaces.FactorialSolver;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 10:34 PM
 */
public class Async implements FactorialSolver {

    static Future<BigInteger> computeAsync(int from, int to) {
        return CompletableFuture.supplyAsync(() ->
                FactorialSolver.computeDirectly(from, to));
    }

    public Future<BigInteger> factorial(int num) {
        return computeAsync(1, num);

    }

}
