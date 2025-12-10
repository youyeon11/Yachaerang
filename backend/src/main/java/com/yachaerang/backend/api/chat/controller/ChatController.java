package com.yachaerang.backend.api.chat.controller;

import com.yachaerang.backend.api.chat.dto.request.ChatRequestDto;
import com.yachaerang.backend.api.chat.dto.response.ChatResponseDto;
import com.yachaerang.backend.api.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 새로운 채팅 세션 시작
     *
     * @return 생성된 세션 ID
     */
    @PostMapping("")
    public Long startNewSession() {
        return chatService.startNewSession();
    }

    /**
     * 특정 세션에 대해 메시지를 보내고, AI 응답을 받는 엔드포인트
     *
     * @param chatSessionId 세션 ID (path)
     * @param requestDto    사용자 메시지 DTO (body)
     * @return AI 응답 DTO
     */
    @PostMapping("/{chatSessionId}/messages")
    public ChatResponseDto.MessageDto chat(
            @PathVariable("chatSessionId") Long chatSessionId,
            @Valid @RequestBody ChatRequestDto.MessageDto requestDto
    ) {
        return chatService.getChatResponse(requestDto, chatSessionId);
    }

    /**
     * 채팅 세션 종료
     */
    @PostMapping("/{chatSessionId}/end")
    public void endSession(
            @PathVariable("chatSessionId") Long chatSessionId
    ) {
        chatService.endSession(chatSessionId);
        return;
    }

    /**
     * 특정 세션의 대화 히스토리 조회
     *
     * @param chatSessionId 세션 ID
     * @return 해당 세션의 메시지 목록
     */
    @GetMapping("/{chatSessionId}/messages")
    public List<ChatResponseDto.HistoryMessageDto> getChatHistory(
            @PathVariable("chatSessionId") Long chatSessionId
    ) {
        return chatService.getChatHistory(chatSessionId);
    }
}
