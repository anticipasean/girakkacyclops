package cyclops.function.combiner;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;


public interface Group<T> extends Monoid<T> {

    static <T> Group<T> fromCurried(UnaryOperator<T> inverse,
                                    final T zero,
                                    final Function<T, Function<T, T>> combiner) {
        return of(inverse,
                  Monoid.of(zero,
                            combiner));

    }

    static <T> Group<T> fromBiFunction(UnaryOperator<T> inverse,
                                       final T zero,
                                       final BiFunction<T, T, T> combiner) {
        return of(inverse,
                  Monoid.fromBiFunction(zero,
                                        combiner));

    }

    static <T> Group<T> of(UnaryOperator<T> inverse,
                           Monoid<T> monoid) {
        return new Group<T>() {


            @Override
            public T invert(T t) {
                return inverse.apply(t);
            }


            @Override
            public T zero() {
                return monoid.zero();
            }

            @Override
            public T apply(T t,
                           T u) {
                return monoid.apply(t,
                                    u);
            }
        };
    }

    T invert(T t);

    default T reduceReverse(final Stream<T> toReduce) {
        return invert(foldLeft(toReduce));
    }
}
