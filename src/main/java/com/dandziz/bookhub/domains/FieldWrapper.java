package com.dandziz.bookhub.domains;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Getter;

import java.io.IOException;

@Getter
@JsonDeserialize(using = FieldWrapper.FieldWrapperDeserializer.class)
@JsonSerialize(using = FieldWrapper.FieldWrapperSerializer.class)
public class FieldWrapper<T> {

    private final boolean present;
    private final T value;

    private FieldWrapper(boolean present, T value) {
        this.present = present;
        this.value = value;
    }

    public static <T> FieldWrapper<T> absent() {
        return new FieldWrapper<>(false, null);
    }

    public static <T> FieldWrapper<T> of(T value) {
        return new FieldWrapper<>(true, value);
    }

    public static <T> FieldWrapper<T> ofValue(T value) {
        return new FieldWrapper<>(true, value);
    }

    public void ifPresent(java.util.function.Consumer<T> consumer) {
        if (present) {
            consumer.accept(value);
        }
    }

    @Override
    public String toString() {
        return present ? "FieldWrapper[present,value=%s]".formatted(value)
            : "FieldWrapper[absent]";
    }

    public static class FieldWrapperDeserializer<T> extends JsonDeserializer<FieldWrapper<T>>
        implements ContextualDeserializer {

        private final JavaType valueType;
        private final Class<?> rawClass;

        public FieldWrapperDeserializer() {
            this.valueType = null;
            this.rawClass = null;
        }

        private FieldWrapperDeserializer(JavaType valueType) {
            this.valueType = valueType;
            this.rawClass = (valueType != null ? valueType.getRawClass() : null);
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
            JavaType wrapperType = property.getType();
            JavaType innerType = wrapperType.containedType(0);
            return new FieldWrapperDeserializer<>(innerType);
        }

        @Override
        public FieldWrapper<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_NULL) {
                return FieldWrapper.ofValue(null);
            }
            if (valueType == null || rawClass == null) {
                Object raw = ctxt.readValue(p, Object.class);
                @SuppressWarnings("unchecked")
                T value = (T) raw;
                return FieldWrapper.ofValue(value);
            }

            Object raw = ctxt.readValue(p, valueType);
            Object casted = rawClass.cast(raw);
            @SuppressWarnings("unchecked") T value = (T) casted;
            return FieldWrapper.ofValue(value);
        }

        @Override
        public FieldWrapper<T> getNullValue(DeserializationContext ctxt) {
            return FieldWrapper.ofValue(null);
        }
    }

    // --- Serializer ---
    public static class FieldWrapperSerializer extends JsonSerializer<FieldWrapper<?>> {
        @Override
        public void serialize(FieldWrapper<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (!value.isPresent()) {
                gen.writeNull();
            } else {
                gen.writeObject(value.getValue());
            }
        }
    }
}
