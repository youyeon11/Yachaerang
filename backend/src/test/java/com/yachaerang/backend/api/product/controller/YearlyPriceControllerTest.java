package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.YearlyPriceResponseDto;
import com.yachaerang.backend.api.product.service.YearlyPriceService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Import(ResponseWrappingAdvice.class)
class YearlyPriceControllerTest extends RestDocsSupport {

    private final YearlyPriceService yearlyPriceService = mock(YearlyPriceService.class);

    @Override
    protected Object initController() {
        return new YearlyPriceController(yearlyPriceService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[] {new ResponseWrappingAdvice()};
    }

    private YearlyPriceResponseDto.PriceDto priceDto1;
    private YearlyPriceResponseDto.PriceDto priceDto2;
    private YearlyPriceResponseDto.PriceDto priceDto3;
    private YearlyPriceResponseDto.PriceDto priceDto4;

    // 공통 응답 필드
    private static final FieldDescriptor[] ENVELOPE_COMMON = new FieldDescriptor[]{
            fieldWithPath("httpStatus").type(STRING).description("HTTP 상태 코드"),
            fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
            fieldWithPath("code").type(STRING).description("응답 코드"),
            fieldWithPath("message").type(STRING).description("응답 메시지")
    };
    /**
     * data 가 List로 존재하는 경우의 필드
     */
    private static final FieldDescriptor DATA_LIST_DESCRIPTOR =
            fieldWithPath("data").type(ARRAY).description("응답 데이터");

    /**
     * data가 단일 객체로 존재하는 경우의 필드
     */
    private static final FieldDescriptor DATA_OBJECT_DESCRIPTOR =
            fieldWithPath("data").type(OBJECT).description("응답 데이터");

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
    @DisplayName("[GET] /api/v1/yearly-prices/{productCode}")
    void getPrices() throws Exception {
        // given
        String productCode = "PROD001";
        int startYear = 2020;
        int endYear = 2023;
        List<YearlyPriceResponseDto.PriceDto> expectedResult = List.of(priceDto1, priceDto2, priceDto3, priceDto4);
        given(yearlyPriceService.getPrices(productCode, startYear, endYear))
                .willReturn(expectedResult);

        // when & then
        mockMvc.perform(get("/api/v1/yearly-prices/{productCode}", productCode)
                .param("startYear", String.valueOf(startYear))
                .param("endYear", String.valueOf(endYear))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-yearly-prices",
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("priceYear").description(NUMBER).description("priceYear"),
                                        fieldWithPath("avgPrice").description(NUMBER).description("avgPrice"),
                                        fieldWithPath("minPrice").description(NUMBER).description("minPrice"),
                                        fieldWithPath("maxPrice").description(NUMBER).description("maxPrice"),
                                        fieldWithPath("startPrice").description(NUMBER).description("startPrice"),
                                        fieldWithPath("endPrice").description(NUMBER).description("endPrice")
                )));
    }

    @Test
    @DisplayName("[GET] /api/v1/yearly-prices/{productCode}")
    void getPriceDetail() throws Exception {
        // given
        String productCode = "PROD001";
        int year = 2020;
        YearlyPriceResponseDto.PriceDto expectedResult = priceDto1;
        given(yearlyPriceService.getPrice(productCode, year)).willReturn(expectedResult);

        // when & then
        mockMvc.perform(get("/api/v1/yearly-prices/{productCode}/detail", productCode)
                        .param("year", String.valueOf(year))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-yearly-price-detail",
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("priceYear").description(NUMBER).description("priceYear"),
                                        fieldWithPath("avgPrice").description(NUMBER).description("avgPrice"),
                                        fieldWithPath("minPrice").description(NUMBER).description("minPrice"),
                                        fieldWithPath("maxPrice").description(NUMBER).description("maxPrice"),
                                        fieldWithPath("startPrice").description(NUMBER).description("startPrice"),
                                        fieldWithPath("endPrice").description(NUMBER).description("endPrice")
                                )));
    }
}