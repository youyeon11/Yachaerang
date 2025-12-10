package com.yachaerang.backend.api.chat.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.common.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSession extends BaseEntity {

    private Long chatSessionId;

    private Long senderId;

    private List<ChatMessage> chatMessageList;

    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Builder.Default
    public SessionStatus sessionStatus = SessionStatus.ACTIVE;

    public boolean isOwnedBy(Long memberId) {
        return this.senderId != null && this.senderId.equals(memberId);
    }

    public void end() {
        this.sessionStatus = SessionStatus.ENDED;
    }
}
