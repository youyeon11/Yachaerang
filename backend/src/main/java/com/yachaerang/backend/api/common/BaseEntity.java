package com.yachaerang.backend.api.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
