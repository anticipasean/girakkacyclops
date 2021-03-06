package cyclops.stream.iterator;

import cyclops.reactive.ReactiveSeq;
import cyclops.stream.type.Streamable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReversedIterator<U> implements Streamable<U> {

    private final List<U> list;

    public ReversedIterator(List<U> list) {
        this.list = list;
    }

    public List<U> getValue() {
        return list;
    }

    @Override
    public Iterable<U> getStreamable() {
        return list;
    }

    @Override
    public ReactiveSeq<U> stream() {
        return ReactiveSeq.fromIterator(reversedIterator());
    }

    public Iterator<U> reversedIterator() {

        final ListIterator<U> iterator = list.listIterator(list.size());

        return new Iterator<U>() {

            @Override
            public boolean hasNext() {
                return iterator.hasPrevious();
            }

            @Override
            public U next() {
                return iterator.previous();
            }

        };
    }

}
