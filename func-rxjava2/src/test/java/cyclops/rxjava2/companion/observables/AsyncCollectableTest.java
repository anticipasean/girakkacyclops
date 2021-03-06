package cyclops.rxjava2.companion.observables;


import cyclops.rxjava2.companion.Observables;
import cyclops.rxjava2.adapter.ObservableReactiveSeq;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.companion.Spouts;


public class AsyncCollectableTest extends CollectableTest {


    public <T> ReactiveSeq<T> of(T... values) {

        ReactiveSeq<T> seq = Spouts.<T>async(s -> {
            Thread t = new Thread(() -> {
                for (T next : values) {
                    s.onNext(next);
                }
                s.onComplete();
            });
            t.start();
        });

        return ObservableReactiveSeq.reactiveSeq(Observables.observableFrom(seq));
    }

}
