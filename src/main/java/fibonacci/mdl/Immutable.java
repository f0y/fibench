package fibonacci.mdl;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 6:35 PM
 */
public class Immutable {

    private final BigInteger next;
    // Текущее значение
    public final BigInteger val;

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

}
