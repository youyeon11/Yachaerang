package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Sql("classpath:H2_schema.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Import(MyBatisConfig.class)
class MonthlyPriceMapperTest {

    @Autowired
    private MonthlyPriceMapper monthlyPriceMapper;

    @Test
    @DisplayName("특정 기간의 가격 데이터를 조회 성공")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 특정기간의_가격데이터를_조회_성공() {
        // given
        String productCode = "KM-411-01-04";
        int startYear = 2025;
        int startMonth = 9;
        int endYear = 2025;
        int endMonth = 10;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // 첫 번째 데이터 검증
        MonthlyPriceResponseDto.PriceDto first = result.get(0);
        assertThat(first.getPriceYear()).isEqualTo(2025);
        assertThat(first.getPriceMonth()).isEqualTo(9);

        // 마지막 데이터 검증
        MonthlyPriceResponseDto.PriceDto last = result.get(result.size() - 1);
        assertThat(last.getPriceYear()).isEqualTo(2025);
        assertThat(last.getPriceMonth()).isEqualTo(10);
    }

    @Test
    @DisplayName("연도를 넘어가는 기간의 가격 데이터 조회 성공")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 연도를_넘어가는_기간의_가격데이터_조회_성공() {
        // given
        String productCode = "KM-411-01-04";
        int startYear = 2024;
        int startMonth = 12;
        int endYear = 2025;
        int endMonth = 11;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(4);

        // 오름차순 정렬
        assertThat(result.get(0).getPriceYear()).isEqualTo(2024);
        assertThat(result.get(0).getPriceMonth()).isEqualTo(12);
        assertThat(result.get(1).getPriceYear()).isEqualTo(2025);
        assertThat(result.get(1).getPriceMonth()).isEqualTo(9);
        assertThat(result.get(3).getPriceYear()).isEqualTo(2025);
        assertThat(result.get(3).getPriceMonth()).isEqualTo(11);
    }

    @Test
    @DisplayName("단일 월의 가격 데이터 조회 성공")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 단일월의_가격데이터_조회_성공() {
        // given
        String productCode = "KM-411-01-04";
        int startYear = 2024;
        int startMonth = 12;
        int endYear = 2024;
        int endMonth = 12;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriceYear()).isEqualTo(2024);
        assertThat(result.get(0).getPriceMonth()).isEqualTo(12);
    }

    @Test
    @DisplayName("존재하지 않는 상품코드로 조회 시 빈 리스트")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 존재하지않는_상품코드로_조회시_빈리스트 () {
        // given
        String productCode = "NOT_EXIST";
        int startYear = 2024;
        int startMonth = 1;
        int endYear = 2024;
        int endMonth = 6;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("데이터가 없는 기간을 조회 시 빈 리스트")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 데이터가_없는기간을_조회시_빈리스트 () {
        // given
        String productCode = "KM-411-01-04";
        int startYear = 2020;
        int startMonth = 1;
        int endYear = 2020;
        int endMonth = 12;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("가격 데이터 매핑 성공")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 가격데이터_매핑_성공() {
        // given
        String productCode = "KM-411-01-04";
        int startYear = 2025;
        int startMonth = 9;
        int endYear = 2025;
        int endMonth = 12;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).hasSize(3);
        MonthlyPriceResponseDto.PriceDto priceDto = result.get(0);

        assertThat(priceDto.getAvgPrice()).isNotNull();
        assertThat(priceDto.getMinPrice()).isNotNull();
        assertThat(priceDto.getMaxPrice()).isNotNull();
        assertThat(priceDto.getMinPrice()).isLessThanOrEqualTo(priceDto.getAvgPrice());
        assertThat(priceDto.getAvgPrice()).isLessThanOrEqualTo(priceDto.getMaxPrice());
    }

    @Test
    @DisplayName("결과가 연도와 월의 오름차순으로 정렬")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/monthly-price-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 결과가_연도와_월의_오름차순으로_정렬() {
        // given
        String productCode = "KM-411-01-04";
        int startYear = 2025;
        int startMonth = 9;
        int endYear = 2025;
        int endMonth = 11;

        // when
        List<MonthlyPriceResponseDto.PriceDto> result = monthlyPriceMapper.getPriceDuration(
                productCode, startYear, startMonth, endYear, endMonth
        );

        // then
        assertThat(result).isNotEmpty();

        for (int i = 0; i < result.size() - 1; i++) {
            MonthlyPriceResponseDto.PriceDto current = result.get(i);
            MonthlyPriceResponseDto.PriceDto next = result.get(i + 1);

            int currentValue = current.getPriceYear() * 12 + current.getPriceMonth();
            int nextValue = next.getPriceYear() * 12 + next.getPriceMonth();

            assertThat(currentValue).isLessThan(nextValue);
        }
    }
}