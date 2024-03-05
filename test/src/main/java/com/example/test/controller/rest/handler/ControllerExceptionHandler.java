package com.example.test.controller.rest.handler;

import com.example.test.exception.EntityValidationException;
import com.example.test.model.dto.rest.ErrorCode;
import com.example.test.model.dto.rest.ErrorResponseDto;
import com.example.test.model.dto.rest.ErrorResponseDto.ErrorPropertyDto;
import org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeAttribute;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private final ServerProperties serverProperties;

    public ControllerExceptionHandler(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
            Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        ErrorResponseDto response = buildErrorResponse(ex);

        return ResponseEntity.status(statusCode).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorResponseDto response = buildErrorResponse(ex);
        response.setErrorCode(ErrorCode.REQUEST_PARAMETER_NOT_VALID);

        return ResponseEntity.status(status).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorResponseDto response = buildErrorResponse(ex);
        response.setErrorCode(ErrorCode.REQUEST_BODY_NOT_VALID);
        response.setErrors(buildErrorProperties(ex.getBindingResult().getFieldErrors()));

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponseDto> handleRestClientException(RestClientException ex) {
        ErrorResponseDto response = buildErrorResponse(ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityValidationException(EntityValidationException ex) {
        ErrorResponseDto response = buildErrorResponse(ex);
        response.setErrorCode(ex.getErrorCode());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ErrorResponseDto buildErrorResponse(Exception ex) {
        ErrorResponseDto response = new ErrorResponseDto();
        if (IncludeAttribute.ALWAYS.equals(serverProperties.getError().getIncludeMessage())) {
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    private Set<ErrorPropertyDto> buildErrorProperties(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(fieldError -> {
                    ErrorResponseDto.ErrorPropertyDto errorPropertyDto = new ErrorResponseDto.ErrorPropertyDto();
                    errorPropertyDto.setTarget(fieldError.getField());
                    errorPropertyDto.setMessage(fieldError.getDefaultMessage());
                    return errorPropertyDto;
                }).collect(Collectors.toSet());
    }

}
