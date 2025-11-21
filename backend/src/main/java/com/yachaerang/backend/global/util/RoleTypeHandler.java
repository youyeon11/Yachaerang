package com.yachaerang.backend.global.util;

import com.yachaerang.backend.api.common.Role;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Role.class)
public class RoleTypeHandler extends CodeEnumTypeHandler<Role> {

    public RoleTypeHandler() {
        super(Role.class);
    }
}

