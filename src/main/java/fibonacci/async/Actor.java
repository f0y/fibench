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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class Actor implements FactorialSolver, AutoCloseable {

    private final ActorRef aggregator;
    private final ActorSystem system;

    private final Map<String, CompletableFuture<BigInteger>> requests =
            new ConcurrentHashMap<>();

    public Actor(int thresholdSize) {
        system = ActorSystem.create("ExampleSystem");
        aggregator = system.actorOf(
                new Props(() -> new Aggregator(requests, thresholdSize)), "aggregator");
    }


    @Override
    public void close() throws Exception {
        aggregator.tell(PoisonPill.getInstance());
        system.shutdown();
    }

    @Override
    public Future<BigInteger> factorial(int num) {
        CompletableFuture<BigInteger> result = new CompletableFuture<>();
        String id = UUID.randomUUID().toString();
        requests.put(id, result);
        aggregator.tell(new Aggregator.Solve(id, num));
        return result;

    }

    public static class Aggregator extends UntypedActor {

        private static final int NWORKERS = Runtime.getRuntime().availableProcessors();
        private final ActorRef workerRouter;
        private final Map<String, PartialResult> responses = new HashMap<>();
        private final Map<String, CompletableFuture<BigInteger>> requests;
        private final int thresholdSize;


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

            public final String id;
            public final int num;

            public Solve(String id, int num) {
                this.id = id;
                this.num = num;
            }
        }

        public Aggregator(Map<String, CompletableFuture<BigInteger>> requests,
                          int thresholdSize) {
            this.thresholdSize = thresholdSize;
            this.requests = requests;
            workerRouter = this.getContext().actorOf(
                    new Props(Worker.class).withRouter(new RoundRobinRouter(NWORKERS)),
                    "router");
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Solve) {
                Solve solve = (Solve) message;
                LinkedList<Interval> intervals = new LinkedList<>();
                Interval.buildIntervals(1, solve.num, intervals, thresholdSize);
                responses.put(solve.id,
                        new PartialResult(BigInteger.ONE, 0, intervals.size()));
                for (Interval interval : intervals) {
                    workerRouter.tell(new Worker.Compute(
                            solve.id, interval.from, interval.to),
                            getSelf());
                }
            } else if (message instanceof Worker.Result) {
                Worker.Result result = (Worker.Result) message;
                PartialResult partialResult = responses.get(result.id);
                PartialResult newResult = partialResult.add(result);
                if (newResult.isCompleted()) {
                    responses.remove(result.id);
                    requests.remove(result.id).complete(newResult.val);
                } else {
                    responses.put(result.id, newResult);
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
