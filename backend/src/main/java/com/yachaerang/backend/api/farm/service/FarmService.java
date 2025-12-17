package com.yachaerang.backend.api.farm.service;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.entity.Farm;
import com.yachaerang.backend.api.farm.repository.FarmMapper;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
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
    private final FarmAsyncService farmAsyncService;
    private final AuthenticatedMemberProvider authenticatedMemberProvider;
    private final MemberMapper memberMapper;

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
                        if (farmMapper.findByMemberId(memberId) != null) {
                            throw GeneralException.of(ErrorCode.FARM_ALREADY_EXISTS);
                        }
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
}
