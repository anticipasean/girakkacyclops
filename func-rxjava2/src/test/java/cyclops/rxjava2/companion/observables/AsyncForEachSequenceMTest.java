package cyclops.rxjava2.companion.observables;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.rxjava2.companion.Observables;
import cyclops.rxjava2.adapter.ObservableReactiveSeq;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.companion.Spouts;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscription;

public class AsyncForEachSequenceMTest {

    volatile boolean complete = false;
    AtomicInteger times = new AtomicInteger(0);
    AtomicInteger errors = new AtomicInteger(0);
    Throwable error;

    @Before
    public void setup() {
        error = null;
        complete = false;
        times = new AtomicInteger(0);
        errors = new AtomicInteger(0);
    }

    @Test
    public void emptyOnComplete() {
        of().forEach(e -> {
                     },
                     e -> {
                     },
                     () -> complete = true);
        while (!complete) {
            LockSupport.parkNanos(0l);
        }
        assertTrue(complete);
    }

    protected <U> ReactiveSeq<U> of(U... array) {

        ReactiveSeq<U> seq = Spouts.async(s -> {
            Thread t = new Thread(() -> {
                for (U next : array) {
                    s.onNext(next);
                }
                s.onComplete();
            });
            t.start();
        });
        return ObservableReactiveSeq.reactiveSeq(Observables.observableFrom(seq));
    }

    @Test
    public void onComplete() {

        for (int i = 0; i < 10000; i++) {
            times = new AtomicInteger(0);
            errors = new AtomicInteger(0);
            of(1,
               2,
               3,
               4,
               5,
               6,
               7,
               8,
               9).map(this::load)
                 .forEach(System.out::println,
                          aEx -> {
                              System.err.println(aEx + ":" + aEx.getMessage());
                              errors.incrementAndGet();
                          },
                          () -> {
                              times.incrementAndGet();
                              System.out.println("Over");
                          });
            while (times.get() == 0) {
                LockSupport.parkNanos(0l);
            }
            assertThat("Errors " + errors.get(),
                       times.get(),
                       equalTo(1));
        }
    }

    @Test
    public void onCompleteXEvents() {
        of(1,
           2,
           3,
           4,
           5,
           6,
           7,
           8,
           9).map(this::load)
             .forEach(Long.MAX_VALUE,
                      System.out::println,
                      aEx -> {
                          System.err.println(aEx + ":" + aEx.getMessage());
                          times.incrementAndGet();
                      },
                      () -> {
                          times.incrementAndGet();
                          System.out.println("Over");
                      });
        while (times.get() == 0) {
            LockSupport.parkNanos(0l);
        }
        assertThat(times.get(),
                   equalTo(1));
    }

    //this::load is simple
    String load(int i) {
        if (i == 2) {
            throw new RuntimeException("test exception:" + i);
        } else {
            return "xx" + i;
        }
    }

    @Test
    public void forEachX() {
        Subscription s = of(1,
                            2,
                            3).forEach(2,
                                       System.out::println);
        s.request(1);
    }

    @Test
    public void forEachXTest() throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        Subscription s = of(1,
                            2,
                            3).forEach(2,
                                       i -> list.add(i));
        Thread.sleep(100);
        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

    }

    @Test
    public void forEachXTestIsComplete() throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        Subscription s = of(1,
                            2,
                            3).forEach(2,
                                       i -> list.add(i));
        Thread.sleep(100);
        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

    }

    @Test
    public void forEachWithErrors2() throws InterruptedException {
        error = null;
        List<Integer> result = new ArrayList<>();
        of(1,
           2,
           3,
           4,
           5,
           6,
           7).map(this::errors)
             .forEach(e -> result.add(e),
                      e -> error = e);

        Thread.sleep(100);
        assertNotNull(error);
        System.out.println(result);
        assertThat(result,
                   hasItems(1,
                            3,
                            4,
                            5,
                            6));
        assertThat(result,
                   not(hasItems(7)));
    }

    @Test
    public void forEachWithEvents2() throws InterruptedException {
        error = null;
        complete = false;
        List<Integer> result = new ArrayList<>();
        of(1,
           2,
           3,
           4,
           5,
           6,
           7).map(this::errors)
             .forEach(e -> result.add(e),
                      e -> error = e,
                      () -> complete = true);
        Thread.sleep(100);
        assertNotNull(error);
        assertThat(result,
                   hasItems(1,
                            3,
                            4,
                            5,
                            6));
        assertThat(result,
                   not(hasItems(7)));
    }

    public Integer errors(Integer ints) {
        if (ints == 7) {
            throw new RuntimeException();
        }
        return ints;
    }

    private String process(int process) {
        return "processed " + process;
    }

    @Test
    public void forEachXWithErrorExample() {

        Subscription s = of(10,
                            20,
                            30).map(this::process)
                               .forEach(2,
                                        System.out::println,
                                        System.err::println);

        System.out.println("Completed 2");
        s.request(1);
        System.out.println("Finished all!");

    }

    @Test
    public void forEachXWithErrors() throws InterruptedException {

        List<Integer> list = new ArrayList<>();

        Subscription s = of(() -> 1,
                            () -> 2,
                            () -> 3,
                            (Supplier<Integer>) () -> {
                                throw new RuntimeException();
                            }).map(Supplier::get)
                              .forEach(2,
                                       i -> list.add(i),
                                       e -> error = e);
        Thread.sleep(100);
        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

        assertThat(error,
                   instanceOf(RuntimeException.class));
    }

    @Test
    public void forEachXWithEvents() throws InterruptedException {

        List<Integer> list = new ArrayList<>();

        Subscription s = of(() -> 1,
                            () -> 2,
                            () -> 3,
                            (Supplier<Integer>) () -> {
                                throw new RuntimeException();
                            }).map(Supplier::get)
                              .forEach(2,
                                       i -> list.add(i),
                                       e -> {
                                           error = e;
                                           complete = true;
                                       },
                                       () -> complete = true);

        while (!complete) {

        }
        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

        assertThat(error,
                   instanceOf(RuntimeException.class));

        assertTrue(complete);
    }


    @Test
    public void forEachWithErrors() throws InterruptedException {

        List<Integer> list = new ArrayList<>();
        assertThat(error,
                   nullValue());
        of(() -> 1,
           () -> 2,
           () -> 3,
           (Supplier<Integer>) () -> {
               throw new RuntimeException();
           }).map(Supplier::get)
             .forEach(i -> list.add(i),
                      e -> error = e);

        Thread.sleep(50);
        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

        assertThat(error,
                   instanceOf(RuntimeException.class));
    }

    @Test
    public void forEachWithEvents() {

        List<Integer> list = new ArrayList<>();
        assertFalse(complete);
        assertThat(error,
                   nullValue());
        of(() -> 1,
           () -> 2,
           () -> 3,
           (Supplier<Integer>) () -> {
               throw new RuntimeException();
           }).map(Supplier::get)
             .forEach(i -> list.add(i),
                      e -> {
                          error = e;
                          complete = true;
                      },
                      () -> complete = true);

        while (!complete) {
            LockSupport.parkNanos(0l);
        }

        assertThat(list,
                   hasItems(1,
                            2,
                            3));
        assertThat(list.size(),
                   equalTo(3));

        assertThat(error,
                   instanceOf(RuntimeException.class));

        assertTrue(complete);
    }
}
