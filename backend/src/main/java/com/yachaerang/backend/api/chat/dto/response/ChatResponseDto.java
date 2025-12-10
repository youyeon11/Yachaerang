package com.yachaerang.backend.api.chat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

public class ChatResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MessageDto {
        private String response;
        private Long chatSessionId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HistoryMessageDto {
        private Long id;
        private String role;
        private String content;
        private LocalDateTime sentAt;
    }
}
