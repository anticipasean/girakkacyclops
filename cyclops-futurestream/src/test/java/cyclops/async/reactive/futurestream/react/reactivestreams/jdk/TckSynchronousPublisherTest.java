package cyclops.async.reactive.futurestream.react.reactivestreams.jdk;


import cyclops.reactive.ReactiveSeq;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

@Test
public class TckSynchronousPublisherTest extends PublisherVerification<Long> {

    public TckSynchronousPublisherTest() {
        super(new TestEnvironment(300L));
    }


    @Override
    public Publisher<Long> createPublisher(long elements) {
        return ReactiveSeq.iterate(0l,
                                   i -> i + 1l)
                          .limit(elements);

    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return null; //not possible to forEachAsync to failed Stream

    }


}
