package com.yachaerang.backend.api.article.service;

import com.yachaerang.backend.api.article.dto.request.ArticleRequestDto;
import com.yachaerang.backend.api.article.dto.response.ArticleResponseDto;
import com.yachaerang.backend.api.article.entity.Article;
import com.yachaerang.backend.api.article.entity.Tag;
import com.yachaerang.backend.api.article.repository.ArticleMapper;
import com.yachaerang.backend.api.article.repository.TagMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    public ArticleResponseDto.PageDto<ArticleResponseDto.ListDto> getAllArticles(
            ArticleRequestDto.PageDto pageRequest) {

        // Article Pagination 조회
        List<Article> articleList = articleMapper.findAllWithPagination(pageRequest.getLimit(), pageRequest.getOffset());

        // 전체 개수 조회
        long totalElements = articleMapper.countAll();
        if (articleList.isEmpty()) {
            return ArticleResponseDto.PageDto.of(
                    Collections.emptyList(), pageRequest, totalElements
            );
        }

        // Article ID 목록
        List<Long> articleIdList = articleList.stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        // 모든 Article의 TagName 한 번에 조회와 mapping
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);
        Map<Long, List<String>> tagNameMap = tagList.stream()
                .peek(tag -> LogUtil.debug("스트림 처리 중 Tag: {}", tag))
                .collect(Collectors.groupingBy(
                        Tag::getArticleId,
                        Collectors.mapping(Tag::getName, Collectors.toList())
                ));

        LogUtil.debug("Articles found: {}", articleIdList);

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

        // 페이지네이션 Response 생성
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

    /*
    기사 검색하기
     */
    public ArticleResponseDto.PageDto<ArticleResponseDto.ListDto> searchArticles(
            ArticleRequestDto.PageDto pageRequest, String keyword) {

        LogUtil.info("{} 키워드 조회 시작...", keyword);
        List<Article> articleList = articleMapper.findByKeyword(pageRequest.getLimit(), pageRequest.getOffset(), keyword);

        // 페이지네이션을 위한 전체 조회
        long totalElements = articleMapper.countByKeyword(keyword);

        // 빈 리스트일 때
        if (articleList.isEmpty()) {
            return ArticleResponseDto.PageDto.of(
                    Collections.emptyList(), pageRequest, totalElements
            );
        }
        List<Long> articleIdList = articleList.stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        // 모든 Article의 TagName 한 번에 조회와 mapping
        List<Tag> tagList = tagMapper.findByArticleIdList(articleIdList);
        Map<Long, List<String>> tagNameMap = tagList.stream()
                .peek(tag -> LogUtil.debug("스트림 처리 중 Tag: {}", tag))
                .collect(Collectors.groupingBy(
                        Tag::getArticleId,
                        Collectors.mapping(Tag::getName, Collectors.toList())
                ));

        LogUtil.debug("Articles found: {}", articleIdList);

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

        return ArticleResponseDto.PageDto.of(articleResponseDtoList, pageRequest, totalElements);
    }
}
