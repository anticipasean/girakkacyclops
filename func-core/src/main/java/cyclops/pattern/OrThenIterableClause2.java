package cyclops.pattern;


import cyclops.container.control.Option;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.reactive.ReactiveSeq;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author smccarron
 */
public interface OrThenIterableClause2<K, V, KI, E, KO, VO> extends Clause<MatchResult2<K, V, KI, Iterable<E>, KO, VO>> {

    static <K, V, KI, E, KO, VO> OrThenIterableClause2<K, V, KI, E, KO, VO> of(Supplier<MatchResult2<K, V, KI, Iterable<E>, KO, VO>> keyValueMatchResultsSupplier) {
        return new OrThenIterableClause2<K, V, KI, E, KO, VO>() {
            @Override
            public MatchResult2<K, V, KI, Iterable<E>, KO, VO> get() {
                return keyValueMatchResultsSupplier.get();
            }
        };
    }

    default OrMatchClause2<K, V, KI, E, KO, VO> then(Function<Tuple2<KI, ReactiveSeq<E>>, Tuple2<KO, VO>> mapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().either()
                                                                .mapLeft(Tuple2::_2)
                                                                .mapLeft(kiviOptTuple -> kiviOptTuple.map(kiviTuple -> mapper.apply(kiviTuple.map2(ReactiveSeq::fromIterable))))
                                                                .flatMapLeft(kovoOptTuple -> kovoOptTuple.toEither(Tuple2.of(subject().either()
                                                                                                                                      .leftOrElse(null)
                                                                                                                                      ._1(),
                                                                                                                             Option.none())))));
    }

    default OrMatchClause2<K, V, KI, E, KO, VO> then(BiFunction<KI, ReactiveSeq<E>, Tuple2<KO, VO>> biMapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().either()
                                                                .mapLeft(Tuple2::_2)
                                                                .mapLeft(kiviOptTuple -> kiviOptTuple.map(kiviTuple -> biMapper.apply(kiviTuple._1(),
                                                                                                                                      ReactiveSeq.fromIterable(kiviTuple._2()))))
                                                                .toEither(Tuple2.of(subject().either()
                                                                                             .leftOrElse(null)
                                                                                             ._1(),
                                                                                    Option.none()))));
    }

    default OrMatchClause2<K, V, KI, E, KO, VO> then(Function<KI, KO> keyMapper,
                                                     Function<ReactiveSeq<E>, VO> valueMapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().either()
                                                                .mapLeft(Tuple2::_2)
                                                                .mapLeft(kiviOptTuple -> kiviOptTuple.map(kiviTuple -> kiviTuple.bimap(keyMapper,
                                                                                                                                       valueMapper.compose(ReactiveSeq::fromIterable))))
                                                                .toEither(Tuple2.of(subject().either()
                                                                                             .leftOrElse(null)
                                                                                             ._1(),
                                                                                    Option.none()))));
    }
}
