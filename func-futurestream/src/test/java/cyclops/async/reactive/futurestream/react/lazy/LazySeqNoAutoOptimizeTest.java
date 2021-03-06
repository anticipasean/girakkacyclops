package cyclops.async.reactive.futurestream.react.lazy;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import cyclops.async.reactive.futurestream.FutureStream;
import cyclops.async.reactive.futurestream.LazyReact;
import cyclops.pure.reactive.ReactiveSeq;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;

public class LazySeqNoAutoOptimizeTest extends LazySeqTest {

    @Override
    protected <U> FutureStream<U> of(U... array) {
        return new LazyReact().of(array);
    }

    @Override
    protected <U> FutureStream<U> ofThread(U... array) {
        return new LazyReact().of(array);
    }

    @Override
    protected <U> FutureStream<U> react(Supplier<U>... array) {
        return new LazyReact().ofAsync(array);
    }

    @Override
    public void testSkipUntilWithNullsInclusive() {
        Supplier<FutureStream<Integer>> s = () -> of(1,
                                                     2,
                                                     null,
                                                     3,
                                                     4,
                                                     5);
        List<Integer> list = s.get()
                              .dropUntilInclusive(i -> true)
                              .toList();
        assertTrue("values " + list,
                   list.size() == 5);
    }

    @Test
    public void testSkipUntilInclusive() {
        Supplier<ReactiveSeq<Integer>> s = () -> of(1,
                                                    2,
                                                    3,
                                                    4,
                                                    5);

        assertEquals(asList(),
                     s.get()
                      .dropUntil(i -> false)
                      .toList());
        assertTrue(s.get()
                    .dropUntilInclusive(i -> true)
                    .toList()
                    .size() == 4);
    }

}
