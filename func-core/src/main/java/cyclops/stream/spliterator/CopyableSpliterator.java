package cyclops.stream.spliterator;

import java.util.Spliterator;

/**
 * Created by johnmcclean on 22/12/2016.
 */
public interface CopyableSpliterator<T> extends Spliterator<T> {

    static <T> Spliterator<T> copy(Spliterator<T> split) {
        if (split instanceof CopyableSpliterator) {
            return ((CopyableSpliterator<T>) split).copy();
        }
        return split;
    }

    static <T> Spliterator[] copy(Spliterator[] spliterators) {
        Spliterator[] copies = new Spliterator[spliterators.length];
        int i = 0;
        for (Spliterator next : spliterators) {
            copies[i++] = copy(next);
        }

        return copies;
    }

    Spliterator<T> copy();
}
