package fibonacci.async;


import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 4/19/14
 * Time: 5:05 PM
 */
public class Interval {

    static int THRESHOLD = 3;
    final int from, to;

    public Interval(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int mid() {
        return (from + to) >>> 1;
    }

    public boolean isTooSmall() {
        return to - from <= THRESHOLD;
    }

    public static Interval to(int num) {
        return new Interval(1, num);
    }

    public BigInteger multiply() {
        BigInteger result = BigInteger.ONE;
        for (int i = from; i <= to; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    List<Interval> split0(Interval interval, List<Interval> list) {
        if (interval.isTooSmall()) {
            list.add(interval);
            return list;
        }
        list.add(new Interval(interval.from, interval.mid()));
        return split0(new Interval(interval.mid() + 1, interval.to), list);
    }

    public List<Interval> split() {
        return split0(this, new LinkedList<>());
    }

}
