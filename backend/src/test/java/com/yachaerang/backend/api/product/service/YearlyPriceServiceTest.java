package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
import com.yachaerang.backend.api.product.dto.response.YearlyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.YearlyPriceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@Transactional
@ExtendWith(MockitoExtension.class)
class YearlyPriceServiceTest {

    @Mock YearlyPriceMapper yearlyPriceMapper;
    @InjectMocks YearlyPriceService yearlyPriceService;

    private YearlyPriceResponseDto.PriceDto priceDto1;
    private YearlyPriceResponseDto.PriceDto priceDto2;
    private YearlyPriceResponseDto.PriceDto priceDto3;
    private YearlyPriceResponseDto.PriceDto priceDto4;

    @BeforeEach
    void setUp() {
        priceDto1 = YearlyPriceResponseDto.PriceDto.builder()
                .priceYear(2020)
                .avgPrice(100000L)
                .minPrice(90000L)
                .maxPrice(110000L)
                .startPrice(95000L)
                .endPrice(105000L)
                .build();
        priceDto2 = YearlyPriceResponseDto.PriceDto.builder()
                .priceYear(2021)
                .avgPrice(100000L)
                .minPrice(90000L)
                .maxPrice(110000L)
                .startPrice(95000L)
                .endPrice(105000L)
                .build();
        priceDto3 = YearlyPriceResponseDto.PriceDto.builder()
                .priceYear(2022)
                .avgPrice(100000L)
                .minPrice(90000L)
                .maxPrice(110000L)
                .startPrice(95000L)
                .endPrice(105000L)
                .build();
        priceDto4 = YearlyPriceResponseDto.PriceDto.builder()
                .priceYear(2023)
                .avgPrice(110000L)
                .minPrice(100000L)
                .maxPrice(120000L)
                .startPrice(105000L)
                .endPrice(115000L)
                .build();
    }

    @Test
    @DisplayName("연도별 가격 데이터 목록 조회 성공")
    void 연도별_가격_데이터_목록_조회_성공() {
        // given
        String productCode = "PROD001";
        int startYear = 2020;
        int endYear = 2024;

        List<YearlyPriceResponseDto.PriceDto> expectedPrices = List.of(
                priceDto1, priceDto2, priceDto3, priceDto4
        );
        given(yearlyPriceMapper.getPriceDuration(productCode, startYear, endYear))
                .willReturn(expectedPrices);

        // when
        List<YearlyPriceResponseDto.PriceDto> result = yearlyPriceService.getPrices(productCode, startYear, endYear);

        // then
        assertThat(result).isEqualTo(expectedPrices);
        assertThat(result).hasSize(expectedPrices.size());
        verify(yearlyPriceMapper, times(1)).getPriceDuration(productCode, startYear, endYear);
    }

    @Test
    @DisplayName("데이터가 없을 때 빈 리스트")
    void 데이터가_없을때_빈리스트() {
        // given
        String productCode = "PROD001";
        int startYear = 2020;
        int endYear = 2024;

        given(yearlyPriceMapper.getPriceDuration(productCode, startYear, endYear))
                .willReturn(Collections.emptyList());

        // when
        List<YearlyPriceResponseDto.PriceDto> result = yearlyPriceService.getPrices(productCode, startYear, endYear);

        // then
        assertThat(result).isEqualTo(Collections.emptyList());
        assertThat(result).hasSize(0);
        verify(yearlyPriceMapper, times(1)).getPriceDuration(productCode, startYear, endYear);
    }

    @Test
    @DisplayName("단일연도 조회 성공")
    void 단일연도_조회_성공() {
        // given
        String productCode = "PROD001";
        int year = 2020;
        List<YearlyPriceResponseDto.PriceDto> expectedResult = List.of(priceDto1);
        given(yearlyPriceMapper.getPriceDuration(productCode, year, year))
                .willReturn(expectedResult);
        // when
        List<YearlyPriceResponseDto.PriceDto> result = yearlyPriceService.getPrices(productCode, year, year);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(priceDto1);
    }

    @Test
    @DisplayName("특정 연도의 가격 데이터만 조회")
    void 특정연도의_가격데이터만_조회() {
        // given
        String productCode = "PROD001";
        int targetYear = 2020;

        YearlyPriceResponseDto.PriceDto expectedPrice = priceDto1;

        given(yearlyPriceMapper.getPriceSpecification(productCode, targetYear))
                .willReturn(expectedPrice);

        // when
        YearlyPriceResponseDto.PriceDto result = yearlyPriceService.getPrice(
                productCode, targetYear
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedPrice);
        assertThat(result.getPriceYear()).isEqualTo(2020);

        verify(yearlyPriceMapper, times(1))
                .getPriceSpecification(productCode, targetYear);
    }
}