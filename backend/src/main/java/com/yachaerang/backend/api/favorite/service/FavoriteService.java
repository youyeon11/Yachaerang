package com.yachaerang.backend.api.favorite.service;

import com.yachaerang.backend.api.common.PeriodType;
import com.yachaerang.backend.api.favorite.dto.request.FavoriteRequestDto;
import com.yachaerang.backend.api.favorite.dto.response.FavoriteResponseDto;
import com.yachaerang.backend.api.favorite.entity.Favorite;
import com.yachaerang.backend.api.favorite.repository.FavoriteMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final AuthenticatedMemberProvider authenticatedMemberProvider;


    /*
    관심 대시보드 등록하기
     */
    @Transactional
    public FavoriteResponseDto.RegisterDto register(FavoriteRequestDto.RegisterDto requestDto) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        Favorite favorite = Favorite.builder()
                .memberId(memberId)
                .productCode(requestDto.getProductCode())
                .periodType(PeriodType.fromCode(requestDto.getPeriodType()))
                .build();
        // 이미 등록되어있는지 확인
        if (favoriteMapper.findByMemberIdAndProductCode(memberId, requestDto.getProductCode(), requestDto.getPeriodType()) != null) {
            throw GeneralException.of(ErrorCode.FAVORITE_DUPLICATED);
        }

        int result = favoriteMapper.save(favorite);

        if (result != 1) {
            throw GeneralException.of(ErrorCode.FAVORITE_SAVE_FAILED);
        }

        return FavoriteResponseDto.RegisterDto.builder()
                .favoriteId(favorite.getFavoriteId())
                .productCode(requestDto.getProductCode())
                .periodType(requestDto.getPeriodType())
                .build();
    }


    /*
    관심 대시보드 목록 전체 조회하기
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponseDto.ReadDto> getAllFavoriteList() {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        return favoriteMapper.findAllByMemberId(memberId).stream()
                .toList();
    }

    /*
    관심 대시보드 해제하기
     */
    @Transactional
    public void erase(Long favoriteId) {

        // 인증 확인 및 삭제
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        int result = favoriteMapper.deleteByIdAndMemberId(favoriteId, memberId);

        if (result != 1) {
            throw GeneralException.of(ErrorCode.FAVORITE_DELETE_FAILED);
        }
    }
}
