package com.oath.cyclops.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import cyclops.control.Future;
import java.io.IOException;

final class FutureDeserializer extends StdDeserializer<Future<?>> implements ResolvableDeserializer {

    JavaType valueType;
    private JsonDeserializer<Object> deser;

    protected FutureDeserializer(JavaType valueType) {
        super(valueType);
        this.valueType = valueType;
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
    public Future<?> deserialize(JsonParser p,
                                 DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Future.ofResult(deser.deserialize(p,
                                                 ctxt));
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        if (valueType.isContainerType()) {
            deser = ctxt.findRootValueDeserializer(valueType.getContentType());
        } else {
            deser = ctxt.findRootValueDeserializer(valueType.containedTypeOrUnknown(0));
        }
    }

    @Override
    public Future<?> getNullValue(DeserializationContext ctxt) {
        return Future.ofResult(null);
    }


}
