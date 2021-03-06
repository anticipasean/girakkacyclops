package cyclops.rxjava2;

import static cyclops.rxjava2.adapter.ObservableReactiveSeq.reactiveSeq;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


import com.oath.cyclops.anym.AnyMSeq;
import cyclops.reactive.collection.container.ReactiveConvertableSequence;
import cyclops.rxjava2.companion.Observables;
import cyclops.rxjava2.container.higherkinded.ObservableAnyM;
import cyclops.rxjava2.container.higherkinded.Rx2Witness.observable;
import cyclops.rxjava2.container.companion.FlowableCollections;
import cyclops.rxjava2.adapter.FlowableReactiveSeq;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.companion.Spouts;
import cyclops.reactive.collection.container.mutable.ListX;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.Test;

public class RxTest {

    @Test
    public void observableTest() {
        Observable.just(1)
                  .singleOrError()
                  .blockingGet();
    }

    @Test
    public void asyncList() {

        AtomicBoolean complete = new AtomicBoolean(false);

        Observable<Integer> async = Observables.fromStream(Spouts.async(Stream.of(100,
                                                                                  200,
                                                                                  300),
                                                                        Executors.newFixedThreadPool(1)))
                                               .doOnComplete(() -> complete.set(true));

        ListX<Integer> asyncList = ListX.listX(reactiveSeq(async))
                                        .map(i -> i + 1);

        System.out.println("Blocked? " + complete.get());

        System.out.println("First value is " + asyncList.get(0));

        System.out.println("Blocked? " + complete.get());


    }

    @Test
    public void asyncFlowableList() {

        AtomicBoolean complete = new AtomicBoolean(false);

        Flowable<Integer> async = Flowable.fromPublisher(Spouts.reactive(Stream.of(100,
                                                                                   200,
                                                                                   300),
                                                                         Executors.newFixedThreadPool(1)))
                                          .map(i -> {
                                              Thread.sleep(5000);
                                              return i;
                                          })
                                          .doOnComplete(() -> complete.set(true));

        ListX<Integer> asyncList = ListX.listX(FlowableReactiveSeq.reactiveSeq(async))
                                        .map(i -> i + 1);

        System.out.println("Calling materialize!");
        asyncList.materialize();
        System.out.println("Blocked? " + complete.get());

        System.out.println("First value is " + asyncList.get(0));

        System.out.println("Blocked? " + complete.get());


    }

    @Test
    public void asyncFlowableList2() {

        AtomicBoolean complete = new AtomicBoolean(false);

        Flowable<Integer> async = Flowable.fromPublisher(Spouts.reactive(Stream.of(100,
                                                                                   200,
                                                                                   300),
                                                                         Executors.newFixedThreadPool(1)))
                                          .map(i -> {
                                              Thread.sleep(100);
                                              return i;
                                          })
                                          .doOnComplete(() -> complete.set(true));

        System.out.println("Initializing!");
        ListX<Integer> asyncList = FlowableCollections.listX(async)
                                                      .map(i -> i + 1);

        System.out.println("Blocked? " + complete.get());

        System.out.println("First value is " + asyncList.get(0));

        System.out.println("Blocked? " + complete.get());


    }

    @Test
    public void anyMAsync() {

        AtomicBoolean complete = new AtomicBoolean(false);

        ReactiveSeq<Integer> asyncSeq = Spouts.async(Stream.of(1,
                                                               2,
                                                               3),
                                                     Executors.newFixedThreadPool(1));
        Observable<Integer> observableAsync = Observables.observableFrom(asyncSeq);
        AnyMSeq<observable, Integer> monad = Observables.anyM(observableAsync);

        monad.map(i -> i * 2)
             .forEach(System.out::println,
                      System.err::println,
                      () -> complete.set(true));

        System.out.println("Blocked? " + complete.get());
        while (!complete.get()) {
            Thread.yield();
        }

        Observable<Integer> converted = ObservableAnyM.raw(monad);
    }

    @Test
    public void observable() {
        assertThat(Observables.anyM(Observable.just(1,
                                                    2,
                                                    3))
                              .to(ReactiveConvertableSequence::converter)
                              .listX(),
                   equalTo(ListX.of(1,
                                    2,
                                    3)));
    }

    @Test
    public void observableFlatMap() {
        assertThat(Observables.anyM(Observable.just(1,
                                                    2,
                                                    3))
                              .flatMap(a -> Observables.anyM(Observable.just(a + 10)))
                              .to(ReactiveConvertableSequence::converter)
                              .listX(),
                   equalTo(ListX.of(11,
                                    12,
                                    13)));
    }


    @Test
    public void observableComp() {
        Observable<Integer> result = Observables.forEach(Observable.just(10,
                                                                         20),
                                                         a -> Observable.<Integer>just(a + 10),
                                                         (a, b) -> a + b);

        assertThat(result.toList()
                         .blockingGet(),
                   equalTo(ListX.of(30,
                                    50)));

    }


}
