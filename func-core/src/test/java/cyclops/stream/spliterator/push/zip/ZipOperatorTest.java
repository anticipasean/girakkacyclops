package cyclops.stream.spliterator.push.zip;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.stream.spliterator.push.AbstractOperatorTest;
import cyclops.stream.spliterator.push.ArrayOfValuesOperator;
import cyclops.stream.spliterator.push.Fixtures;
import cyclops.stream.spliterator.push.Operator;
import cyclops.stream.spliterator.push.ZippingOperator;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.companion.Spouts;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.EmitterProcessor;

/**
 * Created by johnmcclean on 17/01/2017.
 */
public class ZipOperatorTest extends AbstractOperatorTest {


    public Operator<Integer> createEmpty() {
        return new ZippingOperator<Integer, Integer, Integer>(new ArrayOfValuesOperator<>(),
                                                              new ArrayOfValuesOperator<>(),
                                                              (a, b) -> a + b);
    }

    public Operator<Integer> createOne() {
        return new ZippingOperator<Integer, Integer, Integer>(new ArrayOfValuesOperator<>(1),
                                                              new ArrayOfValuesOperator<>(2),
                                                              (a, b) -> a + b);
    }

    public Operator<Integer> createThree() {
        return new ZippingOperator<Integer, Integer, Integer>(new ArrayOfValuesOperator<>(1,
                                                                                          2,
                                                                                          3),
                                                              new ArrayOfValuesOperator<>(10,
                                                                                          11,
                                                                                          12),
                                                              (a, b) -> a + b);
    }

    public Operator<Integer> createTwoAndError() {
        return new ZippingOperator<Integer, Integer, Integer>(Fixtures.twoAndErrorSource,
                                                              new ArrayOfValuesOperator<>(10,
                                                                                          11,
                                                                                          12),
                                                              (a, b) -> a + b);

    }

    public Operator<Integer> createThreeErrors() {
        return new ZippingOperator<Integer, Integer, Integer>(new ArrayOfValuesOperator<>(10,
                                                                                          11,
                                                                                          12),
                                                              Fixtures.threeErrorsSource,
                                                              (a, b) -> a + b);

    }

    @Test
    public void subscribeThreeErrors() throws Exception {
        Subscription sub = threeErrors.subscribe(values::add,
                                                 errors::add,
                                                 () -> onComplete = true);
        sub.request(1l);
        assertThat(values.size(),
                   equalTo(0));
        assertThat(errors.size(),
                   equalTo(3));
        assertTrue(onComplete);

    }

    @Test
    public void reactorTest() {

        EmitterProcessor<Integer> source1 = EmitterProcessor.create();
        EmitterProcessor<Integer> source2 = EmitterProcessor.create();
        ReactiveSeq<Integer> zippedFlux = Spouts.from(source1)
                                                .zip((t1, t2) -> t1 + t2,
                                                     source2);
        AtomicReference<Integer> tap = new AtomicReference<>();
        zippedFlux.forEachAsync(it -> tap.set(it));

        source1.onNext(1);
        source2.onNext(2);
        source2.onNext(3);
        source2.onNext(4);

        assertThat(tap.get(),
                   equalTo(3));

        source2.onNext(5);
        source1.onNext(6);

        assertThat(tap.get(),
                   equalTo(9));
    }


}
