package com.yachaerang.backend.api.farm.dto.resquest;

import lombok.*;

public class FarmRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoDto {
        private Integer manpower;
        private String location;
        private Double cultivatedArea;
        private Double flatArea;
        private String mainCrop;
    }
}
