package cyclops.stream.operator;

import cyclops.function.combiner.Monoid;
import cyclops.function.combiner.Reducer;
import cyclops.stream.companion.Streams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultiReduceOperator<R> {

    private final Stream<R> stream;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<R> reduce(final Iterable<? extends Monoid<R>> reducers) {
        final Reducer<List<R>, R> m = new Reducer<List<R>, R>() {
            @Override
            public Function<? super R, List<R>> conversion() {
                return a -> Arrays.asList(a);
            }

            @Override
            public List<R> zero() {
                return Streams.stream(reducers)
                              .map(r -> r.zero())
                              .collect(Collectors.toList());
            }


            @Override
            public Stream mapToType(final Stream stream) {
                return stream.map(value -> Arrays.asList(value));
            }

            @Override
            public List<R> apply(final List<R> c1,
                                 final List<R> c2) {
                final List l = new ArrayList<>();
                int i = 0;
                for (final Monoid next : reducers) {
                    l.add(next.apply(c1.get(i),
                                     c2.get(0)));
                    i++;
                }

                return l;

            }
        };
        return m.foldMap(stream);
    }
}
