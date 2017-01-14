package com.aol.cyclops2.internal.stream;

import com.aol.cyclops2.internal.stream.spliterators.CopyableSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.IteratableSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.ReversableSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.push.PushingSpliterator;
import cyclops.Streams;
import cyclops.collections.ListX;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class StreamX<T> extends SpliteratorBasedStream<T> {

    public StreamX(Stream<T> stream) {
        super(stream);
    }

    public StreamX(Spliterator<T> stream, Optional<ReversableSpliterator> rev, Optional<PushingSpliterator<?>> split) {
        super(stream, rev, split);
    }

    public StreamX(Stream<T> stream, Optional<ReversableSpliterator> rev, Optional<PushingSpliterator<?>> split) {
        super(stream, rev, split);
    }
    @Override
    public ReactiveSeq<T> reverse() {
        if(this.stream instanceof ReversableSpliterator){
            ReversableSpliterator rev = (ReversableSpliterator)stream;
            return createSeq(rev.invert(),reversible,split);
        }
        return createSeq(Streams.reverse(this), reversible,split);
    }

    @Override
    public ReactiveSeq<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op) {
        return createSeq(new IteratableSpliterator<>(Streams.combineI(this,predicate,op))).flatMapI(i->i);
    }

    @Override
    <X> ReactiveSeq<X> createSeq(Stream<X> stream, Optional<ReversableSpliterator> reversible, Optional<PushingSpliterator<?>> split) {
        return new StreamX<X>(stream,reversible,split);
    }

    @Override
    <X> ReactiveSeq<X> createSeq(Spliterator<X> stream, Optional<ReversableSpliterator> reversible, Optional<PushingSpliterator<?>> split) {
        return new StreamX<X>(stream,reversible,split);
    }

    @Override
    public ReactiveSeq<T> cycle() {

        Spliterator<T> t = copy();
        return  ReactiveSeq.fill(1)
                .flatMap(i->createSeq(CopyableSpliterator.copy(t),reversible,split));
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> duplicate() {

        Tuple2<Iterable<T>, Iterable<T>> copy = Streams.toBufferingDuplicator(() -> Spliterators.iterator(copy()));
        return copy.map((a,b)->Tuple.tuple(createSeq(new IteratableSpliterator<>(a)),createSeq(new IteratableSpliterator<>(b))));

    }

    @Override
    @SuppressWarnings("unchecked")
    public Tuple3<ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>> triplicate() {
        ListX<Iterable<T>> copy = Streams.toBufferingCopier(() -> Spliterators.iterator(copy()), 3);

        return Tuple.tuple(createSeq(new IteratableSpliterator<>(copy.get(0))),
                createSeq(new IteratableSpliterator<>(copy.get(1))),
                createSeq(new IteratableSpliterator<>(copy.get(2))));


    }

    @Override
    @SuppressWarnings("unchecked")
    public Tuple4<ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>, ReactiveSeq<T>> quadruplicate() {
        ListX<Iterable<T>> copy = Streams.toBufferingCopier(() -> Spliterators.iterator(copy()), 4);

        return Tuple.tuple(createSeq(new IteratableSpliterator<>(copy.get(0))),
                createSeq(new IteratableSpliterator<>(copy.get(1))),
                createSeq(new IteratableSpliterator<>(copy.get(2))),
                createSeq(new IteratableSpliterator<>(copy.get(3))));
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Tuple2<Optional<T>, ReactiveSeq<T>> splitAtHead() {
        final Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> Tuple2 = splitAt(1);
        return new Tuple2(
                Tuple2.v1.toOptional()
                        .flatMap(l -> l.size() > 0 ? Optional.of(l.get(0)) : Optional.empty()),
                Tuple2.v2);
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> splitAt(final int where) {
        final Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> Tuple2 = duplicate();
        return new Tuple2(
                Tuple2.v1.limit(where), Tuple2.v2.skip(where));


    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> splitBy(final Predicate<T> splitter) {
        final Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> Tuple2 = duplicate();
        return new Tuple2(
                Tuple2.v1.limitWhile(splitter), Tuple2.v2.skipWhile(splitter));
    }

    @Override
    public Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> partition(final Predicate<? super T> splitter) {
        final Tuple2<ReactiveSeq<T>, ReactiveSeq<T>> Tuple2 = duplicate();
        return new Tuple2(
                Tuple2.v1.filter(splitter), Tuple2.v2.filter(splitter.negate()));

    }
    @Override
    public ReactiveSeq<T> cycle(long times) {
        return ReactiveSeq.fill(1)
                .limit(times)
                .flatMap(i -> createSeq(copy(), reversible, split));

    }

}
