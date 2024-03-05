package com.example.test.controller.rest.handler;

import com.example.test.model.dto.rest.ErrorResponseDto;
import com.example.test.service.json.JsonConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class ControllerErrorAttributes extends DefaultErrorAttributes {

    private final JsonConverter jsonConverter;

    public ControllerErrorAttributes(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        ErrorResponseDto response = new ErrorResponseDto();
        if (options.isIncluded(Include.MESSAGE)) {
            response.setMessage(getMessage(webRequest, extractError(webRequest)));
        }

        return jsonConverter.convertToObject(response, new TypeReference<Map<String, Object>>() {});
    }

    private Throwable extractError(WebRequest webRequest) {
        Throwable error = getError(webRequest);
        while (error instanceof ServletException && error.getCause() != null) {
            error = error.getCause();
        }
        return error;
    }

}
