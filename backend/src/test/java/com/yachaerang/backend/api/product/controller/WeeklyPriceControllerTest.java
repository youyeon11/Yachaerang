package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.WeeklyPriceResponseDto;
import com.yachaerang.backend.api.product.service.WeeklyPriceService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@Import({ResponseWrappingAdvice.class})
class WeeklyPriceControllerTest extends RestDocsSupport {

    private final WeeklyPriceService weeklyPriceService = mock(WeeklyPriceService.class);

    @Override
    protected Object initController() {
        return new WeeklyPriceController(weeklyPriceService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[]{new ResponseWrappingAdvice()};
    }

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

    @Test
    @DisplayName("[GET] /api/v1/weekly-prices/{productCode}")
    void getWeeklyPrice() throws Exception {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<WeeklyPriceResponseDto.PriceRecordDto> priceList = List.of(
                WeeklyPriceResponseDto.PriceRecordDto.builder()
                        .startDate(LocalDate.of(2025, 11, 3))
                        .endDate(LocalDate.of(2025, 11, 9))
                        .avgPrice(15000L)
                        .minPrice(12000L)
                        .maxPrice(18000L)
                        .priceChange(100L)
                        .priceChangeRate(new BigDecimal(1.00))
                        .build(),
                WeeklyPriceResponseDto.PriceRecordDto.builder()
                        .startDate(LocalDate.of(2025, 11, 10))
                        .endDate(LocalDate.of(2025, 11, 16))
                        .avgPrice(16000L)
                        .minPrice(13000L)
                        .maxPrice(19000L)
                        .priceChange(-100L)
                        .priceChangeRate(new BigDecimal(-1.00))
                        .build()
        );

        given(weeklyPriceService.getPriceDuration(productCode, startDate, endDate))
                .willReturn(priceList);

        // when & then
        mockMvc.perform(get("/api/v1/weekly-prices/{productCode}", productCode)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-weekly-prices",
                        requestHeaders(),
                        pathParameters(
                                parameterWithName("productCode").description("상품 코드")
                        ),
                        queryParameters(
                                parameterWithName("startDate").description("시작 날짜"),
                                parameterWithName("endDate").description("종료 날짜")
                        ),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("startDate").type(VARIES).description("기록 시작 날짜"),
                                        fieldWithPath("endDate").type(VARIES).description("기록 종료 날짜"),
                                        fieldWithPath("avgPrice").type(NUMBER).description("평균 가격"),
                                        fieldWithPath("minPrice").type(NUMBER).description("최소 가격"),
                                        fieldWithPath("maxPrice").type(NUMBER).description("최고 가격"),
                                        fieldWithPath("priceChange").type(NUMBER).description("지난주 대비 가격 변화량"),
                                        fieldWithPath("priceChangeRate").type(NUMBER).description("지난주 대비 가격 변화율")
                                )));
    }

    @Test
    @DisplayName("[GET] /api/v1/weekly-prices/{productCode} - 빈 결과")
    void getWeeklyPrice_emptyResult() throws Exception {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 7);

        given(weeklyPriceService.getPriceDuration(productCode, startDate, endDate))
                .willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/v1/weekly-prices/{productCode}", productCode)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-07")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}