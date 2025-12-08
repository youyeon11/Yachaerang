package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PeriodType implements CodeEnum {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    private final String code;

    PeriodType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    /*
    DB의 VARCHAR -> Enum 클래스로 변환
    */
    public static PeriodType fromCode(String code) {

        for (PeriodType periodType : PeriodType.values()) {
            if (periodType.getCode().equals(code)) {
                return periodType;
            }
        }
        log.warn("Invalid period type code: {}", code);
        throw GeneralException.of(ErrorCode.ENUM_MAPPED_FAILED);
    }
}
