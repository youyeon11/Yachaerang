package com.yachaerang.backend.api.chat.repository;

import com.yachaerang.backend.api.chat.entity.ChatMessage;
import com.yachaerang.backend.api.common.SenderRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatMessageMapper {

    /**
     * 새로운 채팅 메시지를 저장합니다.
     * @param chatMessage 저장할 메시지
     * @return 영향받은 행 수
     */
    int save(ChatMessage chatMessage);

    /**
     * 여러 메시지를 한 번에 저장합니다 (Batch Insert).
     * @param chatMessages 저장할 메시지 목록
     * @return 영향받은 행 수
     */
    int saveAll(List<ChatMessage> chatMessages);

    /**
     * ID로 메시지를 조회합니다.
     * @param id 메시지 ID
     * @return 채팅 메시지 (없으면 null)
     */
    Optional<ChatMessage> findById(Long id);

    /**
     * 세션의 모든 메시지를 생성시간 오름차순으로 조회합니다.
     * @param chatSessionId 세션 ID
     * @param limit 최대 조회 개수
     * @return 채팅 메시지 목록
     */
    List<ChatMessage> findAllByChatSessionIdOrderByCreatedAtAsc(
            @Param("chatSessionId") Long chatSessionId,
            @Param("limit") int limit
    );

    /**
     * 세션의 최근 메시지를 조회합니다 (최신순).
     * @param chatSessionId 세션 ID
     * @param limit 최대 조회 개수
     * @return 채팅 메시지 목록
     */
    List<ChatMessage> findRecentMessagesByChatSessionId(
            @Param("chatSessionId") Long chatSessionId,
            @Param("limit") int limit
    );

    /**
     * 세션의 메시지 개수를 조회합니다.
     * @param chatSessionId 세션 ID
     * @return 메시지 개수
     */
    int countByChatSessionId(Long chatSessionId);

    /**
     * 특정 역할의 메시지만 조회합니다.
     * @param chatSessionId 세션 ID
     * @param senderRole 발신자 역할
     * @return 채팅 메시지 목록
     */
    List<ChatMessage> findByChatSessionIdAndSenderRole(
            @Param("chatSessionId") Long chatSessionId,
            @Param("senderRole") SenderRole senderRole
    );

    /**
     * 세션의 모든 메시지를 삭제합니다.
     * @param chatSessionId 세션 ID
     * @return 영향받은 행 수
     */
    int deleteByChatSessionId(Long chatSessionId);

    /**
     * 특정 메시지를 삭제합니다.
     * @param id 메시지 ID
     * @return 영향받은 행 수
     */
    int deleteById(Long id);

    /**
     * 특정 시간 이전의 메시지를 삭제합니다 (정리용).
     * @param chatSessionId 세션 ID
     * @param before 기준 시간
     * @return 영향받은 행 수
     */
    int deleteOldMessages(
            @Param("chatSessionId") Long chatSessionId,
            @Param("before") LocalDateTime before
    );
}
