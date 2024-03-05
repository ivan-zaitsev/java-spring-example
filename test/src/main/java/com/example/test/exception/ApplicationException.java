package com.example.test.exception;

import com.example.test.model.dto.rest.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.StandardException;

import java.io.Serial;

@Setter
@Getter
@StandardException
public class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;

}
