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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

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
        LogUtil.debug(">>> [FarmService] 진입, Thread: {}", Thread.currentThread().getName());

        // Debugging 용도
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LogUtil.debug(">>> [FarmService] 인증 정보: {}", auth != null ? auth.getName() : "NULL");

        final Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        LogUtil.debug(">>> [FarmService] memberId 추출 성공: {}", memberId);

        return farmEvaluationService.generateGradeAndComment(requestDto)
                .handle((evaluationDto, ex) -> {
                    LogUtil.debug(">>> [handle] Thread: {}", Thread.currentThread().getName());
                    if (ex != null) {
                        LogUtil.error(">>> [handle] 평가 실패: {}", ex.getMessage());
                        throw GeneralException.of(ErrorCode.AI_EVALUATION_FAILED);
                    }
                    LogUtil.info(">>> [handle] 평가 성공: grade={}", evaluationDto.getGrade());
                    return evaluationDto;
                })
                .thenApply(evaluationDto -> {
                    LogUtil.debug(">>> [thenApply] Thread: {}", Thread.currentThread().getName());

                    // 비동기 응답 이후에도 유지되어있는지 확인
                    Authentication authInAsync = SecurityContextHolder.getContext().getAuthentication();
                    LogUtil.info(">>> [thenApply] 비동기 내 인증 정보: {}",
                            authInAsync != null ? authInAsync.getName() : "NULL");

                    try {
                        if (farmMapper.findByMemberId(memberId) != null) {
                            throw GeneralException.of(ErrorCode.FARM_ALREADY_EXISTS);
                        }

                        Farm farm = farmAsyncService.saveFarmWithEvaluation(
                                memberId,
                                requestDto,
                                evaluationDto.getGrade(),
                                evaluationDto.getFarmType(),
                                evaluationDto.getComment()
                        );

                        LogUtil.info(">>> [thenApply] Farm 저장 성공: farmId={}", farm.getId());
                        return FarmResponseDto.toInfoDto(farm);

                    } catch (GeneralException ge) {
                        LogUtil.error(">>> [thenApply] GeneralException: {}", ge.getMessage());
                        throw ge;
                    } catch (Exception e) {
                        LogUtil.error(">>> [thenApply] Exception: {}", e.getMessage());
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

        if (found == null) {
            return null;
        }

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

                    // 비동기 응답 이후에도 유지되어있는지 확인
                    Authentication authInAsync = SecurityContextHolder.getContext().getAuthentication();
                    LogUtil.info(">>> [thenApply] 비동기 내 인증 정보: {}",
                            authInAsync != null ? authInAsync.getName() : "NULL");
                    Farm updatedFarm = farmAsyncService.updateFarmWithEvaluation(
                                    memberId,
                                    requestDto,
                                    evaluationDto.getGrade(),
                                    evaluationDto.getFarmType(),
                                    evaluationDto.getComment());
                    return FarmResponseDto.toInfoDto(updatedFarm);
                })
                .exceptionally(
                        e -> {
                            LogUtil.error("농장 정보 업데이트 실패: memberId={}, error={}", memberId, e.getMessage());
                            throw GeneralException.of(ErrorCode.FARM_UPDATE_FAILED);
                        });
    }
}
