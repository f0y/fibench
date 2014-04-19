package fibonacci.async;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import fibonacci.async.interfaces.FactorialSolver;
import fibonacci.async.interfaces.Interval;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Actor implements FactorialSolver, AutoCloseable {

    private final ActorRef aggregator;
    private final ActorSystem system;

    public Actor(int thresholdSize) {
        system = ActorSystem.create("ExampleSystem");
        aggregator = system.actorOf(
                new Props(() -> new Aggregator(thresholdSize)), "aggregator");
    }


    @Override
    public void close() throws Exception {
        aggregator.tell(PoisonPill.getInstance());
        system.shutdown();
    }

    @Override
    public Future<BigInteger> factorial(int num) {
        CompletableFuture<BigInteger> result = new CompletableFuture<>();
        ActorRef completor = system.actorOf(new Props(() ->
                new Completor(result)));
        aggregator.tell(new Aggregator.Solve(num), completor);
        return result;

    }

    public static class Completor extends UntypedActor {

        public static class FinalResult {
            public final BigInteger val;
            public FinalResult(BigInteger val) {
                this.val = val;
            }
        }

        private final CompletableFuture<BigInteger> result;

        public Completor(CompletableFuture<BigInteger> result) {
            this.result = result;
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof FinalResult) {
                FinalResult finalResult = (FinalResult) message;
                result.complete(finalResult.val);
            } else {
                unhandled(message);
            }
        }
    }

    public static class Aggregator extends UntypedActor {

        private static final int NWORKERS = Runtime.getRuntime().availableProcessors();
        private final ActorRef workerRouter;
        private final Map<String, Tuple> responses = new HashMap<>();
        private final int thresholdSize;

        public static class Tuple {
            public final ActorRef sender;
            public final PartialResult partialResult;

            public Tuple(ActorRef sender, PartialResult partialResult) {
                this.sender = sender;
                this.partialResult = partialResult;
            }
        }

        public static class PartialResult {
            public final BigInteger val;
            public final int nMessages;
            public final int maxMessages;

            public PartialResult(BigInteger val, int nMessages, int maxMessages) {
                this.val = val;
                this.nMessages = nMessages;
                this.maxMessages = maxMessages;
            }

            public PartialResult add(Worker.Result res) {
                return new PartialResult(
                        val.multiply(res.val), nMessages + 1, maxMessages);
            }

            public boolean isCompleted() {
                return nMessages == maxMessages;
            }

        }

        public static class Solve {
            public final int num;
            public Solve(int num) {
                this.num = num;
            }
        }

        public Aggregator(int thresholdSize) {
            this.thresholdSize = thresholdSize;
            workerRouter = this.getContext().actorOf(
                    new Props(Worker.class).withRouter(
                            new RoundRobinRouter(NWORKERS)), "router");
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Solve) {
                Solve solve = (Solve) message;
                final String id = UUID.randomUUID().toString();
                LinkedList<Interval> intervals = new LinkedList<>();
                Interval.buildIntervals(1, solve.num, intervals, thresholdSize);
                responses.put(id, new Tuple(
                        getSender(), new PartialResult(
                                BigInteger.ONE, 0, intervals.size())));
                for (Interval interval : intervals) {
                    workerRouter.tell(new Worker.Compute(
                            id, interval.from, interval.to),
                            getSelf());
                }
            } else if (message instanceof Worker.Result) {
                Worker.Result result = (Worker.Result) message;
                Tuple tuple = responses.get(result.id);
                PartialResult newResult = tuple.partialResult.add(result);
                if (newResult.isCompleted()) {
                    responses.remove(result.id).sender.tell(
                            new Completor.FinalResult(newResult.val),
                            getSelf());
                } else {
                    responses.put(result.id, new Tuple(tuple.sender, newResult));
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static class Worker extends UntypedActor {

        public static class Compute {
            public final String id;
            public final int from;
            public final int to;

            public Compute(String id, int from, int to) {
                this.id = id;
                this.from = from;
                this.to = to;
            }
        }

        public static class Result {
            public final String id;
            public final BigInteger val;

            public Result(String id, BigInteger val) {
                this.id = id;
                this.val = val;
            }
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Compute) {
                Compute compute = (Compute) message;
                BigInteger result = FactorialSolver.computeDirectly
                        (compute.from, compute.to);
                getSender().tell(new Result(compute.id, result), getSelf());
            } else {
                unhandled(message);
            }
        }

    }
}
