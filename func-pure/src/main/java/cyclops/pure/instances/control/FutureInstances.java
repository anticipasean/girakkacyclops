package cyclops.pure.instances.control;

import static cyclops.async.Future.narrowK;

import cyclops.function.higherkinded.DataWitness.future;
import cyclops.function.higherkinded.Higher;
import cyclops.pure.arrow.Cokleisli;
import cyclops.pure.arrow.Kleisli;
import cyclops.pure.arrow.MonoidK;
import cyclops.pure.arrow.MonoidKs;
import cyclops.container.control.Either;
import cyclops.async.Future;
import cyclops.container.control.Option;
import cyclops.function.combiner.Monoid;
import cyclops.pure.container.functional.Active;
import cyclops.pure.container.functional.Coproduct;
import cyclops.pure.container.functional.Nested;
import cyclops.pure.container.functional.Product;
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
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Futures
 *
 * @author johnmcclean
 */
@UtilityClass
public class FutureInstances {

    private final FutureTypeclasses INSTANCE = new FutureTypeclasses();

    public static <T> Kleisli<future, Future<T>, T> kindKleisli() {
        return Kleisli.of(FutureInstances.monad(),
                          Future::widen);
    }

    public static <T> Cokleisli<future, T, Future<T>> kindCokleisli() {
        return Cokleisli.of(Future::narrowK);
    }

    public static <W1, T> Nested<future, W1, T> nested(Future<Higher<W1, T>> nested,
                                                       InstanceDefinitions<W1> def2) {
        return Nested.of(nested,
                         FutureInstances.definitions(),
                         def2);
    }

    public static <W1, T> Product<future, W1, T> product(Future<T> f,
                                                         Active<W1, T> active) {
        return Product.of(allTypeclasses(f),
                          active);
    }

    public static <W1, T> Coproduct<W1, future, T> coproduct(Future<T> f,
                                                             InstanceDefinitions<W1> def2) {
        return Coproduct.right(f,
                               def2,
                               FutureInstances.definitions());
    }

    public static <T> Active<future, T> allTypeclasses(Future<T> f) {
        return Active.of(f,
                         FutureInstances.definitions());
    }

    public static <W2, R, T> Nested<future, W2, R> mapM(Future<T> f,
                                                        Function<? super T, ? extends Higher<W2, R>> fn,
                                                        InstanceDefinitions<W2> defs) {
        return Nested.of(f.map(fn),
                         FutureInstances.definitions(),
                         defs);
    }

    public static InstanceDefinitions<future> definitions() {
        return new InstanceDefinitions<future>() {
            @Override
            public <T, R> Functor<future> functor() {
                return FutureInstances.functor();
            }

            @Override
            public <T> Pure<future> unit() {
                return FutureInstances.unit();
            }

            @Override
            public <T, R> Applicative<future> applicative() {
                return FutureInstances.applicative();
            }

            @Override
            public <T, R> Monad<future> monad() {
                return FutureInstances.monad();
            }

            @Override
            public <T, R> Option<MonadZero<future>> monadZero() {
                return Option.some(FutureInstances.monadZero());
            }

            @Override
            public <T> Option<MonadPlus<future>> monadPlus() {
                return Option.some(FutureInstances.monadPlus());
            }

            @Override
            public <T> MonadRec<future> monadRec() {
                return FutureInstances.monadRec();
            }

            @Override
            public <T> Option<MonadPlus<future>> monadPlus(MonoidK<future> m) {
                return Option.some(FutureInstances.monadPlus(m));
            }

            @Override
            public <C2, T> Traverse<future> traverse() {
                return FutureInstances.traverse();
            }

            @Override
            public <T> Foldable<future> foldable() {
                return FutureInstances.foldable();
            }

            @Override
            public <T> Option<Comonad<future>> comonad() {
                return Option.none();
            }

            @Override
            public <T> Option<Unfoldable<future>> unfoldable() {
                return Option.none();
            }
        };
    }

    public static <T, R> Functor<future> functor() {
        return INSTANCE;
    }

    public static <T> Pure<future> unit() {
        return INSTANCE;
    }

    public static <T, R> Applicative<future> applicative() {
        return INSTANCE;
    }

    public static <T, R> Monad<future> monad() {
        return INSTANCE;
    }

    public static <T, R> MonadZero<future> monadZero() {
        return INSTANCE;
    }

    public static <T, R> MonadRec<future> monadRec() {
        return INSTANCE;
    }

    public static <T> MonadPlus<future> monadPlus() {
        return INSTANCE;
    }

    public static <T> MonadPlus<future> monadPlus(MonoidK<future> m) {
        return INSTANCE;
    }

    public static <L> Traverse<future> traverse() {
        return INSTANCE;
    }

    public static <L> Foldable<future> foldable() {
        return INSTANCE;
    }

    @AllArgsConstructor
    @lombok.With
    public static class FutureTypeclasses implements MonadPlus<future>, MonadRec<future>, TraverseByTraverse<future>,
                                                     Foldable<future> {

        private final MonoidK<future> monoidK;

        public FutureTypeclasses() {
            monoidK = MonoidKs.firstSuccessfulFuture();
        }

        @Override
        public <T> T foldRight(Monoid<T> monoid,
                               Higher<future, T> ds) {
            return narrowK(ds).fold(monoid);
        }


        @Override
        public <T> T foldLeft(Monoid<T> monoid,
                              Higher<future, T> ds) {
            return narrowK(ds).fold(monoid);
        }


        @Override
        public <T, R> Higher<future, R> flatMap(Function<? super T, ? extends Higher<future, R>> fn,
                                                Higher<future, T> ds) {
            return narrowK(ds).flatMap(t -> narrowK(fn.apply(t)));
        }

        @Override
        public <C2, T, R> Higher<C2, Higher<future, R>> traverseA(Applicative<C2> applicative,
                                                                  Function<? super T, ? extends Higher<C2, R>> fn,
                                                                  Higher<future, T> ds) {
            Future<T> future = narrowK(ds);
            return future.fold(right -> applicative.map(m -> Future.ofResult(m),
                                                        fn.apply(right)),
                               left -> applicative.unit(Future.ofError(left)));
        }

        @Override
        public <T, R> R foldMap(Monoid<R> mb,
                                Function<? super T, ? extends R> fn,
                                Higher<future, T> ds) {
            Future<R> opt = narrowK(ds).map(fn);
            return opt.fold(mb);
        }

        @Override
        public <T, R> Higher<future, R> ap(Higher<future, ? extends Function<T, R>> fn,
                                           Higher<future, T> apply) {
            return narrowK(apply).zip(narrowK(fn),
                                      (a, b) -> b.apply(a));
        }

        @Override
        public <T> Higher<future, T> unit(T value) {
            return Future.ofResult(value);
        }

        @Override
        public <T, R> Higher<future, R> map(Function<? super T, ? extends R> fn,
                                            Higher<future, T> ds) {
            return narrowK(ds).map(fn);
        }

        @Override
        public <T, R> Higher<future, R> tailRec(T initial,
                                                Function<? super T, ? extends Higher<future, ? extends Either<T, R>>> fn) {
            return Future.tailRec(initial,
                                  t -> narrowK(fn.apply(t)));
        }


        @Override
        public <T> MonoidK<future> monoid() {
            return monoidK;
        }
    }


}
