package fibonacci.async.interfaces;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/15/14
 * Time: 6:31 PM
 */
public class Interval {
    public final int from, to;

    public Interval(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public static void buildIntervals(int from,
                                      int to,
                                      List<Interval> intervals,
                                      final int thresholdSize) {
        if (to - from <= thresholdSize) {
            intervals.add(new Interval(from, to));
            return;
        }
        int mid = (from + to) >>> 1;
        intervals.add(new Interval(from, mid));
        buildIntervals(mid + 1, to, intervals, thresholdSize);
    }
}
