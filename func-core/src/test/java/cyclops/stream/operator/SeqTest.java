package cyclops.stream.operator;

import static cyclops.container.immutable.tuple.Tuple.tuple;
import static org.junit.Assert.assertTrue;

import cyclops.container.immutable.tuple.Tuple2;
import cyclops.reactive.ReactiveSeq;
import java.util.Arrays;
import java.util.function.Supplier;
import org.junit.Test;

public class SeqTest {

    @Test
    public void testUnzipWithLimits() {

        Supplier<ReactiveSeq<Tuple2<Integer, String>>> s = () -> ReactiveSeq.of(tuple(1,
                                                                                      "a"),
                                                                                tuple(2,
                                                                                      "b"),
                                                                                tuple(3,
                                                                                      "c"));

        Tuple2<ReactiveSeq<Integer>, ReactiveSeq<String>> u1 = ReactiveSeq.unzip(s.get());

        assertTrue(u1._1()
                     .limit(2)
                     .toList()
                     .containsAll(Arrays.asList(1,
                                                2)));

        assertTrue(u1._2()
                     .toList()
                     .containsAll(Arrays.asList("a",
                                                "b",
                                                "c")));

    }
}
