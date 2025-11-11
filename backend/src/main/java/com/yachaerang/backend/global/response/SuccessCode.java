package com.yachaerang.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    SUCCESS_CODE(HttpStatus.OK, "200OK", "성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
