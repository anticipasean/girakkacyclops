package cyclops.pure.transformers;


import cyclops.function.higherkinded.Higher;
import cyclops.pure.container.functional.Nested;
import java.util.function.Function;

public interface Transformer<W1, W2, T> {

    <R> Nested<W1, W2, R> flatMap(Function<? super T, ? extends Nested<W1, W2, R>> fn);

    <R> Nested<W1, W2, R> flatMapK(Function<? super T, ? extends Higher<W1, Higher<W2, R>>> fn);
}
