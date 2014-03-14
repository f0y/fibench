package fibonacci.mdl;

import fibonacci.mdl.interfaces.CurrentValueSupplier;
import fibonacci.mdl.interfaces.Generator;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 6:35 PM
 */
public class Immutable implements
        Generator<Immutable>,
        CurrentValueSupplier<BigInteger> {

    private final BigInteger next;
    private final BigInteger val;

    private Immutable(BigInteger next, BigInteger val) {
        this.next = next;
        this.val = val;
    }

    public Immutable next() {
        return new Immutable(val.add(next), next);
    }

    public static Immutable first() {
        return new Immutable(BigInteger.ONE, BigInteger.ONE);
    }

    @Override
    public BigInteger val() {
        return val;
    }
}
