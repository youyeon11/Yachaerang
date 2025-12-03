-- V1: 기본 테이블 생성

-- 1. member table
CREATE TABLE member (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE,
    member_code VARCHAR(200) NOT NULL UNIQUE,
    member_status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    member_role VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    password VARCHAR(500),
    image_url VARCHAR(500) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    inactivated_at TIMESTAMP,

    INDEX idx_member_email (email),
    INDEX idx_member_code (member_code),

    CONSTRAINT chk_member_status_type
        CHECK (member_status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_role_type
        CHECK (member_role IN ('ROLE_ANONYMOUS', 'ROLE_USER', 'ROLE_ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. farm table
CREATE TABLE farm (
    farm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,

    manpower INT NULL,
    location VARCHAR(255) NULL,
    cultivated_area DOUBLE(15, 2) NULL,
    flat_area DOUBLE(15, 2) NULL,
    main_crop VARCHAR(100) NULL,
    grade VARCHAR(5) NULL,
    comment VARCHAR(50) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_farm_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id)
            ON DELETE CASCADE,

        INDEX idx_farm_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. bot_session table
CREATE TABLE bot_session (
                             bot_session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             member_id BIGINT NOT NULL,
                             started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT fk_bot_session_member
                                 FOREIGN KEY (member_id)
                                     REFERENCES member(member_id)
                                     ON DELETE CASCADE,

                             INDEX idx_bot_session_member_id (member_id),
                             INDEX idx_bot_session_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. bot_message table
CREATE TABLE bot_message (
                             bot_message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             sender_id BIGINT NOT NULL,
                             content TEXT NOT NULL,
                             bot_session_id BIGINT NOT NULL,

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT fk_bot_message_sender
                                 FOREIGN KEY (sender_id) REFERENCES member(member_id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_bot_message_bot_session
                                 FOREIGN KEY (bot_session_id) REFERENCES bot_session(bot_session_id)
                                     ON DELETE CASCADE,

                             INDEX idx_bot_message_sender_id (sender_id),
                             INDEX idx_bot_message_bot_session_id (bot_session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. product 테이블
CREATE TABLE product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    product_code VARCHAR(100) UNIQUE NOT NULL,

    item_name VARCHAR(50),
    item_code VARCHAR(50),
    kind_name VARCHAR(50),
    kind_code VARCHAR(50),
    product_rank VARCHAR(50),
    rank_code VARCHAR(50),
    unit VARCHAR(50),
    origin VARCHAR(100),
    image_url VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. article table
CREATE TABLE article (
                         article_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(500) NOT NULL,
                         content TEXT NOT NULL,

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. tag table
CREATE TABLE tag (
                     tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     name VARCHAR(100) NOT NULL,
                     article_id BIGINT NOT NULL,

                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                     CONSTRAINT fk_tag_article
                         FOREIGN KEY (article_id)
                             REFERENCES article(article_id)
                             ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. member_product table
CREATE TABLE member_product (
    member_product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    interest_type VARCHAR(10),
    quantity INT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_member_product_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_member_product_product
        FOREIGN KEY (product_id)
            REFERENCES product(product_id)
            ON DELETE CASCADE,
    CONSTRAINT chk_interest_type
        CHECK (interest_type IN ('WATCHING', 'FARMING')),

    UNIQUE KEY uk_member_product (member_id, product_id),

    INDEX idx_member_product_member_id (member_id),
    INDEX idx_member_product_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. daily_price table
CREATE TABLE daily_price (
    daily_price_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    price_date DATE NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    product_code VARCHAR(100) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                              CONSTRAINT fk_daily_product_product
                                  FOREIGN KEY (product_code)
                                      REFERENCES product(product_code)
                                      ON DELETE CASCADE,

                              UNIQUE KEY uk_daily_product_product_date (product_code, price_date),
                              INDEX idx_daily_product_product_code (product_code),
                              INDEX idx_daily_product_price_date (price_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. weekly_price table
CREATE TABLE weekly_price (
    weekly_price_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(100) NOT NULL,
    price_year INT NOT NULL,
    week_number INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL ,
    avg_price DECIMAL(15, 2),
    max_price DECIMAL(15, 0),
    min_price DECIMAL(15, 0),
    price_count INT DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                CONSTRAINT fk_weekly_product_product
                                    FOREIGN KEY (product_code)
                                        REFERENCES product(product_code)
                                        ON DELETE CASCADE,

                                UNIQUE KEY uk_weekly_product_product_week (product_code, start_date),
                                INDEX idx_weekly_product_product_code (product_code),
                                INDEX idx_weekly_product_week_start (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. monthly_price table
CREATE TABLE monthly_price (
    monthly_product_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(100) NOT NULL,
    price_year INT,
    price_month INT,
    avg_price DECIMAL(15, 2),
    min_price DECIMAL(15, 0),
    max_price DECIMAL(15, 0),
    price_count INT DEFAULT 0,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_monthly_product_product
                                     FOREIGN KEY (product_code)
                                         REFERENCES product(product_code)
                                         ON DELETE CASCADE,

    UNIQUE KEY uk_monthly_product_product_month (product_code, price_year, price_month),
    INDEX idx_monthly_product_month (price_year, price_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. yearly_price table
CREATE TABLE yearly_price (
    yearly_product_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(100) NOT NULL,
    price_year INT,
    avg_price DECIMAL(15, 2),
    min_price DECIMAL(15, 0),
    max_price DECIMAL(15, 0),
    start_price DECIMAL(15, 0) NULL,
    end_price DECIMAL(15, 0) NULL,
    price_count INT DEFAULT 0,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                              CONSTRAINT fk_yearly_price_product
                                  FOREIGN KEY (product_code) REFERENCES product(product_code)
                                      ON DELETE CASCADE,

                              UNIQUE KEY uk_yearly_price (product_code, price_year),
                              INDEX idx_year (price_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;