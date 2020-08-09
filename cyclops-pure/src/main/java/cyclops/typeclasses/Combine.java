package cyclops.typeclasses;

import com.oath.cyclops.hkt.Higher;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public interface Combine<CRE> {


    default <T> Higher<CRE, T> plus(Higher<CRE, T> identity,
                                    BinaryOperator<Higher<CRE, T>> accumulator,
                                    Higher<CRE, T>... tocombine) {
        return Stream.of(tocombine)
                     .reduce(identity,
                             accumulator);
    }

    default <T> Higher<CRE, T> plus(Higher<CRE, T> identity,
                                    BinaryOperator<Higher<CRE, T>> accumulator,
                                    Stream<Higher<CRE, T>> tocombine) {
        return tocombine.reduce(identity,
                                accumulator);
    }
}
