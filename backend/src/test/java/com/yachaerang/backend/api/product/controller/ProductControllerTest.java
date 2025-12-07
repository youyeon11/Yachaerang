package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.ProductResponseDto;
import com.yachaerang.backend.api.product.service.ProductService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@Import({ResponseWrappingAdvice.class})
class ProductControllerTest extends RestDocsSupport {

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController()
    {
        return new ProductController(productService);
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
    @DisplayName("[GET] /api/v1/products/item")
    void getItems() throws Exception {
        // given
        List<ProductResponseDto.ItemDto> items = List.of(
                ProductResponseDto.ItemDto.builder()
                        .itemCode("ITEM001")
                        .itemName("상품1")
                        .build(),
                ProductResponseDto.ItemDto.builder()
                        .itemCode("ITEM002")
                        .itemName("상품2")
                        .build()
        );
        given(productService.getItemNames()).willReturn(items);

        // when & then
        mockMvc.perform(get("/api/v1/products/item")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-items",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("itemName").type(STRING).description("상위 품목 이름"),
                                        fieldWithPath("itemCode").type(STRING).description("상위 품목 코드")
                        )
                ));
    }

    @Test
    @DisplayName("[GET] /api/v1/products/{itemCode}/subItem")
    void getProducts() throws Exception {
        // given
        String itemCode = "ITEM001";
        List<ProductResponseDto.SubItemDto> products = List.of(
                ProductResponseDto.SubItemDto.builder()
                        .name("상품명1")
                        .productCode("PROD001")
                        .itemName("상품1")
                        .itemCode("ITEM001")
                        .kindName("종류1")
                        .kindCode("KIND001")
                        .productRank("A")
                        .rankCode("RANK001")
                        .build(),
                ProductResponseDto.SubItemDto.builder()
                        .name("상품명2")
                        .productCode("PROD002")
                        .itemName("상품1")
                        .itemCode("ITEM001")
                        .kindName("종류1")
                        .kindCode("KIND001")
                        .productRank("B")
                        .rankCode("RANK002")
                        .build()
        );
        given(productService.getProductNames(itemCode)).willReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/products/{itemCode}/subItem", itemCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-products",
                        requestHeaders(),
                        pathParameters(
                                parameterWithName("itemCode").description("상위 품목 코드")
                        ),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_LIST_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("name").type(STRING).description("name"),
                                        fieldWithPath("productCode").type(STRING).description("productCode"),
                                        fieldWithPath("itemName").type(STRING).description("itemName"),
                                        fieldWithPath("itemCode").type(STRING).description("itemCode"),
                                        fieldWithPath("kindName").type(STRING).description("kindName"),
                                        fieldWithPath("kindCode").type(STRING).description("kindCode"),
                                        fieldWithPath("productRank").type(STRING).description("productRank"),
                                        fieldWithPath("rankCode").type(STRING).description("rankCode"))
                ));
    }

    @Test
    @DisplayName("존재하지 않는 itemCode로 빈 목록 반환")
    void getProducts_NotFound() throws Exception {
        // given
        String itemCode = "NON_EXISTENT";
        given(productService.getProductNames(itemCode)).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/v1/products/{productCode}/subItem", "NON_EXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}