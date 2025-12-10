package com.yachaerang.backend.api.chat.dto.request;

import lombok.*;

public class ChatRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MessageDto {
        private String message;
    }
}
