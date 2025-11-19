package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;

public enum MemberStatus {
    ACTIVE("active"),
    INACTIVE("inactive")
    ;

    private String description;
    MemberStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public MemberStatus findMemberStatusByDescription(String description) {
        for (MemberStatus memberStatus : MemberStatus.values()) {
            if (memberStatus.getDescription().equals(description)) {
                return memberStatus;
            }
        }
        throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
