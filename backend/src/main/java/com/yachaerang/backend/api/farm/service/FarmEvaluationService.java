package com.yachaerang.backend.api.farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
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
        너는 입력된 농장 정보를 평가해 등급(A/B/C/D)과 코멘트를 남기는 AI야.

        중요: 입력값이 부족해도 반드시 등급을 부여해야 한다.
        입력되지 않은 항목은 0점으로 계산하고, 입력된 항목만으로 총점을 산출한다.
        "평가할 수 없습니다", "정보가 부족합니다" 같은 표현은 절대 사용하지 마.

        
        [평가 기준 - 총 100점]
        1) 평지비율 점수 (40점)
        - 평지비율 = 평지면적 / 경작면적
        - ≥80%: 40점 | 60~80%: 30점 | 40~60%: 20점 | 20~40%: 10점 | <20%: 0점
                
        2) 인력밀도 점수 (30점)
        - 인력밀도 = 인력수 × 1000 / 경작면적 (10a당 인원)
        - ≥0.5: 30점 | 0.3~0.5: 25점 | 0.2~0.3: 15점 | 0.1~0.2: 5점 | <0.1: 0점
                        
        3) 위치 점수 (15점)
        - rural: 15점 | peri_urban: 10점 | urban: 5점
                        
        4) 주품목 점수 (15점)
        - 내부 리스크 테이블 참조
        - low: 15점 | mid: 10점 | high: 5점
                        
        [등급 산출]
        80점 이상: A | 60점 이상: B | 40점 이상: C | 40점 미만: D
        
        ```json
        {
          "grade": "A|B|C|D 중 하나",
          "comment": "등급에 대한 간결하고 친절한 말투의 한마디"
        }
        ```
        
        [예시 답변1]
        ```json
        {
          "grade": "A",
          "comment": "너무나도 좋은 농장이네요! 이대로만 관리해요!"
        }
        ```
        
        [예시 답변2]
        ```json
        {
          "grade": "D",
          "comment": "경작지에 대한 정보가 없어서 아쉬워요!"
        }
        ```
        
        내가 입력해주는 정보들을 기반으로 A, B, C, D 등급을 매기고, 등급에 대한 한마디를 comment 필드에 작성해.
        """);

        String userMessageContent = String.format("""
                인력: %s명, 위치: %s, 경작면적: %s㎡, 평지면적: %s㎡, 주품목: %s
                (값이 null이거나 0인 항목은 입력되지 않은 것으로 간주)
                """,
                requestDto.getManpower() != null ? requestDto.getManpower() : "없음",
                requestDto.getLocation() != null ? requestDto.getLocation() : "없음",
                requestDto.getCultivatedArea() != null ? requestDto.getCultivatedArea() : "없음",
                requestDto.getFlatArea() != null ? requestDto.getFlatArea() : "없음",
                requestDto.getMainCrop() != null ? requestDto.getMainCrop() : "없음");
        UserMessage userMessage = new UserMessage(userMessageContent);

        List<Message> messageList = List.of(systemMessage, userMessage);

        String response = googleGenAiChatModel.call(new Prompt(messageList))
                .getResult().getOutput().getText();

        try {

            // JSON 코드 블록 제거
            String cleanedResponse = response
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            FarmResponseDto.EvaluateDto responseDto = objectMapper.readValue(
                    cleanedResponse, FarmResponseDto.EvaluateDto.class);
            // 유효성 검증
            if (responseDto.getGrade() == null || !responseDto.getGrade().matches("[ABCD]")) {
                throw GeneralException.of(ErrorCode.FARM_GRADE_INVALID);
            }
            return CompletableFuture.completedFuture(responseDto);

        } catch (Exception e) {
            LogUtil.error("JSON 파싱 오류: {} JSON 원본 응답 : {}", e.getMessage(), response);
            return CompletableFuture.failedFuture(GeneralException.of(ErrorCode.FAILED_JSON_PARSING));
        }
    }
}
