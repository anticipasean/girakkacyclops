package cyclops.reactor.container.higherkinded;

import com.oath.cyclops.anym.AnyMValue;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.monads.XorM;
import cyclops.reactor.container.MonoT;
import cyclops.reactor.container.higherkinded.ReactorWitness.mono;
import reactor.core.publisher.Mono;

public interface MonoAnyM {

    static <W1 extends WitnessType<W1>, T> XorM<W1, mono, T> xorM(Mono<T> type) {
        return XorM.right(anyM(type));
    }

    static <T> Mono<T> raw(AnyM<mono, T> anyM) {
        return ReactorWitness.mono(anyM);
    }

    /**
     * Construct an AnyM type from a Mono. This allows the Mono to be manipulated according to a standard interface along with a
     * vast array of other Java Monad implementations
     *
     * <pre>
     * {@code
     *
     *    AnyMSeq<Integer> mono = Fluxs.anyM(Mono.just(1,2,3));
     *    AnyMSeq<Integer> transformedMono = myGenericOperation(mono);
     *
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     *
     * @param mono To wrap inside an AnyM
     * @return AnyMSeq wrapping a Mono
     */
    static <T> AnyMValue<ReactorWitness.mono, T> anyM(Mono<T> mono) {
        return AnyM.ofValue(mono,
                            ReactorWitness.mono.INSTANCE);
    }

    static <W extends WitnessType<W>, T> MonoT<W, T> liftM(AnyM<W, Mono<T>> nested) {
        return MonoT.of(nested);
    }

    static <T, W extends WitnessType<W>> MonoT<W, T> liftM(Mono<T> opt,
                                                           W witness) {
        return MonoT.of(witness.adapter()
                               .unit(opt));
    }
}
