package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public enum MemberStatus implements CodeEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE")
    ;

    private final String code;
    MemberStatus(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
    /*
    DB의 VARCHAR -> Enum 클래스로 변환
     */
    public static MemberStatus fromCode(String code) {

        for (MemberStatus memberStatus : MemberStatus.values()) {
            if (memberStatus.getCode().equals(code)) {
                return memberStatus;
            }
        }
        log.warn("Invalid member status code: {}", code);
        throw GeneralException.of(ErrorCode.ENUM_MAPPED_FAILED);
    }
}
