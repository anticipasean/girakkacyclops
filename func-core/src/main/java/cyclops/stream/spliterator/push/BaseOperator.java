package cyclops.stream.spliterator.push;

import java.util.concurrent.locks.LockSupport;
import lombok.AllArgsConstructor;

/**
 * Created by johnmcclean on 12/01/2017.
 */
@AllArgsConstructor
public abstract class BaseOperator<T, R> implements Operator<R> {

    final Operator<T> source;


    protected void request(StreamSubscription[] upstream,
                           long req) {
        while (upstream[0] == null) {
            LockSupport.parkNanos(10l);
        }
        upstream[0].request(req);
    }

}
