package cyclops.stream.operator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import cyclops.reactive.ReactiveSeq;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscription;

public class ForEachSequenceMFutureTest {

    Executor exec = Executors.newFixedThreadPool(1);
    volatile boolean complete = false;

    @Before
    public void setup() {

        complete = false;
    }

    @Test
    public void forEachX() {
        Subscription s = ReactiveSeq.of(1,
                                        2,
                                        3)
                                    .foldFuture(exec,
                                                t -> t.forEach(2,
                                                               System.out::println))
                                    .orElse(null);

        System.out.println("takeOne batch");
        s.request(1);

    }

    @Test
    public void forEachXTest() {
        List<Integer> list = new ArrayList<>();
        Subscription s = ReactiveSeq.of(1,
                                        2,
                                        3)
                                    .foldFuture(exec,
                                                t -> t.forEach(2,
                                                               i -> list.add(i)))
                                    .toOptional()
                                    .get();

        while (list.size() != 2) {

        }
        assertThat(list,
                   hasItems(1,
                            2));
        assertThat(list.size(),
                   equalTo(2));
        s.request(1);

        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));
    }


}
