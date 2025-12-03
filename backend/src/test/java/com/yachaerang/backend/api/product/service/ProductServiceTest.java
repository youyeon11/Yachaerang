package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.ProductResponseDto;
import com.yachaerang.backend.api.product.repository.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductMapper productMapper;
    @InjectMocks ProductService productService;

    @Test
    @DisplayName("아이템 목록 반환 성공")
    void 아이템목록_반환_성공() {
        // given
        List<ProductResponseDto.ItemDto> expectedItems = List.of(
                ProductResponseDto.ItemDto.builder()
                        .itemCode("ITEM001")
                        .itemName("상품1")
                        .build(),
                ProductResponseDto.ItemDto.builder()
                        .itemCode("ITEM002")
                        .itemName("상품2")
                        .build()
        );
        given(productMapper.findAllItemNameAndItemCodes()).willReturn(expectedItems);

        // when
        List<ProductResponseDto.ItemDto> result = productService.getItemNames();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItemCode()).isEqualTo("ITEM001");
        assertThat(result.get(0).getItemName()).isEqualTo("상품1");
        assertThat(result.get(1).getItemCode()).isEqualTo("ITEM002");
        assertThat(result.get(1).getItemName()).isEqualTo("상품2");

        verify(productMapper, times(1)).findAllItemNameAndItemCodes();
    }

    @Test
    @DisplayName("빈 목록일 때의 반환 성공")
    void 빈목록일때의_반환_성공() {
        // given
        given(productMapper.findAllItemNameAndItemCodes()).willReturn(Collections.emptyList());

        // when
        List<ProductResponseDto.ItemDto> result = productService.getItemNames();

        // then
        assertThat(result).isEmpty();
        verify(productMapper, times(1)).findAllItemNameAndItemCodes();
    }

    @Test
    @DisplayName("ProductMapper 호출 성공")
    void productMapper_호출_성공() {
        // given
        given(productMapper.findAllItemNameAndItemCodes()).willReturn(Collections.emptyList());

        // when
        productService.getItemNames();

        // then
        verify(productMapper).findAllItemNameAndItemCodes();
        verifyNoMoreInteractions(productMapper);
    }


    @Test
    @DisplayName("제품 목록 하위부류까지 반환 성공")
    void 제품목록_하위부류까지_반환_성공() {
        // given
        String itemCode = "ITEM001";
        List<ProductResponseDto.SubItemDto> expectedProducts = List.of(
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
        given(productMapper.findProductNameByItemCode(itemCode)).willReturn(expectedProducts);

        // when
        List<ProductResponseDto.SubItemDto> result = productService.getProductNames(itemCode);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(product -> {
            assertThat(product.getItemCode()).isEqualTo(itemCode);
        });

        verify(productMapper, times(1)).findProductNameByItemCode(itemCode);
    }

    @Test
    @DisplayName("존재하지 않을 때의 하위부류 빈 목록 반환 성공")
    void 존재하지않을때의_하위부류_빈목록_반환_성공() {
        // given
        String itemCode = "NON_EXISTENT";
        given(productMapper.findProductNameByItemCode(itemCode)).willReturn(Collections.emptyList());

        // when
        List<ProductResponseDto.SubItemDto> result = productService.getProductNames(itemCode);

        // then
        assertThat(result).isEmpty();
        verify(productMapper, times(1)).findProductNameByItemCode(itemCode);
    }

    @Test
    @DisplayName("하위부류DTO 반환 필드 검증")
    void 하위부류DTO_반환_필드_검증() {
        // given
        String itemCode = "ITEM001";
        ProductResponseDto.SubItemDto expectedProduct = ProductResponseDto.SubItemDto.builder()
                .name("테스트상품")
                .productCode("PROD001")
                .itemName("아이템1")
                .itemCode("ITEM001")
                .kindName("종류A")
                .kindCode("KIND001")
                .productRank("특")
                .rankCode("RANK001")
                .build();

        given(productMapper.findProductNameByItemCode(itemCode))
                .willReturn(List.of(expectedProduct));

        // when
        List<ProductResponseDto.SubItemDto> result = productService.getProductNames(itemCode);

        // then
        assertThat(result).hasSize(1);
        ProductResponseDto.SubItemDto actual = result.get(0);

        assertThat(actual.getName()).isEqualTo("테스트상품");
        assertThat(actual.getProductCode()).isEqualTo("PROD001");
        assertThat(actual.getItemName()).isEqualTo("아이템1");
        assertThat(actual.getItemCode()).isEqualTo("ITEM001");
        assertThat(actual.getKindName()).isEqualTo("종류A");
        assertThat(actual.getKindCode()).isEqualTo("KIND001");
        assertThat(actual.getProductRank()).isEqualTo("특");
        assertThat(actual.getRankCode()).isEqualTo("RANK001");
    }

    @Test
    @DisplayName("productMapper 호출 시 파라미터 전달 검증")
    void setProductMapper_호출시_파라미터_전달_검증() {
        // given
        String itemCode = "ITEM001";
        given(productMapper.findProductNameByItemCode(anyString()))
                .willReturn(Collections.emptyList());

        // when
        productService.getProductNames(itemCode);

        // then
        verify(productMapper).findProductNameByItemCode(eq("ITEM001"));
    }
}