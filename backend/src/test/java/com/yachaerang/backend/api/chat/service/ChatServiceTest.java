package com.yachaerang.backend.api.chat.service;

import com.yachaerang.backend.api.chat.dto.request.ChatRequestDto;
import com.yachaerang.backend.api.chat.dto.response.ChatResponseDto;
import com.yachaerang.backend.api.chat.entity.ChatMessage;
import com.yachaerang.backend.api.chat.entity.ChatSession;
import com.yachaerang.backend.api.chat.repository.ChatMessageMapper;
import com.yachaerang.backend.api.chat.repository.ChatSessionMapper;
import com.yachaerang.backend.api.common.SenderRole;
import com.yachaerang.backend.api.common.SessionStatus;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private GoogleGenAiChatModel googleGenAiChatModel;

    @Mock private AuthenticatedMemberProvider authenticatedMemberProvider;

    @Mock private ChatSessionMapper chatSessionMapper;

    @Mock private ChatMessageMapper chatMessageMapper;

    @InjectMocks private ChatService chatService;

    private Long memberId;
    private Long sessionId;
    private ChatSession chatSession;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        sessionId = 100L;
        chatSession = ChatSession.builder()
                .chatSessionId(sessionId)
                .senderId(memberId)
                .sessionStatus(SessionStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("새로운 채팅 세션 성공")
    void 새로운_채팅_세션_성공() {
        // given
        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.save(any(ChatSession.class))).thenAnswer(invocationOnMock -> {
            ChatSession session = invocationOnMock.getArgument(0);
            session.setChatSessionId(sessionId);
            return 1;
        });

        // when
        Long result = chatService.startNewSession();

        // then
        assertThat(result).isEqualTo(sessionId);
        verify(authenticatedMemberProvider).getCurrentMemberId();
        verify(chatSessionMapper).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("세션 저장 실패 시 예외")
    void 세션_저장_실패시_예외() {
        // given
        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.save(any(ChatSession.class))).thenReturn(0);

        // when & then
        assertThatThrownBy(() -> chatService.startNewSession())
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("채팅 응답 받기 성공")
    void 채팅_응답받기_성공() {
        // given
        String userMessage = "안녕하세요";
        String response = "안녕하세요! 무엇을 도와드릴까요?";
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message(userMessage)
                .build();

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageMapper.findAllByChatSessionIdOrderByCreatedAtAsc(eq(sessionId), anyInt()))
                .thenReturn(new ArrayList<>()); // 여태까지의 기록

        // mock Response from AI
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(response);

        when(googleGenAiChatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);

        // when
        ChatResponseDto.MessageDto responseDto = chatService.getChatResponse(requestDto, sessionId);

        // then
        assertThat(responseDto.getChatSessionId()).isEqualTo(sessionId);
        assertThat(responseDto.getResponse()).isEqualTo(response);
        verify(chatMessageMapper, times(2)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("세션을 찾을 수 없으면 예외")
    void 세션을_찾을수없으면_예외() {
        // given
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message("테스트 메시지")
                .build();

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatService.getChatResponse(requestDto, sessionId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("다른 사람의 세션에 접근 시 예외")
    void 다른사람의_세션_접근시_예외() {
        // given
        Long otherMemberId = 999L;
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message("테스트 메시지")
                .build();

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(otherMemberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));

        // when & then
        assertThatThrownBy(() -> chatService.getChatResponse(requestDto, sessionId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("이미 종료된 세션이면 예외")
    void 이미_종료된_세션이면_예외(){
        // given
        chatSession.end(); // 종료 처리
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message("테스트 메시지")
                .build();

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));

        // when & then
        assertThatThrownBy(() -> chatService.getChatResponse(requestDto, sessionId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("모델 호출 실패 시 예외")
    void 모델호출_실패시_예외() {
        // given
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message("테스트 메시지")
                .build();

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageMapper.findAllByChatSessionIdOrderByCreatedAtAsc(eq(sessionId), anyInt()))
                .thenReturn(new ArrayList<>());
        when(googleGenAiChatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("AI 오류"));

        // when & then
        assertThatThrownBy(() -> chatService.getChatResponse(requestDto, sessionId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("이전 대화기록과 함게 채팅 응답 받기 성공")
    void 이전_대화기록과_함께_채팅_응답받기_성공() {
        // given
        String userMessage = "이전 대화 기록을 참고해서 대답해줘."; // 새로운 메시지
        String response = "네, 이전 대화 기록을 참고해서 답변 드리겠습니다.";
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message(userMessage)
                .build();

        List<ChatMessage> history = List.of(
                ChatMessage.ofUser(sessionId, memberId, "안녕하세요"),
                ChatMessage.ofAssistant(sessionId, "안녕하세요!")
        );

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageMapper.findAllByChatSessionIdOrderByCreatedAtAsc(sessionId, 10))
                .thenReturn(history);

        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(response);

        when(googleGenAiChatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);

        // when
        ChatResponseDto.MessageDto responseDto = chatService.getChatResponse(requestDto, sessionId);

        // then
        assertThat(responseDto.getResponse()).isEqualTo(response);
        verify(googleGenAiChatModel).call(argThat((Prompt prompt) ->
                prompt.getInstructions().size() == 3 // history 2개 + 현재 새로 보내는 Message
        ));
    }

    @Test
    @DisplayName("채팅 세션 종료 성공")
    void 채팅_세션_종료_성공() {
        // given
        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));

        // when
        chatService.endSession(sessionId);

        // then
        verify(chatSessionMapper).updateStatus(any(ChatSession.class));
    }

    @Test
    @DisplayName("존재하지 않는 세션 종료 시 예외")
    void 존재하지_않는_세션_종료시_예외() {
        // given
        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatService.endSession(sessionId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("채팅 기록 조회 성공")
    void 채팅_기록_조회_성공() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<ChatMessage> messages = List.of(
                ChatMessage.builder()
                        .id(1L)
                        .chatSessionId(sessionId)
                        .senderId(memberId)
                        .senderRole(SenderRole.USER)
                        .content("안녕하세요")
                        .sentAt(now)
                        .build(),
                ChatMessage.builder()
                        .id(2L)
                        .chatSessionId(sessionId)
                        .senderId(memberId)
                        .senderRole(SenderRole.ASSISTANT)
                        .content("안녕하세요!")
                        .sentAt(now.plusSeconds(5))
                        .build()
        );

        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageMapper.findAllByChatSessionIdOrderByCreatedAtAsc(sessionId, Integer.MAX_VALUE))
                .thenReturn(messages);

        // when
        List<ChatResponseDto.HistoryMessageDto> history = chatService.getChatHistory(sessionId);

        // then
        assertThat(history).hasSize(2);
        assertThat(history.get(0).getRole()).isEqualTo("USER");
        assertThat(history.get(0).getContent()).isEqualTo("안녕하세요");
        assertThat(history.get(1).getRole()).isEqualTo("ASSISTANT");
        assertThat(history.get(1).getContent()).isEqualTo("안녕하세요!");
    }

    @Test
    @DisplayName("빈 채팅 조회")
    void 빈채팅_조회() {
        // given
        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(memberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageMapper.findAllByChatSessionIdOrderByCreatedAtAsc(sessionId, Integer.MAX_VALUE))
                .thenReturn(new ArrayList<>());

        // when
        List<ChatResponseDto.HistoryMessageDto> history = chatService.getChatHistory(sessionId);

        // then
        assertThat(history).isEmpty();
    }

    @Test
    @DisplayName("다른 사용자의 채팅 기록을 조회 시 예외")
    void 다른사용자의_채팅기록을_조회시_예외() {
        // given
        Long otherMemberId = 999L;
        when(authenticatedMemberProvider.getCurrentMemberId()).thenReturn(otherMemberId);
        when(chatSessionMapper.findById(sessionId)).thenReturn(Optional.of(chatSession));

        // when & then
        assertThatThrownBy(() -> chatService.getChatHistory(sessionId))
                .isInstanceOf(GeneralException.class);
    }
}