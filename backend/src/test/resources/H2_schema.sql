-- 1. member table
CREATE TABLE IF NOT EXISTS member (
                        member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(50) NOT NULL,
                        nickname VARCHAR(50),
                        email VARCHAR(100) NOT NULL UNIQUE,
                        member_code VARCHAR(200) NOT NULL UNIQUE,
                        member_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                        member_role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
                        password VARCHAR(500),

                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        inactivated_at TIMESTAMP,

                        CONSTRAINT chk_member_status CHECK (member_status IN ('ACTIVE', 'INACTIVE')),
                        CONSTRAINT chk_member_role CHECK (member_role IN ('ROLE_ANONYMOUS', 'ROLE_USER', 'ROLE_ADMIN'))
);

CREATE INDEX IF NOT EXISTS idx_member_email ON member(email);
CREATE INDEX IF NOT EXISTS idx_member_code ON member(member_code);

-- 2. farm table
CREATE TABLE IF NOT EXISTS farm (
                      farm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      member_id BIGINT NOT NULL,

                      manpower INT,
                      location VARCHAR(255),
                      total_area INT,
                      cultivated_area INT,
                      flat_area INT,

                      main_crop VARCHAR(100),
                      manual_yield DECIMAL(15, 2),
                      annual_yield DECIMAL(15, 2),

                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      CONSTRAINT fk_farm_member
                          FOREIGN KEY (member_id)
                              REFERENCES member(member_id)
                              ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_farm_member_id ON farm(member_id);

-- 3. bot_session table
CREATE TABLE IF NOT EXISTS bot_session (
                             bot_session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             member_id BIGINT NOT NULL,
                             started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_bot_session_member
                                 FOREIGN KEY (member_id)
                                     REFERENCES member(member_id)
                                     ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bot_session_member_id ON bot_session(member_id);
CREATE INDEX IF NOT EXISTS idx_bot_session_started_at ON bot_session(started_at);

-- 4. bot_message table
CREATE TABLE IF NOT EXISTS bot_message (
                             bot_message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             sender_id BIGINT NOT NULL,
                             content TEXT NOT NULL,
                             bot_session_id BIGINT NOT NULL,

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_bot_message_sender
                                 FOREIGN KEY (sender_id) REFERENCES member(member_id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_bot_message_bot_session
                                 FOREIGN KEY (bot_session_id) REFERENCES bot_session(bot_session_id)
                                     ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bot_message_sender_id ON bot_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_bot_message_bot_session_id ON bot_message(bot_session_id);

-- 5. category 테이블
CREATE TABLE IF NOT EXISTS category (
                          category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          api_category_code VARCHAR(50),

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. product 테이블
CREATE TABLE IF NOT EXISTS product (
                         product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(200) NOT NULL,
                         product_code VARCHAR(100) UNIQUE NOT NULL,

                         item_code VARCHAR(50),
                         item_category_code VARCHAR(50),
                         kind_code VARCHAR(50),
                         product_rank_code VARCHAR(50),
                         country_code VARCHAR(50),

                         unit VARCHAR(50),
                         standard_weight VARCHAR(50),
                         grade VARCHAR(50),
                         origin VARCHAR(100),
                         image_url VARCHAR(500),

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_product_code ON product(product_code);

-- 7. product_category table
CREATE TABLE IF NOT EXISTS product_category (
                                  product_category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  product_id BIGINT NOT NULL,
                                  category_id BIGINT NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_product_category_product
                                      FOREIGN KEY (product_id)
                                          REFERENCES product(product_id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_product_category_category
                                      FOREIGN KEY (category_id)
                                          REFERENCES category(category_id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT uk_product_category UNIQUE (product_id, category_id)
);

CREATE INDEX IF NOT EXISTS idx_product_category_product_id ON product_category(product_id);
CREATE INDEX IF NOT EXISTS idx_product_category_category_id ON product_category(category_id);

-- 8. article table
CREATE TABLE IF NOT EXISTS article (
                         article_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(500) NOT NULL,
                         content TEXT NOT NULL,

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. tag table
CREATE TABLE IF NOT EXISTS tag (
                     tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     name VARCHAR(100) NOT NULL,
                     article_id BIGINT NOT NULL,

                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                     CONSTRAINT fk_tag_article
                         FOREIGN KEY (article_id)
                             REFERENCES article(article_id)
                             ON DELETE CASCADE
);

-- 10. member_product table
CREATE TABLE IF NOT EXISTS member_product (
                                member_product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                member_id BIGINT NOT NULL,
                                product_id BIGINT NOT NULL,
                                interest_type VARCHAR(20),
                                quantity INT,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_member_product_member
                                    FOREIGN KEY (member_id)
                                        REFERENCES member(member_id)
                                        ON DELETE CASCADE,
                                CONSTRAINT fk_member_product_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES product(product_id)
                                        ON DELETE CASCADE,

                                CONSTRAINT uk_member_product UNIQUE (member_id, product_id),
                                CONSTRAINT chk_interest_type CHECK (interest_type IN ('WATCHING', 'FARMING'))
);

CREATE INDEX IF NOT EXISTS idx_member_product_member_id ON member_product(member_id);
CREATE INDEX IF NOT EXISTS idx_member_product_product_id ON member_product(product_id);

-- 11. daily_product table
CREATE TABLE IF NOT EXISTS daily_product(
                              daily_product_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                              price_date DATE NOT NULL,
                              price DECIMAL(15, 2) NOT NULL,
                              message VARCHAR(100),
                              high_price DECIMAL(15, 2),
                              low_price DECIMAL(15, 2),
                              volume INT,
                              product_code VARCHAR(100) NOT NULL,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_daily_product_product
                                  FOREIGN KEY (product_code)
                                      REFERENCES product(product_code)
                                      ON DELETE CASCADE,

                              CONSTRAINT uk_daily_product_product_date UNIQUE (product_code, price_date)
);

CREATE INDEX IF NOT EXISTS idx_daily_product_product_code ON daily_product(product_code);
CREATE INDEX IF NOT EXISTS idx_daily_product_price_date ON daily_product(price_date);

-- 12. weekly_product
CREATE TABLE IF NOT EXISTS weekly_product (
                                weekly_product_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                week_start_date DATE NOT NULL,
                                price DECIMAL(15, 2) NOT NULL,
                                message VARCHAR(100),
                                high_price DECIMAL(15, 2),
                                low_price DECIMAL(15, 2),
                                volume INT,
                                product_code VARCHAR(100) NOT NULL,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_weekly_product_product
                                    FOREIGN KEY (product_code)
                                        REFERENCES product(product_code)
                                        ON DELETE CASCADE,

                                CONSTRAINT uk_weekly_product_product_week UNIQUE (product_code, week_start_date)
);

CREATE INDEX IF NOT EXISTS idx_weekly_product_product_code ON weekly_product(product_code);
CREATE INDEX IF NOT EXISTS idx_weekly_product_week_start ON weekly_product(week_start_date);

-- 13. monthly_product
CREATE TABLE IF NOT EXISTS monthly_product (
                                 monthly_product_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                 month_start_date DATE NOT NULL,
                                 price DECIMAL(15, 2) NOT NULL,
                                 message VARCHAR(100),
                                 high_price DECIMAL(15, 2),
                                 low_price DECIMAL(15, 2),
                                 volume INT,
                                 product_code VARCHAR(100) NOT NULL,

                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_monthly_product_product
                                     FOREIGN KEY (product_code)
                                         REFERENCES product(product_code)
                                         ON DELETE CASCADE,

                                 CONSTRAINT uk_monthly_product_product_month UNIQUE (product_code, month_start_date)
);

CREATE INDEX IF NOT EXISTS idx_monthly_product_product_code ON monthly_product(product_code);
CREATE INDEX IF NOT EXISTS idx_monthly_product_month_start ON monthly_product(month_start_date);