package com.yachaerang.backend.api.article.repository;

import com.yachaerang.backend.api.article.entity.Tag;
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
@Import(MyBatisConfig.class)
@Sql(scripts = {"classpath:H2_schema.sql", "/sql/article-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class TagMapperTest {

    @Autowired TagMapper tagMapper;

    @Test
    @DisplayName("여러 Article의 Tag 조회 성공")
    void 여러_Article의_Tag_조회_성공() {
        // given
        List<Long> articleIdList = List.of(1L, 2L);

        // when
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);

        // then
        assertThat(tagList).isNotEmpty();
        assertThat(tagList).allSatisfy(tag -> {
            assertThat(tag.getArticleId()).isIn(1L, 2L);
            assertThat(tag.getName()).isNotNull();
        });
    }

    @Test
    @DisplayName("단일 Article의 Tag 조회")
    void 단일_Article의_Tag_조회() {
        // given
        List<Long> articleIdList = List.of(1L);

        // when
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);

        // then
        assertThat(tagList).isNotEmpty();
        assertThat(tagList).allSatisfy(tag ->
                assertThat(tag.getArticleId()).isEqualTo(1L)
        );
    }

    @Test
    @DisplayName("존재하지 않는 Article ID로 조회 시 빈 리스트 반환")
    void 존재하지_않는_Article_ID_조회() {
        // given
        List<Long> articleIdList = List.of(9999L);

        // when
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);

        // then
        assertThat(tagList).isEmpty();
    }

    @Test
    @DisplayName("Tag 정보가 올바르게 매핑")
    void Tag_정보_매핑_확인() {
        // given
        List<Long> articleIdList = List.of(1L);

        // when
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);

        // then
        assertThat(tagList).isNotEmpty();
        Tag tag = tagList.get(0);
        assertThat(tag.getId()).isNotNull();
        assertThat(tag.getName()).isNotNull();
        assertThat(tag.getArticleId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("특정 Article의 Tag Name 조회 성공")
    void 특정_Article의_Tag_Name_조회_성공() {
        // given
        Long articleId = 1L;

        // when
        List<String> tagNameList = tagMapper.findNameByArticleId(articleId);

        // then
        assertThat(tagNameList).isNotEmpty();
        assertThat(tagNameList).allSatisfy(name ->
                assertThat(name).isNotBlank()
        );
    }

    @Test
    @DisplayName("Tag가 여러 개인 Article 조회")
    void Tag가_여러개인_Article_조회() {
        // given
        Long articleId = 1L;

        // when
        List<String> tagNameList = tagMapper.findNameByArticleId(articleId);

        // then
        assertThat(tagNameList).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Tag가 없는 Article 조회 시 빈 리스트 반환")
    void Tag가_없는_Article_조회() {
        // given
        Long articleId = 10L;  // test-data에서 tag가 없는 article

        // when
        List<String> tagNameList = tagMapper.findNameByArticleId(articleId);

        // then
        assertThat(tagNameList).isEmpty();
    }
}