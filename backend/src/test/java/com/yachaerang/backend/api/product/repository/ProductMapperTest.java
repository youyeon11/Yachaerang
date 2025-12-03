package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.ProductResponseDto;
import com.yachaerang.backend.global.config.MyBatisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Sql("classpath:H2_schema.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Import(MyBatisConfig.class)
class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

    @Test
    @DisplayName("전체 ItemName과 ItemCode 조회 - 성공")
    @Sql(scripts = "/product-test-data.sql", executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 전체ItemName_ItemCode조회_성공() {
        // when
        List<ProductResponseDto.ItemDto> result = productMapper.findAllItemNameAndItemCodes();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItemName()).isNotNull();
        assertThat(result.get(0).getItemCode()).isNotNull();
    }

    @Test
    @DisplayName("ItemCode로 ProdcutName 조회 - 성공")
    @Sql(scripts = "/product-test-data.sql", executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void ItemCode로_ProductName조회_성공() {
        // given
        String itemCode = "315";
        // when
        List<ProductResponseDto.SubItemDto> result = productMapper.findProductNameByItemCode(itemCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(subItemDto ->  {
            assertThat(subItemDto.getItemCode()).isEqualTo(itemCode);
            assertThat(subItemDto.getItemName()).isNotNull();
            assertThat(subItemDto.getProductCode()).isNotNull();
        });
    }

    @Test
    @DisplayName("존재하지 않는 ItemCode로 ProductName 조회")
    void 존재하지않는_ItemCode로_ProductName조회 () {
        // given
        String nonExistentItemCode = "NON_EXISTENT";

        // when
        List<ProductResponseDto.SubItemDto> result = productMapper.findProductNameByItemCode(nonExistentItemCode);

        // then
        assertThat(result).isEmpty(); // [] 응답
    }

    @Test
    @DisplayName("전체 Item 조회 - 반환된 데이터 검증")
    @Sql(scripts = "/product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 전체_Item_조회_반환된_데이터_검증() {
        // when
        List<ProductResponseDto.ItemDto> result = productMapper.findAllItemNameAndItemCodes();

        // then
        assertThat(result)
                .extracting(ProductResponseDto.ItemDto::getItemCode)
                .contains("224", "225");

        assertThat(result)
                .extracting(ProductResponseDto.ItemDto::getItemName)
                .contains("호박", "토마토");
    }

    @Test
    @DisplayName("SubItemDto 전체 필드 검증")
    @Sql(scripts = "/product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void SubItemDto_전체필드_검증 () {
        // given
        String itemCode = "215";

        // when
        List<ProductResponseDto.SubItemDto> result = productMapper.findProductNameByItemCode(itemCode);

        // then
        assertThat(result).isNotEmpty();

        // 처음만 검증
        ProductResponseDto.SubItemDto firstItem = result.get(0);
        assertThat(firstItem.getName()).isNotNull();
        assertThat(firstItem.getProductCode()).isNotNull();
        assertThat(firstItem.getItemName()).isNotNull();
        assertThat(firstItem.getItemCode()).isEqualTo(itemCode);
        assertThat(firstItem.getKindName()).isNotNull();
        assertThat(firstItem.getKindCode()).isNotNull();
        assertThat(firstItem.getProductRank()).isNotNull();
        assertThat(firstItem.getRankCode()).isNotNull();
    }
}