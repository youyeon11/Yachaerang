package com.yachaerang.backend.global.util;

import com.yachaerang.backend.api.common.SenderRole;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(SenderRole.class)
public class SenderRoleHandler extends CodeEnumTypeHandler<SenderRole> {

    public SenderRoleHandler() {
        super(SenderRole.class);
    }
}
