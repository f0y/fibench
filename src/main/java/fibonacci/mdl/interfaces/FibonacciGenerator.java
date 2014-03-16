package fibonacci.mdl.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 7:49 PM
 */
public interface FibonacciGenerator<T> {
    /**
     * Следующее сгенерированное значение
     */
    T next();

    /**
     * Текущее значение в генераторе
     */
    public T val();
}
