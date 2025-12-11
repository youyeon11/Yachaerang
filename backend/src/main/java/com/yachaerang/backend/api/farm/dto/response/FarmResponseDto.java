package com.yachaerang.backend.api.farm.dto.response;

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
}
