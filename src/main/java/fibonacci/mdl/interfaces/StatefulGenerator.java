package fibonacci.mdl.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: kandaurov
 * Date: 3/14/14
 * Time: 7:53 PM
 */
public interface StatefulGenerator<T> extends Generator<T> {
    void clear();
}
