package com.yachaerang.backend.api.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Farm 정보 중 grade와 comment가 비어있으면 이에 대하여 평가를 실행하는 메서드
 */
@Component
@RequiredArgsConstructor
public class FarmEvaluationScheduler {

    private final FarmService farmService;

    @Scheduled(cron = "0 0 * * * *")
    public void fillMissing() {
        farmService.fillMissingFarmEvaluations();
    }
}