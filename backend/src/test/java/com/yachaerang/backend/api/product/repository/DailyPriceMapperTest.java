package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.DailyPriceResponseDto;
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
class DailyPriceMapperTest {

    @Autowired DailyPriceMapper dailyPriceMapper;

    @Test
    @DisplayName("특정 기간 동안의 가격 정보 가져오기")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 특정기간동안의_가격정보_가져오기() {
        // given
        String productCode = "KM-141-01-04";
        LocalDate startDate = LocalDate.of(2025, 10, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 31);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(record -> {
            assertThat(record.getPriceDate()).isAfterOrEqualTo(startDate);
            assertThat(record.getPriceDate()).isBeforeOrEqualTo(endDate);
            assertThat(record.getPrice()).isNotNull();
            assertThat(record.getPrice()).isPositive();
        });
    }

    @Test
    @DisplayName("정확한 날짜 범위 검증")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 정확한_날짜_범위_검증 () {
        // given
        String productCode = "KM-141-01-04";
        LocalDate startDate = LocalDate.of(2025, 10, 10);
        LocalDate endDate = LocalDate.of(2025, 10, 15);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).allMatch(record ->
                !record.getPriceDate().isBefore(startDate) &&
                        !record.getPriceDate().isAfter(endDate)
        );
    }

    @Test
    @DisplayName("하루 기간 조회 (startDate == endDate)")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 하루기간_조회_성공 () {
        // given
        String productCode = "KM-141-01-04";
        LocalDate singleDate = LocalDate.of(2025, 11, 3);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, singleDate, singleDate);

        // then
        assertThat(result).isNotNull();
        if (!result.isEmpty()) {
            assertThat(result).allMatch(record ->
                    record.getPriceDate().isEqual(singleDate));
        }
    }

    @Test
    @DisplayName("빈 결과 - 존재하지 않는 productCode")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 빈_결과_존재하지않는_productCode() {
        // given
        String productCode = "NON_EXISTENT";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 결과 - 데이터 없는 기간 조회")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 빈_결과_데이터없는기간() {
        // given
        String productCode = "KM-141-01-04";
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 1, 31);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 결과 - 미래 날짜 조회")
    void 빈_결과_미래기간() {
        // given
        String productCode = "KM-141-01-04";
        LocalDate startDate = LocalDate.of(2099, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("결과 정렬 확인 - 날짜순 정렬")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 결과정렬_확인_날짜순정렬() {
        // given
        String productCode = "KM-141-01-04";
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        if (result.size() > 1) {
            for (int i = 0; i < result.size() - 1; i++) {
                assertThat(result.get(i).getPriceDate())
                        .isBeforeOrEqualTo(result.get(i + 1).getPriceDate());
            }
        }
    }

    @Test
    @DisplayName("가격 데이터 유효성 검증")
    @Sql(scripts = {"/sql/product-test-data.sql", "/sql/daily-price-test-data.sql"}, executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 가격데이터_유효성_검증() {
        // given
        String productCode = "KM-141-01-04";
        LocalDate startDate = LocalDate.of(2025, 10, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 31);

        // when
        List<DailyPriceResponseDto.PriceRecordDto> result =
                dailyPriceMapper.getPriceDuration(productCode, startDate, endDate);

        // then
        assertThat(result).allSatisfy(record -> {
            assertThat(record.getPrice()).isNotNull();
            assertThat(record.getPrice()).isGreaterThanOrEqualTo(0L);
        });
    }
}