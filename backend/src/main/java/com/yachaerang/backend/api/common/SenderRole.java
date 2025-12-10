package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;

public enum SenderRole implements CodeEnum {
    USER("USER"),
    ASSISTANT("ASSISTANT");

    private final String code;

    SenderRole(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public static SenderRole fromCode(String code) {

        for (SenderRole role : SenderRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        LogUtil.warn("Invalid sender role code: {}", code);
        throw GeneralException.of(ErrorCode.ENUM_MAPPED_FAILED);
    }
}
