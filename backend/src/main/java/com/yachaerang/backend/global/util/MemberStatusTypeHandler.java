package com.yachaerang.backend.global.util;

import com.yachaerang.backend.api.common.MemberStatus;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(MemberStatus.class)
public class MemberStatusTypeHandler extends CodeEnumTypeHandler<MemberStatus> {
    public MemberStatusTypeHandler() {
        super(MemberStatus.class);
    }
}
