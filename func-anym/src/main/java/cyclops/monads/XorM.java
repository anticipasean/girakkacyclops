package cyclops.monads;

import cyclops.container.filterable.Filterable;
import cyclops.container.foldable.Foldable;
import cyclops.container.transformable.To;
import cyclops.container.transformable.Transformable;
import cyclops.reactive.collection.container.immutable.LinkedListX;
import cyclops.reactive.collection.container.immutable.PersistentSetX;
import cyclops.reactive.collection.container.immutable.VectorX;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.reactive.collection.container.mutable.SetX;
import cyclops.container.control.lazy.maybe.Maybe;
import cyclops.container.control.eager.either.Either;
import cyclops.container.control.lazy.either.LazyEither;
import cyclops.monads.Witness.*;
import cyclops.reactive.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * A Sum type for monads of the same type.
 * XorM is active type biased (rather than right biased).
 * e.g.
 * <pre>
 *     {@code
 *          XorM<stream,optional,Integer> nums = XorM.stream(1,2,3)
                                                     .swap();
 *          int result = nums.map(i->i*2)
 *                           .foldLeft(Monoids.intSum);
 *          //12
 *     }
 *
 * </pre>
 *
 * @param <W1> Witness type of monad
 * @param <W2> Witness type of monad
 * @param <T> Data type
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class XorM<W1 extends WitnessType<W1>,W2 extends WitnessType<W2>,T> implements Filterable<T>,
                                                                                      Transformable<T>, Foldable<T>,
                                                                                      Publisher<T>,
                                                                                      To<XorM<W1,W2,T>> {

    @Getter
    private final Either<AnyM<W1,T>,AnyM<W2,T>> xor;

    public static  <W1 extends WitnessType<W1>,W2 extends WitnessType<W2>,T> XorM<W1,W2,T> of(Either<? extends AnyM<? extends W1,? extends T>,
                    ? extends AnyM<? extends W2,? extends T>> xor){
        return new XorM<>((Either)xor);
    }
    public static  <W1 extends WitnessType<W1>,W2 extends WitnessType<W2>,T> XorM<W1,W2,T> right(AnyM<W2,T> right){
        return new XorM<>(Either.right(right));
    }
    public static  <W1 extends WitnessType<W1>,W2 extends WitnessType<W2>,T> XorM<W1,W2,T> left(AnyM<W1,T> left){
        return new XorM<>(Either.left(left));
    }
    public XorM<W1,W2,T> filter(Predicate<? super T> test) {
      return of(xor.map(m -> m.filter(test)).mapLeft(m->m.filter(test)));
    }

    public <R>  XorM<W1,W2,R> coflatMap(final Function<? super  XorM<W1,W2,T>, R> mapper){

        return fold(leftM ->  left(leftM.unit(mapper.apply(this))),
                     rightM -> right(rightM.unit(mapper.apply(this))));
    }

    @Override
    public <U> XorM<W1,W2,U> ofType(Class<? extends U> type) {
        Either<? extends AnyM<W1, ? extends U>, ? extends AnyM<W2, ? extends U>> x = xor.map(m -> m.ofType(type)).mapLeft(m -> m.ofType(type));
        return of(x);
    }

    @Override
    public XorM<W1,W2,T> filterNot(Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    @Override
    public XorM<W1,W2,T> notNull() {
        return of(xor.map(m -> m.notNull()).mapLeft(m->m.notNull()));
    }



    @Override
    public <R>  XorM<W1,W2,R> map(Function<? super T, ? extends R> fn) {
        return of(xor.map(m->m.map(fn)).mapLeft(m->m.map(fn)));
    }


    @Override
    public  XorM<W1,W2,T> peek(Consumer<? super T> c) {
        return map(a->{
            c.accept(a);
            return a;
        });
    }

    @Override
    public String toString() {
        return "XorM["+xor.toString()+"]";
    }


    @Override
    public ReactiveSeq<T> stream() {
        return xor.fold(a->a.stream(), b->b.stream());
    }


    @Override
    public void subscribe(Subscriber<? super T> s) {
        xor.fold(a->{
            a.subscribe(s);
            return null;
        },b->{
            b.subscribe(s);
            return null;
        });

    }

    public <R> R fold(Function<? super AnyM<W1,? super T>, ? extends R> left, Function<? super AnyM<W2,? super T>, ? extends R> right ){
        return xor.fold(left,right);
    }

    public XorM<W2,W1,T> swap(){
        return of(xor.swap());
    }

    @Override
    public Iterator<T> iterator() {
        return xor.fold(a->a.iterator(), b->b.iterator());
    }


    public static  <W1 extends WitnessType<W1>,T> XorM<W1,vectorX,T> vectorX(VectorX<T> list){
        return new XorM<>(Either.right(AnyM.fromVectorX(list)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,vectorX,T> vectorX(T... values){
        return new XorM<>(Either.right(AnyM.fromVectorX(VectorX.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,linkedListX,T> linkedListX(LinkedListX<T> list){
        return new XorM<>(Either.right(AnyM.fromLinkedListX(list)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,linkedListX,T> linkedListX(T... values){
        return new XorM<>(Either.right(AnyM.fromLinkedListX(LinkedListX.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,persistentSetX,T> persistentsetX(PersistentSetX<T> list){
        return new XorM<>(Either.right(AnyM.fromPersistentSetX(PersistentSetX.fromIterable(list))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,persistentSetX,T> persistentsetX(T... values){
        return new XorM<>(Either.right(AnyM.fromPersistentSetX(PersistentSetX.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,set,T> setX(Set<T> list){
        return new XorM<>(Either.right(AnyM.fromSet(SetX.fromIterable(list))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,set,T> setX(T... values){
        return new XorM<>(Either.right(AnyM.fromSet(SetX.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,list,T> listX(List<T> list){
        return new XorM<>(Either.right(AnyM.fromList(ListX.fromIterable(list))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,list,T> listX(T... values){
        return new XorM<>(Either.right(AnyM.fromList(ListX.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,stream,T> stream(Stream<T> stream){
        return new XorM<>(Either.right(AnyM.fromStream(stream)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,stream,T> stream(T... values){
        return new XorM<>(Either.right(AnyM.fromStream(Stream.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,reactiveSeq,T> reactiveSeq(ReactiveSeq<T> stream){
        return new XorM<>(Either.right(AnyM.fromStream(stream)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,reactiveSeq,T> reactiveSeq(T... values){
        return new XorM<>(Either.right(AnyM.fromStream(ReactiveSeq.of(values))));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,tryType,T> success(T value){
        return new XorM<>(Either.right(AnyM.success(value)));
    }
    public static  <W1 extends WitnessType<W1>,X extends Throwable,T> XorM<W1,tryType,T> failure(X value){
        return new XorM<>(Either.right(AnyM.failure(value)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,future,T> futureOf(Supplier<T> value, Executor ex){
        return new XorM<>(Either.right(AnyM.futureOf(value,ex)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,completableFuture,T> completableFutureOf(Supplier<T> value, Executor ex){
        return new XorM<>(Either.right(AnyM.completableFutureOf(value,ex)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,eval,T> later(Supplier<T> value){
        return new XorM<>(LazyEither.right(AnyM.later(value)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,optional,T> ofNullable(T value){
        return new XorM<>(Either.right(AnyM.ofNullable(value)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,maybe,T> just(T value){
        return new XorM<>(Either.right(AnyM.just(value)));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,maybe,T> none(){
        return new XorM<>(Either.right(AnyM.none()));
    }
    public static  <W1 extends WitnessType<W1>,T> XorM<W1,maybe,T> maybeNullabe(T value){
        return new XorM<>(Either.right(AnyM.fromMaybe(Maybe.ofNullable(value))));
    }
}
