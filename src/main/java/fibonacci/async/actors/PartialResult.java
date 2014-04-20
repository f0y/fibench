package fibonacci.async.actors;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 4/20/14
 * Time: 10:00 AM
 */
class PartialResult {
    final BigInteger val;
    final int nMessages;
    final int maxMessages;

    private PartialResult(BigInteger val, int nMessages, int maxMessages) {
        this.val = val;
        this.nMessages = nMessages;
        this.maxMessages = maxMessages;
    }

    static PartialResult init(int maxMessages) {
        return new PartialResult(BigInteger.ONE, 0, maxMessages);
    }

    PartialResult add(WorkerResult res) {
        return new PartialResult(val.multiply(res.val), nMessages + 1, maxMessages);
    }

    boolean isCompleted() {
        return nMessages == maxMessages;
    }

}
