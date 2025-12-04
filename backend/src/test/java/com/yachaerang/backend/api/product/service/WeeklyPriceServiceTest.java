package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.WeeklyPriceResponseDto;
import com.yachaerang.backend.api.product.entity.WeeklyPrice;
import com.yachaerang.backend.api.product.repository.WeeklyPriceMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class WeeklyPriceServiceTest {

    @Mock
    WeeklyPriceMapper weeklyPriceMapper;

    @InjectMocks
    WeeklyPriceService weeklyPriceService;

    private String productCode;

    @BeforeEach
    void setUp() {
        productCode = "PROD001";
        WeeklyPrice weeklyPrice1 = WeeklyPrice.builder()
                .productCode(productCode)
                .startDate(LocalDate.of(2025, 11, 3))
                .endDate(LocalDate.of(2025, 11, 9))
                .build();
        WeeklyPrice weeklyPrice2 = WeeklyPrice.builder()
                .productCode(productCode)
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 16))
                .build();
        WeeklyPrice weeklyPrice3 = WeeklyPrice.builder()
                .productCode(productCode)
                .startDate(LocalDate.of(2025, 11, 17))
                .endDate(LocalDate.of(2025, 11, 18))
                .build();
    }

    @Test
    @DisplayName("기간 조회 성공")
    void 기간_조회_성공() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 20);

        LocalDate expectedWeekStart = LocalDate.of(2025, 11, 3);
        LocalDate expectedWeekEnd = LocalDate.of(2025, 11, 16);

        List<WeeklyPriceResponseDto.PriceRecordDto> mockResult = List.of(
                WeeklyPriceResponseDto.PriceRecordDto.builder()
                        .startDate(LocalDate.of(2025, 11, 3))
                        .endDate(LocalDate.of(2025, 11, 9))
                        .avgPrice(15000L)
                        .minPrice(12000L)
                        .maxPrice(18000L)
                        .build(),
                WeeklyPriceResponseDto.PriceRecordDto.builder()
                        .startDate(LocalDate.of(2025, 11, 10))
                        .endDate(LocalDate.of(2025, 11, 16))
                        .avgPrice(16000L)
                        .minPrice(13000L)
                        .maxPrice(19000L)
                        .build()
        );

        when(weeklyPriceMapper.getPriceDuration(productCode, expectedWeekStart, expectedWeekEnd))
                .thenReturn(mockResult);

        // when
        List<WeeklyPriceResponseDto.PriceRecordDto> result =
                weeklyPriceService.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).hasSize(2);
        verify(weeklyPriceMapper).getPriceDuration(productCode, expectedWeekStart, expectedWeekEnd);
    }

    @Test
    @DisplayName("endDate가 startDate보다 이전일 때 실패")
    void endDate가_startDate보다_이전_실패() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 20);
        LocalDate endDate = LocalDate.of(2025, 11, 10);

        // when & then
        assertThatThrownBy(() ->
                weeklyPriceService.getPriceDuration(productCode, startDate, endDate))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ex = (GeneralException) exception;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.WRONG_REQUEST_DATE);
                });

        verify(weeklyPriceMapper, never()).getPriceDuration(any(), any(), any());
    }

    @Test
    @DisplayName("endDate가 오늘 이후인 경우 실패")
    void endDate가_오늘이후인경우_실패() {
        // given
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // when & then
        assertThatThrownBy(() ->
                weeklyPriceService.getPriceDuration(productCode, startDate, endDate))
                .isInstanceOf(GeneralException.class)
                .satisfies(exception -> {
                    GeneralException ex = (GeneralException) exception;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.WRONG_REQUEST_DATE);
                });

        verify(weeklyPriceMapper, never()).getPriceDuration(any(), any(), any());
    }

    @Test
    @DisplayName("endDate가 오늘인 경우 성공")
    void endDate가_오늘인경우_성공() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 25); // fixedClock과 동일

        when(weeklyPriceMapper.getPriceDuration(eq(productCode), any(), any()))
                .thenReturn(Collections.emptyList());

        // when & then
        assertThatCode(() ->
                weeklyPriceService.getPriceDuration(productCode, startDate, endDate))
                .doesNotThrowAnyException();

        verify(weeklyPriceMapper).getPriceDuration(eq(productCode), any(), any());
    }

    @Test
    @DisplayName("주간의 시작일과 종료일 성공")
    void 주간의_시작일과_종료일_성공() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 5);  // 수요일
        LocalDate endDate = LocalDate.of(2025, 11, 20);   // 목요일

        // startDate의 다음주 월요일
        LocalDate expectedWeekStart = LocalDate.of(2025, 11, 10);
        // endDate의 저번주 일요일
        LocalDate expectedWeekEnd = LocalDate.of(2025, 11, 16);

        when(weeklyPriceMapper.getPriceDuration(productCode, expectedWeekStart, expectedWeekEnd))
                .thenReturn(Collections.emptyList());

        // when
        weeklyPriceService.getPriceDuration(productCode, startDate, endDate);

        // then
        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);

        verify(weeklyPriceMapper).getPriceDuration(eq(productCode), startCaptor.capture(), endCaptor.capture());

        assertThat(startCaptor.getValue()).isEqualTo(expectedWeekStart);
        assertThat(startCaptor.getValue().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(endCaptor.getValue()).isEqualTo(expectedWeekEnd);
        assertThat(endCaptor.getValue().getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 리스트를 반환")
    void 조회결과_없으면_빈리스트_반환_성공() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 20);

        when(weeklyPriceMapper.getPriceDuration(eq(productCode), any(), any()))
                .thenReturn(Collections.emptyList());

        // when
        List<WeeklyPriceResponseDto.PriceRecordDto> result =
                weeklyPriceService.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isEmpty();
    }
}