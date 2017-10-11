package cyclops.data;


import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.BiTransformable;
import com.aol.cyclops2.types.functor.Transformable;
import com.aol.cyclops2.types.recoverable.OnEmpty;
import com.aol.cyclops2.types.recoverable.OnEmptySwitch;
import com.aol.cyclops2.util.ExceptionSoftener;
import cyclops.collectionx.immutable.*;
import cyclops.collectionx.mutable.ListX;
import cyclops.control.Option;
import cyclops.control.lazy.Trampoline;
import cyclops.function.Function3;
import cyclops.function.Function4;
import cyclops.stream.ReactiveSeq;
import cyclops.data.tuple.Tuple;
import cyclops.data.tuple.Tuple2;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Collectors;

public interface ImmutableMap<K,V> extends Iterable<Tuple2<K,V>>,
                                            Folds<Tuple2<K,V>>,
                                            Filters<Tuple2<K,V>>,
                                            Transformable<V>,
                                            BiTransformable<K, V>,
                                            OnEmpty<Tuple2<K, V>>,
                                            OnEmptySwitch<Tuple2<K, V>,ImmutableMap<K, V>> {

    PersistentMapX<K,V> persistentMapX();
    ImmutableMap<K,V> put(K key, V value);
    ImmutableMap<K,V> put(Tuple2<K, V> keyAndValue);
    ImmutableMap<K,V> putAll(ImmutableMap<K, V> map);

    ImmutableMap<K,V> remove(K key);
    ImmutableMap<K,V> removeAll(K... keys);


    default boolean containsValue(V value){
        return stream().anyMatch(t-> Objects.equals(value,t._2()));
    }
    default boolean isEmpty(){
        return size()==0;
    }
    boolean containsKey(K key);

    boolean contains(Tuple2<K, V> t);
    Option<V> get(K key);
    V getOrElse(K key, V alt);
    V getOrElseGet(K key, Supplier<V> alt);

    int size();


    default Map<K,V> javaMap(){
        return new Map<K, V>() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public String toString() {
                return ImmutableMap.this.toString();
            }

            @Override
            public boolean equals(Object obj) {
                if(obj instanceof Map){
                    Map<K,V> m = (Map<K,V>)obj;
                    if(m.size()!=size())
                        return false;
                    return stream().collect(Collectors.toMap(t->t._1(),t->t._2()))
                                   .equals(m);
                }
                return super.equals(obj);
            }

            @Override
            public int size() {
                return ImmutableMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return ImmutableMap.this.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) {
                return ImmutableMap.this.containsKey((K)key);
            }

            @Override
            public boolean containsValue(Object value) {
                return ImmutableMap.this.containsValue((V)value);
            }

            @Override
            public V get(Object key) {
                return ImmutableMap.this.get((K)key).orElse(null);
            }

            @Override @Deprecated
            public V put(K key, V value) {
                throw new UnsupportedOperationException("Attempt to put on an ImmutableMap");
            }

            @Override
            public V remove(Object key) {
                throw new UnsupportedOperationException("Attempt to remove on an ImmutableMap");
            }

            @Override
            public void putAll(Map<? extends K, ? extends V> m) {
                throw new UnsupportedOperationException("Attempt to put on an ImmutableMap");
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Attempt to clear on an ImmutableMap");

            }

            @Override
            public Set<K> keySet() {
                return ImmutableMap.this.stream().map(t->t._1()).toSet();
            }

            @Override
            public Collection<V> values() {
                return ImmutableMap.this.stream().map(t->t._2()).toList();
            }

            @Override
            public Set<Entry<K, V>> entrySet() {
                return ImmutableMap.this.stream().peek(System.out::println).map(t->(Entry<K,V>)new AbstractMap.SimpleEntry<>(t._1(),t._2())).toSet();
            }
        };
    }

    default String mkString(){

        return stream().map(t->"{"+t._1()+"="+t._2()+"}").join(",","[","]");
    }

    <K2,V2> DMap.Two<K,V,K2,V2> merge(ImmutableMap<K2, V2> one);
    <K2,V2,K3,V3> DMap.Three<K,V,K2,V2,K3,V3> merge(DMap.Two<K2, V2, K3, V3> two);

    ReactiveSeq<Tuple2<K,V>> stream();

    <R> ImmutableMap<K,R> mapValues(Function<? super V, ? extends R> map);
    <R> ImmutableMap<R,V> mapKeys(Function<? super K, ? extends R> map);
    <R1,R2> ImmutableMap<R1,R2> bimap(BiFunction<? super K, ? super V, ? extends Tuple2<R1, R2>> map);

    <K2, V2> ImmutableMap<K2, V2> flatMap(BiFunction<? super K, ? super V, ? extends ImmutableMap<K2, V2>> mapper);
    <K2, V2> ImmutableMap<K2, V2> flatMapI(BiFunction<? super K, ? super V, ? extends Iterable<Tuple2<K2, V2>>> mapper);

    ImmutableMap<K,V> filter(Predicate<? super Tuple2<K, V>> predicate);


    ImmutableMap<K,V> filterKeys(Predicate<? super K> predicate);
    ImmutableMap<K,V> filterValues(Predicate<? super V> predicate);

    @Override
    default <U> ImmutableMap<K,U> cast(Class<? extends U> type) {
        return (HashMap<K,U>)Transformable.super.cast(type);
    }
    @Override
    default ImmutableMap<K,V> filterNot(Predicate<? super Tuple2<K, V>> predicate){
        return (HashMap<K,V>)Filters.super.filterNot(predicate);
    }
    @Override
    default ImmutableMap<K,V> notNull(){
        return (HashMap<K,V>)Filters.super.notNull();
    }

    @Override
    <R> ImmutableMap<K,R> map(Function<? super V, ? extends R> fn);

    @Override
    default ImmutableMap<K,V> peek(Consumer<? super V> c) {
        return (HashMap<K,V>)Transformable.super.peek(c);
    }

    @Override
    default <R> ImmutableMap<K,R> trampoline(Function<? super V, ? extends Trampoline<? extends R>> mapper) {
        return (ImmutableMap<K,R>)Transformable.super.trampoline(mapper);
    }

    @Override
    default <R> ImmutableMap<K,R> retry(Function<? super V, ? extends R> fn) {
        return (ImmutableMap<K,R>)Transformable.super.retry(fn);
    }

    @Override
    default <R> ImmutableMap<K,R> retry(Function<? super V, ? extends R> fn, int retries, long delay, TimeUnit timeUnit) {
        return (ImmutableMap<K,R>)Transformable.super.retry(fn,retries,delay,timeUnit);
    }

    @Override
    <R1, R2> ImmutableMap<R1, R2> bimap(Function<? super K, ? extends R1> fn1, Function<? super V, ? extends R2> fn2);

    @Override
    default ImmutableMap<K, V> bipeek(Consumer<? super K> c1, Consumer<? super V> c2) {
        return (ImmutableMap<K,V>)BiTransformable.super.bipeek(c1,c2);
    }

    @Override
    default <U1, U2> ImmutableMap<U1, U2> bicast(Class<U1> type1, Class<U2> type2) {
        return (ImmutableMap<U1,U2>)BiTransformable.super.bicast(type1,type2);
    }

    @Override
    default <R1, R2> ImmutableMap<R1, R2> bitrampoline(Function<? super K, ? extends Trampoline<? extends R1>> mapper1, Function<? super V, ? extends Trampoline<? extends R2>> mapper2) {
        return (ImmutableMap<R1,R2>)BiTransformable.super.bitrampoline(mapper1,mapper2);
    }

    @Override
    default ImmutableMap<K, V> onEmpty(Tuple2<K, V> value){
        if(size()==0){
            return put(value);
        }
        return this;
    }

    @Override
    default ImmutableMap<K, V> onEmptyGet(Supplier<? extends Tuple2<K, V>> supplier){
        return onEmpty(supplier.get());
    }

    @Override
    default <X extends Throwable> ImmutableMap<K, V> onEmptyThrow(Supplier<? extends X> supplier){
        if(size()==0)
            throw ExceptionSoftener.throwSoftenedException(supplier.get());
        return this;
    }

    @Override
    default ImmutableMap<K, V> onEmptySwitch(Supplier<? extends ImmutableMap<K, V>> supplier){
        if(size()==0){
            return supplier.get();
        }
        return this;
    }

    default <K1,K2,K3,K4,R1, R2, R3, R> ImmutableMap<K4,R> forEach4(Function<? super Tuple2<K, V>, ? extends Iterable<Tuple2<K1, R1>>> iterable1,
                                                                    BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? extends Iterable<Tuple2<K2, R2>>> iterable2,
                                                                    Function3<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? extends Iterable<Tuple2<K3, R3>>> iterable3,
                                                                    Function4<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? super Tuple2<K3, R3>, ? extends Tuple2<K4, R>> yieldingFunction) {

        return this.flatMapI((a1,b1) -> {
            Tuple2<K, V> in = Tuple.tuple(a1, b1);
            ReactiveSeq<Tuple2<K1,R1>> a = ReactiveSeq.fromIterable(iterable1.apply(in));
            return a.flatMap(ina -> {
                ReactiveSeq<Tuple2<K2,R2>> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                return b.flatMap(inb -> {
                    ReactiveSeq<Tuple2<K3,R3>> c = ReactiveSeq.fromIterable(iterable3.apply(in, ina, inb));
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    default <K1,K2,K3,K4,R1, R2, R3, R> ImmutableMap<K4,R> forEach4(Function<? super Tuple2<K, V>, ? extends Iterable<Tuple2<K1, R1>>> iterable1,
                                                                    BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? extends Iterable<Tuple2<K2, R2>>> iterable2,
                                                                    Function3<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? extends Iterable<Tuple2<K3, R3>>> iterable3,
                                                                    Function4<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? super Tuple2<K3, R3>, Boolean> filterFunction,
                                                                    Function4<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? super Tuple2<K3, R3>, ? extends Tuple2<K4, R>> yieldingFunction) {

        return this.flatMapI((a1,b1) -> {
            Tuple2<K, V> in = Tuple.tuple(a1, b1);
            ReactiveSeq<Tuple2<K1,R1>> a = ReactiveSeq.fromIterable(iterable1.apply(in));
            return a.flatMap(ina -> {
                ReactiveSeq<Tuple2<K2,R2>> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                return b.flatMap(inb -> {
                    ReactiveSeq<Tuple2<K3,R3>> c = ReactiveSeq.fromIterable(iterable3.apply(in, ina, inb));
                    return c.filter(in2 -> filterFunction.apply(in, ina, inb, in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    default <K1,K2,K3,R1, R2, R> ImmutableMap<K3,R> forEach3(Function<? super Tuple2<K, V>, ? extends Iterable<Tuple2<K1, R1>>> iterable1,
                                                             BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? extends Iterable<Tuple2<K2, R2>>> iterable2,
                                                             Function3<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? extends Tuple2<K3, R>> yieldingFunction) {

        return this.flatMapI((a1,b1) -> {
            Tuple2<K, V> in = Tuple.tuple(a1, b1);

            Iterable<Tuple2<K1,R1>> a = iterable1.apply(in);
            return ReactiveSeq.fromIterable(a)
                    .flatMap(ina -> {
                        ReactiveSeq<Tuple2<K2,R2>> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                        return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
                    });

        });
    }


    default <K1,K2,K3,R1, R2, R> ImmutableMap<K3,R> forEach3(Function<? super Tuple2<K, V>, ? extends Iterable<Tuple2<K1, R1>>> iterable1,
                                                             BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? extends Iterable<Tuple2<K2, R2>>> iterable2,
                                                             Function3<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, Boolean> filterFunction,
                                                             Function3<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? super Tuple2<K2, R2>, ? extends Tuple2<K3, R>> yieldingFunction) {

        return this.flatMapI((a1,b1) -> {
            Tuple2<K, V> in = Tuple.tuple(a1, b1);
            Iterable<Tuple2<K1,R1>> a = iterable1.apply(in);
            return ReactiveSeq.fromIterable(a)
                    .flatMap(ina -> {
                        ReactiveSeq<Tuple2<K2,R2>> b = ReactiveSeq.fromIterable(iterable2.apply(in, ina));
                        return b.filter(in2 -> filterFunction.apply(in, ina, in2))
                                .map(in2 -> yieldingFunction.apply(in, ina, in2));
                    });

        });
    }


    default <K1,K2,R1, R> ImmutableMap<K2,R> forEach2(Function<? super Tuple2<K, V>, ? extends Iterable<Tuple2<K1, R1>>> iterable1,
                                                      BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? extends Tuple2<K2, R>> yieldingFunction) {

        return this.flatMapI((a1,b1) -> {
            Tuple2<K, V> in = Tuple.tuple(a1, b1);
            Iterable<Tuple2<K1,R1>> b = iterable1.apply(in);
            return ReactiveSeq.fromIterable(b)
                    .map(in2->yieldingFunction.apply(in, in2));
        });
    }


    default <K1,K2,R1, R> ImmutableMap<K2,R> forEach2(Function<? super Tuple2<K, V>, ? extends Iterable<Tuple2<K1, R1>>> iterable1,
                                                      BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, Boolean> filterFunction,
                                                      BiFunction<? super Tuple2<K, V>, ? super Tuple2<K1, R1>, ? extends Tuple2<K2, R>> yieldingFunction) {

        return this.flatMapI((a1,b1) -> {
            Tuple2<K, V> in = Tuple.tuple(a1, b1);
            Iterable<? extends Tuple2<K1,R1>> b = iterable1.apply(in);
            return ReactiveSeq.fromIterable(b)
                    .filter(in2-> filterFunction.apply(in,in2))
                    .map(in2->yieldingFunction.apply(in, in2));
        });
    }
    /**
     * Convert this MapX to a ListX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return ListX of transformed values
     */
    default <T> ListX<T> toListX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return ListX.narrow(stream().map(fn)
                .toListX());
    }

    /**
     * Convert this MapX to a PersistentSetX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return PersistentSetX of transformed values
     */
    default <T> PersistentSetX<T> toPersistentSetX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return PersistentSetX.narrow(stream().map(fn).to()
                .persistentSetX());
    }

    /**
     * Convert this MapX to a POrderdSetX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return OrderedSetX of transformed values
     */
    default <T> OrderedSetX<T> toOrderedSetX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return OrderedSetX.narrow(stream().map(fn).to().orderedSetX());
    }

    /**
     * Convert this MapX to a QueueX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return QueueX of transformed values
     */
    default <T> PersistentQueueX<T> toPersistentQueueX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return PersistentQueueX.narrow(stream().map(fn).to().persistentQueueX());
    }

    /**
     * Convert this MapX to a LinkedListX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return LinkedListX of transformed values
     */
    default <T> LinkedListX<T> toLinkedListX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return LinkedListX.narrow(stream().map(fn).to().linkedListX());

    }
    /**
     * Convert this MapX to a VectorX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return VectorX of transformed values
     */
    default <T> VectorX<T> toVectorX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return VectorX.narrow(stream().map(fn).to().vectorX());
    }
    /**
     * Convert this MapX to a BagX via the provided transformation function
     *
     * @param fn Mapping function to transform each Map entry into a singleUnsafe value
     * @return BagX of transformed values
     */
    default <T> BagX<T> toBagX(final Function<? super Tuple2<? super K, ? super V>, ? extends T> fn) {
        return BagX.narrow(stream().map(fn).to().bagX());
    }
}
