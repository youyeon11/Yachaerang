package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.MonthlyPriceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class MonthlyPriceServiceTest {

    @Mock MonthlyPriceMapper monthlyPriceMapper;
    @InjectMocks MonthlyPriceService monthlyPriceService;

    @Test
    @DisplayName("월별 가격 데이터 반환 성공")
    void 월별가격데이터를_반환_성공() {
        // given
        String productCoe = "PRODUCT01";
        int startYear = 2024;
        int startMonth = 1;
        int endYear = 2024;
        int endMonth = 6;

        List<MonthlyPriceResponseDto.PriceDto> expectedResult = List.of(
                MonthlyPriceResponseDto.PriceDto.builder()
                        .priceYear(2024)
                        .priceMonth(1)
                        .avgPrice(10000L)
                        .minPrice(9000L)
                        .maxPrice(11000L)
                        .build(),
                MonthlyPriceResponseDto.PriceDto.builder()
                        .priceYear(2024)
                        .priceMonth(2)
                        .avgPrice(11000L)
                        .minPrice(10000L)
                        .maxPrice(12000L)
                        .build(),
                MonthlyPriceResponseDto.PriceDto.builder()
                        .priceYear(2024)
                        .priceMonth(3)
                        .avgPrice(90000L)
                        .minPrice(8000L)
                        .maxPrice(10000L)
                        .build()
                );
        given(monthlyPriceMapper.getPriceDuration(productCoe, startYear, startMonth, endYear, endMonth))
                .willReturn(expectedResult);

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceService.getMonthlyPrices(
                productCoe, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).hasSize(expectedResult.size());
        assertThat(result).isEqualTo(expectedResult);
        verify(monthlyPriceMapper, times(1))
                .getPriceDuration(productCoe, startYear, startMonth, endYear, endMonth);
    }

    @Test
    @DisplayName("데이터가 없으면 빈 리스트")
    void 데이터가_없으면_빈리스트(){
        // given
        String productCoe = "PRODUCT01";
        int startYear = 2024;
        int startMonth = 1;
        int endYear = 2024;
        int endMonth = 6;
        given(monthlyPriceMapper.getPriceDuration(productCoe, startYear, startMonth, endYear, endMonth))
                .willReturn(Collections.emptyList());

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceService.getMonthlyPrices(
                productCoe, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).hasSize(0);
        verify(monthlyPriceMapper, times(1))
                .getPriceDuration(productCoe, startYear, startMonth, endYear, endMonth);
    }

    @Test
    @DisplayName("단일 월 조회 성공")
    void 단일_월_조회_성공() {
        // given
        String productCode = "PROD001";
        int year = 2024;
        int month = 5;

        List<MonthlyPriceResponseDto.PriceDto> expectedPrices = List.of(
                MonthlyPriceResponseDto.PriceDto.builder()
                        .priceYear(2024)
                        .priceMonth(5)
                        .avgPrice(10000L)
                        .minPrice(9000L)
                        .maxPrice(11000L)
                        .build()
        );

        given(monthlyPriceMapper.getPriceDuration(productCode, year, month, year, month))
                .willReturn(expectedPrices);

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceService.getMonthlyPrices(
                productCode, year, month, year, month
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriceYear()).isEqualTo(2024);
        assertThat(result.get(0).getPriceMonth()).isEqualTo(5);
    }
}