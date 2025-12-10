package com.yachaerang.backend.api.chat.repository;

import com.yachaerang.backend.api.chat.entity.ChatMessage;
import com.yachaerang.backend.api.common.SenderRole;
import com.yachaerang.backend.global.config.MyBatisConfig;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@MybatisTest
@ActiveProfiles("test")
@Sql("classpath:H2_schema.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(scripts = "/sql/cleanup.sql",  executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MyBatisConfig.class)
class ChatMessageMapperTest {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    private Long testChatSessionId;
    private Long testMemberId;


    /*
    테스트를 위한 목 데이터 메시지 저장
     */
    private void saveSampleMessages(Long chatSessionId, int count) {
        for (int i = 0; i < count; i++) {
            ChatMessage message = ChatMessage.ofUser(
                    chatSessionId,
                    testMemberId,
                    "테스트 메시지 " + (i + 1)
            );
            chatMessageMapper.save(message);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @BeforeEach
    void setUp() {
        testChatSessionId = 1L;
        testMemberId = 100L;
    }

    @Test
    @DisplayName("단일 메시지 저장 성공")
    void 단일_메시지_저장_성공() {
        // given
        ChatMessage message = ChatMessage.ofUser(testChatSessionId, testMemberId, "안녕하세요");

        // when
        int result = chatMessageMapper.save(message);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(message.getId()).isNotNull();
    }


    @Test
    @DisplayName("ID로 메시지 조회 성공")
    void ID로_메시지_조회_성공() {
        // given
        ChatMessage savedMessage = ChatMessage.ofUser(testChatSessionId, testMemberId, "테스트 메시지");
        chatMessageMapper.save(savedMessage);
        Long messageId = savedMessage.getId();

        // when
        Optional<ChatMessage> result = chatMessageMapper.findById(messageId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(messageId);
        assertThat(result.get().getContent()).isEqualTo("테스트 메시지");
        assertThat(result.get().getSenderRole()).isEqualTo(SenderRole.USER);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional")
    void 존재하지않는_ID로_조회시_empty() {
        // Given
        Long nonExistentId = 99999L;

        // When
        Optional<ChatMessage> result = chatMessageMapper.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("세션별 메시지 전체 조회 성공")
    void 세션별_메시지_전체_조회_성공() {
        // Given
        saveSampleMessages(testChatSessionId, 5);

        // When
        List<ChatMessage> result = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(testChatSessionId, null);

        // Then
        assertThat(result).hasSize(5);
        assertThat(result).extracting(ChatMessage::getChatSessionId)
                .containsOnly(testChatSessionId);
    }

    @Test
    @DisplayName("세션별 메시지 조회 LIMIT 성공")
    void 세션별_메시지_조회_LIMIT_성공() {
        // Given
        saveSampleMessages(testChatSessionId, 10);

        // When
        List<ChatMessage> result = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(testChatSessionId, 3);

        // Then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("세션별 메시지 오름차순 정렬 조회 성공")
    void 세션별_메시지_오름차순_정렬_조회_성공() {
        // Given
        saveSampleMessages(testChatSessionId, 5);

        // When
        List<ChatMessage> result = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(testChatSessionId, null);

        // Then
        assertThat(result).isSortedAccordingTo(
                (m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())
        );
    }

    @Test
    @DisplayName("세션별 최근 N개 메시지 조회 (최근 N개를 오래된 순으로 반환)")
    void 세션별_최근_N개_메시지_조회_성공() {
        // Given
        saveSampleMessages(testChatSessionId, 10);

        // When
        List<ChatMessage> result = chatMessageMapper
                .findRecentMessagesByChatSessionId(testChatSessionId, 3);

        // Then
        assertThat(result).hasSize(3);
        // Mapper 쿼리가 가장 최근 N개를 가져와서 created_at ASC(오래된 순)으로 정렬하므로 ASC 기준으로 검증
        assertThat(result).isSortedAccordingTo(
                (m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())
        );
    }

    @Test
    @DisplayName("세션별 메시지 개수 조회")
    void 세션별_메시지개수_조회() {
        // given
        saveSampleMessages(testChatSessionId, 7);

        // when
        int count = chatMessageMapper.countByChatSessionId(testChatSessionId);

        // then
        assertThat(count).isEqualTo(7);
    }

    @Test
    @DisplayName("여러 세션에 메시지가 있을 때 특정 세션만 조회")
    void 여러세션_메시지가_있을때_특정세션만_조회() {
        // given
        Long sessionId1 = 1L;
        Long sessionId2 = 2L;

        saveSampleMessages(sessionId1, 3);
        saveSampleMessages(sessionId2, 5);

        // when
        List<ChatMessage> session1Messages = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(sessionId1, null);
        List<ChatMessage> session2Messages = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(sessionId2, null);

        // then
        assertThat(session1Messages).hasSize(3);
        assertThat(session2Messages).hasSize(5);
        assertThat(session1Messages).extracting(ChatMessage::getChatSessionId)
                .containsOnly(sessionId1);
        assertThat(session2Messages).extracting(ChatMessage::getChatSessionId)
                .containsOnly(sessionId2);
    }

    @Test
    @DisplayName("사용자와 어시스턴트 메시지 구분 저장 및 조회")
    void 사용자와_어시스턴트_메시지_구분저장_및_조회() {
        // Given
        ChatMessage userMessage = ChatMessage.ofUser(testChatSessionId, testMemberId, "질문입니다");
        ChatMessage assistantMessage = ChatMessage.ofAssistant(testChatSessionId, "답변입니다");

        chatMessageMapper.save(userMessage);
        chatMessageMapper.save(assistantMessage);

        // When
        List<ChatMessage> messages = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(testChatSessionId, null);

        // Then
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).isUserMessage()).isTrue();
        assertThat(messages.get(0).getSenderId()).isEqualTo(testMemberId);
        assertThat(messages.get(1).isAssistantMessage()).isTrue();
        assertThat(messages.get(1).getSenderId()).isNull();
    }
}