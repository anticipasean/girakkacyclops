package cyclops.async;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.async.reactive.futurestream.pipeline.FastFuture;
import cyclops.async.reactive.futurestream.pipeline.collector.EmptyCollector;
import cyclops.async.reactive.futurestream.pipeline.collector.MaxActive;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

public class EmptyCollectorTest {

    EmptyCollector collector;

    @Before
    public void setup() {
        collector = new EmptyCollector();
    }

    @Test
    public void testAccept() {
        for (int i = 0; i < 1000; i++) {
            collector.accept(FastFuture.completedFuture(10l));
        }
    }

    @Test
    public void testAcceptMock() {
        FastFuture cf = Mockito.mock(FastFuture.class);
        BDDMockito.given(cf.isDone())
                  .willReturn(true);
        for (int i = 0; i < 1000; i++) {
            collector.accept(cf);
        }
        Mockito.verify(cf,
                       Mockito.atLeastOnce())
               .isDone();
    }

    @Test
    public void testAcceptMock495() {
        collector = new EmptyCollector<>(new MaxActive(500,
                                                       5),
                                         cf -> cf.join());
        FastFuture cf = Mockito.mock(FastFuture.class);
        BDDMockito.given(cf.isDone())
                  .willReturn(true);
        for (int i = 0; i < 1000; i++) {
            collector.accept(cf);
        }
        Mockito.verify(cf,
                       Mockito.times(501))
               .isDone();
    }

    @Test
    public void testAcceptMock50() {
        collector = new EmptyCollector<>(new MaxActive(500,
                                                       450),
                                         cf -> cf.join());
        FastFuture cf = Mockito.mock(FastFuture.class);
        BDDMockito.given(cf.isDone())
                  .willReturn(true);
        for (int i = 0; i < 1000; i++) {
            collector.accept(cf);
        }
        Mockito.verify(cf,
                       Mockito.times(501))
               .isDone();
    }

    @Test
    public void testWithResults() {

        collector = collector.withMaxActive(new MaxActive(4,
                                                          3));
        assertThat(collector.withResults(null)
                            .getMaxActive()
                            .getMaxActive(),
                   is(4));
    }

    @Test
    public void testGetResults() {
        assertTrue(collector.getResults()
                            .isEmpty());
    }

    @Test
    public void testGetMaxActive() {
        assertThat(collector.getMaxActive()
                            .getMaxActive(),
                   is(MaxActive.IO.getMaxActive()));
    }


}
