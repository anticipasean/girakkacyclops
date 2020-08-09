package cyclops.reactive.collections.persistent;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.oath.cyclops.ReactiveConvertableSequence;
import com.oath.cyclops.data.collections.extensions.FluentCollectionX;
import com.oath.cyclops.types.foldable.Evaluation;
import cyclops.control.Option;
import cyclops.data.tuple.Tuple;
import cyclops.data.tuple.Tuple2;
import cyclops.reactive.Spouts;
import cyclops.reactive.collections.AbstractCollectionXTest;
import cyclops.reactive.collections.immutable.PersistentQueueX;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

public class PQueueXTest extends AbstractCollectionXTest {

    AtomicLong counter = new AtomicLong(0);

    @Before
    public void setup() {

        counter = new AtomicLong(0);
        super.setup();
    }

    @Test
    public void span() {

        assertThat(of(1,
                      2,
                      3,
                      4,
                      1,
                      2,
                      3,
                      4).span(i -> i < 3),
                   equalTo(Tuple.tuple(of(1,
                                          2),
                                       of(3,
                                          4,
                                          1,
                                          2,
                                          3,
                                          4))));
        assertThat(of(1,
                      2,
                      3).span(i -> i < 9),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
        assertThat(of(1,
                      2,
                      3).span(i -> i < 0),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
    }

    @Test
    public void splitBy() {

        assertThat(of(1,
                      2,
                      3,
                      4,
                      1,
                      2,
                      3,
                      4).splitBy(i -> i > 3),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of(4,
                                          1,
                                          2,
                                          3,
                                          4))));
        assertThat(of(1,
                      2,
                      3).splitBy(i -> i < 9),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
        assertThat(of(1,
                      2,
                      3).splitBy(i -> i < 0),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
    }

    @Test
    public void splitAtTest() {
        assertThat(of(1,
                      2,
                      3).splitAt(4),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
        assertThat(of(1,
                      2,
                      3).splitAt(3),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
        assertThat(of(1,
                      2,
                      3).splitAt(2),
                   equalTo(Tuple.tuple(of(1,
                                          2),
                                       of(3))));
        assertThat(of(1,
                      2,
                      3).splitAt(1),
                   equalTo(Tuple.tuple(of(1),
                                       of(2,
                                          3))));
        assertThat(of(1,
                      2,
                      3).splitAt(0),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
        assertThat(of(1,
                      2,
                      3).splitAt(-1),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
    }

    @Test
    public void testPartition() {

        assertEquals(asList(1,
                            3,
                            5),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 != 0)
                          ._1()
                          .toList());
        assertEquals(asList(2,
                            4,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 != 0)
                          ._2()
                          .toList());

        assertEquals(asList(2,
                            4,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 == 0)
                          ._1()
                          .toList());
        assertEquals(asList(1,
                            3,
                            5),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 == 0)
                          ._2()
                          .toList());

        assertEquals(asList(1,
                            2,
                            3),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i <= 3)
                          ._1()
                          .toList());
        assertEquals(asList(4,
                            5,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i <= 3)
                          ._2()
                          .toList());

        assertEquals(asList(1,
                            2,
                            3,
                            4,
                            5,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> true)
                          ._1()
                          .toList());
        assertEquals(asList(),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> true)
                          ._2()
                          .toList());

        assertEquals(asList(),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> false)
                          ._1()
                          .toList());
        assertEquals(asList(1,
                            2,
                            3,
                            4,
                            5,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).splitBy(i -> false)
                          ._1()
                          .toList());
    }

    @Test
    public void asyncTest() throws InterruptedException {
        Spouts.async(Stream.generate(() -> "next"),
                     Executors.newFixedThreadPool(1))
              .onePer(1,
                      TimeUnit.MILLISECONDS)
              .take(1000)
              .to(ReactiveConvertableSequence::converter)
              .persistentQueueX(Evaluation.LAZY)
              .peek(i -> counter.incrementAndGet())
              .materialize();

        long current = counter.get();
        Thread.sleep(400);
        assertTrue(current < counter.get());
    }

    @Override
    public <T> PersistentQueueX<T> of(T... values) {
        return PersistentQueueX.of(values);
    }

    @Test
    public void onEmptySwitch() {
        assertThat(PersistentQueueX.empty()
                                   .onEmptySwitch(() -> PersistentQueueX.of(1,
                                                                            2,
                                                                            3))
                                   .toList(),
                   equalTo(PersistentQueueX.of(1,
                                               2,
                                               3)
                                           .toList()));
    }

    @Test
    public void coflatMap() {
        assertThat(PersistentQueueX.of(1,
                                       2,
                                       3)
                                   .coflatMap(s -> s.sumInt(i -> i))
                                   .singleOrElse(null),
                   equalTo(6));

    }

    /* (non-Javadoc)
     * @see com.oath.cyclops.function.collections.extensions.AbstractCollectionXTest#zero()
     */
    @Override
    public <T> FluentCollectionX<T> empty() {
        return PersistentQueueX.empty();
    }

    @Override
    public FluentCollectionX<Integer> range(int start,
                                            int end) {
        return PersistentQueueX.range(start,
                                      end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start,
                                             long end) {
        return PersistentQueueX.rangeLong(start,
                                          end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times,
                                            T seed,
                                            UnaryOperator<T> fn) {
        return PersistentQueueX.iterate(times,
                                        seed,
                                        fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times,
                                             Supplier<T> fn) {
        return PersistentQueueX.generate(times,
                                         fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed,
                                              Function<? super U, Option<Tuple2<T, U>>> unfolder) {
        return PersistentQueueX.unfold(seed,
                                       unfolder);
    }
}
