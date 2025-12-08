package com.yachaerang.backend.api.favorite.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.common.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Favorite extends BaseEntity {

    private Long favoriteId;

    private Long memberId;

    private String productCode;

    private PeriodType periodType;
}
