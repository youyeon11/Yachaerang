package com.yachaerang.backend.api.farm.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Farm extends BaseEntity {

    private Long id;

    private Integer manpower;

    private String location;

    private Double cultivatedArea;

    private Double flatArea;

    private String mainCrop;

    private String grade;

    private String farmType;

    private String comment;

    private Long memberId;
}
