package cyclops.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import cyclops.container.control.LazyEither5;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

final class LazyEither5Deserializer extends StdDeserializer<LazyEither5<?, ?, ?, ?, ?>> {

    protected LazyEither5Deserializer(JavaType valueType) {
        super(valueType);

    }

    @Override
    public Object deserializeWithType(JsonParser p,
                                      DeserializationContext ctxt,
                                      TypeDeserializer typeDeserializer) throws IOException {
        return super.deserializeWithType(p,
                                         ctxt,
                                         typeDeserializer);
    }

    @Override
    public LazyEither5<?, ?, ?, ?, ?> deserialize(JsonParser p,
                                                  DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonDeserializer ser = ctxt.findRootValueDeserializer(ctxt.getTypeFactory()
                                                                  .constructSimpleType(LazyEitherBean.class,
                                                                                       new JavaType[0]));
        LazyEitherBean x = (LazyEitherBean) ser.deserialize(p,
                                                            ctxt);
        if (x.left1 != null) {
            return LazyEither5.left1(x.left1);
        }
        if (x.left2 != null) {
            return LazyEither5.left2(x.left2);
        }
        if (x.left3 != null) {
            return LazyEither5.left3(x.left2);
        }
        if (x.left4 != null) {
            return LazyEither5.left4(x.left2);
        }
        return LazyEither5.right(x.right);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class LazyEitherBean {

        @Getter
        @Setter
        private Object left1;
        @Getter
        @Setter
        private Object left2;
        @Getter
        @Setter
        private Object left3;
        @Getter
        @Setter
        private Object left4;
        @Getter
        @Setter
        private Object right;
    }


}
