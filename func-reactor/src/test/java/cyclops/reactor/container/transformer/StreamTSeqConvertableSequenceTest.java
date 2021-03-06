package cyclops.reactor.container.transformer;


import cyclops.container.foldable.AbstractConvertableSequenceTest;
import cyclops.container.immutable.impl.ConvertableSequence;
import cyclops.monads.AnyMs;
import cyclops.monads.Witness.list;
import cyclops.reactor.stream.FluxReactiveSeq;


public class StreamTSeqConvertableSequenceTest extends AbstractConvertableSequenceTest {

    @Override
    public <T> ConvertableSequence<T> of(T... elements) {

        return AnyMs.liftM(FluxReactiveSeq.of(elements),
                           list.INSTANCE)
                    .to();
    }

    @Override
    public <T> ConvertableSequence<T> empty() {

        return AnyMs.liftM(FluxReactiveSeq.<T>empty(),
                           list.INSTANCE)
                    .to();
    }

}
