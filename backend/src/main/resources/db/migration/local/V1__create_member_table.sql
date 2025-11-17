-- V1: 사용자 테이블 생성
CREATE TABLE member (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE,
    member_code VARCHAR(200) NOT NULL UNIQUE,
    member_status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    role ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    inactivated_at TIMESTAMP,

    INDEX idx_member_email (email),
    INDEX idx_member_code (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE farm (
    farm_id BIGINT PRIMARY KEY AUTO INCREMENT,
    member_id BIGINT NOT NULL,

    manpower INT,
    location VARCHAR(255),
    total_area INT NOT NULL DEFAULT 0,
    cultivated_area INT NOT NULL DEFAULT 0,
    flat_area INT NOT NULL DEFAULT 0,

    main_crop VARCHAR(100),
    manual_yield DECIMAL(15, 2),
    annual_yield DECIMAL(15, 2),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_farm_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id)
            ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

