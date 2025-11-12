package com.yachaerang.backend.global.exception;

import com.yachaerang.backend.global.response.ErrorCode;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static CustomException of(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }
}
