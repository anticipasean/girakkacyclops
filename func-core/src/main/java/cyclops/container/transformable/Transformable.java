package cyclops.container.transformable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An interface that represents a type that can transform a value from one type to another
 *
 * @param <T> Data type of element(s) stored in this Transformable
 * @author johnmcclean
 */
@FunctionalInterface
public interface Transformable<T> {


    /**
     * Transform this functor using the supplied transformation function
     *
     * <pre>
     * {@code
     *
     *
     *    of(1,2,3).map(i->i*2)
     *
     *    //[2,4,6]
     *
     * }
     * </pre>
     *
     * @param fn Transformation function
     * @return Transformed Transformable
     */
    <R> Transformable<R> map(Function<? super T, ? extends R> fn);

    /**
     * Peek at the current value of this Transformable, without transforming it
     *
     * <pre>
     * {@code
     *
     *
     *    of(1,2,3).map(System.out::println)
     *
     *    1
     *    2
     *    3
     *
     * }
     * </pre>
     *
     * @param c Consumer that recieves each element from this Transformable
     * @return Transformable that will peek at each value
     */
    default Transformable<T> peek(final Consumer<? super T> c) {
        return map(input -> {
            c.accept(input);
            return input;
        });
    }


}
