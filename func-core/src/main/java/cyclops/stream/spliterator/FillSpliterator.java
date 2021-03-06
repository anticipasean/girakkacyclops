package cyclops.stream.spliterator;

import java.util.Spliterator;
import java.util.function.Consumer;


public class FillSpliterator<T> implements Spliterator<T> {

    private static final int characteristics = IMMUTABLE & SIZED;
    private final T value;

    public FillSpliterator(T value) {
        super();
        this.value = value;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }


    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {

        action.accept(value);

        return true;

    }

    @Override
    public Spliterator<T> trySplit() {

        return this;
    }


}
