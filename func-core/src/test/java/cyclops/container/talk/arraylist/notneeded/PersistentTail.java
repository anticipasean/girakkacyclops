package cyclops.container.talk.arraylist.notneeded;

import java.util.Arrays;
import lombok.Getter;

public class PersistentTail<E> {

    @Getter
    private final E[] array;

    public PersistentTail(E[] array) {
        this.array = array;
    }

    public E getOrElse(int pos,
                       E alt) {
        int indx = pos & 0x01f;
        if (indx < array.length) {
            return (E) array[indx];
        }
        return alt;
    }

    public PersistentTail<E> set(int pos,
                                 E value) {
        E[] updatedNodes = Arrays.copyOf(array,
                                         array.length);
        updatedNodes[pos] = value;
        return new PersistentTail<>(updatedNodes);
    }
}
