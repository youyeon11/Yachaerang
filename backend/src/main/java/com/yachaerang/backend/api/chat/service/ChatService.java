package com.yachaerang.backend.api.chat.service;

import com.yachaerang.backend.api.chat.dto.request.ChatRequestDto;
import com.yachaerang.backend.api.chat.dto.response.ChatResponseDto;
import com.yachaerang.backend.api.chat.entity.ChatMessage;
import com.yachaerang.backend.api.chat.entity.ChatSession;
import com.yachaerang.backend.api.chat.repository.ChatMessageMapper;
import com.yachaerang.backend.api.chat.repository.ChatSessionMapper;
import com.yachaerang.backend.api.common.SessionStatus;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_CONTEXT_MESSAGES = 10;

    private final GoogleGenAiChatModel googleGenAiChatModel;

    private final AuthenticatedMemberProvider authenticatedMemberProvider;
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;


    /*
    새로운 채팅 세션 시작
     */
    @Transactional
    public Long startNewSession() {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        ChatSession chatSession = ChatSession.builder()
                .memberId(memberId)
                .build();

        int result = chatSessionMapper.save(chatSession);
        if (result != 1) {
            throw GeneralException.of(ErrorCode.SESSION_FAILED);
        }

        Long sessionId = chatSession.getChatSessionId();
        LogUtil.info("새로운 채팅 세션 시작: sessionId={}, memberId={}", sessionId, memberId);

        return sessionId;
    }

    /**
     * GoogleGenAI에게 메시지를 전달
     * @param requestDto : 사용자 메시지
     * @param chatSessionId
     * @return : 모델 응답
     */
    @Transactional
    public ChatResponseDto.MessageDto getChatResponse(
            ChatRequestDto.MessageDto requestDto, Long chatSessionId) {

        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        ChatSession session = getValidatedSession(chatSessionId, memberId);

        String userMessage = requestDto.getMessage();
        validateMessage(userMessage);

        // 이전 대화 기록과 함께 프롬프트 구성 및 전송
        List<Message> promptMessages = buildPromptMessages(chatSessionId, userMessage);
        String responseContent = call(promptMessages);

        saveMessages(chatSessionId, memberId, userMessage, responseContent);

        LogUtil.debug("채팅 응답 생성 완료: sessionId={}", chatSessionId);

        return ChatResponseDto.MessageDto.builder()
                .chatSessionId(chatSessionId)
                .response(responseContent)
                .build();
    }

    /**
     * 채팅 세션을 종료
     * @param chatSessionId
     */
    @Transactional
    public void endSession(Long chatSessionId) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        ChatSession session = getValidatedSession(chatSessionId, memberId);

        session.end();
        chatSessionMapper.updateStatus(session);

        LogUtil.info("채팅 세션 종료: sessionId={}", chatSessionId);
    }

    /**
     * 특정 세션의 대화 기록을 조회
     * @param chatSessionId 세션 ID
     * @return 대화 기록 목록
     */
    @Transactional(readOnly = true)
    public List<ChatResponseDto.HistoryMessageDto> getChatHistory(Long chatSessionId) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();
        getValidatedSession(chatSessionId, memberId);
        List<ChatMessage> chatMassageList = chatMessageMapper.findAllByChatSessionIdOrderByCreatedAtAsc(chatSessionId, Integer.MAX_VALUE);
        return chatMassageList.stream()
                .map(chatMessage -> ChatResponseDto.HistoryMessageDto.builder()
                        .id(chatSessionId)
                        .role(chatMessage.getSenderRole().name())
                        .content(chatMessage.getContent())
                        .sentAt(chatMessage.getSentAt())
                        .build()
                ).toList();
    }


    /**
     * Session에 대한 유효성 확인
     * @param chatSessionId
     * @param memberId
     * @return
     */
    private ChatSession getValidatedSession(Long chatSessionId, Long memberId) {
        ChatSession session = chatSessionMapper.findById(chatSessionId).orElse(null);

        if (session == null) {
            throw GeneralException.of(ErrorCode.SESSION_NOT_FOUND);
        }

        if (!session.isOwnedBy(memberId)) {
            throw GeneralException.of(ErrorCode.SESSION_ACCESS_DENIED);
        }

        if (session.getSessionStatus() == SessionStatus.ENDED) {
            throw GeneralException.of(ErrorCode.SESSION_ALREADY_ENDED);
        }

        return session;
    }

    private void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw GeneralException.of(ErrorCode.INVALID_MESSAGE);
        }
    }

    /**
     * 이전 대화기록을 조회하여 프롬프트 생성
     * @param chatSessionId
     * @param currentUserMessage
     * @return
     */
    private List<Message> buildPromptMessages(Long chatSessionId, String currentUserMessage) {
        List<ChatMessage> history = chatMessageMapper
                .findAllByChatSessionIdOrderByCreatedAtAsc(chatSessionId, MAX_CONTEXT_MESSAGES);

        List<Message> promptMessages = history.stream()
                .map(this::toPromptMessage)
                .collect(Collectors.toCollection(ArrayList::new));

        promptMessages.add(new UserMessage(currentUserMessage));

        return promptMessages;
    }

    /*
    등록한 모델 프롬프트 전송
     */
    private String call(List<Message> promptMessages) {
        try {
            return googleGenAiChatModel.call(new Prompt(promptMessages))
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (Exception e) {
            LogUtil.error("모델 호출 실패", e);
            throw GeneralException.of(ErrorCode.AI_MODEL_ERROR);
        }
    }

    /*
    Message 내용을 저장
     */
    private void saveMessages(Long chatSessionId, Long memberId, String userMessage, String assistantResponse) {
        ChatMessage userChatMessage = ChatMessage.ofUser(chatSessionId, memberId, userMessage);
        ChatMessage assistantChatMessage = ChatMessage.ofAssistant(chatSessionId, assistantResponse);

        chatMessageMapper.save(userChatMessage);
        chatMessageMapper.save(assistantChatMessage);
    }

    /*
    기존의 String을 프롬프트 Message로 변환
     */
    private Message toPromptMessage(ChatMessage chatMessage) {
        return chatMessage.isUserMessage()
                ? new UserMessage(chatMessage.getContent())
                : new AssistantMessage(chatMessage.getContent());
    }
}
