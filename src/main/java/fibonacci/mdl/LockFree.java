package fibonacci.mdl;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 2/4/14
 * Time: 2:47 PM
 */
public class LockFree implements FibonacciGenerator {

    private final FibonacciSeqElem START_VALUE = new FibonacciSeqElem(
            BigInteger.ONE, BigInteger.ONE);

    private static class FibonacciSeqElem {
        private final BigInteger next;
        private final BigInteger curr;

        public FibonacciSeqElem(BigInteger curr, BigInteger next) {
            this.next = next;
            this.curr = curr;
        }

        public BigInteger getNext() {return next;}

        public BigInteger getCurr() {return curr;}
    }

    private final AtomicReference<FibonacciSeqElem> seqElemAtomic = new AtomicReference<>(START_VALUE);

    @Override
    public void clear() {
        seqElemAtomic.set(START_VALUE);
    }

    public BigInteger next() {
        BigInteger nextValue = null;
        while (true) {
            FibonacciSeqElem seqElem = seqElemAtomic.get();
            nextValue = seqElem.getCurr();
            FibonacciSeqElem newSeqElem = new FibonacciSeqElem(
                    seqElem.getNext(), seqElem.getCurr().add(seqElem.getNext()));
            if (seqElemAtomic.compareAndSet(seqElem, newSeqElem)) {
                break;
            }
        }
        return nextValue;
    }
}
