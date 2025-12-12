package com.yachaerang.backend.api.farm.dto.resquest;

import lombok.*;

import java.util.Optional;

public class FarmRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoDto {
        private Optional<Integer> manpower = Optional.empty();
        private Optional<String> location = Optional.empty();
        private Optional<Double> cultivatedArea = Optional.empty();
        private Optional<Double> flatArea = Optional.empty();
        private Optional<String> mainCrop = Optional.empty();
    }
}
