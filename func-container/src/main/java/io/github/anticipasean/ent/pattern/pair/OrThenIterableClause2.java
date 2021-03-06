package io.github.anticipasean.ent.pattern.pair;


import cyclops.container.control.Option;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.stream.type.Streamable;
import io.github.anticipasean.ent.pattern.Clause;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OrThenIterableClause2<K, V, KI, E, KO, VO> extends Clause<MatchResult2<K, V, KI, Iterable<E>, KO, VO>> {

    static <K, V, KI, E, KO, VO> OrThenIterableClause2<K, V, KI, E, KO, VO> of(Supplier<MatchResult2<K, V, KI, Iterable<E>, KO, VO>> keyValueMatchResultsSupplier) {
        return new OrThenIterableClause2<K, V, KI, E, KO, VO>() {
            @Override
            public MatchResult2<K, V, KI, Iterable<E>, KO, VO> get() {
                return keyValueMatchResultsSupplier.get();
            }
        };
    }

    default OrMatchClause2<K, V, KI, E, KO, VO> then(Function<Tuple2<KI, Streamable<E>>, Tuple2<KO, VO>> mapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().either()
                                                                .mapLeft(Tuple2::_2)
                                                                .mapLeft(kiviOptTuple -> kiviOptTuple.map(kiviTuple -> mapper.apply(kiviTuple.map2(Streamable::fromIterable))))
                                                                .flatMapLeft(kovoOptTuple -> kovoOptTuple.toEither(Tuple2.of(subject().either()
                                                                                                                                      .leftOrElse(null)
                                                                                                                                      ._1(),
                                                                                                                             Option.none())))));
    }

    default OrMatchClause2<K, V, KI, E, KO, VO> then(BiFunction<KI, Streamable<E>, Tuple2<KO, VO>> biMapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().either()
                                                                .mapLeft(Tuple2::_2)
                                                                .mapLeft(kiviOptTuple -> kiviOptTuple.map(kiviTuple -> biMapper.apply(kiviTuple._1(),
                                                                                                                                      Streamable.fromIterable(kiviTuple._2()))))
                                                                .toEither(Tuple2.of(subject().either()
                                                                                             .leftOrElse(null)
                                                                                             ._1(),
                                                                                    Option.none()))));
    }

    default OrMatchClause2<K, V, KI, E, KO, VO> then(Function<KI, KO> keyMapper,
                                                     Function<Streamable<E>, VO> valueMapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().either()
                                                                .mapLeft(Tuple2::_2)
                                                                .mapLeft(kiviOptTuple -> kiviOptTuple.map(kiviTuple -> kiviTuple.bimap(keyMapper,
                                                                                                                                       valueMapper.compose(Streamable::fromIterable))))
                                                                .toEither(Tuple2.of(subject().either()
                                                                                             .leftOrElse(null)
                                                                                             ._1(),
                                                                                    Option.none()))));
    }
}
