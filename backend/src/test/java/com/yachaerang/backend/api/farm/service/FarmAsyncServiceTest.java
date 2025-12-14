package com.yachaerang.backend.api.farm.service;

import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.entity.Farm;
import com.yachaerang.backend.api.farm.repository.FarmMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmAsyncServiceTest {

    @Mock private FarmMapper farmMapper;
    @InjectMocks private FarmAsyncService farmAsyncService;

    private static final Long MEMBER_ID = 1L;
    private static final Long FARM_ID = 100L;

    /*
    DTO 생성 메서드
     */
    private FarmRequestDto.InfoDto createRequestDto() {
        return FarmRequestDto.InfoDto.builder()
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .build();
    }

    /*
    Farm 생성 메서드
     */
    private Farm createFarm() {
        return Farm.builder()
                .id(FARM_ID)
                .memberId(MEMBER_ID)
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .grade("B")
                .comment("좋은 농장입니다")
                .build();
    }

    @Test
    @DisplayName("농장 저장 성공")
    void 농장_저장_성공() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();
        String grade = "A";
        String comment = "훌륭한 농장이네요!";

        doAnswer(invocation -> {
            Farm farm = invocation.getArgument(0);
            farm.setId(FARM_ID);
            return null;
        }).when(farmMapper).save(any(Farm.class));

        // when
        Farm result = farmAsyncService.saveFarmWithEvaluation(
                MEMBER_ID, requestDto, grade, comment);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(FARM_ID);
        assertThat(result.getMemberId()).isEqualTo(MEMBER_ID);
        assertThat(result.getManpower()).isEqualTo(requestDto.getManpower());
        assertThat(result.getLocation()).isEqualTo(requestDto.getLocation());
        assertThat(result.getGrade()).isEqualTo(grade);
        assertThat(result.getComment()).isEqualTo(comment);

        verify(farmMapper).save(any(Farm.class));
    }

    @Test
    @DisplayName("이미 저장된 농장이 있다면 저장 실패")
    void 이미_저장된_농장이_있다면_저장_실패() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();

        doThrow(new DuplicateKeyException("Duplicate entry"))
                .when(farmMapper).save(any(Farm.class));

        // when & then
        assertThatThrownBy(() ->
                farmAsyncService.saveFarmWithEvaluation(MEMBER_ID, requestDto, "A", "좋아요"))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ge = (GeneralException) e;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FARM_ALREADY_EXISTS);
                });
    }

    @Test
    @DisplayName("농장 수정 성공")
    void 농장_수정_성공() {
        // given
        Farm existingFarm = createFarm();
        FarmRequestDto.InfoDto requestDto = FarmRequestDto.InfoDto.builder()
                .manpower(10)
                .location("urban")
                .cultivatedArea(2000.0)
                .flatArea(1500.0)
                .mainCrop("토마토")
                .build();

        when(farmMapper.findByMemberIdForUpdate(MEMBER_ID)).thenReturn(existingFarm);

        // when
        Farm result = farmAsyncService.updateFarmWithEvaluation(
                MEMBER_ID, requestDto, "A", "매우 좋은 농장이에요!");

        // then
        assertThat(result.getManpower()).isEqualTo(10);
        assertThat(result.getLocation()).isEqualTo("urban");
        assertThat(result.getCultivatedArea()).isEqualTo(2000.0);
        assertThat(result.getFlatArea()).isEqualTo(1500.0);
        assertThat(result.getMainCrop()).isEqualTo("토마토");
        assertThat(result.getGrade()).isEqualTo("A");
        assertThat(result.getComment()).isEqualTo("매우 좋은 농장이에요!");

        verify(farmMapper).updateFarm(existingFarm);
    }

    @Test
    @DisplayName("농장 일부 필드만 수정 성공")
    void 농장_일부_필드만_수정_성공() {
        // given
        Farm farm = createFarm();
        FarmRequestDto.InfoDto requestDto = FarmRequestDto.InfoDto.builder()
                .manpower(10)       // 변경
                .location(null)     // 유지
                .cultivatedArea(null) // 유지
                .flatArea(null)     // 유지
                .mainCrop("토마토")  // 변경
                .build();

        when(farmMapper.findByMemberIdForUpdate(MEMBER_ID)).thenReturn(farm);

        // when
        Farm result = farmAsyncService.updateFarmWithEvaluation(
                MEMBER_ID, requestDto, "B", "좋아요");

        // then
        assertThat(result.getManpower()).isEqualTo(requestDto.getManpower());
        assertThat(result.getLocation()).isEqualTo(farm.getLocation());
        assertThat(result.getCultivatedArea()).isEqualTo(farm.getCultivatedArea());
        assertThat(result.getFlatArea()).isEqualTo(farm.getFlatArea());
        assertThat(result.getMainCrop()).isEqualTo(requestDto.getMainCrop());
        assertThat(result.getGrade()).isEqualTo("B");
        assertThat(result.getComment()).isEqualTo("좋아요");

        verify(farmMapper).updateFarm(farm);
    }


    @Test
    @DisplayName("저장된 농장이 없을 시 예외")
    void 저장된_농장이_없을시_예외() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();

        when(farmMapper.findByMemberIdForUpdate(MEMBER_ID)).thenReturn(null);

        // when & then
        assertThatThrownBy(() ->
                farmAsyncService.updateFarmWithEvaluation(MEMBER_ID, requestDto, "A", "좋아요"))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ge = (GeneralException) e;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FARM_NOT_FOUND);
                });

        verify(farmMapper, never()).updateFarm(any());
    }

    @Test
    @DisplayName("평가만 수정 성공")
    void 평가만_수정_성공() {
        // given
        when(farmMapper.updateEvaluation(FARM_ID, "A", "훌륭해요")).thenReturn(1);

        // when
        farmAsyncService.updateEvaluationOnly(FARM_ID, "A", "훌륭해요");

        // then
        verify(farmMapper).updateEvaluation(FARM_ID, "A", "훌륭해요");
    }
}