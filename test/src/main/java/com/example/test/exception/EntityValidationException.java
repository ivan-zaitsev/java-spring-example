package com.example.test.exception;

import lombok.experimental.StandardException;

import java.io.Serial;

@StandardException
public class EntityValidationException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

}
