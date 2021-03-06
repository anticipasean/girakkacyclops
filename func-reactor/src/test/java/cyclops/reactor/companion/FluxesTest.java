package cyclops.reactor.companion;

import static cyclops.reactor.stream.FluxReactiveSeq.reactiveSeq;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.container.immutable.tuple.Tuple;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.monads.AnyM;
import cyclops.monads.AnyMs;
import cyclops.monads.Witness;
import cyclops.monads.Witness.optional;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.reactor.container.higherkinded.FluxAnyM;
import cyclops.reactor.container.transformer.StreamT;
import cyclops.reactor.stream.impl.FluxReactiveSeqImpl;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxesTest {

    @Test
    public void fluxTest() {
        Flux.just(2)
            .single()
            .block();
    }

    @Test
    public void fluxifyTest() {

        StreamT<optional, Integer> streamT = AnyMs.liftM(ReactiveSeq.of(1,
                                                                        2,
                                                                        3),
                                                         optional.INSTANCE);
        StreamT<optional, Integer> fluxes = FluxAnyM.fluxify(streamT);
        AnyM<optional, Stream<Integer>> anyM = fluxes.unwrap();
        Optional<Stream<Integer>> opt = Witness.optional(anyM);
        Stream<Integer> stream = opt.get();
        assertTrue(stream instanceof FluxReactiveSeqImpl);
        FluxReactiveSeqImpl<Integer> f = (FluxReactiveSeqImpl) stream;
        assertTrue(f.getFlux() instanceof Flux);
    }

    @Test
    public void fluxComp() {

        Flux<Tuple2<Integer, Integer>> stream = Fluxs.forEach(Flux.range(1,
                                                                         10),
                                                              i -> Flux.range(i,
                                                                              10),
                                                              Tuple::tuple);
        Flux<Integer> result = Fluxs.forEach(Flux.just(10,
                                                       20),
                                             a -> Flux.just(a + 10),
                                             (a, b) -> a + b);
        assertThat(result.collectList()
                         .block(),
                   equalTo(ListX.of(30,
                                    50)));
    }

    @Test
    public void tupleGen() {
        Fluxs.forEach(Flux.range(1,
                                 10),
                      i -> Flux.range(i,
                                      10),
                      Tuple::tuple)
             .subscribe(System.out::println);
    }

    @Test
    public void tupleGenFilter() {
        Fluxs.forEach(Flux.range(1,
                                 10),
                      i -> Flux.range(i,
                                      10),
                      (a, b) -> a > 2 && b < 10,
                      Tuple::tuple)
             .subscribe(System.out::println);
    }

    @Test
    public void asyncList() {

        AtomicBoolean complete = new AtomicBoolean(false);

        Flux<Integer> async = Flux.just(1,
                                        2,
                                        3)
                                  .publishOn(Schedulers.single())
                                  .map(i -> {
                                      try {
                                          Thread.sleep(5000);
                                      } catch (InterruptedException e) {

                                      }
                                      return i;
                                  })
                                  .doOnComplete(() -> complete.set(true));

        ListX<Integer> asyncList = ListX.listX(reactiveSeq(async))
                                        .map(i -> i + 1);

        System.out.println("Triggering list population!");
        asyncList.materialize();

        System.out.println("Were we blocked? Has the stream completed? " + complete.get());

        System.out.println("First value is " + asyncList.get(0));

        System.out.println("Completed? " + complete.get());


    }


}
