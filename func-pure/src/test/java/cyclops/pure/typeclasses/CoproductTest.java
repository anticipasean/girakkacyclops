package cyclops.pure.typeclasses;

import cyclops.container.control.Maybe;
import cyclops.function.higherkinded.DataWitness.option;
import cyclops.function.higherkinded.DataWitness.stream;
import cyclops.pure.container.functional.Coproduct;
import cyclops.pure.instances.jdk.StreamInstances;
import cyclops.pure.kinds.StreamKind;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class CoproductTest {

    Coproduct<stream,option,Integer> just = Coproduct.just(10, StreamInstances.definitions());

    @Test
    public void map(){
        assertThat(just.map(i->i*2),equalTo(Coproduct.just(20, StreamInstances.definitions())));
    }
    @Test
    public void filter(){
        assertThat(just.filter(i->i<10),equalTo(Coproduct.none(StreamInstances.definitions())));
    }
    @Test
    public void filterTrue(){
        assertThat(just.filter(i->i<11),equalTo(just));
    }

    @Test
    public void visit(){
        assertThat(just.fold(s-> StreamKind.narrowK(s).count(), m-> Maybe.narrowK(m).toOptional().get()),equalTo(10));
    }
}
