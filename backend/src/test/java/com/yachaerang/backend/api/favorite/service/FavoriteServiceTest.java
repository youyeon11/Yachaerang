package com.yachaerang.backend.api.favorite.service;

import com.yachaerang.backend.api.common.PeriodType;
import com.yachaerang.backend.api.favorite.dto.request.FavoriteRequestDto;
import com.yachaerang.backend.api.favorite.dto.response.FavoriteResponseDto;
import com.yachaerang.backend.api.favorite.entity.Favorite;
import com.yachaerang.backend.api.favorite.repository.FavoriteMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private AuthenticatedMemberProvider authenticatedMemberProvider;

    @InjectMocks
    private FavoriteService favoriteService;

    private static final Long TEST_MEMBER_ID = 1L;
    private static final Long TEST_FAVORITE_ID = 100L;
    private static final String TEST_PRODUCT_CODE = "PROD001";
    private static final String TEST_PERIOD_TYPE = "DAILY";

    @BeforeEach
    void setUp() {
        // 공통 테스트 회원
        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(TEST_MEMBER_ID);
    }

    @Nested
    @DisplayName("register")
    class RegisterTest {

        @Test
        @DisplayName("관심 대시보드 등록 성공")
        void 관심_대시보드_등록_성공() {
            // given
            FavoriteRequestDto.RegisterDto requestDto = FavoriteRequestDto.RegisterDto.builder()
                    .productCode(TEST_PRODUCT_CODE)
                    .periodType(TEST_PERIOD_TYPE)
                    .build();

            Favorite savedFavorite = Favorite.builder()
                    .favoriteId(TEST_FAVORITE_ID)
                    .memberId(TEST_MEMBER_ID)
                    .productCode(TEST_PRODUCT_CODE)
                    .periodType(PeriodType.fromCode(TEST_PERIOD_TYPE))
                    .build();

            // 중복 체크
            given(favoriteMapper.findByMemberIdAndProductCode(
                    eq(TEST_MEMBER_ID), eq(TEST_PRODUCT_CODE), eq(TEST_PERIOD_TYPE)))
                    .willReturn(null)  // 첫 번째 호출 (중복 체크)
                    .willReturn(savedFavorite);  // 두 번째 호출 (저장 후 조회)

            given(favoriteMapper.save(any(Favorite.class))).willReturn(1);

            // when
            FavoriteResponseDto.RegisterDto result = favoriteService.register(requestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getFavoriteId()).isEqualTo(TEST_FAVORITE_ID);
            assertThat(result.getProductCode()).isEqualTo(TEST_PRODUCT_CODE);
            assertThat(result.getPeriodType()).isEqualTo(TEST_PERIOD_TYPE);

            verify(favoriteMapper).save(any(Favorite.class));
            verify(favoriteMapper, times(2))
                    .findByMemberIdAndProductCode(TEST_MEMBER_ID, TEST_PRODUCT_CODE, TEST_PERIOD_TYPE);
        }

        @Test
        @DisplayName("이미 등록되어있으면 예외")
        void 이미_등록되어있으면_예외() {
            // given
            FavoriteRequestDto.RegisterDto requestDto = FavoriteRequestDto.RegisterDto.builder()
                    .productCode(TEST_PRODUCT_CODE)
                    .periodType(TEST_PERIOD_TYPE)
                    .build();

            Favorite existingFavorite = Favorite.builder()
                    .favoriteId(TEST_FAVORITE_ID)
                    .memberId(TEST_MEMBER_ID)
                    .productCode(TEST_PRODUCT_CODE)
                    .periodType(PeriodType.fromCode(TEST_PERIOD_TYPE))
                    .build();

            given(favoriteMapper.findByMemberIdAndProductCode(
                    TEST_MEMBER_ID, TEST_PRODUCT_CODE, TEST_PERIOD_TYPE))
                    .willReturn(existingFavorite);

            // when & then
            assertThatThrownBy(() -> favoriteService.register(requestDto))
                    .isInstanceOf(GeneralException.class)
                    .satisfies(exception -> {
                        GeneralException ge = (GeneralException) exception;
                        assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_DUPLICATED);
                    });

            verify(favoriteMapper, never()).save(any(Favorite.class));
        }

        @Test
        @DisplayName("저장에 실패하면 예외")
        void 저장에_실패하면_예외() {
            // given
            FavoriteRequestDto.RegisterDto requestDto = FavoriteRequestDto.RegisterDto.builder()
                    .productCode(TEST_PRODUCT_CODE)
                    .periodType(TEST_PERIOD_TYPE)
                    .build();

            given(favoriteMapper.findByMemberIdAndProductCode(
                    TEST_MEMBER_ID, TEST_PRODUCT_CODE, TEST_PERIOD_TYPE))
                    .willReturn(null);

            given(favoriteMapper.save(any(Favorite.class))).willReturn(0);  // 저장 실패

            // when & then
            assertThatThrownBy(() -> favoriteService.register(requestDto))
                    .isInstanceOf(GeneralException.class)
                    .satisfies(exception -> {
                        GeneralException ge = (GeneralException) exception;
                        assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_SAVE_FAILED);
                    });
        }
    }

    @Nested
    @DisplayName("getAllFavoriteList")
    class GetAllFavoriteListTest {

        @Test
        @DisplayName("회원의 모든 관심사 조회 성공")
        void 회원의_모든_관심사_조회_성공() {
            // given
            List<Favorite> favorites = List.of(
                    Favorite.builder()
                            .favoriteId(1L)
                            .memberId(TEST_MEMBER_ID)
                            .productCode("PROD_A")
                            .periodType(PeriodType.fromCode("DAILY"))
                            .build(),
                    Favorite.builder()
                            .favoriteId(2L)
                            .memberId(TEST_MEMBER_ID)
                            .productCode("PROD_B")
                            .periodType(PeriodType.fromCode("WEEKLY"))
                            .build(),
                    Favorite.builder()
                            .favoriteId(3L)
                            .memberId(TEST_MEMBER_ID)
                            .productCode("PROD_C")
                            .periodType(PeriodType.fromCode("MONTHLY"))
                            .build()
            );

            given(favoriteMapper.findAllByMemberId(TEST_MEMBER_ID)).willReturn(favorites);

            // when
            List<FavoriteResponseDto.RegisterDto> result = favoriteService.getAllFavoriteList();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getFavoriteId()).isEqualTo(1L);
            assertThat(result.get(0).getProductCode()).isEqualTo("PROD_A");
            assertThat(result.get(1).getFavoriteId()).isEqualTo(2L);
            assertThat(result.get(2).getFavoriteId()).isEqualTo(3L);

            verify(favoriteMapper).findAllByMemberId(TEST_MEMBER_ID);
        }

        @Test
        @DisplayName("즐겨찾기가 없으면 빈 리스트")
        void 즐겨찾기가_없으면_빈리스트() {
            // given
            given(favoriteMapper.findAllByMemberId(TEST_MEMBER_ID))
                    .willReturn(Collections.emptyList());

            // when
            List<FavoriteResponseDto.RegisterDto> result = favoriteService.getAllFavoriteList();

            // then
            assertThat(result).isEmpty();

            verify(favoriteMapper).findAllByMemberId(TEST_MEMBER_ID);
        }

        @Test
        @DisplayName("DTO 매핑 성공")
        void DTO_매핑_성공() {
            // given
            Favorite favorite = Favorite.builder()
                    .favoriteId(TEST_FAVORITE_ID)
                    .memberId(TEST_MEMBER_ID)
                    .productCode(TEST_PRODUCT_CODE)
                    .periodType(PeriodType.fromCode(TEST_PERIOD_TYPE))
                    .build();

            given(favoriteMapper.findAllByMemberId(TEST_MEMBER_ID))
                    .willReturn(List.of(favorite));

            // when
            List<FavoriteResponseDto.RegisterDto> result = favoriteService.getAllFavoriteList();

            // then
            assertThat(result).hasSize(1);
            FavoriteResponseDto.RegisterDto dto = result.get(0);
            assertThat(dto.getFavoriteId()).isEqualTo(TEST_FAVORITE_ID);
            assertThat(dto.getProductCode()).isEqualTo(TEST_PRODUCT_CODE);
            assertThat(dto.getPeriodType()).isEqualTo(TEST_PERIOD_TYPE);
        }
    }

    @Nested
    @DisplayName("erase")
    class EraseTest {

        @Test
        @DisplayName("즐겨찾기 삭제 성공")
        void 즐겨찾기_삭제_성공() {
            // given
            given(favoriteMapper.deleteByIdAndMemberId(TEST_FAVORITE_ID, TEST_MEMBER_ID))
                    .willReturn(1);

            // when
            favoriteService.erase(TEST_FAVORITE_ID);

            // then
            verify(favoriteMapper).deleteByIdAndMemberId(TEST_FAVORITE_ID, TEST_MEMBER_ID);
        }

        @Test
        @DisplayName("삭제할 즐겨찾기가 없으면 예외")
        void 삭제할_즐겨찾기가_없으면_예외() {
            // given
            Long nonExistingFavoriteId = 99999L;

            given(favoriteMapper.deleteByIdAndMemberId(nonExistingFavoriteId, TEST_MEMBER_ID))
                    .willReturn(0);

            // when & then
            assertThatThrownBy(() -> favoriteService.erase(nonExistingFavoriteId))
                    .isInstanceOf(GeneralException.class)
                    .satisfies(exception -> {
                        GeneralException ge = (GeneralException) exception;
                        assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_DELETE_FAILED);
                    });
        }

        @Test
        @DisplayName("다른 회원의 즐겨찾기 삭제 시 예외")
        void 다른_회원의_즐겨찾기_삭제시_예외() {
            // given
            given(favoriteMapper.deleteByIdAndMemberId(TEST_FAVORITE_ID, TEST_MEMBER_ID))
                    .willReturn(0); // 다른 회원

            // when & then
            assertThatThrownBy(() -> favoriteService.erase(TEST_FAVORITE_ID))
                    .isInstanceOf(GeneralException.class)
                    .satisfies(exception -> {
                        GeneralException ge = (GeneralException) exception;
                        assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_DELETE_FAILED);
                    });
        }
    }

    @Test
    @DisplayName("현재 로그인한 회원 ID를 사용")
    void 현재_로그인한_회원_ID를_사용() {
        // given
        Long specificMemberId = 999L;
        given(authenticatedMemberProvider.getCurrentMemberId()).willReturn(specificMemberId);

        FavoriteRequestDto.RegisterDto requestDto = FavoriteRequestDto.RegisterDto.builder()
                .productCode(TEST_PRODUCT_CODE)
                .periodType(TEST_PERIOD_TYPE)
                .build();

        Favorite savedFavorite = Favorite.builder()
                .favoriteId(TEST_FAVORITE_ID)
                .memberId(specificMemberId)
                .productCode(TEST_PRODUCT_CODE)
                .periodType(PeriodType.fromCode(TEST_PERIOD_TYPE))
                .build();

        given(favoriteMapper.findByMemberIdAndProductCode(
                eq(specificMemberId), eq(TEST_PRODUCT_CODE), eq(TEST_PERIOD_TYPE)))
                .willReturn(null)
                .willReturn(savedFavorite);
        given(favoriteMapper.save(any(Favorite.class))).willReturn(1);

        // when
        favoriteService.register(requestDto);

        // then
        verify(authenticatedMemberProvider).getCurrentMemberId();
        verify(favoriteMapper, times(2)).findByMemberIdAndProductCode(
                specificMemberId, TEST_PRODUCT_CODE, TEST_PERIOD_TYPE);
    }
}