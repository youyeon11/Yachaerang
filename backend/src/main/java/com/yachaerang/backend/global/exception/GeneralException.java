package com.yachaerang.backend.global.exception;


import com.yachaerang.backend.global.response.ErrorCode;

public class GeneralException extends RuntimeException {

    private final ErrorCode errorCode;

    public GeneralException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public static GeneralException of(ErrorCode errorCode) {
        return new GeneralException(errorCode);
    }
}
