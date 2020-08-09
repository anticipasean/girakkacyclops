package cyclops.data.talk;

import cyclops.data.LazySeq;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotLazySeqStub<T> {

    public final T head;
    public final Supplier<LazySeq<T>> tail;

}
