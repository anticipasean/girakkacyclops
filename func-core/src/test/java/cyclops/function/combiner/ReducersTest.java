package cyclops.function.combiner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.container.immutable.impl.Bag;
import cyclops.function.companion.Reducers;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.Test;

public class ReducersTest {

    @Test
    public void toBagX() throws Exception {
        assertThat(Reducers.toPersistentBag()
                           .foldMap(Stream.of(1,
                                              2,
                                              3)),
                   equalTo(Bag.of(1,
                                  2,
                                  3)));
        assertThat(Bag.empty()
                      .plus(10),
                   equalTo(Bag.of(10)));
        assertThat(Bag.empty()
                      .plusAll(Arrays.asList(10)),
                   equalTo(Bag.of(10)));
        assertThat(Bag.empty()
                      .plus(5)
                      .plusAll(Arrays.asList(10,
                                             20)),
                   equalTo(Bag.of(5,
                                  10,
                                  20)));
    }

}
