package fibonacci.async.actors;

import fibonacci.async.Interval;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 4/20/14
 * Time: 10:00 AM
 */
class Compute {
    final String id;
    final Interval interval;

    Compute(String id, Interval interval) {
        this.id = id;
        this.interval = interval;
    }
}
