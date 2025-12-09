package com.yachaerang.backend.global.util;

import com.yachaerang.backend.api.common.PeriodType;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(PeriodType.class)
public class PeriodTypeHandler extends CodeEnumTypeHandler<PeriodType> {

    public PeriodTypeHandler() {
        super(PeriodType.class);
    }
}
