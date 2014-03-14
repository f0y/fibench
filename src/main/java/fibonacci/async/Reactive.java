package fibonacci.async;

import fibonacci.mdl.LockFree;
import fibonacci.mdl.interfaces.StatefulGenerator;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 10:34 PM
 */
public class Reactive implements
        StatefulGenerator<CompletableFuture<BigInteger>> {

    private StatefulGenerator<BigInteger> generator = new LockFree();

    @Override
    public CompletableFuture<BigInteger> next() {
        return CompletableFuture.supplyAsync(generator::next);
    }

    @Override
    public void clear() {
        generator.clear();
    }
}
