package com.example.test.service.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class JsonConverterJacksonTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JsonConverterJackson jsonConverter;

    @Test
    void convertToObject_shouldThrowException_whenObjectMapperThrowsException() {
        TestObject testObject = new TestObject("test");
        TypeReference<Map<String, String>> type = new TypeReference<>() {};

        doThrow(RuntimeException.class).when(objectMapper).convertValue(testObject, type);

        Executable executable = () -> jsonConverter.convertToObject(testObject, type);

        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void convertToObject_shouldReturnConvertedObject() {
        TestObject testObject = new TestObject("test");
        TypeReference<Map<String, String>> type = new TypeReference<>() {};

        Map<String, String> expectedConvertedObject = Map.of("name", "test");
        doReturn(expectedConvertedObject).when(objectMapper).convertValue(testObject, type);

        Map<String, String> actualConvertedObject = jsonConverter.convertToObject(testObject, type);

        assertEquals(expectedConvertedObject, actualConvertedObject);
    }

    class TestObject {

        private final String name;

        public TestObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
