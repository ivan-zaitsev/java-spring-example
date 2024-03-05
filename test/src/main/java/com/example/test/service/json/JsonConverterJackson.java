package com.example.test.service.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonConverterJackson implements JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverterJackson(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T convertToObject(Object value, TypeReference<T> type) {
        try {
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert value to object", e);
        }
    }

    @Override
    public String convertToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert object to string", e);
        }
    }

}
