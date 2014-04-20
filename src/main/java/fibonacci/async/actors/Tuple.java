package fibonacci.async.actors;

import akka.actor.ActorRef;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 4/20/14
 * Time: 10:01 AM
 */
class Tuple {

    final ActorRef requester;
    final PartialResult partialResult;

    Tuple(ActorRef requester, PartialResult partialResult) {
        this.requester = requester;
        this.partialResult = partialResult;
    }
}
