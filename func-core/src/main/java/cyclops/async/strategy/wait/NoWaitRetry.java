package cyclops.async.strategy.wait;

/**
 * Repeatedly retry to take or offer element to Queue if full or data unavailable
 *
 * @param <T> Data type of elements in the async.Queue
 * @author johnmcclean
 */
public class NoWaitRetry<T> implements WaitStrategy<T> {

    /* (non-Javadoc)
     * @see cyclops2.async.wait.WaitStrategy#take(cyclops2.async.wait.WaitStrategy.Takeable)
     */
    @Override
    public T take(final cyclops.async.strategy.wait.WaitStrategy.Takeable<T> t) throws InterruptedException {
        T result;

        while ((result = t.take()) == null) {

        }

        return result;
    }

    /* (non-Javadoc)
     * @see cyclops2.async.wait.WaitStrategy#offer(cyclops2.async.wait.WaitStrategy.Offerable)
     */
    @Override
    public boolean offer(final WaitStrategy.Offerable o) throws InterruptedException {
        while (!o.offer()) {

        }
        return true;
    }

}
