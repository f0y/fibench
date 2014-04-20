package fibonacci.async.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import fibonacci.async.Interval;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

class MessagePassing {

    final ActorRef aggregator;
    final ActorSystem system;

    MessagePassing() {
        system = ActorSystem.create();
        aggregator = system.actorOf(new Props(Aggregator::new));
    }

    Future<BigInteger> factorial(int num) {
        CompletableFuture<BigInteger> future = new CompletableFuture<>();
        ActorRef completor = system.actorOf(new Props(() -> new Completor(future)));
        aggregator.tell(new Solve(num), completor);
        return future;
    }

    static class Completor extends UntypedActor {

        final CompletableFuture<BigInteger> future;

        Completor(CompletableFuture<BigInteger> future) {
            this.future = future;
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof FinalResult) {
                future.complete(((FinalResult) message).val);
            } else {
                unhandled(message);
            }
        }
    }

    static class Aggregator extends UntypedActor {

        final ActorRef router = this.getContext().
                actorOf(new Props(Worker.class).withRouter(
                        new RoundRobinRouter(4)));
        final Map<String, Tuple> awaiting = new HashMap<>();


        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Solve) {
                final String id = UUID.randomUUID().toString();
                List<Interval> intervals = Interval.to(((Solve) message).num).split();
                awaiting.put(id,
                        new Tuple(getSender(), PartialResult.init(intervals.size())));
                for (Interval interval : intervals) {
                    router.tell(new Compute(id, interval), getSelf());
                }
            } else if (message instanceof WorkerResult) {
                WorkerResult workerResult = (WorkerResult) message;
                Tuple tuple = awaiting.get(workerResult.id);
                PartialResult newResult = tuple.partialResult.add(workerResult);
                if (newResult.isCompleted()) {
                    awaiting.remove(workerResult.id).requester.tell(
                            new FinalResult(newResult.val), getSelf());
                } else {
                    awaiting.put(workerResult.id, new Tuple(tuple.requester, newResult));
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static class Worker extends UntypedActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Compute) {
                Compute compute = (Compute) message;
                BigInteger result = compute.interval.multiply();
                getSender().tell(new WorkerResult(compute.id, result), getSelf());
            } else {
                unhandled(message);
            }
        }

    }

}
