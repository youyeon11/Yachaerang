package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
import com.yachaerang.backend.api.product.service.MonthlyPriceService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@Import({ResponseWrappingAdvice.class})
class MonthlyPriceControllerTest extends RestDocsSupport {

    private final MonthlyPriceService monthlyPriceService = mock(MonthlyPriceService.class);

    @Override
    protected Object initController()
    {
        return new MonthlyPriceController(monthlyPriceService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[] { new ResponseWrappingAdvice() };
    }

    private MonthlyPriceResponseDto.PriceDto priceDto1;
    private MonthlyPriceResponseDto.PriceDto priceDto2;
    private MonthlyPriceResponseDto.PriceDto priceDto3;

    // 공통 응답 필드
    private static final FieldDescriptor[] ENVELOPE_COMMON = new FieldDescriptor[]{
            fieldWithPath("httpStatus").type(STRING).description("HTTP 상태 코드"),
            fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
            fieldWithPath("code").type(STRING).description("응답 코드"),
            fieldWithPath("message").type(STRING).description("응답 메시지")
    };
    /**
     * data 가 존재하는 경우의 필드
     */
    private static final FieldDescriptor DATA_LIST_DESCRIPTOR =
            fieldWithPath("data").type(ARRAY).description("응답 데이터");

    @BeforeEach
    void setUp() {
        priceDto1 = MonthlyPriceResponseDto.PriceDto.builder()
                .priceYear(2024)
                .priceMonth(1)
                .avgPrice(10000L)
                .minPrice(9000L)
                .maxPrice(11000L)
                .priceChange(-1000L)
                .priceChangeRate(new BigDecimal(-1.00))
                .build();
        priceDto2 = MonthlyPriceResponseDto.PriceDto.builder()
                        .priceYear(2024)
                        .priceMonth(2)
                        .avgPrice(11000L)
                        .minPrice(10000L)
                        .maxPrice(12000L)
                        .priceChange(-1000L)
                        .priceChangeRate(new BigDecimal(-1.00))
                        .build();
        priceDto3 = MonthlyPriceResponseDto.PriceDto.builder()
                        .priceYear(2024)
                        .priceMonth(3)
                        .avgPrice(90000L)
                        .minPrice(8000L)
                        .maxPrice(10000L)
                        .priceChange(100L)
                        .priceChangeRate(new BigDecimal(1.00))
                        .build();
    }

    @Test
    @DisplayName("[GET] /api/v1/monthly-prices/{productCode}")
    void getPrices() throws Exception {
        // given
        String productCode = "PROD001";
        int startYear = 2024;
        int startMonth = 1;
        int endYear = 2024;
        int endMonth = 3;

        List<MonthlyPriceResponseDto.PriceDto> expectedPrices = List.of(
                priceDto1, priceDto2, priceDto3
        );

        given(monthlyPriceService.getMonthlyPrices(productCode, startYear, startMonth, endYear, endMonth))
                .willReturn(expectedPrices);

        // when & then
        mockMvc.perform(get("/api/v1/monthly-prices/{productCode}", productCode)
                        .param("startYear", "2024")
                        .param("startMonth", "1")
                        .param("endYear", "2024")
                        .param("endMonth", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-monthly-prices",
                        requestHeaders(),
                        pathParameters(
                                parameterWithName("productCode").description("상품 코드")
                        ),
                        queryParameters(
                                parameterWithName("startYear").description("시작 날짜의 연도"),
                                parameterWithName("startMonth").description("시작 날짜의 달"),
                                parameterWithName("endYear").description("종료 날짜의 연도"),
                                parameterWithName("endMonth").description("종료 날짜의 연도")
                        ),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("priceYear").type(NUMBER).description("기록 연도"),
                                        fieldWithPath("priceMonth").type(NUMBER).description("기록 월"),
                                        fieldWithPath("avgPrice").type(NUMBER).description("평균 가격"),
                                        fieldWithPath("minPrice").type(NUMBER).description("최소 가격"),
                                        fieldWithPath("maxPrice").type(NUMBER).description("최고 가격"),
                                        fieldWithPath("priceChange").type(NUMBER).description("전월 대비 가격 변화량"),
                                        fieldWithPath("priceChangeRate").type(NUMBER).description("전월 대비 가격 변화율")
                                        )
                        )
                );
    }


    @Test
    @DisplayName("데이터가 없으면 빈 배열을 반환")
    void returnsEmptyArray_whenNoData() throws Exception {
        // given
        String productCode = "PROD001";
        int startYear = 2020;
        int startMonth = 1;
        int endYear = 2020;
        int endMonth = 12;

        given(monthlyPriceService.getMonthlyPrices(productCode, startYear, startMonth, endYear, endMonth))
                .willReturn(Collections.emptyList());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/monthly-prices/{productCode}", productCode)
                .param("startYear", String.valueOf(startYear))
                .param("startMonth", String.valueOf(startMonth))
                .param("endYear", String.valueOf(endYear))
                .param("endMonth", String.valueOf(endMonth)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}