package com.yachaerang.backend.api.chat.repository;

import com.yachaerang.backend.api.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatMessageMapper {

    /**
     * 단일 메시지 저장
     */
    int save(ChatMessage chatMessage);

    /**
     * 여러 메시지 배치 저장
     */
    int saveAll(@Param("list") List<ChatMessage> messages);

    /**
     * ID로 메시지 조회
     */
    Optional<ChatMessage> findById(@Param("id") Long id);

    /**
     * 세션별 메시지 전체 조회
     * - limit 가 null 이면 전체 조회
     */
    List<ChatMessage> findAllByChatSessionIdOrderByCreatedAtAsc(
            @Param("chatSessionId") Long chatSessionId,
            @Param("limit") Integer limit
    );

    /**
     * 세션별 최근 N개 메시지 조회
     */
    List<ChatMessage> findRecentMessagesByChatSessionId(
            @Param("chatSessionId") Long chatSessionId,
            @Param("limit") Integer limit
    );

    /**
     * 세션별 메시지 개수 조회
     */
    int countByChatSessionId(@Param("chatSessionId") Long chatSessionId);

    /**
     * 세션별 메시지 전체 삭제
     */
    int deleteByChatSessionId(@Param("chatSessionId") Long chatSessionId);
}
