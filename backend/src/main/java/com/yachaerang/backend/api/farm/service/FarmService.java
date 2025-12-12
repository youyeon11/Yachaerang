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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmMapper farmMapper;
    private final FarmEvaluationService farmEvaluationService;
    private final FarmAsyncService farmAsyncService;
    private final AuthenticatedMemberProvider authenticatedMemberProvider;

    /**
     * 나의 농장 정보 저장
     * @param requestDto : 비동기 파이프라인에 평가입력값들 전달
     * @return
     */
    public CompletableFuture<FarmResponseDto.InfoDto> saveFarmInfo(FarmRequestDto.InfoDto requestDto) {
        final Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        return farmEvaluationService.generateGradeAndComment(requestDto)
                // 평가 생성 (외부 transaction)
                .handle((evaluationDto, ex) -> {
                    if (ex != null) {
                        LogUtil.error("농장 평가 생성 실패: memberId={}, error={}", memberId, ex.getMessage());
                        throw GeneralException.of(ErrorCode.AI_EVALUATION_FAILED);
                    }
                    return evaluationDto;
                })
                // DB transaction 필요 단계
                .thenApply(evaluationDto -> {
                    try {
                        // 비동기 스레드 Context 의존 제거
                        Farm farm = farmAsyncService.saveFarmWithEvaluation(
                                memberId,
                                requestDto,
                                evaluationDto.getGrade(),
                                evaluationDto.getComment()
                        );

                        return FarmResponseDto.toInfoDto(farm);

                    } catch (GeneralException ge) {
                        // 트랜잭션 확보 세션에서 에러 출력
                        throw ge;
                    } catch (Exception e) {
                        LogUtil.error("농장 정보 저장 실패: memberId={}, error={}", memberId, e.getMessage());
                        throw GeneralException.of(ErrorCode.FARM_SAVE_FAILED);
                    }
                });
    }


    /**
     * 나의 농장 정보 조회하기
     */
    @Transactional(readOnly = true)
    public FarmResponseDto.InfoDto getFarm() {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        Farm found = farmMapper.findByMemberId(memberId);

        return FarmResponseDto.toInfoDto(found);
    }

    /**
     * 나의 농장 정보 수정하기
     * @param requestDto : 비동기 파이프라인에 평가입력값들 전달
     * @return
     */
    public CompletableFuture<FarmResponseDto.InfoDto> updateFarmInfo(FarmRequestDto.InfoDto requestDto) {
        final Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        Farm existingFarm = farmMapper.findByMemberId(memberId);
        if (existingFarm == null) {
            throw GeneralException.of(ErrorCode.FARM_NOT_FOUND);
        }

        return farmEvaluationService.generateGradeAndComment(requestDto)
                // 비동기 평가 결과 요청
                .handle((evaluationDto, ex) -> {
                    if (ex != null) {
                        LogUtil.error("농장 평가 생성 실패: memberId={}, error={}", memberId, ex.getMessage());
                        throw GeneralException.of(ErrorCode.AI_EVALUATION_FAILED);
                    }
                    return evaluationDto;
                })
                // transaction 세션 확보
                .thenApply(evaluationDto -> {
                    try {
                        Farm updatedFarm = farmAsyncService.updateFarmWithEvaluation(
                                memberId,
                                requestDto,
                                evaluationDto.getGrade(),
                                evaluationDto.getComment()
                        );

                        return FarmResponseDto.toInfoDto(updatedFarm);

                    } catch (GeneralException ge) {
                        throw ge;
                    } catch (Exception e) {
                        LogUtil.error("농장 정보 업데이트 실패: memberId={}, error={}", memberId, e.getMessage());
                        throw GeneralException.of(ErrorCode.FARM_UPDATE_FAILED);
                    }
                });
    }

    /**
     * 비동기 작업으로 grade와 comment 미처리 DB 처리
     * 평가 누락 농장 일괄 처리
     */
    @Async("asyncExecutor")
    public CompletableFuture<Void> fillMissingFarmEvaluations() {
        LogUtil.info("평가 누락 농장 일괄 처리 시작");

        List<Farm> unevaluatedFarms = farmMapper.findFarmsWithMissingEvaluation();

        if (unevaluatedFarms.isEmpty()) {
            LogUtil.info("평가가 필요한 농장이 없습니다.");
            return CompletableFuture.completedFuture(null);
        }

        LogUtil.info("처리 대상 농장 수: {}", unevaluatedFarms.size());

        int batchSize = 10;

        List<CompletableFuture<Void>> allFutures =
                partitionList(unevaluatedFarms, batchSize).stream()
                        .map(this::processBatch)
                        .collect(Collectors.toList());

        return CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0]))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        LogUtil.error("평가 누락 농장 일괄 처리 중 오류 발생", throwable);
                    } else {
                        LogUtil.info("평가 누락 농장 일괄 처리 완료");
                    }
                });
    }


    /**
     * 배치 처리하기
     * @param batch
     * @return
     */
    private CompletableFuture<Void> processBatch(List<Farm> batch) {
        List<CompletableFuture<Void>> futures = batch.stream()
                .map(this::processSingleFarm)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * 하나의 요소 처리하기
     * @param farm
     * @return
     */
    private CompletableFuture<Void> processSingleFarm(Farm farm) {
        FarmRequestDto.InfoDto requestDto = FarmRequestDto.InfoDto.builder()
                .manpower(farm.getManpower())
                .location(farm.getLocation())
                .cultivatedArea(farm.getCultivatedArea())
                .flatArea(farm.getFlatArea())
                .mainCrop(farm.getMainCrop())
                .build();

        return farmEvaluationService.generateGradeAndComment(requestDto)
                .thenAccept(evaluationDto -> {
                    try {
                        farmAsyncService.updateEvaluationOnly(
                                farm.getId(),
                                evaluationDto.getGrade(),
                                evaluationDto.getComment()
                        );
                        LogUtil.info("Farm {} 평가 완료", farm.getId());
                    } catch (Exception e) {
                        LogUtil.error("Farm {} 평가 업데이트 실패: {}", farm.getId(), e.getMessage());
                    }
                })
                .exceptionally(e -> {
                    LogUtil.error("Farm {} 평가 생성 실패: {}", farm.getId(), e.getMessage());
                    return null;
                });
    }

    /**
     * 배치 사이즈로 나누기
     * @param list
     * @param batchSize
     * @return
     * @param <T>
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        return java.util.stream.IntStream.range(0, (list.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> list.subList(
                        i * batchSize,
                        Math.min((i + 1) * batchSize, list.size())
                ))
                .collect(Collectors.toList());
    }
}
