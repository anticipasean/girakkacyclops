package cyclops.function.higherkinded;

import cyclops.async.Future;
import cyclops.container.control.Option;
import cyclops.function.higherkinded.DataWitness.future;
import cyclops.function.higherkinded.DataWitness.option;
import java.util.function.Function;


@FunctionalInterface
public interface NaturalTransformation<W1, W2> {

    static <T> NaturalTransformation<T, T> identity() {
        return new NaturalTransformation<T, T>() {
            @Override
            public <T1> Higher<T, T1> apply(Higher<T, T1> a) {
                return a;
            }
        };
    }

    <T> Higher<W2, T> apply(Higher<W1, T> a);

    default <T> Function<Higher<W1, T>, Higher<W2, T>> asFunction() {
        return this::apply;
    }

    default <W3> NaturalTransformation<W1, W3> andThen(NaturalTransformation<W2, W3> after) {
        return new NaturalTransformation<W1, W3>() {
            @Override
            public <T> Higher<W3, T> apply(Higher<W1, T> a) {
                return after.apply(NaturalTransformation.this.apply(a));
            }
        };
    }

    default <W3> NaturalTransformation<W3, W2> compose(NaturalTransformation<W3, W1> before) {
        return new NaturalTransformation<W3, W2>() {
            @Override
            public <T> Higher<W2, T> apply(Higher<W3, T> a) {
                return NaturalTransformation.this.apply(before.apply(a));
            }
        };
    }

    class NatEx implements NaturalTransformation<future, option> {

        @Override
        public <T> Option<T> apply(Higher<future, T> a) {
            return a.convert(Future::narrowK)
                    .to(Option::fromPublisher);
        }
    }
}
