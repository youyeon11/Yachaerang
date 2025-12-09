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
     * ID로 채팅 세션을 조회합니다.
     * @param id 세션 ID
     * @return 채팅 세션 (없으면 null)
     */
    Optional<ChatSession> findById(Long id);

    /**
     * 회원의 모든 채팅 세션을 조회합니다.
     * @param memberId 회원 ID
     * @return 채팅 세션 목록
     */
    List<ChatSession> findAllByMemberId(Long memberId);

    /**
     * 회원의 활성 채팅 세션을 조회합니다.
     * @param memberId 회원 ID
     * @return 활성 채팅 세션 목록
     */
    List<ChatSession> findActiveSessionsByMemberId(Long memberId);

    /**
     * 채팅 세션 상태를 업데이트합니다.
     * @param chatSession 업데이트할 세션
     * @return 영향받은 행 수
     */
    int updateStatus(ChatSession chatSession);

    /**
     * 채팅 세션을 삭제합니다 (Hard Delete).
     * @param id 세션 ID
     * @return 영향받은 행 수
     */
    int deleteById(Long id);

    /**
     * 회원의 모든 채팅 세션을 삭제합니다.
     * @param memberId 회원 ID
     * @return 영향받은 행 수
     */
    int deleteAllByMemberId(Long memberId);

    /**
     * 세션과 메시지를 함께 조회합니다 (with messages).
     * @param id 세션 ID
     * @return 메시지가 포함된 채팅 세션
     */
    ChatSession findByIdWithMessages(Long id);
}
