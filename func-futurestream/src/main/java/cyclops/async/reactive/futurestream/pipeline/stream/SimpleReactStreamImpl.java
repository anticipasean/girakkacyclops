package cyclops.async.reactive.futurestream.pipeline.stream;

import cyclops.async.companion.QueueFactories;
import cyclops.async.queue.Queue;
import cyclops.async.queue.QueueFactory;
import cyclops.reactive.subscription.AlwaysContinue;
import cyclops.reactive.subscription.Continueable;
import cyclops.async.reactive.futurestream.SimpleReact;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@lombok.With
@Getter
@AllArgsConstructor
public class SimpleReactStreamImpl<U> implements SimpleReactStream<U>, EagerToQueue<U> {

    private final Optional<Consumer<Throwable>> errorHandler;
    private final EagerStreamWrapper lastActive;
    private final QueueFactory<U> queueFactory;
    private final SimpleReact simpleReact;
    private final Continueable subscription;

    public SimpleReactStreamImpl(final SimpleReact simpleReact,
                                 final Stream<CompletableFuture<U>> stream) {
        this.simpleReact = simpleReact;
        final Stream s = stream;

        this.errorHandler = Optional.empty();
        this.lastActive = new EagerStreamWrapper(s,
                                                 this.errorHandler);
        this.queueFactory = QueueFactories.unboundedQueue();
        this.subscription = new AlwaysContinue();

    }

    @Override
    public SimpleReactStream<U> withAsync(final boolean b) {

        return this.withSimpleReact(this.simpleReact.withAsync(b));
    }

    @Override
    public <R> SimpleReactStream<R> thenSync(final Function<? super U, ? extends R> fn) {
        return SimpleReactStream.super.thenSync(fn);
    }

    @Override
    public <R1, R2> SimpleReactStream<R2> allOf(final Collector<? super U, ?, R1> collector,
                                                final Function<? super R1, ? extends R2> fn) {
        return SimpleReactStream.super.allOf(collector,
                                             fn);
    }

    @Override
    public Executor getTaskExecutor() {
        return this.simpleReact.getExecutor();
    }


    @Override
    public boolean isAsync() {
        return this.simpleReact.isAsync();
    }

    @Override
    public Queue<U> toQueue() {
        return EagerToQueue.super.toQueue();
    }

    @Override
    public SimpleReactStream<U> withTaskExecutor(final Executor e) {
        return this.withSimpleReact(simpleReact.withExecutor(e));
    }


}
