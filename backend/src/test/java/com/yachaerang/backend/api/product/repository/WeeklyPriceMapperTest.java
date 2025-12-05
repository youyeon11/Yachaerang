package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.WeeklyPriceResponseDto;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Sql("classpath:H2_schema.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Import(MyBatisConfig.class)
class WeeklyPriceMapperTest {

    @Autowired
    private WeeklyPriceMapper weeklyPriceMapper;

    @Test
    @DisplayName("주어진 기간 동안의 주차별 가격정보 조회")
    void 주어진기간_동안의_주차별_가격정보_조회() {
        // given
        String productCode = "PROD001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // when
        List<WeeklyPriceResponseDto.PriceRecordDto> result =
                weeklyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        for (WeeklyPriceResponseDto.PriceRecordDto record : result) {
            assertThat(record.getStartDate()).isNotNull();
            assertThat(record.getEndDate()).isNotNull();
            assertThat(record.getStartDate()).isBeforeOrEqualTo(record.getEndDate());
            assertThat(record.getMinPrice()).isLessThanOrEqualTo(record.getAvgPrice());
            assertThat(record.getAvgPrice()).isLessThanOrEqualTo(record.getMaxPrice());
        }
    }

    @Test
    @DisplayName("존재하지 않는 상품코드로 조회시 빈 리스트")
    void 존재하지않는_상품_조회시_빈_리스트() {
        // given
        String productCode = "INVALID_CODE";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // when
        List<WeeklyPriceResponseDto.PriceRecordDto> result =
                weeklyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("시작일과 종료일이 같은 경우 정상 조회")
    void 시작과_종료일_같은경우_정상조회() {
        // given
        String productCode = "PROD001";
        LocalDate sameDate = LocalDate.of(2024, 1, 15);

        // when
        List<WeeklyPriceResponseDto.PriceRecordDto> result =
                weeklyPriceMapper.getPriceDuration(productCode, sameDate, sameDate);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("모든 필드가 올바르게 매핑된다")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql", "/sql/weekly-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 모든필드_올바르게_매핑_성공 () {
        // given
        String productCode = "KM-611-01-05";
        LocalDate startDate = LocalDate.of(2024, 11, 10);
        LocalDate endDate = LocalDate.of(2024, 11, 16);

        // when
        List<WeeklyPriceResponseDto.PriceRecordDto> result =
                weeklyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        WeeklyPriceResponseDto.PriceRecordDto dto = result.get(0);
        assertThat(dto.getStartDate()).isEqualTo(startDate);
        assertThat(dto.getEndDate()).isEqualTo(endDate);
        assertThat(dto.getMinPrice()).isEqualTo(1000);
        assertThat(dto.getMaxPrice()).isEqualTo(1500);
        assertThat(dto.getAvgPrice()).isEqualTo(1250);
    }
}