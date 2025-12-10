package com.yachaerang.backend.api.article.dto.request;

import lombok.*;

public class ArticleRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PageDto {
        private int page;
        private int size;

        // OFFSET 계산
        public int getOffset() {
            return (page - 1) * size;
        }

        // LIMIT (size와 동일)
        public int getLimit() {
            return size;
        }
    }
}
