package com.yachaerang.backend.global.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ApiResponse<T> {

    private final HttpStatus httpStatus;
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    // 내부에서 status 추출
    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(code.getHttpStatus(),true, code.getCode(), code.getMessage(), data);
    }

    public static ApiResponse<Void> success(SuccessCode code) {
        return new ApiResponse<>(code.getHttpStatus(), true, code.getCode(), code.getMessage(), null);
    }
}
