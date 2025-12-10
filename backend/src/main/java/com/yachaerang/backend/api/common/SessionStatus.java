package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;

public enum SessionStatus implements CodeEnum{
    ACTIVE("ACTIVE"),
    ENDED("ENDED");

    private final String code;

    SessionStatus(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public static SessionStatus fromCode(String code) {
        for (SessionStatus status : SessionStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        LogUtil.warn("Invalid session status code: {}", code);
        throw GeneralException.of(ErrorCode.ENUM_MAPPED_FAILED);
    }
}
