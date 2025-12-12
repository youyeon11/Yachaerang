package com.yachaerang.backend.api.farm.service;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.entity.Farm;
import com.yachaerang.backend.api.farm.repository.FarmMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

            // 평가 결과 DB 업데이트
            updateEvaluation(farm.getId(), evaluationDto.getGrade(), evaluationDto.getComment());

            return FarmResponseDto.InfoDto.builder()
                    .manpower(farm.getManpower())
                    .location(farm.getLocation())
                    .cultivatedArea(farm.getCultivatedArea())
                    .flatArea(farm.getFlatArea())
                    .mainCrop(farm.getMainCrop())
                    .grade(evaluationDto.getGrade())
                    .comment(evaluationDto.getComment())
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

        if (farmMapper.findByMemberId(memberId) != null) {
            throw GeneralException.of(ErrorCode.FARM_ALREADY_EXISTS);
        }

        Farm farm = new Farm();
        farm.setMemberId(memberId);
        requestDto.getManpower().ifPresent(value -> farm.setManpower(value)); // value가 null이면 null 저장됨
        requestDto.getLocation().ifPresent(value -> farm.setLocation(value));
        requestDto.getCultivatedArea().ifPresent(value -> farm.setCultivatedArea(value));
        requestDto.getFlatArea().ifPresent(value -> farm.setFlatArea(value));
        requestDto.getMainCrop().ifPresent(value -> farm.setMainCrop(value));

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

        // grade와 comment 초기화
        Farm farm = new Farm();
        farm.setMemberId(memberId);
        requestDto.getManpower().ifPresent(value -> farm.setManpower(value)); // value가 null이면 null 저장됨
        requestDto.getLocation().ifPresent(value -> farm.setLocation(value));
        requestDto.getCultivatedArea().ifPresent(value -> farm.setCultivatedArea(value));
        requestDto.getFlatArea().ifPresent(value -> farm.setFlatArea(value));
        requestDto.getMainCrop().ifPresent(value -> farm.setMainCrop(value));
        farmMapper.updateFarm(farm);
        return farm;
    }

    /**
     * Grade와 comment만 수정 요청
     */
    @Transactional
    public void updateEvaluation(Long farmId, String grade, String comment) {
        farmMapper.updateEvaluation(farmId, grade, comment);
    }

    /**
     * 비동기 작업으로 grade와 comment 미처리 DB 처리
     */
    @Async("asyncExecutor")
    public void fillMissingFarmEvaluations() {
        List<Farm> unevaluatedFarmList = farmMapper.findFarmsWithMissingEvaluation();

        List<CompletableFuture<Void>> futureList = unevaluatedFarmList.stream()
                .map(farm -> {
                    FarmRequestDto.InfoDto requestDto = FarmRequestDto.InfoDto.builder()
                            .manpower(Optional.of(farm.getManpower()))
                            .location(Optional.of(farm.getLocation()))
                            .cultivatedArea(Optional.of(farm.getCultivatedArea()))
                            .flatArea(Optional.of(farm.getFlatArea()))
                            .mainCrop(Optional.of(farm.getMainCrop()))
                            .build();
                    return farmEvaluationService.generateGradeAndComment(requestDto)
                            .thenAccept(evaluationDto -> {
                                updateEvaluation(farm.getId(), evaluationDto.getGrade(), evaluationDto.getComment());
                            }).exceptionally(e -> {
                                LogUtil.error("Farm {} 평가 실패: {}", farm.getId(), e.getMessage());
                                return null;
                            });
                }).collect(Collectors.toUnmodifiableList());
        // 모든 작업 완료 대기
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }
}
