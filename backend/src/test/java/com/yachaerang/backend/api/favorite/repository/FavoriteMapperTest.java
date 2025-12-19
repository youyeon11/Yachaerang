package com.yachaerang.backend.api.favorite.repository;

import com.yachaerang.backend.api.common.PeriodType;
import com.yachaerang.backend.api.favorite.dto.response.FavoriteResponseDto;
import com.yachaerang.backend.api.favorite.entity.Favorite;
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
import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Import(MyBatisConfig.class)
@Sql(value = "classpath:H2_schema.sql", executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/product-test-data.sql", "/sql/favorite-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/cleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FavoriteMapperTest {

    @Autowired private FavoriteMapper favoriteMapper;

    private static final String TEST_ITEM_NAME = "사과";
    private static final String TEST_ITEM_CODE = "411";
    private static final String TEST_PRODUCT_CODE = "KM-411-02-04"; // 테스트 데이터와는 다른 productCode
    private static final String TEST_PRODUCT_NAME = "홍로(10kg) - 상품";
    private static final Long TEST_MEMBER_ID = 1L;
    private static final String TEST_PERIOD_TYPE = "DAILY";

    /*
    저장 용 메서드
    */
    private Favorite createAndSaveFavorite(Long memberId, String productCode, String periodType) {
        Favorite favorite = Favorite.builder()
                .memberId(memberId)
                .productCode(productCode)
                .periodType(PeriodType.fromCode(periodType))
                .build();
        favoriteMapper.save(favorite);
        return favorite;
    }

    @Test
    @DisplayName("관심사 등록 성공")
    void 관심사_등록_성공 () {
        // given
        Favorite favorite = Favorite.builder()
                .memberId(TEST_MEMBER_ID)
                .productCode(TEST_PRODUCT_CODE)
                .periodType(PeriodType.fromCode("DAILY"))
                .build();

        // when
        int result = favoriteMapper.save(favorite);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(favorite.getFavoriteId()).isNotNull();
    }

    @Test
    @DisplayName("저장 후 PK 존재")
    void 저장_후_PK_존재() {
        // given
        Favorite favorite = Favorite.builder()
                .memberId(TEST_MEMBER_ID)
                .productCode(TEST_PRODUCT_CODE)
                .periodType(PeriodType.fromCode("DAILY"))
                .build();

        // when
        favoriteMapper.save(favorite);

        // then
        assertThat(favorite.getFavoriteId()).isPositive();
    }

    @Test
    @DisplayName("관심사 해제 성공")
    void 관심사_해제_성공() {
        // given
        Favorite favorite = createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");
        Long favoriteId = favorite.getFavoriteId();

        // when
        int result = favoriteMapper.deleteByIdAndMemberId(favoriteId, TEST_MEMBER_ID);

        // then
        assertThat(result).isEqualTo(1);

        // 삭제 확인
        FavoriteResponseDto.ReadDto deleted = favoriteMapper.findByMemberIdAndProductCode(
                TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("다른 회원의 관심사 삭제 실패")
    void 다른회원의_관심사_삭제_실패() {
        // given
        Favorite favorite = createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");
        Long favoriteId = favorite.getFavoriteId();
        Long otherMemberId = 2L;

        // when
        int result = favoriteMapper.deleteByIdAndMemberId(favoriteId, otherMemberId);

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("관심사 찾기 성공")
    void 관심사_찾기_성공() {
        // given
        createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");

        // when
        FavoriteResponseDto.ReadDto found = favoriteMapper.findByMemberIdAndProductCode(
                TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getItemCode()).isEqualTo(TEST_ITEM_CODE);
        assertThat(found.getItemName()).isEqualTo(TEST_ITEM_NAME);
        assertThat(found.getProductCode()).isEqualTo(TEST_PRODUCT_CODE);
        assertThat(found.getProductName()).isEqualTo(TEST_PRODUCT_NAME);
        assertThat(found.getPeriodType()).isEqualTo("DAILY");
    }

    @Test
    @DisplayName("존재하지 않는 조합으로 조회 시 null")
    void 존재하지않는_조합으로_조회시_null() {
        // when
        FavoriteResponseDto.ReadDto found = favoriteMapper.findByMemberIdAndProductCode(
                TEST_MEMBER_ID, "NON_EXISTING_PROD", TEST_PERIOD_TYPE);

        // then
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("기간타입이 다른 것 조회")
    void 기간타입이_다른것_조회() {
        // given
        createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");
        createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "WEEKLY");

        // when
        FavoriteResponseDto.ReadDto dailyFavorite = favoriteMapper.findByMemberIdAndProductCode(
                TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");
        FavoriteResponseDto.ReadDto weeklyFavorite = favoriteMapper.findByMemberIdAndProductCode(
                TEST_MEMBER_ID, TEST_PRODUCT_CODE, "WEEKLY");

        // then
        assertThat(dailyFavorite).isNotNull();
        assertThat(weeklyFavorite).isNotNull();
        assertThat(dailyFavorite.getFavoriteId())
                .isNotEqualTo(weeklyFavorite.getFavoriteId());
    }

    @Test
    @DisplayName("회원의 모든 관심사 조회 성공")
    void 회원의_모든_관심사_조회_성공() {
        // given
        createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "DAILY");
        createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "WEEKLY");
        createAndSaveFavorite(TEST_MEMBER_ID, TEST_PRODUCT_CODE, "MONTHLY");

        // when
        List<FavoriteResponseDto.ReadDto> favoriteList = favoriteMapper.findAllByMemberId(TEST_MEMBER_ID);

        // then
        assertThat(favoriteList).hasSize(8); // 기존의 테스트 데이터 5
    }

    @Test
    @DisplayName("관심사가 없는 회원은 빈 리스트를 반환")
    void 관심사가_없는_회원은_빈리스트를_반환() {
        // given
        Long memberWithNoFavorites = 3L;

        // when
        List<FavoriteResponseDto.ReadDto> favorites = favoriteMapper.findAllByMemberId(memberWithNoFavorites);

        // then
        assertThat(favorites).isEmpty();
    }

    @Test
    @DisplayName("최신순으로 정렬")
    void 최신순으로_정렬() throws InterruptedException {
        // given
        Favorite first = createAndSaveFavorite(3L, TEST_PRODUCT_CODE, "DAILY");
        Thread.sleep(10);
        Favorite second = createAndSaveFavorite(3L, TEST_PRODUCT_CODE, "WEEKLY");
        Thread.sleep(10);
        Favorite third = createAndSaveFavorite(3L, TEST_PRODUCT_CODE, "MONTHLY");

        // when
        List<FavoriteResponseDto.ReadDto> favorites = favoriteMapper.findAllByMemberId(3L);

        // then
        assertThat(favorites).hasSize(3);
        assertThat(favorites.get(0).getPeriodType()).isEqualTo("DAILY");
        assertThat(favorites.get(1).getPeriodType()).isEqualTo("WEEKLY");
        assertThat(favorites.get(2).getPeriodType()).isEqualTo("MONTHLY");
    }

    @Test
    @DisplayName("다른 회원의 관심사는 조회불가")
    void 다른_회원의_관심사는_조회불가() {
        // given
        Long otherMemberId = 3L;
        createAndSaveFavorite(otherMemberId, TEST_PRODUCT_CODE,"DAILY");

        // when
        List<FavoriteResponseDto.ReadDto> myFavorites = favoriteMapper.findAllByMemberId(TEST_MEMBER_ID);
        List<FavoriteResponseDto.ReadDto> otherFavorites = favoriteMapper.findAllByMemberId(otherMemberId);

        // then
        assertThat(myFavorites).hasSize(5); // 테스트 데이터
        assertThat(otherFavorites).hasSize(1);
        assertThat(myFavorites.get(0).getProductCode()).isNotEqualTo(otherFavorites.get(0).getProductCode());
    }
}