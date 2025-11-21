package com.yachaerang.backend.global.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode implements BaseCode{

    TEST_ERROR(HttpStatus.BAD_REQUEST, "TESTERROR", "테스트 에러입니다."),

    // request
    UNMATCHED_REQUEST(HttpStatus.BAD_REQUEST, "METHODERROR", "요청 방식이 잘못됐습니다."),

    // MyBatis
    ENUM_MAPPED_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MYBATIS_001", "DB로부터 Java의 Eum을 매핑하는데에 실패했습니다."),
    MYBATIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MYBATIS_002", "DB로부터 오류 발생하였습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER40002", "이미 존재하는 회원입니다."),

    // login
    UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "LOGIN4001", "잘못된 비밀번호입니다."),

    // Cookie
    COOKIE_NOT_FOUND(HttpStatus.NOT_FOUND, "COOKIE4041", "쿠키를 찾을 수 없습니다."),

    // token
    TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_001", "토큰 생성에 실패하였습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_002", "만료된 토큰입니다"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_003", "유효하지 않은 토큰입니다"),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "TOKEN_004", "지원하지 않는 토큰 형식입니다"),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_005", "토큰 서명이 유효하지 않습니다"),

    CLAIM_EXTRACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_006", "토큰에서 정보 추출에 실패하였습니다."),
    CLAIM_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN_007", "토큰에 필수 정보가 없습니다"),
    CLAIM_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "TOKEN_008", "토큰 정보 형식이 올바르지 않습니다"),

    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증된 사용자가 아닙니다."),

    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_009", "토큰이 존재하지 않습니다"),
    TOKEN_HEADER_INVALID(HttpStatus.BAD_REQUEST, "TOKEN_010", "토큰 헤더 형식이 올바르지 않습니다"),

    UUID_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "UUID_001", "UUID 생성에 실패했습니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500ERROR", "서버에서 장애가 일어났습니다."),
    ;

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
