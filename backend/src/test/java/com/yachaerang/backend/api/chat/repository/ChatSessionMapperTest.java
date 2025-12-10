package com.yachaerang.backend.api.chat.repository;

import com.yachaerang.backend.api.chat.entity.ChatSession;
import com.yachaerang.backend.api.common.SessionStatus;
import com.yachaerang.backend.global.config.MyBatisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(scripts = {"/H2_schema.sql", "/sql/chat-session-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MyBatisConfig.class)
class ChatSessionMapperTest {

    @Autowired private ChatSessionMapper chatSessionMapper;

    private Long testSenderId;

    @BeforeEach
    void setUp() {
        testSenderId = 1L;
    }

    private ChatSession createActiveSession(Long senderId, String statusName) {
        ChatSession session = new ChatSession();
        session.setSenderId(senderId);
        session.setStartedAt(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.valueOf(statusName));
        return session;
    }

    private ChatSession createActiveSession(Long senderId) {
        return createActiveSession(senderId, "ACTIVE");
    }

    @Test
    @DisplayName("단일 세션 저장 성공")
    void 단일_세션_저장_성공() {
        // given
        ChatSession session = createActiveSession(testSenderId);

        // when
        int result = chatSessionMapper.save(session);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(session.getChatSessionId()).isNotNull();
        assertThat(session.getSenderId()).isEqualTo(testSenderId);
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.ACTIVE);
    }

    @Test
    @DisplayName("ID로 세션 조회 성공")
    void ID로_세션_조회_성공() {
        // given
        ChatSession session = createActiveSession(testSenderId);
        chatSessionMapper.save(session);
        Long sessionId = session.getChatSessionId();

        // when
        Optional<ChatSession> found = chatSessionMapper.findById(sessionId);

        // then
        assertThat(found).isPresent();
        ChatSession result = found.get();
        assertThat(result.getChatSessionId()).isEqualTo(sessionId);
        assertThat(result.getSenderId()).isEqualTo(testSenderId);
        assertThat(result.getSessionStatus()).isEqualTo(SessionStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 empty")
    void 존재하지_않는_ID로_조회시_empty() {
        // given
        Long nonExistentId = 99999L;

        // when
        Optional<ChatSession> found = chatSessionMapper.findById(nonExistentId);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("회원별 전체 세션 조회 성공")
    void 회원별_전체_세션_조회_성공() {
        // given
        Long otherSenderId = 2L;

        ChatSession s1 = createActiveSession(testSenderId);
        ChatSession s2 = createActiveSession(testSenderId);
        ChatSession s3 = createActiveSession(otherSenderId);

        chatSessionMapper.save(s1);
        chatSessionMapper.save(s2);
        chatSessionMapper.save(s3);

        // when
        List<ChatSession> result = chatSessionMapper.findAllByMemberId(testSenderId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ChatSession::getSenderId)
                .containsOnly(testSenderId);
    }

    @Test
    @DisplayName("회원별 ACTIVE 세션 조회 성공")
    void 회원별_ACTIVE_세션_조회_성공() {
        // given
        ChatSession active1 = createActiveSession(testSenderId, "ACTIVE");
        ChatSession active2 = createActiveSession(testSenderId, "ACTIVE");
        ChatSession closed = createActiveSession(testSenderId, "ENDED");

        chatSessionMapper.save(active1);
        chatSessionMapper.save(active2);
        chatSessionMapper.save(closed);

        // when
        List<ChatSession> activeSessions =
                chatSessionMapper.findActiveSessionsByMemberId(testSenderId);

        // then
        assertThat(activeSessions).isNotEmpty();
        assertThat(activeSessions)
                .extracting(ChatSession::getSessionStatus)
                .containsOnly(SessionStatus.ACTIVE);
        assertThat(activeSessions)
                .extracting(ChatSession::getSenderId)
                .containsOnly(testSenderId);
    }

    @Test
    @DisplayName("세션 상태 업데이트 성공")
    void 세션_상태_업데이트_성공() {
        // given
        ChatSession session = createActiveSession(testSenderId);
        chatSessionMapper.save(session);

        Long sessionId = session.getChatSessionId();

        // when: 상태를 CLOSED 로 변경
        session.setSessionStatus(SessionStatus.ENDED);
        int updated = chatSessionMapper.updateStatus(session);

        // then
        assertThat(updated).isEqualTo(1);

        Optional<ChatSession> found = chatSessionMapper.findById(sessionId);
        assertThat(found).isPresent();
        assertThat(found.get().getSessionStatus()).isEqualTo(SessionStatus.ENDED);
    }

}