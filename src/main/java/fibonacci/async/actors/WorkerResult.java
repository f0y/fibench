package fibonacci.async.actors;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 4/20/14
 * Time: 10:00 AM
 */
class WorkerResult {
    final String id;
    final BigInteger val;

    WorkerResult(String id, BigInteger val) {
        this.id = id;
        this.val = val;
    }
}
