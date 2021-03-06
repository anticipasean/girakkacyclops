package cyclops.stream.spliterator.push.flatMap.iterable;


import cyclops.reactive.companion.Spouts;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

@Test
public class FlatMapTckPublisherTest extends PublisherVerification<Long> {

    public FlatMapTckPublisherTest() {
        super(new TestEnvironment(300L));
    }


    @Override
    public Publisher<Long> createPublisher(long elements) {
        return Spouts.iterate(0l,
                              i -> i + 1l)
                     .concatMap(i -> Spouts.of(i))
                     .limit(elements);

    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return null; //not possible to forEachAsync to failed Stream

    }


}
