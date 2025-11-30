package com.yachaerang.backend.api.farm.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.member.entity.Member;
import lombok.Data;

@Data
public class Farm extends BaseEntity {

    private Long id;

    private int manpower;

    private String location;

    private double cultivatedArea;

    private double flatArea;

    private String mainCrop;

    private String grade;

    private String comment;

    private Member member;
}
