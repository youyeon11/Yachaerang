package com.yachaerang.backend.api.farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.repository.FarmMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class FarmEvaluationService {

    private final GoogleGenAiChatModel googleGenAiChatModel;
    private final ObjectMapper objectMapper;

    public FarmEvaluationService(GoogleGenAiChatModel googleGenAiChatModel, ObjectMapper objectMapper) {
        this.googleGenAiChatModel = googleGenAiChatModel;
        this.objectMapper = objectMapper;
    }

    /**
     * 입력을 기반으로 Farm에 대한 grade와 comment 작성하기
     */
    @Async("asyncExecutor")
    public CompletableFuture<FarmResponseDto.EvaluateDto> generateGradeAndComment(FarmRequestDto.InfoDto requestDto) {
        SystemMessage systemMessage = new SystemMessage("""
        너는 입력된 정보를 기반으로 농촌에 대한 기준을 작성해주는 도우미봇이야.
        **응답은 반드시 아래 JSON 스키마를 따라야 하며, 다른 부가적인 설명은 포함하지 않아야 해.**
        
        ```json
        {
          "grade": "A|B|C|D 중 하나",
          "comment": "등급에 대한 간결한 한마디"
        }
        ```
        
        내가 입력해주는 정보들을 기반으로 A, B, C, D 등급을 매기고, 등급에 대한 한마디를 comment 필드에 작성해.
        입력된 것이 없다면 해당 부분은 넘어가.
        """);

        String userMessageContent = String.format("""
            다음은 나의 정보들이야.
            내 농장의 인력 수: %s,
            내 농장의 위치: %s,
            내 농장의 경작면적: %s,
            내 농장의 평지면적: %s,
            내 농장의 주품종: %s
            """,
                requestDto.getManpower(),
                requestDto.getLocation(),
                requestDto.getCultivatedArea(),
                requestDto.getFlatArea(),
                requestDto.getMainCrop());
        UserMessage userMessage = new UserMessage(userMessageContent);

        List<Message> messageList = List.of(systemMessage, userMessage);

        String response = googleGenAiChatModel.call(new Prompt(messageList))
                .getResult().getOutput().getText();

        try {
            FarmResponseDto.EvaluateDto responseDto = objectMapper.readValue(
                        response, FarmResponseDto.EvaluateDto.class);
            return CompletableFuture.completedFuture(responseDto);

        } catch (Exception e) {
            LogUtil.error("JSON 파싱 오류: {} JSON 원본 응답 : {}", e.getMessage(), response);
            return CompletableFuture.failedFuture(GeneralException.of(ErrorCode.FAILED_JSON_PARSING));
        }
    }
}
