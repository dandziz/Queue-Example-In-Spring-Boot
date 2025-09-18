package com.dandziz.bookhub;

import com.dandziz.bookhub.domains.BookInfo;
import com.dandziz.bookhub.domains.FieldWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldWrapperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testDeserializeAbsent() throws Exception {
        String json = "{}";
        BookInfo req = mapper.readValue(json, BookInfo.class);

        assertFalse(req.getStartDate().isPresent());
    }

    @Test
    void testDeserializeNull() throws Exception {
        String json = "{\"startDate\": null}";
        BookInfo req = mapper.readValue(json, BookInfo.class);

        assertTrue(req.getStartDate().isPresent());
        assertNull(req.getStartDate().getValue());
    }

    @Test
    void testDeserializeValue() throws Exception {
        String json = "{\"startDate\": 123}";
        BookInfo req = mapper.readValue(json, BookInfo.class);

        assertTrue(req.getStartDate().isPresent());
        assertEquals(123L, req.getStartDate().getValue());
    }

    @Test
    void testSerializeNull() throws Exception {
        BookInfo req = new BookInfo();
        req.setStartDate(FieldWrapper.of(null));

        String json = mapper.writeValueAsString(req);

        assertEquals("{\"startDate\":null}", json);
    }

    @Test
    void testSerializeValue() throws Exception {
        BookInfo req = new BookInfo();
        req.setStartDate(FieldWrapper.of(123L));

        String json = mapper.writeValueAsString(req);

        assertEquals("{\"startDate\":123}", json);
    }

    @Test
    void testDeserializeExplicitNullVsAbsent() throws Exception {
        String absentJson = "{}";
        String nullJson = "{\"startDate\": null}";

        BookInfo absentReq = mapper.readValue(absentJson, BookInfo.class);
        BookInfo nullReq = mapper.readValue(nullJson, BookInfo.class);

        assertNotNull(absentReq.getStartDate());
        assertFalse(absentReq.getStartDate().isPresent());

        assertNotNull(nullReq.getStartDate());
        assertTrue(nullReq.getStartDate().isPresent());
        assertNull(nullReq.getStartDate().getValue());
    }

    @Test
    void testDeserializeDifferentNumberTypes() throws Exception {
        String intJson = "{\"startDate\": 123}";
        BookInfo intReq = mapper.readValue(intJson, BookInfo.class);
        assertEquals(123L, intReq.getStartDate().getValue());

        String longJson = "{\"startDate\": 1234567890123}";
        BookInfo longReq = mapper.readValue(longJson, BookInfo.class);
        assertEquals(1234567890123L, longReq.getStartDate().getValue());
    }

    @Test
    void testDeserializeStringValue() throws Exception {
        String json = "{\"startDate\": \"456\"}";
        BookInfo req = mapper.readValue(json, BookInfo.class);

        assertTrue(req.getStartDate().isPresent());
        assertEquals(456L, req.getStartDate().getValue());
    }

    @Test
    void testSerializeExplicitNull() throws Exception {
        BookInfo req = new BookInfo();
        req.setStartDate(FieldWrapper.of(null));

        String json = mapper.writeValueAsString(req);

        assertEquals("{\"startDate\":null}", json);
    }

    @Test
    void testSerializeExplicitValue() throws Exception {
        BookInfo req = new BookInfo();
        req.setStartDate(FieldWrapper.of(789L));

        String json = mapper.writeValueAsString(req);

        assertEquals("{\"startDate\":789}", json);
    }

    @Test
    void testRoundTripValue() throws Exception {
        String originalJson = "{\"startDate\": 321}";
        BookInfo req = mapper.readValue(originalJson, BookInfo.class);

        String serialized = mapper.writeValueAsString(req);
        BookInfo again = mapper.readValue(serialized, BookInfo.class);

        assertEquals(321L, again.getStartDate().getValue());
    }

    @Test
    void testRoundTripNull() throws Exception {
        String originalJson = "{\"startDate\": null}";
        BookInfo req = mapper.readValue(originalJson, BookInfo.class);

        String serialized = mapper.writeValueAsString(req);
        BookInfo again = mapper.readValue(serialized, BookInfo.class);

        assertTrue(again.getStartDate().isPresent());
        assertNull(again.getStartDate().getValue());
    }

    @Test
    void testAbsentFieldIsNotNullWrapper() throws Exception {
        String json = "{}";
        BookInfo book = mapper.readValue(json, BookInfo.class);
        assertNotNull(book.getStartDate());
        assertFalse(book.getStartDate().isPresent());
    }

    @Test
    void testExplicitNullIsPresentButNullValue() throws Exception {
        String json = "{\"startDate\": null}";
        BookInfo book = mapper.readValue(json, BookInfo.class);
        assertTrue(book.getStartDate().isPresent());
        assertNull(book.getStartDate().getValue());
    }

    @Test
    void testInvalidTypeThrowsException() {
        String json = "{\"startDate\": \"not-a-date\"}";
        assertThrows(JsonProcessingException.class, () -> mapper.readValue(json, BookInfo.class));
    }

    @Test
    void testInvalidStringForLocalDateFails() {
        String json = "{\"startDate\": \"not-a-date\"}";
        assertThrows(JsonProcessingException.class,
            () -> mapper.readValue(json, BookInfo.class));
    }

    @Test
    void testObjectForLocalDateFails() {
        String json = "{\"startDate\": {\"year\":2025}}";
        assertThrows(JsonProcessingException.class,
            () -> mapper.readValue(json, BookInfo.class));
    }

    @Test
    void testArrayInsteadOfValueFails() {
        String json = "{\"startDate\": []}";
        assertThrows(JsonProcessingException.class, () -> mapper.readValue(json, BookInfo.class));
    }

    @Test
    void testObjectInsteadOfValueFails() {
        String json = "{\"startDate\": {\"day\":1}}";
        assertThrows(JsonProcessingException.class, () -> mapper.readValue(json, BookInfo.class));
    }

    @Test
    void testOfNullIsPresent() {
        FieldWrapper<String> wrapper = FieldWrapper.ofValue(null);
        assertTrue(wrapper.isPresent());
        assertNull(wrapper.getValue());
    }

    @Test
    void testSerializeAbsentAsNull() throws Exception {
        BookInfo book = new BookInfo();
        book.setStartDate(FieldWrapper.absent());

        String json = mapper.writeValueAsString(book);
        assertTrue(json.contains("\"startDate\":null"));
    }

    @Test
    void testSerializePresentNullAsNull() throws Exception {
        BookInfo book = new BookInfo();
        book.setStartDate(FieldWrapper.ofValue(null));

        String json = mapper.writeValueAsString(book);
        assertTrue(json.contains("\"startDate\":null"));
    }

    @Test
    void testIfPresentNotCalledWhenAbsent() {
        FieldWrapper<String> wrapper = FieldWrapper.absent();
        wrapper.ifPresent(v -> fail("Should not be called"));
    }

    @Test
    void testToStringReflectsState() {
        assertEquals("FieldWrapper[absent]", FieldWrapper.absent().toString());
        assertEquals("FieldWrapper[present,value=hello]", FieldWrapper.ofValue("hello").toString());
    }
}

