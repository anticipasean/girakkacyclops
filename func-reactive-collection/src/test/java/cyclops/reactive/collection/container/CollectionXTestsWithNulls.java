package cyclops.reactive.collection.container;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import cyclops.reactive.collection.container.fluent.FluentCollectionX;
import java.util.function.Supplier;
import org.junit.Test;

public abstract class CollectionXTestsWithNulls extends AbstractOrderDependentCollectionXTest {

    public abstract <T> FluentCollectionX<T> of(T... values);

    @Test
    public void testSkipUntilWithNulls() {
        Supplier<CollectionX<Integer>> s = () -> of(1,
                                                    2,
                                                    null,
                                                    3,
                                                    4,
                                                    5);

        assertTrue(s.get()
                    .dropUntil(i -> true)
                    .toList()
                    .containsAll(asList(1,
                                        2,
                                        null,
                                        3,
                                        4,
                                        5)));
    }

    @Test
    public void testLimitUntilWithNulls() {

        System.out.println(of(1,
                              2,
                              null,
                              3,
                              4,
                              5).takeUntil(i -> false)
                                .toList());
        assertTrue(of(1,
                      2,
                      null,
                      3,
                      4,
                      5).takeUntil(i -> false)
                        .toList()
                        .containsAll(asList(1,
                                            2,
                                            null,
                                            3,
                                            4,
                                            5)));
    }
}
