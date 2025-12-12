package com.yachaerang.backend.api.farm.dto.response;

import com.yachaerang.backend.api.farm.entity.Farm;
import lombok.*;

public class FarmResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoDto {
        private Long id;
        private Integer manpower;
        private String location;
        private Double cultivatedArea;
        private Double flatArea;
        private String mainCrop;
        private String grade;
        private String comment;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EvaluateDto {
        private String grade;
        private String comment;
    }

    /*
    변환 메서드
     */
    public static FarmResponseDto.InfoDto toInfoDto(Farm farm) {
        return FarmResponseDto.InfoDto.builder()
                .id(farm.getId())
                .manpower(farm.getManpower())
                .location(farm.getLocation())
                .cultivatedArea(farm.getCultivatedArea())
                .flatArea(farm.getFlatArea())
                .mainCrop(farm.getMainCrop())
                .grade(farm.getGrade())
                .comment(farm.getComment())
                .build();
    }
}
