package cyclops.container.control.trampoline;

import static cyclops.container.control.Eval.now;
import static cyclops.container.control.Maybe.just;
import static cyclops.container.immutable.tuple.Tuple.tuple;

import cyclops.container.control.Eval;
import cyclops.container.control.Maybe;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.container.immutable.tuple.Tuple3;
import org.junit.Test;


public class MaybeTrampolineTest {

    @Test
    public void fib() {
        fibonacci(just(tuple(100_000,
                             1l,
                             0l))).toTrampoline();
        System.out.println(fibonacci(just(tuple(100_000,
                                                1l,
                                                0l))));
    }

    @Test
    public void gcdTest() {
        System.out.println(gcd(now(100),
                               now(10000)));
    }

    public Maybe<Long> fibonacci(Maybe<Tuple3<Integer, Long, Long>> fib) {
        return fib.flatMap(t -> t._1() == 0 ? just(t._3()) : fibonacci(just(tuple(t._1() - 1,
                                                                                  t._2() + t._3(),
                                                                                  t._2()))));
    }

    @Test
    public void concurrent() {

        Maybe<Long> fib = fibonacci(just(tuple(100_000,
                                               1l,
                                               0l)));
        Eval<Integer> gcd = gcd(now(100),
                                now(10000));

        Tuple2<Maybe<Long>, Integer> res = fib.toTrampoline()
                                              .zip(gcd.toTrampoline())
                                              .get();

        System.out.println("Result is " + res);

    }


    public Eval<Integer> gcd(Eval<Integer> ea,
                             Eval<Integer> eb) {
        return eb.flatMap(b -> ea.flatMap(a -> b == 0 ? ea : gcd(eb,
                                                                 now(a % b))));
    }

    @Test
    public void bounce() {
        even(just(200000)).toTrampoline()
                          .bounce();
    }

    @Test
    public void odd() {
        System.out.println(even(just(200000)).toTrampoline()
                                             .zip(odd1(just(200000)).toTrampoline())
                                             .get());

        //use zip to interleave execution of even and odd algorithms!
        even(just(200000)).toTrampoline()
                          .zip(odd1(just(200000)).toTrampoline())
                          .get();


    }

    @Test

    public void interleave() {

        //use zip to interleave execution of even and odd algorithms!
        even(just(200000)).toTrampoline()
                          .zip(odd1(just(200000)).toTrampoline())
                          .get();


    }


    public Maybe<String> odd(Maybe<Integer> n) {
        System.out.println("A");
        return n.flatMap(x -> even(just(x - 1)));
    }

    public Maybe<String> even(Maybe<Integer> n) {
        return n.flatMap(x -> {
            return x <= 0 ? just("done") : odd(just(x - 1));
        });
    }


    public Maybe<String> odd1(Maybe<Integer> n) {
        System.out.println("B");
        return n.flatMap(x -> even1(just(x - 1)));
    }

    public Maybe<String> even1(Maybe<Integer> n) {

        return n.flatMap(x -> {
            return x <= 0 ? just("done") : odd1(just(x - 1));
        });
    }

}
