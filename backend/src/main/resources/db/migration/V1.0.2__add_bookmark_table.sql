-- bookmark table 생성
CREATE TABLE IF NOT EXISTS bookmark (
    bookmark_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id   BIGINT NOT NULL,
    article_id  BIGINT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (bookmark_id),
    UNIQUE KEY uk_bookmark_member_article (member_id, article_id),
    KEY idx_bookmark_article_member (article_id, member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
