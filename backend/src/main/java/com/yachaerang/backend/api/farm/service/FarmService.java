package com.yachaerang.backend.api.farm.service;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.entity.Farm;
import com.yachaerang.backend.api.farm.repository.FarmMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmMapper farmMapper;
    private final FarmEvaluationService farmEvaluationService;
    private final AuthenticatedMemberProvider authenticatedMemberProvider;

    /**
     * 나의 농장 정보 저장하기(신규 저장)
     */
    public CompletableFuture<FarmResponseDto.InfoDto> saveFarmInfo(FarmRequestDto.InfoDto requestDto) {

        Farm farm = saveFarm(requestDto);

        // 비동기 작업 시작
        CompletableFuture<FarmResponseDto.EvaluateDto> evaluationFuture = farmEvaluationService.generateGradeAndComment(requestDto);

        return evaluationFuture.thenApply(evaluationDto -> {

            // 평가 결과 DB 반영
            farmMapper.updateEvaluation(
                    farm.getId(), evaluationDto.getGrade(), evaluationDto.getComment()
            );
            farm.setGrade(evaluationDto.getGrade());
            farm.setComment(evaluationDto.getComment());

            return FarmResponseDto.InfoDto.builder()
                    .manpower(farm.getManpower())
                    .location(farm.getLocation())
                    .cultivatedArea(farm.getCultivatedArea())
                    .flatArea(farm.getFlatArea())
                    .mainCrop(farm.getMainCrop())
                    .grade(farm.getGrade())
                    .grade(farm.getComment())
                    .build();
        }).exceptionally(e -> {
            LogUtil.error("비동기 작업 중 오류 : {}", e.getMessage());
            return FarmResponseDto.InfoDto.builder()
                    .manpower(farm.getManpower())
                    .location(farm.getLocation())
                    .cultivatedArea(farm.getCultivatedArea())
                    .mainCrop(farm.getMainCrop())
                    .grade("N/A")
                    .comment("평가 실패")
                    .build();
        });
    }

    /**
     * 실제 DB에 저장하기 위한 메서드(트랜잭션 확보 및 비동기와 분리)
     * @param requestDto
     * @return
     */
    @Transactional
    public Farm saveFarm(FarmRequestDto.InfoDto requestDto) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        Farm farm = Farm.builder()
                .manpower(requestDto.getManpower())
                .location(requestDto.getLocation())
                .cultivatedArea(requestDto.getCultivatedArea())
                .flatArea(requestDto.getFlatArea())
                .mainCrop(requestDto.getMainCrop())
                .memberId(memberId)
                .build();
        farmMapper.save(farm);
        return farm;
    }


    /**
     * 나의 농장 정보 조회하기
     */
    @Transactional(readOnly = true)
    public FarmResponseDto.InfoDto getFarm() {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        Farm found = farmMapper.findByMemberId(memberId);

        return FarmResponseDto.InfoDto.builder()
                .id(found.getId())
                .manpower(found.getManpower())
                .location(found.getLocation())
                .cultivatedArea(found.getCultivatedArea())
                .flatArea(found.getFlatArea())
                .mainCrop(found.getMainCrop())
                .grade(found.getGrade())
                .comment(found.getComment())
                .build();
    }

    /**
     * 나의 농장 정보 수정하기
     */
    public CompletableFuture<FarmResponseDto.InfoDto> updateFarmInfo(FarmRequestDto.InfoDto requestDto) {
        Farm farm = updateFarm(requestDto);

        // 비동기 작업 시작
        CompletableFuture<FarmResponseDto.EvaluateDto> evaluationFuture = farmEvaluationService.generateGradeAndComment(requestDto);

        return evaluationFuture.thenApply(evaluateDto -> {

            // DB 반영
            farmMapper.updateEvaluation(farm.getId(), evaluateDto.getGrade(), evaluateDto.getComment());
            farm.setGrade(evaluateDto.getGrade());
            farm.setComment(evaluateDto.getComment());

            return FarmResponseDto.InfoDto.builder()
                    .manpower(farm.getManpower())
                    .location(farm.getLocation())
                    .cultivatedArea(farm.getCultivatedArea())
                    .flatArea(farm.getFlatArea())
                    .mainCrop(farm.getMainCrop())
                    .grade(farm.getGrade())
                    .comment(farm.getComment())
                    .build();
        }).exceptionally(e -> {
            LogUtil.error("비동기 작업 중 오류 : {}", e.getMessage());
            return FarmResponseDto.InfoDto.builder()
                    .manpower(farm.getManpower())
                    .location(farm.getLocation())
                    .cultivatedArea(farm.getCultivatedArea())
                    .mainCrop(farm.getMainCrop())
                    .grade("N/A")
                    .comment("평가 실패")
                    .build();
        });
    }


    /**
     * 실제 DB에 저장하기 반영하기 위한 메서드(트랜잭션 확보 및 비동기와 분리)
     * @param requestDto
     * @return
     */
    @Transactional
    public Farm updateFarm(FarmRequestDto.InfoDto requestDto) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        Farm farm = Farm.builder()
                .manpower(requestDto.getManpower())
                .location(requestDto.getLocation())
                .cultivatedArea(requestDto.getCultivatedArea())
                .flatArea(requestDto.getFlatArea())
                .mainCrop(requestDto.getMainCrop())
                .memberId(memberId)
                .build();
        farmMapper.updateFarm(farm);
        return farm;
    }
}
