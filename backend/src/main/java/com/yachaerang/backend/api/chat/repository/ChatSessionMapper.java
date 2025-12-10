package com.yachaerang.backend.api.chat.repository;

import com.yachaerang.backend.api.chat.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatSessionMapper {

    /**
     * 새로운 채팅 세션을 저장합니다.
     * @param chatSession 저장할 세션
     * @return 영향받은 행 수
     */
    int save(ChatSession chatSession);

    /**
     * ID로 채팅 세션을 조회
     * @param chatSessionId
     * @return
     */
    Optional<ChatSession> findById(@Param("id") Long chatSessionId);

    /**
     * 회원별 전체 세션 조회
     */
    List<ChatSession> findAllByMemberId(@Param("senderId") Long memberId);

    /**
     * 회원별 ACTIVE 세션 조회
     */
    List<ChatSession> findActiveSessionsByMemberId(@Param("senderId") Long memberId);

    /**
     * 세션 업데이트
     */
    int updateStatus(ChatSession chatSession);

    /**
     * ID로 세션 삭제
     */
    int deleteById(@Param("id") Long chatSessionId);

    /**
     * 회원 ID 기준으로 세션 전체 삭제
     */
    int deleteAllByMemberId(@Param("senderId") Long memberId);
}
