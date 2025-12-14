package com.yachaerang.backend.api.farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmEvaluationServiceTest {

    @Mock private GoogleGenAiChatModel googleGenAiChatModel;

    private ObjectMapper objectMapper;
    private FarmEvaluationService farmEvaluationService;

    /*
    외부 AI 응답 Mock 객체로 생성
     */
    private void mockAiResponse(String response) {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = mock(AssistantMessage.class);

        when(googleGenAiChatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn(response);
    }

    /*
    DTO 생성 메서드
     */
    private FarmRequestDto.InfoDto createRequestDto() {
        return FarmRequestDto.InfoDto.builder()
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .build();
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        farmEvaluationService = new FarmEvaluationService(googleGenAiChatModel, objectMapper);
    }

    @Test
    @DisplayName("AI 평가 성공 - JSON 응답 정상")
    void AI평가_성공_JSON_응답_정상() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();
        String response = """
                ```json
                                {
                                    "grade": "B",
                                    "comment": "좋은 농장이에요!"
                                }
                ```
                """;
        mockAiResponse(response);

        // when
        FarmResponseDto.EvaluateDto resultDto = farmEvaluationService.generateGradeAndComment(requestDto).join();

        // then
        assertThat(resultDto.getGrade()).isEqualTo("B");
        assertThat(resultDto.getComment()).isEqualTo("좋은 농장이에요!");
    }

    @Test
    @DisplayName("AI 평가 실패 - JSON 응답 비정상")
    void AI평가_실패_JSON_응답_비정상() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();
        String invalidJson = "이것은 JSON이 아닙니다";
        mockAiResponse(invalidJson);

        // when
        CompletableFuture<FarmResponseDto.EvaluateDto> result =
                farmEvaluationService.generateGradeAndComment(requestDto);

        // then
        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .cause()
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ge = (GeneralException) e;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAILED_JSON_PARSING);
                });
    }

    @Test
    @DisplayName("AI 서비스 예외 발생")
    void AI서비스_예외_발생() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();
        when(googleGenAiChatModel.call(any(Prompt.class))).thenThrow(GeneralException.of(ErrorCode.AI_MODEL_ERROR));

        // when & then
        assertThatThrownBy(() -> farmEvaluationService.generateGradeAndComment(requestDto))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ge = (GeneralException) e;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.AI_MODEL_ERROR);
                });
    }

    @Test
    @DisplayName("null 입력값 처리 시 '없음'으로 변환")
    void null_입력값_처리시_없음으로_변환() {
        // given
        FarmRequestDto.InfoDto requestDto = FarmRequestDto.InfoDto.builder()
                .manpower(null)
                .location(null)
                .cultivatedArea(null)
                .flatArea(null)
                .mainCrop(null)
                .build();

        String aiResponse = """
                {"grade": "D", "comment": "정보가 부족해요"}
                """;
        mockAiResponse(aiResponse);

        // when
        FarmResponseDto.EvaluateDto result =
                farmEvaluationService.generateGradeAndComment(requestDto).join();

        // then
        assertThat(result.getGrade()).isEqualTo("D");
        // captor로 확인
        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(googleGenAiChatModel).call(promptCaptor.capture());

        String content = promptCaptor.getValue().getContents().toString();
        assertThat(content).contains("없음");
    }
}