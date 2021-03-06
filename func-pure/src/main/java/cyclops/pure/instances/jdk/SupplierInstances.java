package cyclops.pure.instances.jdk;

import cyclops.function.higherkinded.DataWitness.supplier;
import cyclops.function.higherkinded.Higher;
import cyclops.pure.kinds.SupplierKind;
import cyclops.pure.typeclasses.functor.Functor;
import java.util.function.Function;

public class SupplierInstances {


    public static final Functor<supplier> functor = new Functor<supplier>() {
        @Override
        public <T, R> SupplierKind<R> map(Function<? super T, ? extends R> f,
                                          Higher<supplier, T> fa) {
            return ((SupplierKind<T>) fa).mapFn(f);
        }
    };
}
