package com.yachaerang.backend.api.article.service;

import com.yachaerang.backend.api.article.dto.request.ArticleRequestDto;
import com.yachaerang.backend.api.article.dto.response.ArticleResponseDto;
import com.yachaerang.backend.api.article.entity.Article;
import com.yachaerang.backend.api.article.entity.Tag;
import com.yachaerang.backend.api.article.repository.ArticleMapper;
import com.yachaerang.backend.api.article.repository.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;

    /*
    기사 목록 전체 조회하기
     */
    public ArticleResponseDto.PageDto<?> getAllArticles(
            ArticleRequestDto.PageDto pageRequest) {

        // Article Pagination 조회
        List<Article> articleList = articleMapper.findAllWithPagination(pageRequest);

        if (articleList.isEmpty()) {
            return ArticleResponseDto.PageDto.of(articleList, pageRequest, 0L);
        }

        List<Long> articleIdList = articleList.stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        // 모든 Article의 Tag를 한 번에 조회
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);

        // Article 별로 Tag 그룹핑
        Map<Long, List<Tag>> tagMap = tagList.stream()
                .collect(Collectors.groupingBy(Tag::getArticleId));

        // 각 Article에 Tag 설정
        articleList.forEach(article ->
                article.setTagList(tagMap.getOrDefault(article.getId(), new ArrayList<>()))
        );

        // 전체 개수 조회 및 PageResponse 생성
        long totalElements = articleMapper.countAll();
        return ArticleResponseDto.PageDto.of(articleList, pageRequest, totalElements);
    }

    /*
    특정 기사만 조회하기
     */
    public ArticleResponseDto.DetailDto getArticle(Long articleId) {
        List<String> tagList = tagMapper.findNameByArticleId(articleId);
        Article article = articleMapper.findById(articleId);
        return ArticleResponseDto.DetailDto.builder()
                .id(articleId)
                .title(article.getTitle())
                .content(article.getContent())
                .url(article.getUrl())
                .tagList(tagList)
                .createdAt(article.getCreatedAt().toLocalDate())
                .build();
    }
}
