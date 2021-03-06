package cyclops.container.persistent.views;

import cyclops.container.persistent.PersistentSet;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;

public interface SetView<T> extends Set<T> {

    @Override
    @Deprecated
    default boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    boolean add(T t);

    @Override
    @Deprecated
    boolean remove(Object o);

    @Override
    @Deprecated
    boolean addAll(Collection<? extends T> c);


    @Override
    @Deprecated
    boolean removeAll(Collection<?> c);

    @Override
    @Deprecated
    boolean retainAll(Collection<?> c);


    @Override
    @Deprecated
    void clear();

    @AllArgsConstructor
    class Impl<T> extends AbstractSet<T> implements SetView<T> {

        private final PersistentSet<T> host;

        @Override
        public int size() {
            return host.size();
        }

        @Override
        public boolean isEmpty() {
            return host.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return host.containsValue((T) o);
        }

        @Override
        public Iterator<T> iterator() {
            return host.iterator();
        }

        @Override
        public Object[] toArray() {
            return host.stream()
                       .toArray();
        }


        @Override
        public boolean add(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {

            for (Object n : c) {
                if (!contains(n)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }


        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }


    }
}
