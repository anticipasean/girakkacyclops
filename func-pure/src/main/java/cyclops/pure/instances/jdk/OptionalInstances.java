package cyclops.pure.instances.jdk;

import static cyclops.pure.kinds.OptionalKind.narrowK;
import static cyclops.pure.kinds.OptionalKind.widen;

import cyclops.function.higherkinded.DataWitness.optional;
import cyclops.function.higherkinded.Higher;
import cyclops.pure.arrow.MonoidK;
import cyclops.pure.arrow.MonoidKs;
import cyclops.companion.Optionals;
import cyclops.container.control.eager.either.Either;
import cyclops.container.control.eager.option.Option;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.function.combiner.Monoid;
import cyclops.pure.instances.control.OptionInstances;
import cyclops.pure.kinds.OptionalKind;
import cyclops.pure.typeclasses.InstanceDefinitions;
import cyclops.pure.typeclasses.Pure;
import cyclops.pure.typeclasses.comonad.Comonad;
import cyclops.pure.typeclasses.foldable.Foldable;
import cyclops.pure.typeclasses.foldable.Unfoldable;
import cyclops.pure.typeclasses.functor.Functor;
import cyclops.pure.typeclasses.monad.Applicative;
import cyclops.pure.typeclasses.monad.Monad;
import cyclops.pure.typeclasses.monad.MonadPlus;
import cyclops.pure.typeclasses.monad.MonadRec;
import cyclops.pure.typeclasses.monad.MonadZero;
import cyclops.pure.typeclasses.monad.Traverse;
import cyclops.pure.typeclasses.monad.TraverseByTraverse;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Optionals
 *
 * @author johnmccleanP
 */
@UtilityClass
public class OptionalInstances {

    private final OptionTypeclasses INSTANCE = new OptionTypeclasses();

    public static InstanceDefinitions<optional> definitions() {
        return new InstanceDefinitions<optional>() {
            @Override
            public <T, R> Functor<optional> functor() {
                return OptionalInstances.functor();
            }

            @Override
            public <T> Pure<optional> unit() {
                return OptionalInstances.unit();
            }

            @Override
            public <T, R> Applicative<optional> applicative() {
                return OptionalInstances.applicative();
            }

            @Override
            public <T, R> Monad<optional> monad() {
                return OptionalInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<optional>> monadZero() {
                return Option.some(OptionalInstances.monadZero());
            }

            @Override
            public <T> Option<MonadPlus<optional>> monadPlus() {
                return Option.some(OptionalInstances.monadPlus());
            }

            @Override
            public <T> MonadRec<optional> monadRec() {
                return OptionalInstances.monadRec();
            }

            @Override
            public <T> Option<MonadPlus<optional>> monadPlus(MonoidK<optional> m) {
                return Option.some(OptionalInstances.monadPlus(m));
            }

            @Override
            public <C2, T> Traverse<optional> traverse() {
                return OptionalInstances.traverse();
            }

            @Override
            public <T> Foldable<optional> foldable() {
                return OptionalInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<optional>> comonad() {
                return Option.none();
            }

            @Override
            public <T> Option<Unfoldable<optional>> unfoldable() {
                return Option.some(INSTANCE);
            }
        };
    }

    public static <T, R> Functor<optional> functor() {
        return INSTANCE;
    }

    public static <T> Pure<optional> unit() {
        return INSTANCE;
    }

    public static <T, R> Applicative<optional> applicative() {
        return INSTANCE;
    }

    public static <T, R> Monad<optional> monad() {
        return INSTANCE;
    }

    public static <T, R> MonadRec<optional> monadRec() {

        return INSTANCE;
    }

    public static <T, R> MonadZero<optional> monadZero() {

        return INSTANCE;
    }

    public static <T> MonadPlus<optional> monadPlus() {
        return INSTANCE;
    }

    public static <T> MonadPlus<optional> monadPlus(MonoidK<optional> m) {

        return INSTANCE.withMonoidK(m);
    }

    public static <C2, T> Traverse<optional> traverse() {
        return INSTANCE;
    }

    public static <T, R> Foldable<optional> foldable() {
        return INSTANCE;
    }

    @AllArgsConstructor
    @lombok.With
    public static class OptionTypeclasses implements MonadPlus<optional>, MonadRec<optional>, TraverseByTraverse<optional>,
                                                     Foldable<optional>, Unfoldable<optional> {

        private final MonoidK<optional> monoidK;

        public OptionTypeclasses() {
            monoidK = MonoidKs.firstPresentOptional();
        }

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<optional, T> ds) {
            return Option.fromOptional(narrowK(ds))
                         .fold(monoid);
        }


        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<optional, T> ds) {
            return Option.fromOptional(narrowK(ds))
                         .fold(monoid);
        }

        @Override
        public <R, T> Higher<optional, R> unfold(T b,
                                                 Function<? super T, Option<Tuple2<R, T>>> fn) {
            return widen(fn.apply(b)
                           .map(t -> t._1())
                           .toOptional());
        }

        @Override
        public <T> MonoidK<optional> monoid() {
            return monoidK;
        }

        @Override
        public <T, R> Higher<optional, R> flatMap(Function<? super T, ? extends Higher<optional, R>> fn,
                                                  Higher<optional, T> ds) {
            return widen(narrowK(ds).flatMap(t -> narrowK(fn.apply(t))));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<optional, R>> traverseA(Applicative<C2> applicative,
                                                                    Function<? super T, ? extends Higher<C2, R>> fn,
                                                                    Higher<optional, T> ds) {
            Optional<T> opt = narrowK(ds);
            return opt.isPresent() ? applicative.map(OptionalKind::of,
                                                     fn.apply(opt.get())) : applicative.unit(OptionalKind.empty());
        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<optional, T> ds) {
            Option<R> opt = Option.fromOptional(narrowK(ds).map(fn));
            return opt.fold(mb);
        }

        @Override
        public <T, R> Higher<optional, R> ap(Higher<optional, ? extends Function<T, R>> fn,
                                             Higher<optional, T> apply) {
            OptionalKind<? extends Function<T, R>> lt = OptionalKind.narrow(fn);
            OptionalKind<T> list = OptionalKind.narrow(apply);
            return widen(OptionInstances.fromOptionalKind(lt)
                                        .zip(OptionInstances.fromOptionalKind(list),
                                             (a, b) -> a.apply(b))
                                        .toOptional());
        }

        @Override
        public <T> Higher<optional, T> unit(T value) {
            return OptionalKind.of(value);
        }

        @Override
        public <T, R> Higher<optional, R> map(Function<? super T, ? extends R> fn,
                                              Higher<optional, T> ds) {
            return OptionalKind.narrow(ds)
                               .map(fn);
        }

        @Override
        public <T, R> Higher<optional, R> tailRec(T initial,
                                                  Function<? super T, ? extends Higher<optional, ? extends Either<T, R>>> fn) {
            Optional<R> x = Optionals.tailRec(initial,
                                              fn.andThen(a -> narrowK(a)));
            return widen(x);
        }
    }


}

