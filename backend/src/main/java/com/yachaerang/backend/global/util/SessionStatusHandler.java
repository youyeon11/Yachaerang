package com.yachaerang.backend.global.util;

import com.yachaerang.backend.api.common.SessionStatus;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(SessionStatus.class)
public class SessionStatusHandler extends CodeEnumTypeHandler<SessionStatus> {

    public SessionStatusHandler() {
        super(SessionStatus.class);
    }
}
