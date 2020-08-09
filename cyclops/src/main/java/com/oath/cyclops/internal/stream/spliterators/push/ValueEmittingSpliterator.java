package com.oath.cyclops.internal.stream.spliterators.push;

import cyclops.reactive.ReactiveSeq;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class ValueEmittingSpliterator<T> extends AbstractSpliterator<T> {

    AtomicReference<T> value = new AtomicReference<T>(null);
    boolean emitted = false;
    public ValueEmittingSpliterator(long est,
                                    int additionalCharacteristics,
                                    ReactiveSeq<T> seq) {
        super(est,
              additionalCharacteristics & Spliterator.ORDERED);
        seq.forEach(e -> value.set(e));
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (emitted) {
            return false;
        }
        T local = null;
        while ((local = value.get()) == null) {
            LockSupport.parkNanos(0l);//spin until a value is present
        }
        action.accept(local);
        emitted = true;
        return false;
    }


}
