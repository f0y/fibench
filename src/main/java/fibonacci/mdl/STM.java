package fibonacci.mdl;

import akka.stm.Atomic;
import akka.stm.Ref;
import akka.stm.TransactionFactory;
import akka.stm.TransactionFactoryBuilder;
import fibonacci.mdl.interfaces.FibonacciGenerator;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/6/14
 * Time: 12:54 PM
 */
public class STM implements FibonacciGenerator<BigInteger> {

    // Оборачиваем переменные в транзакционные ссылки
    // система будет отслеживать изменения этих переменных внутри транзакции
    private final Ref<BigInteger> curr = new Ref<>(BigInteger.ONE);
    private final Ref<BigInteger> next = new Ref<>(BigInteger.ONE);

    static {
        Logger.getLogger("org.multiverse.api.GlobalStmInstance").setLevel(Level.OFF);
        Logger.getLogger("org.multiverse.stms.alpha.AlphaStm").setLevel(Level.OFF);
    }

    private final TransactionFactory txFactory = new TransactionFactoryBuilder()
            .setMaxRetries(1_000_000)
            .build();


    @Override
    public BigInteger next() {
        // Создаем транзакцию
        return new Atomic<BigInteger>(txFactory) {
            // Изменения внутри метода
            // будут обладают АСI (https://en.wikipedia.org/wiki/ACID)
            @Override
            public BigInteger atomically() {
                // Все значения отслеживаемых переменных согласованы
                BigInteger result = curr.get();
                // Изменения не видны другим потокам
                curr.set(next.get());
                next.set(result.add(next.get()));
                // Проверяется были ли изменения над отслеживаемыми
                // переменными. Если да, то нас опередили, но мы
                // оптимистичны и повторяем транзакцию еще раз.
                // Если мы первые, то атомарно записываем новые значения.
                return result;
            }
            // и выполняем ее
        }.execute();
    }

    @Override
    public BigInteger val() {
        // Транзакция создается неявно
        return curr.get();
    }

}
