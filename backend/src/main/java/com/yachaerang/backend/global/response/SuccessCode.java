package com.yachaerang.backend.global.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SuccessCode implements BaseCode{

    OK(HttpStatus.OK, "200", "요청이 성공적으로 처리되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "204", "성공하였으나 반환할 내용이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
