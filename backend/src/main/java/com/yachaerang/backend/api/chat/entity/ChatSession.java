package com.yachaerang.backend.api.chat.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.common.SessionStatus;
import com.yachaerang.backend.api.member.entity.Member;
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

    private Long id;

    private Long memberId;

    private List<ChatMessage> chatMessageList;

    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Builder.Default
    public SessionStatus sessionStatus = SessionStatus.ACTIVE;

    public boolean isOwnedBy(Long memberId) {
        return this.memberId != null && this.memberId.equals(memberId);
    }

    public void end() {
        this.sessionStatus = SessionStatus.ENDED;
    }
}
