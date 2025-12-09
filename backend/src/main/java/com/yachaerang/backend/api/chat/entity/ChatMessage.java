package com.yachaerang.backend.api.chat.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.common.SenderRole;
import com.yachaerang.backend.api.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    private Long id;

    private Long chatSessionId;

    private SenderRole senderRole;

    private Long senderId;

    private String content;

    private ChatSession chatSession;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    // 정적 팩토리 메서드
    public static ChatMessage ofUser(Long chatSessionId, Long memberId, String content) {
        return ChatMessage.builder()
                .chatSessionId(chatSessionId)
                .senderRole(SenderRole.USER)
                .senderId(memberId)
                .content(content)
                .build();
    }

    public static ChatMessage ofAssistant(Long chatSessionId, String content) {
        return ChatMessage.builder()
                .chatSessionId(chatSessionId)
                .senderRole(SenderRole.ASSISTANT)
                .senderId(null)
                .content(content)
                .build();
    }

    public boolean isUserMessage() {
        return this.senderRole == SenderRole.USER;
    }

    public boolean isAssistantMessage() {
        return this.senderRole == SenderRole.ASSISTANT;
    }
}
