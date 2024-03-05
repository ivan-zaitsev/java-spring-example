package com.example.test.service.json;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

@Service
public interface JsonConverter {

    <T> T convertToObject(Object value, TypeReference<T> type);

    String convertToString(Object value);

}
