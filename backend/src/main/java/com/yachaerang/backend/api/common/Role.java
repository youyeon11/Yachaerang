package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public enum Role implements CodeEnum {
    ROLE_ANONYMOUS("ROLE_ANONYMOUS"),
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ;

    private String code;

    Role(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    public static Role fromCode(String code) {
        for (Role role: Role.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        log.warn("Invalid member status code: {}", code);
        throw GeneralException.of(ErrorCode.ENUM_MAPPED_FAILED);
    }
}
