package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.DailyPriceResponseDto;
import com.yachaerang.backend.api.product.service.DailyPriceService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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

    private List<DailyPriceResponseDto.RankDto> rankList1;
    private List<DailyPriceResponseDto.RankDto> rankList2;

    @BeforeEach
    void setUp() {
        rankList1 = List.of(
                DailyPriceResponseDto.RankDto.builder()
                        .itemName("수박")
                        .itemCode("WATERMELON")
                        .unit("개")
                        .price(20000L)
                        .build(),
                DailyPriceResponseDto.RankDto.builder()
                        .itemName("포도")
                        .itemCode("GRAPE")
                        .unit("kg")
                        .price(15000L)
                        .build(),
                DailyPriceResponseDto.RankDto.builder()
                        .itemName("딸기")
                        .itemCode("STRAWBERRY")
                        .unit("kg")
                        .price(12000L)
                        .build()
            );
        rankList2 = List.of(
                DailyPriceResponseDto.RankDto.builder()
                        .itemName("오렌지")
                        .itemCode("ORANGE")
                        .unit("개")
                        .price(3000L)
                        .build(),
                DailyPriceResponseDto.RankDto.builder()
                        .itemName("자두")
                        .itemCode("PLUM")
                        .unit("kg")
                        .price(4000L)
                        .build(),
                DailyPriceResponseDto.RankDto.builder()
                        .itemName("사과")
                        .itemCode("APPLE")
                        .unit("kg")
                        .price(5000L)
                        .build()
        );
    }


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
                                        fieldWithPath("priceDate").type(STRING).description("기록 날짜"),
                                        fieldWithPath("price").type(NUMBER).description("가격")
                                )));
    }

    @Test
    @DisplayName("[GET] /api/v1/daily-prices/rank/high-prices")
    public void getHighPrices() throws Exception {
        // given
        given(dailyPriceService.getHighPriceRank())
                .willReturn(rankList1);
        // when & then
        mockMvc.perform(get("/api/v1/daily-prices/rank/high-prices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-high-prices",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("itemName").type(STRING).description("상품 이름"),
                                        fieldWithPath("itemCode").type(STRING).description("상품 코드"),
                                        fieldWithPath("unit").type(STRING).description("단위"),
                                        fieldWithPath("price").type(NUMBER).description("가격")
                                )));
    }

    @Test
    @DisplayName("[GET] /api/v1/daily-prices/rank/low-prices")
    public void getLowPrices() throws Exception {
        // given
        given(dailyPriceService.getLowPriceRank())
                .willReturn(rankList2);
        // when & then
        mockMvc.perform(get("/api/v1/daily-prices/rank/low-prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-low-prices",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("itemName").type(STRING).description("상품 이름"),
                                        fieldWithPath("itemCode").type(STRING).description("상품 코드"),
                                        fieldWithPath("unit").type(STRING).description("단위"),
                                        fieldWithPath("price").type(NUMBER).description("가격")
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