package fibonacci.mdl;

import fibonacci.mdl.interfaces.StatefulGenerator;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:47 PM
 */
public class LockFree implements StatefulGenerator<BigInteger> {

    private final Pair START_VALUE = new Pair(BigInteger.ONE, BigInteger.ONE);

    private static class Pair {
        final BigInteger next;
        final BigInteger curr;

        public Pair(BigInteger curr, BigInteger next) {
            this.next = next;
            this.curr = curr;
        }
    }

    private final AtomicReference<Pair> atomic = new AtomicReference<>(START_VALUE);

    @Override
    public void clear() {
        atomic.set(START_VALUE);
    }

    public BigInteger next() {
        BigInteger nextValue = null;
        while (true) {
            Pair pair = atomic.get();
            nextValue = pair.curr;
            Pair newPair = new Pair(pair.next, pair.curr.add(pair.next));
            if (atomic.compareAndSet(pair, newPair)) {
                break;
            }
        }
        return nextValue;
    }
}
