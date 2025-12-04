package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.DailyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.DailyPriceMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class DailyPriceServiceTest {

    @Mock DailyPriceMapper dailyPriceMapper;

    @InjectMocks DailyPriceService dailyPriceService;

    @Test
    @DisplayName("기간 내 가격 목록 반환 성공")
    void 기간내가격목록_반환_성공() {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<DailyPriceResponseDto.PriceRecordDto> expectedPrices = List.of(
                DailyPriceResponseDto.PriceRecordDto.builder()
                        .priceDate(LocalDate.of(2024, 1, 10))
                        .price(10000L)
                        .build(),
                DailyPriceResponseDto.PriceRecordDto.builder()
                        .priceDate(LocalDate.of(2024, 1, 15))
                        .price(10500L)
                        .build(),
                DailyPriceResponseDto.PriceRecordDto.builder()
                        .priceDate(LocalDate.of(2024, 1, 20))
                        .price(9800L)
                        .build()
        );

        given(dailyPriceMapper.getPriceDuration(productCode, startDate, endDate))
                .willReturn(expectedPrices);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceService.getDailyPrice(productCode, startDate, endDate);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPriceDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(result.get(0).getPrice()).isEqualTo(10000L);
        assertThat(result.get(1).getPriceDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(result.get(1).getPrice()).isEqualTo(10500L);
        assertThat(result.get(2).getPriceDate()).isEqualTo(LocalDate.of(2024, 1, 20));
        assertThat(result.get(2).getPrice()).isEqualTo(9800L);

        verify(dailyPriceMapper, times(1)).getPriceDuration(productCode, startDate, endDate);
    }

    @Test
    @DisplayName("빈 목록 반환 성공")
    void 빈목록_반환_성공() {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        given(dailyPriceMapper.getPriceDuration(productCode, startDate, endDate))
                .willReturn(Collections.emptyList());

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceService.getDailyPrice(productCode, startDate, endDate);

        // then
        assertThat(result).isEmpty();
        verify(dailyPriceMapper, times(1)).getPriceDuration(productCode, startDate, endDate);
    }

    @Test
    @DisplayName("하루 조회 성공")
    void 하루_조회_성공() {
        // given
        String productCode = "PROD001";
        LocalDate singleDate = LocalDate.of(2024, 1, 15);

        List<DailyPriceResponseDto.PriceRecordDto> expectedPrice = List.of(
                DailyPriceResponseDto.PriceRecordDto.builder()
                        .priceDate(singleDate)
                        .price(10000L)
                        .build()
        );

        given(dailyPriceMapper.getPriceDuration(productCode, singleDate, singleDate))
                .willReturn(expectedPrice);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceService.getDailyPrice(productCode, singleDate, singleDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriceDate()).isEqualTo(singleDate);

        verify(dailyPriceMapper).getPriceDuration(productCode, singleDate, singleDate);
    }

    @Test
    @DisplayName("존재하지 않는 productCode 빈 목록 반환")
    void 존재하지않는_productCode_빈목록_반환 () {
        // given
        String productCode = "NON_EXISTENT";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        given(dailyPriceMapper.getPriceDuration(productCode, startDate, endDate))
                .willReturn(Collections.emptyList());

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceService.getDailyPrice(productCode, startDate, endDate);

        // then
        assertThat(result).isEmpty();
        verify(dailyPriceMapper).getPriceDuration(productCode, startDate, endDate);
    }
}