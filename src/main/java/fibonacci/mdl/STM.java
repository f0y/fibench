package fibonacci.mdl;

import akka.stm.Atomic;
import akka.stm.Ref;
import akka.stm.TransactionFactory;
import akka.stm.TransactionFactoryBuilder;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/6/14
 * Time: 12:54 PM
 */
public class STM implements FibonacciGenerator {

    private final Ref<BigInteger> curr = new Ref<>(BigInteger.ONE);
    private final Ref<BigInteger> next = new Ref<>(BigInteger.ONE);

    static {
        Logger.getLogger("org.multiverse.api.GlobalStmInstance").setLevel(Level.OFF);
        Logger.getLogger("org.multiverse.stms.alpha.AlphaStm").setLevel(Level.OFF);
    }

    private final TransactionFactory txFactory = new TransactionFactoryBuilder()
            .setMaxRetries(1_000_000)
            .build();


    @Override
    public BigInteger next() {
        return new Atomic<BigInteger>(txFactory) {
            @Override
            public BigInteger atomically() {
                BigInteger result = curr.get();
                curr.set(next.get());
                next.set(result.add(next.get()));
                return result;
            }
        }.execute();
    }

    @Override
    public void clear() {
       new Atomic(txFactory) {
           @Override
           public Object atomically() {
               next.set(BigInteger.ONE);
               curr.set(BigInteger.ONE);
               return null;
           }
       }.execute();
    }
}
