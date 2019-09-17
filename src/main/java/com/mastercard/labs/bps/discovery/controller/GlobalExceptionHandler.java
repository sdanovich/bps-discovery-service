package com.mastercard.labs.bps.discovery.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.mastercard.labs.bps.discovery.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final HttpHeaders headers = new HttpHeaders();
    private static Properties messages;

    static {
        headers.setContentType(MediaType.APPLICATION_JSON);
        messages = new Properties();
        try {
            messages.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/messages_" + Locale.getDefault().getLanguage() + ".properties"));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Provides handling for exceptions throughout this service.
     */
    @ExceptionHandler({Throwable.class})
    public final ResponseEntity<ErrorData> handleException(Exception ex) {
        if (ex instanceof ResourceNotFoundException) {
            return handleExceptionInternal(((ResourceNotFoundException) ex).getStatus(), ((ResourceNotFoundException) ex).getMessages());
        } else if (ex.getCause() instanceof MismatchedInputException || ex.getCause() instanceof InvalidFormatException) {
            return handleExceptionInternal(HttpStatus.BAD_REQUEST, ((MismatchedInputException) ex.getCause()).getPath().stream().
                    filter(path -> messages.get((StringUtils.delimitedListToStringArray(path.getDescription(), "[")[0])) != null).
                    map(path -> (StringUtils.delimitedListToStringArray(path.getDescription(), "[")[0])).toArray(String[]::new));
        }
        return handleExceptionInternal(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public final ResponseEntity<ErrorData> handleExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return handleExceptionInternal(HttpStatus.NOT_FOUND, processFieldErrors(fieldErrors));
    }

    private String[] processFieldErrors(List<FieldError> fieldErrors) {
        if (fieldErrors != null) {
            return fieldErrors.parallelStream().map(fieldError -> String.format("Field %s was rejected with value %s", fieldError.getField(),
                    ObjectUtils.nullSafeToString(fieldError.getRejectedValue()))).toArray(String[]::new);
        }
        return new String[0];
    }

    /**
     * A single place to customize the response body of all Exception types.
     */
    private ResponseEntity<ErrorData> handleExceptionInternal(HttpStatus status, String... messages) {
        return new ResponseEntity<>(new ErrorData(status, messages), headers, status);
    }

    public static ErrorData getErrorData(HttpClientErrorException e, ObjectMapper jacksonObjectMapper) {
        try {
            return jacksonObjectMapper.readValue(e.getResponseBodyAsString(), ErrorData.class);
        } catch (IOException e1) {
            return new ErrorData();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorData {

        private HttpStatus status;
        private String[] messages;

        public HttpStatus getStatus() {
            return status;
        }

        public String[] getMessages() {
            return Arrays.stream(messages).map(o ->
                    GlobalExceptionHandler.messages.getProperty(o) != null ?
                            GlobalExceptionHandler.messages.getProperty(o) : o).toArray(String[]::new);
        }
    }
}