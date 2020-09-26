package cyclops.container.filterable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Trait that represents any class with a single argument Filter method Will coerce that method into accepting JDK 8
 * java.util.function.Predicates
 *
 * @param <T> Type of this Filterable
 * @author johnmcclean
 */
public interface Filterable<T> {

    /**
     * Keep only elements for which the supplied predicates hold
     * <p>
     * e.g.
     * <pre>
     * {@code
     *
     *     of(1,2,3).filter(i->i>2);
     *
     *     //[3]
     *
     * }
     * </pre>
     *
     * @param predicate to filter elements by, retaining matches
     * @return Filters with elements removed where the predicate does not hold
     */
    Filterable<T> filter(Predicate<? super T> predicate);

    /**
     * Keep only those elements in a stream that are of a given type.
     *
     * <pre>
     * {@code
     *
     * // (1, 2, 3) ReactiveSeq.of(1, "a", 2, "b",3).ofType(Integer.class)
     *
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    default <U> Filterable<U> ofType(final Class<? extends U> type) {
        Objects.requireNonNull(type,
                               "class object parameter of type U may not be null");
        return (Filterable<U>) filter(tObj -> tObj != null && ((!type.isPrimitive() && type.isAssignableFrom(tObj.getClass()))
            || (type.isPrimitive() && type.isInstance(tObj))));
    }

    /**
     * Remove any elements for which the predicate holds (inverse operation to filter)
     * <p>
     * e.g.
     * <pre>
     * {@code
     *
     *     of(1,2,3).filter(i->i>2);
     *
     *     //[1,2]
     *
     * }
     * </pre>
     *
     * @param predicate to filter elements by, retaining matches
     * @return Filters with elements removed where the predicate does not hold
     */
    default Filterable<T> filterNot(final Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    /**
     * Filter elements retaining only values which are not null
     *
     * <pre>
     * {@code
     *
     *   of(1,2,null,4).nonNull();
     *
     *   //[1,2,4]
     *
     * }
     * </pre>
     *
     * @return Filters with nulls removed
     */
    default Filterable<T> notNull() {
        return filter(t -> t != null);
    }

}
