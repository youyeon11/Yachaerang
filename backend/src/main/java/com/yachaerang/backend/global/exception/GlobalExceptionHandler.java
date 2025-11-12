package com.yachaerang.backend.global.exception;

import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    protected ResponseEntity<ErrorResponse> handlerGeneralException(GeneralException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ErrorResponse.of(errorCode);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request
    ) {
        logError(ex, request);
        return ErrorResponse.of(ErrorCode.TEST_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        logError(ex, request);
        return ErrorResponse.of(ErrorCode.TEST_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        logError(ex, request);
        return ErrorResponse.of(ErrorCode.TEST_ERROR);
    }

    /*
    Error 기록을 위한 logError
     */
    private void logError(Exception e, HttpServletRequest request) {
        log.error("Request URI : [{}] {}", request.getMethod(), request.getRequestURI());
        log.error("Exception : ", e);

        log.error("Exception Type : {}", e.getClass().getName());
        log.error("Exception Message : {}", e.getMessage());
    }
}
