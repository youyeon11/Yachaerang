package com.yachaerang.backend.api.favorite.controller;

import com.yachaerang.backend.api.favorite.dto.request.FavoriteRequestDto;
import com.yachaerang.backend.api.favorite.dto.response.FavoriteResponseDto;
import com.yachaerang.backend.api.favorite.service.FavoriteService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(ResponseWrappingAdvice.class)
class FavoriteControllerTest extends RestDocsSupport {

    private final FavoriteService favoriteService = mock(FavoriteService.class);

    @Override
    protected Object initController() {
        return new FavoriteController(favoriteService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[]{ new ResponseWrappingAdvice() };
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
    private static final FieldDescriptor DATA_ARRAY_DESCRIPTOR =
            fieldWithPath("data").type(ARRAY).description("응답 데이터");

    /**
     * data 가 단일객체로 존재하는 경우의 필드
     */
    private static final FieldDescriptor DATA_OBJECT_DESCRIPTOR =
            fieldWithPath("data").type(OBJECT).description("응답 데이터 (없음)");

    /**
     * data 가 존재하지 않는 경우(null) 의 필드
     */
    private static final FieldDescriptor DATA_NULL_DESCRIPTOR =
            fieldWithPath("data").type(NULL).description("응답 데이터 (없음)");

    private static final String AUTH_HEADER = "Authorization";
    private static final String TEST_TOKEN = "Bearer test-access-token";
    private static final Long TEST_FAVORITE_ID = 1L;
    private static final String TEST_PRODUCT_CODE = "PROD001";
    private static final String TEST_PERIOD_TYPE = "DAILY";

    @Test
    @DisplayName("[POST] /api/v1/favorites")
    void register_Success() throws Exception {
        // given
        FavoriteRequestDto.RegisterDto requestDto = FavoriteRequestDto.RegisterDto.builder()
                .productCode(TEST_PRODUCT_CODE)
                .periodType(TEST_PERIOD_TYPE)
                .build();

        FavoriteResponseDto.RegisterDto responseDto = FavoriteResponseDto.RegisterDto.builder()
                .favoriteId(TEST_FAVORITE_ID)
                .productCode(TEST_PRODUCT_CODE)
                .periodType(TEST_PERIOD_TYPE)
                .build();

        given(favoriteService.register(any(FavoriteRequestDto.RegisterDto.class)))
                .willReturn(responseDto);

        // when
        mockMvc.perform(post("/api/v1/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER, TEST_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isOk())
                .andDo(doc(
                        "register",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_OBJECT_DESCRIPTOR)
                            .andWithPrefix("data.",
                                    fieldWithPath("favoriteId").type(NUMBER).description("관심 대시보드(favorite) 고유 ID"),
                                    fieldWithPath("productCode").type(STRING).description("상품 코드"),
                                    fieldWithPath("periodType").type(STRING).description("기간 타입")
                            )));
    }

    @Test
    @DisplayName("[DELETE] /api/v1/favorites/{favoriteId}")
    void erase_Success() throws Exception {
        // given
        willDoNothing().given(favoriteService).erase(TEST_FAVORITE_ID);

        // when & then
        mockMvc.perform(delete("/api/v1/favorites/{favoriteId}", TEST_FAVORITE_ID)
                        .header(AUTH_HEADER, TEST_TOKEN)
                ).andExpect(status().isOk())
                .andDo(doc(
                        "erase",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("favoriteId").description("관심 대시보드(favorite) 고유 ID")
                        ),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)));
    }

    @Test
    @DisplayName("관심사 목록을 성공적으로 조회한다")
    void getAll_Success() throws Exception {
        // given
        List<FavoriteResponseDto.ReadDto> responseList = List.of(
                FavoriteResponseDto.ReadDto.builder()
                        .favoriteId(1L)
                        .itemCode("ITEM_A")
                        .itemName("A")
                        .productCode("PROD_A")
                        .productName("PRODUCT_A")
                        .periodType("DAILY")
                        .build(),
                FavoriteResponseDto.ReadDto.builder()
                        .favoriteId(2L)
                        .itemCode("ITEM_B")
                        .itemName("B")
                        .productCode("PROD_B")
                        .productName("PRODUCT_B")
                        .periodType("WEEKLY")
                        .build(),
                FavoriteResponseDto.ReadDto.builder()
                        .favoriteId(3L)
                        .itemCode("ITEM_C")
                        .itemName("C")
                        .productCode("PROD_C")
                        .productName("PRODUCT_C")
                        .periodType("MONTHLY")
                        .build()
        );

        given(favoriteService.getAllFavoriteList()).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/v1/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TEST_TOKEN)
                ).andExpect(status().isOk())
                .andDo(doc(
                        "get-all",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_ARRAY_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("favoriteId").type(NUMBER).description("관심 대시보드(favorite) 고유 ID"),
                                        fieldWithPath("itemCode").type(STRING).description("상위 품목 코드"),
                                        fieldWithPath("itemName").type(STRING).description("상위 품목 이름"),
                                        fieldWithPath("productCode").type(STRING).description("상품 코드"),
                                        fieldWithPath("productName").type(STRING).description("상품 이름"),
                                        fieldWithPath("periodType").type(STRING).description("기간 타입")
                                )));
    }






    @Test
    @DisplayName("요청 본문 없이 등록 시도하면 실패한다")
    void register_NoRequestBody_Fails() throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/v1/favorites")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Authorization 헤더 없이 요청하면 실패한다")
    void register_NoAuthHeader_Fails() throws Exception {
        // given
        FavoriteRequestDto.RegisterDto requestDto = FavoriteRequestDto.RegisterDto.builder()
                .productCode(TEST_PRODUCT_CODE)
                .periodType(TEST_PERIOD_TYPE)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }
}