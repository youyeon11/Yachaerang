package com.yachaerang.backend.api.common;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;

public enum Role {
    ROLE_ANONYMOUS("anonymous"),
    ROLE_USER("user"),
    ROLE_ADMIN("admin"),
    ;

    private String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Role findRoleByDescription(String description) {
        for (Role role : Role.values()) {
            if (role.getDescription().equals(description)) {
                return role;
            }
        }
        throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
