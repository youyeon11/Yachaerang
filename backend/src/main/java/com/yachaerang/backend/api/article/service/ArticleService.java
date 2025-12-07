package com.yachaerang.backend.api.article.service;

import com.yachaerang.backend.api.article.dto.request.ArticleRequestDto;
import com.yachaerang.backend.api.article.dto.response.ArticleResponseDto;
import com.yachaerang.backend.api.article.entity.Article;
import com.yachaerang.backend.api.article.entity.Tag;
import com.yachaerang.backend.api.article.repository.ArticleMapper;
import com.yachaerang.backend.api.article.repository.TagMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;

    /*
    기사 목록 전체 조회하기
     */
    public ArticleResponseDto.PageDto<ArticleResponseDto.ListDto> getAllArticles(
            ArticleRequestDto.PageDto pageRequest) {

        // Article Pagination 조회
        List<Article> articleList = articleMapper.findAllWithPagination(pageRequest.getLimit(), pageRequest.getOffset());

        // 빈 리스트일 때
        if (articleList.isEmpty()) {
            return ArticleResponseDto.PageDto.of(
                    Collections.emptyList(), pageRequest, 0L
            );
        }

        // Article ID 목록
        List<Long> articleIdList = articleList.stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        // 모든 Article의 TagName 한 번에 조회와 mapping
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);
        Map<Long, List<String>> tagNameMap = tagList.stream()
                .peek(tag -> log.debug("스트림 처리 중 Tag: {}", tag))
                .collect(Collectors.groupingBy(
                        Tag::getArticleId,
                        Collectors.mapping(Tag::getName, Collectors.toList())
                ));

        log.debug("Articles found: {}", articleIdList);

        List<ArticleResponseDto.ListDto> articleResponseDtoList
                 = articleList.stream()
                .map(article -> ArticleResponseDto.ListDto.builder()
                        .articleId(article.getId())
                        .title(article.getTitle())
                        .imageUrl(article.getImageUrl())
                        .createdAt(article.getCreatedAt().toLocalDate())
                        .tagList(tagNameMap.getOrDefault(article.getId(), Collections.emptyList()))
                        .build()
                )
                .toList();

        // 전체 개수 조회 및 PageResponse 생성
        long totalElements = articleMapper.countAll();
        return ArticleResponseDto.PageDto.of(articleResponseDtoList, pageRequest, totalElements);
    }

    /*
    특정 기사만 조회하기
     */
    public ArticleResponseDto.DetailDto getArticle(Long articleId) {
        List<String> tagList = tagMapper.findNameByArticleId(articleId);
        Article article = articleMapper.findById(articleId);

        if (article == null) {
            throw GeneralException.of(ErrorCode.ARTICLE_NOT_FOUND);
        }

        return ArticleResponseDto.DetailDto.builder()
                .articleId(articleId)
                .title(article.getTitle())
                .imageUrl(article.getImageUrl())
                .content(article.getContent())
                .url(article.getUrl())
                .tagList(tagList)
                .createdAt(article.getCreatedAt().toLocalDate())
                .build();
    }
}
