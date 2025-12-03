package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.DailyPriceResponseDto;
import com.yachaerang.backend.api.product.service.DailyPriceService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import({ResponseWrappingAdvice.class})
class DailyPriceControllerTest extends RestDocsSupport {

    private final DailyPriceService dailyPriceService = mock(DailyPriceService.class);

    @Override
    protected Object initController() {
        return new DailyPriceController(dailyPriceService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[] { new ResponseWrappingAdvice() };
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
    @DisplayName("[GET] /api/v1/daily-prices/{productCode}")
    void getDailyPrice() throws Exception {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<DailyPriceResponseDto.PriceRecordDto> prices = List.of(
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

        given(dailyPriceService.getDailyPrice(productCode, startDate, endDate))
                .willReturn(prices);

        // when & then
        mockMvc.perform(get("/api/v1/daily-prices/{productCode}", productCode)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-daily-prices",
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("priceDate").description(STRING).description("priceDate"),
                                        fieldWithPath("price").description(NUMBER).description("price")
                                )));
    }



    @Test
    @DisplayName("빈 목록 반환 성공")
    void getDailyPrice_EmptyList() throws Exception {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        given(dailyPriceService.getDailyPrice(productCode, startDate, endDate))
                .willReturn(Collections.emptyList());

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/daily-prices/{productCode}", productCode)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(dailyPriceService).getDailyPrice(productCode, startDate, endDate);
    }

    @Test
    @DisplayName("하루 기간 조회 성공")
    void getDailyPrice_SingleDay() throws Exception {
        // given
        String productCode = "PROD001";
        LocalDate singleDate = LocalDate.of(2024, 1, 15);

        List<DailyPriceResponseDto.PriceRecordDto> price = List.of(
                DailyPriceResponseDto.PriceRecordDto.builder()
                        .priceDate(singleDate)
                        .price(10000L)
                        .build()
        );

        given(dailyPriceService.getDailyPrice(productCode, singleDate, singleDate))
                .willReturn(price);

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/daily-prices/{productCode}", productCode)
                        .param("startDate", "2024-01-15")
                        .param("endDate", "2024-01-15"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)));}

    @Test
    @DisplayName("다양한 productCode 테스트 성공")
    void getDailyPrice_VariousProductCodes() throws Exception {
        // given
        String[] productCodes = {"PROD001", "prod-with-dash", "123"};
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        for (String productCode : productCodes) {
            given(dailyPriceService.getDailyPrice(productCode, startDate, endDate))
                    .willReturn(Collections.emptyList());

            // when & then
            mockMvc.perform(
                            get("/api/v1/daily-prices/{productCode}", productCode)
                                    .param("startDate", "2024-01-01")
                                    .param("endDate", "2024-01-31"))
                    .andExpect(status().isOk());

            verify(dailyPriceService).getDailyPrice(productCode, startDate, endDate);
        }
    }

    @Test
    @DisplayName("실패 - startDate 누락")
    void getDailyPrice_MissingStartDate() throws Exception {
        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/daily-prices/{productCode}", "PROD001")
                        .param("endDate", "2024-01-31"));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 - endDate 누락")
    void getDailyPrice_MissingEndDate() throws Exception {
        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/daily-prices/{productCode}", "PROD001")
                        .param("startDate", "2024-01-01"));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 - 모든 RequestParam 누락")
    void getDailyPrice_MissingAllParams() throws Exception {
        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/daily-prices/{productCode}", "PROD001"));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 - 잘못된 날짜 형식")
    void getDailyPrice_InvalidDateFormat() throws Exception {
        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/daily-prices/{productCode}", "PROD001")
                        .param("startDate", "01-01-2024")  // 잘못된 형식
                        .param("endDate", "2024-01-31"));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }
}