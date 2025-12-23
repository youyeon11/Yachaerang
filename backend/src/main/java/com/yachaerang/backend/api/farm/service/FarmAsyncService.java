package com.yachaerang.backend.api.farm.service;

import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.entity.Farm;
import com.yachaerang.backend.api.farm.repository.FarmMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실제 DB에 저장하기 위한 동기식 클래스(트랜잭션 확보 및 비동기와 분리)
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FarmAsyncService {

    private final FarmMapper farmMapper;

    @Transactional
    public Farm saveFarmWithEvaluation(
            Long memberId,
            FarmRequestDto.InfoDto requestDto,
            String grade,
            String farmType,
            String comment) {

        LogUtil.debug(">>> [saveFarmWithEvaluation] grade={}, farmType={}, comment={}", grade, farmType, comment);


        if (farmMapper.findByMemberId(memberId) != null) {
            throw GeneralException.of(ErrorCode.FARM_ALREADY_EXISTS);
        }

        Farm farm = Farm.builder()
                .manpower(requestDto.getManpower())
                .location(requestDto.getLocation())
                .cultivatedArea(requestDto.getCultivatedArea())
                .flatArea(requestDto.getFlatArea())
                .mainCrop(requestDto.getMainCrop())
                .grade(grade)
                .farmType(farmType)
                .comment(comment)
                .memberId(memberId)
                .build();

        try {
            farmMapper.save(farm);
            LogUtil.debug(">>> [saveFarmWithEvaluation] 저장된 Farm: grade={}, farmType={}, comment={}",
                    farm.getGrade(), farm.getFarmType(), farm.getComment());

            return farm;
        } catch (DuplicateKeyException e) {
            throw GeneralException.of(ErrorCode.FARM_ALREADY_EXISTS);
        }
    }

    @Transactional
    public Farm updateFarmWithEvaluation(
            Long memberId,
            FarmRequestDto.InfoDto requestDto,
            String grade,
            String farmType,
            String comment) {

        Farm existingFarm = farmMapper.findByMemberIdForUpdate(memberId);

        if (existingFarm == null) {
            throw GeneralException.of(ErrorCode.FARM_NOT_FOUND);
        }

        // PATCH update
        if (requestDto.getManpower() != null) {
            existingFarm.setManpower(requestDto.getManpower());
        }
        if (requestDto.getLocation() != null) {
            existingFarm.setLocation(requestDto.getLocation());
        }
        if (requestDto.getCultivatedArea() != null) {
            existingFarm.setCultivatedArea(requestDto.getCultivatedArea());
        }
        if (requestDto.getFlatArea() != null) {
            existingFarm.setFlatArea(requestDto.getFlatArea());
        }
        if (requestDto.getMainCrop() != null) {
            existingFarm.setMainCrop(requestDto.getMainCrop());
        }
        existingFarm.setGrade(grade);
        existingFarm.setFarmType(farmType);
        existingFarm.setComment(comment);

        farmMapper.updateFarm(existingFarm);

        return existingFarm;
    }

    /**
     * Grade와 comment만 수정 요청
     */
    @Transactional
    public void updateEvaluationOnly(Long farmId, String grade, String farmType, String comment) {
        int updatedCount = farmMapper.updateEvaluation(farmId, grade, farmType, comment);

        if (updatedCount == 0) {
            throw GeneralException.of(ErrorCode.FARM_NOT_FOUND);
        }
    }
}
