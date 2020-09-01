package cyclops.jackson.deserializers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import cyclops.container.persistent.PersistentMap;
import cyclops.container.traversable.IterableX;
import cyclops.container.control.Either;
import cyclops.container.control.Eval;
import cyclops.async.Future;
import cyclops.container.control.Ior;
import cyclops.container.control.LazyEither;
import cyclops.container.control.LazyEither3;
import cyclops.container.control.LazyEither4;
import cyclops.container.control.Maybe;
import cyclops.container.control.Option;
import cyclops.container.control.Trampoline;
import cyclops.container.control.Unrestricted;
import cyclops.container.immutable.tuple.Tuple0;
import cyclops.container.immutable.tuple.Tuple1;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.container.immutable.tuple.Tuple3;
import cyclops.container.immutable.tuple.Tuple4;
import cyclops.container.immutable.tuple.Tuple5;
import cyclops.container.immutable.tuple.Tuple6;
import cyclops.container.immutable.tuple.Tuple7;
import cyclops.container.immutable.tuple.Tuple8;
import java.util.HashSet;
import java.util.Set;

public class CyclopsDeserializers extends Deserializers.Base {

    private Set<Class> tuples = new HashSet<>();

    {
        tuples.add(Tuple0.class);
        tuples.add(Tuple1.class);
        tuples.add(Tuple2.class);
        tuples.add(Tuple3.class);
        tuples.add(Tuple4.class);
        tuples.add(Tuple5.class);
        tuples.add(Tuple6.class);
        tuples.add(Tuple7.class);
        tuples.add(Tuple8.class);
    }

    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type,
                                                              DeserializationConfig config,
                                                              BeanDescription beanDesc,
                                                              TypeDeserializer elementTypeDeserializer,
                                                              JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException {
        Class<?> raw = type.getRawClass();
        if (IterableX.class.isAssignableFrom(type.getRawClass())) {
            return new IterableXDeserializer(raw,
                                             type.containedTypeOrUnknown(0)
                                                 .getRawClass(),
                                             elementTypeDeserializer,
                                             elementDeserializer,
                                             type);
        }
        return super.findCollectionLikeDeserializer(type,
                                                    config,
                                                    beanDesc,
                                                    elementTypeDeserializer,
                                                    elementDeserializer);
    }

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                                                    DeserializationConfig config,
                                                    BeanDescription beanDesc) throws JsonMappingException {
        Class<?> raw = type.getRawClass();

        if (raw == Maybe.class) {
            return new MaybeDeserializer(type);
        }
        if (raw == Option.class) {
            return new OptionDeserializer(type);
        }
        if (raw == Eval.class) {
            return new EvalDeserializer(type);
        }
        if (raw == Future.class) {
            return new EvalDeserializer(type);
        }
        if (raw == Ior.class) {
            return new IorDeserializer(type);
        }
        if (raw == LazyEither.class) {
            return new LazyEitherDeserializer(type);
        }
        if (raw == LazyEither3.class) {
            return new LazyEither3Deserializer(type);
        }
        if (raw == LazyEither4.class) {
            return new LazyEither4Deserializer(type);
        }

        if (raw == Either.class) {
            return new EitherDeserializer(type);
        }
        if (raw == Trampoline.class) {
            return new TrampolineDeserializer(type);
        }
        if (raw == Unrestricted.class) {
            return new TrampolineDeserializer(type);
        }
        if (tuples.contains(raw)) {
            return new TupleDeserializer(raw);
        }
        if (PersistentMap.class.isAssignableFrom(type.getRawClass())) {
            return new PersistentMapDeserializer(raw);
        }
        return super.findBeanDeserializer(type,
                                          config,
                                          beanDesc);
    }

    @Override
    public JsonDeserializer<?> findCollectionDeserializer(CollectionType type,
                                                          DeserializationConfig config,
                                                          BeanDescription beanDesc,
                                                          TypeDeserializer elementTypeDeserializer,
                                                          JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        if (IterableX.class.isAssignableFrom(type.getRawClass())) {
            return new IterableXDeserializer(type.getRawClass(),
                                             type.containedTypeOrUnknown(0)
                                                 .getRawClass(),
                                             elementTypeDeserializer,
                                             elementDeserializer,
                                             type);
        }
        return super.findCollectionDeserializer(type,
                                                config,
                                                beanDesc,
                                                elementTypeDeserializer,
                                                elementDeserializer);
    }

    @Override
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType type,
                                                         DeserializationConfig config,
                                                         BeanDescription bean,
                                                         TypeDeserializer typeDeserializer,
                                                         JsonDeserializer<?> jsonDeserializer) throws JsonMappingException {

        return super.findReferenceDeserializer(type,
                                               config,
                                               bean,
                                               typeDeserializer,
                                               jsonDeserializer);
    }

}