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
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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

    private void mockAiResponse(String response) {
        when(googleGenAiChatModel.call(any(SystemMessage.class), any(UserMessage.class)))
                .thenReturn(response);
    }

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
                  "farmType": "집중형 농장",
                  "comment": "좋은 농장이에요!"
                }
                ```
                """;
        mockAiResponse(response);

        // when
        FarmResponseDto.EvaluateDto resultDto =
                farmEvaluationService.generateGradeAndComment(requestDto).join();

        // then
        assertThat(resultDto.getGrade()).isEqualTo("B");
        assertThat(resultDto.getFarmType()).isEqualTo("집중형 농장");
        assertThat(resultDto.getComment()).isEqualTo("좋은 농장이에요!");
        verify(googleGenAiChatModel, times(1))
                .call(any(SystemMessage.class), any(UserMessage.class));
    }

    @Test
    @DisplayName("AI 평가 실패 - JSON 응답 비정상")
    void AI평가_실패_JSON_응답_비정상() {
        // given
        FarmRequestDto.InfoDto requestDto = createRequestDto();
        String invalidJson = "이것은 JSON이 아닙니다";
        mockAiResponse(invalidJson);

        // when
        CompletableFuture<FarmResponseDto.EvaluateDto> future =
                farmEvaluationService.generateGradeAndComment(requestDto);

        // then
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .cause()
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ge = (GeneralException) e;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAILED_JSON_PARSING);
                });
    }

    @Test
    @DisplayName("AI 평가 실패 - grade 값이 A/B/C/D가 아니면 FARM_GRADE_INVALID -> 최종 FAILED_JSON_PARSING")
    void AI평가_실패_등급_유효성_실패() {
        // given: JSON은 파싱되지만 grade가 잘못됨
        FarmRequestDto.InfoDto requestDto = createRequestDto();
        String response = """
                {"grade":"E","farmType":"전통형 농장","comment":"테스트"}
                """;
        mockAiResponse(response);

        // when
        CompletableFuture<FarmResponseDto.EvaluateDto> future =
                farmEvaluationService.generateGradeAndComment(requestDto);

        // then
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .cause()
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ge = (GeneralException) e;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.FAILED_JSON_PARSING);
                });
    }

    @Test
    @DisplayName("AI 호출 예외 발생 시 RuntimeException이 그대로 전파(Async 프록시 없음)")
    void AI호출_예외_전파() {
        FarmRequestDto.InfoDto requestDto = createRequestDto();

        when(googleGenAiChatModel.call(any(SystemMessage.class), any(UserMessage.class)))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> farmEvaluationService.generateGradeAndComment(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");
    }


    @Test
    @DisplayName("null 입력값 처리 시 '없음'으로 변환되어 UserMessage에 포함")
    void null_입력값_처리시_없음으로_변환() {
        // given
        FarmRequestDto.InfoDto requestDto = FarmRequestDto.InfoDto.builder()
                .manpower(null)
                .location(null)
                .cultivatedArea(null)
                .flatArea(null)
                .mainCrop(null)
                .build();

        String response = """
                {"grade":"D","farmType":"개선 필요형","comment":"정보가 부족해요"}
                """;
        mockAiResponse(response);

        // when
        FarmResponseDto.EvaluateDto result =
                farmEvaluationService.generateGradeAndComment(requestDto).join();

        // then
        assertThat(result.getGrade()).isEqualTo("D");
        assertThat(result.getFarmType()).isEqualTo("개선 필요형");

        ArgumentCaptor<SystemMessage> systemCaptor = ArgumentCaptor.forClass(SystemMessage.class);
        ArgumentCaptor<UserMessage> userCaptor = ArgumentCaptor.forClass(UserMessage.class);

        verify(googleGenAiChatModel).call(systemCaptor.capture(), userCaptor.capture());

        String userPrompt = userCaptor.getValue().toString();
        assertThat(userPrompt).contains("없음");
    }
}