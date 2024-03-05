package com.example.test.model.dto.rest;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class ErrorResponseDto {

    private ErrorCode errorCode;
    private String message;
    private Set<ErrorPropertyDto> errors = new LinkedHashSet<>();

    @Data
    public static class ErrorPropertyDto {

        private String target;
        private String message;

    }

}
