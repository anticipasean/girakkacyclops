package cyclops.function.enhanced;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.async.Future;
import cyclops.container.control.Maybe;
import cyclops.container.control.Option;
import cyclops.container.control.Try;
import java.util.concurrent.ForkJoinPool;
import org.junit.Test;

public class Function1Test {

    Function1<Integer, Integer> some = __ -> __;
    Function1<Integer, Integer> none = __ -> null;
    Function1<Integer, Integer> ex = __ -> {
        throw new RuntimeException();
    };

    @Test
    public void lift() {
        assertThat(some.lift()
                       .apply(10),
                   equalTo(Option.some(10)));
        assertThat(none.lift()
                       .apply(10),
                   equalTo(Option.none()));
    }

    @Test
    public void liftEx() {
        assertThat(some.lift(ForkJoinPool.commonPool())
                       .apply(10)
                       .orElse(-1),
                   equalTo(Future.ofResult(10)
                                 .orElse(-1)));
        assertThat(none.lift(ForkJoinPool.commonPool())
                       .apply(10)
                       .orElse(-1),
                   equalTo(null));
    }

    @Test
    public void liftTry() {
        assertThat(some.liftTry()
                       .apply(10),
                   equalTo(Try.success(10)));
        assertThat(ex.liftTry()
                     .apply(10)
                     .isFailure(),
                   equalTo(true));
        assertThat(none.liftTry()
                       .apply(10)
                       .isFailure(),
                   equalTo(false));
    }

    @Test
    public void lazyLift() {
        assertThat(some.lazyLift()
                       .apply(10),
                   equalTo(Maybe.just(10)));
        assertThat(none.lazyLift()
                       .apply(10),
                   equalTo(Maybe.nothing()));
    }
}
