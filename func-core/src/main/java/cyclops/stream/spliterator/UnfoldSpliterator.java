package cyclops.stream.spliterator;

import cyclops.container.control.Option;
import cyclops.container.immutable.tuple.Tuple;
import cyclops.container.immutable.tuple.Tuple2;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;


public class UnfoldSpliterator<T1, T> implements Spliterator<T>, CopyableSpliterator<T> {

    private final T1 in;
    private final Function<? super T1, ? extends Option<Tuple2<T, T1>>> fn;
    private Tuple2<T, T1> current;

    public UnfoldSpliterator(T1 in,
                             Function<? super T1, ? extends Option<Tuple2<T, T1>>> fn) {
        this.in = in;
        this.fn = fn;
        current = Tuple.tuple(null,
                              in);

    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {

        return fn.apply(current._2())
                 .map(result -> {
                     current = result;
                     action.accept(result._1());
                     return result;
                 })
                 .isPresent();

    }


    @Override
    public Spliterator<T> trySplit() {

        return this;
    }


    @Override
    public Spliterator<T> copy() {
        return new UnfoldSpliterator<>(in,
                                       fn);
    }
}
