package fibonacci.async;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import fibonacci.mdl.interfaces.StatefulGenerator;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Actor implements
        StatefulGenerator<CompletableFuture<BigInteger>>,
        AutoCloseable {

    private final ActorRef producer;
    private final ActorRef consumer;
    private final ActorSystem system;
    private final AtomicLong requestId = new AtomicLong(0);

    final ConcurrentHashMap<Long, CompletableFuture<BigInteger>> results =
            new ConcurrentHashMap<>();

    public Actor(final Consumer<BigInteger> callback) {
        system = ActorSystem.create("ExampleSystem");
        //noinspection Convert2MethodRef
        consumer = system.actorOf(new Props(() -> new UntypedActor() {
            @Override
            public void onReceive(Object message) throws Exception {
                if (message instanceof Producer.Value) {
                    Producer.Value value = (Producer.Value) message;
                    CompletableFuture<BigInteger> future = results.remove(value.id);
                    future.complete(value.raw);
                    callback.accept(value.raw);
                } else {
                    unhandled(message);
                }
            }
        }), "consumer");
        producer = system.actorOf(new Props(Producer.class), "producer");
    }

    @Override
    public CompletableFuture<BigInteger> next() {
        Long id = requestId.getAndIncrement();
        CompletableFuture<BigInteger> result = new CompletableFuture<>();
        results.put(id, result);
        producer.tell(new Producer.GetNext(id), consumer);
        return result;
    }

    @Override
    public void clear() {
        producer.tell(new Producer.Clear());
    }

    @Override
    public void close() throws Exception {
        producer.tell(PoisonPill.getInstance());
        system.shutdown();
    }

    public static class Producer extends UntypedActor {

        private BigInteger curr;
        private BigInteger next;

        public static class GetNext {
            final Long id;

            public GetNext(Long id) {
                this.id = id;
            }
        }

        public static class Clear {
        }

        public static class Value {
            final Long id;
            final BigInteger raw;

            public Value(Long id, BigInteger raw) {
                this.raw = raw;
                this.id = id;
            }
        }

        public Producer() {
            clear();
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof GetNext) {
                GetNext next = (GetNext) message;
                BigInteger result = evalNext();
                Value value = new Value(next.id, result);
                getSender().tell(value, getSelf());
            } else if (message instanceof Clear) {
                clear();
            } else {
                unhandled(message);
            }
        }

        private void clear() {
            curr = BigInteger.ONE;
            next = BigInteger.ONE;
        }

        private BigInteger evalNext() {
            BigInteger result = curr;
            curr = next;
            next = result.add(next);
            return result;
        }
    }
}
