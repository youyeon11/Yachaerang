package com.yachaerang.backend.api.article.controller;

import com.yachaerang.backend.api.article.dto.request.ArticleRequestDto;
import com.yachaerang.backend.api.article.dto.response.ArticleResponseDto;
import com.yachaerang.backend.api.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/articles")
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ArticleResponseDto.PageDto<?> getAllArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        ArticleRequestDto.PageDto pageRequest
                = ArticleRequestDto.PageDto.builder()
                    .page(page)
                    .size(size)
                    .build();

        return articleService.getAllArticles(pageRequest);
    }

    @GetMapping("/{articleId}")
    public ArticleResponseDto.DetailDto getArticle(
            @PathVariable("articleId") Long articleId
    ) {
        return articleService.getArticle(articleId);
    }

    @GetMapping("/search")
    public ArticleResponseDto.PageDto<?> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "keyword") String keyword
    ) {
        ArticleRequestDto.PageDto pageRequest
                = ArticleRequestDto.PageDto.builder()
                .page(page)
                .size(size)
                .build();
        return articleService.searchArticles(pageRequest, keyword);
    }
}
