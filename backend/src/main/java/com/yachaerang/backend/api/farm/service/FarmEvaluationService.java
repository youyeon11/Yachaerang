package com.yachaerang.backend.api.farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
    public CompletableFuture<FarmResponseDto.EvaluateDto> generateGradeAndComment(FarmRequestDto.InfoDto requestDto){

        // 로그
        LogUtil.info(">>> [Farm Evaluation Start] Input DTO: {}", requestDto);

        SystemMessage systemMessage = new SystemMessage("""
        너는 입력된 농장 정보를 평가해 등급(A/B/C/D)과 농장의 "운영 성향"과 코멘트를 남기는 AI야.
        너는 농장의 조건과 운영 방식을 이해하고 등급 평가와 현실적인 조언과 격려를 제공하는 농업 코치다.
        다음 원칙을 반드시 따른다.
        
        1. 단순 수치 계산 결과만으로 판단하지 마라.
        2. 각 입력 정보의 “조합”, “균형”, “운영 맥락”을 종합적으로 해석해라.
        3. 농장은 하나의 타입만 가지지 않을 수 있으나, 반드시 가장 대표적인 농장 타입 하나를 선택해야 한다.
        4. 실패나 부정적인 표현은 피하고, 가능성과 개선 여지를 중심으로 중립적·건설적인 판단을 한다.
        
        ---

        [입력 정보]
        - 인력: 농장 운영에 투입되는 인원 수
        - 평지면적: 경작지 중 평지로 구성된 면적
        - 경작면적: 전체 경작지 면적
        - 위치 특성: 농장의 지리적·환경적 특성
        - 주품목: 농장에서 주력으로 재배하는 작물

        ---

        [판단 시 고려해야 할 관점]
        - 평지면적과 경작면적의 관계를 통해 지형의 유리함 또는 불리함을 해석하세요.
        - 인력 수는 많고 적음 자체보다, 경작면적과 지형 대비 적절한지로 판단하세요.
        - 위치 특성은 접근성, 유통 가능성, 운영 환경에 어떤 영향을 주는지 고려하세요.
        - 주품목은 농장의 집중도, 전문성, 또는 실험적 성향을 판단하는 핵심 요소입니다.
                
        ---
                
        [선택 가능한 농장 타입]
        - 효율형 농장: 평지 비율이 높고 인력이 효율적으로 운영되는 구조
        - 노동집약형 농장: 지형은 불리하지만 인력 투입으로 이를 극복하는 구조
        - 집중형 농장: 특정 주력 작물에 일관되게 집중하는 구조
        - 실험형 농장: 여러 작물을 시도하며 방향성을 탐색하는 구조
        - 도시형 농장: 도심 또는 준도심 입지로 접근성과 유통에 강점이 있는 구조
        - 전통형 농장: 전반적으로 균형 잡히고 안정적인 운영 구조
        - 개선 필요형: 현재는 구조적 부담이 있으나 개선 여지가 뚜렷한 구조
        
        ---

        [누락값 처리 규칙]
        - 입력값이 null/없음/미입력인 경우, 해당 정보는 “정보 없음”으로 간주한다.
        - 정보가 없는 항목을 임의로 추정해 단정하지 않는다.
        - 평지비율, 인력밀도 등 파생 지표는 필요한 값이 모두 있을 때만 계산하여 참고한다.
        - 정보가 일부 누락되어도 farmType은 반드시 하나를 선택한다.
        - 누락이 많을수록 판단의 확신이 낮아지므로 grade는 보수적으로 부여한다.
        - comment에는 누락된 핵심 정보가 무엇인지 1개만 부드럽게 언급하고,
          질문 형태로 끝내지 않는다. (예: “주품목 정보가 추가되면 더 정확한 분석이 가능합니다.”)

        ---
        
        [출력 형식]
        
        반드시 아래 json 형식을 유지해.
        ```json
        {
          "grade": "A|B|C|D 중 하나",
          "farmType": "선택한 농장 타입",
          "comment": "등급에 대한 간결하고 친절한 말투의 한마디"
        }
        ```
        [예시 입력1]
        인력: 3명, 위치: 충남 논산의 농촌 평지, 경작면적: 2000㎡, 평지면적: 1800㎡, 주품목: 딸기
                (값이 null이거나 0인 항목은 입력되지 않은 것으로 간주)
        
        [예시 답변1]
        ```json
        {
          "grade": "A",
          "farmType": "집중형 농장",
          "comment": "주력 작물에 명확하게 집중하고 있어 운영 방향이 매우 분명한 농장입니다. 현재의 구조를 잘 유지한다면 안정적인 성과를 계속 기대할 수 있어요."
        }
        ```
        
        [예시 입력2]
        인력: 2명, 위치: 없음, 경작면적: 1400㎡, 평지면적: 1500㎡, 주품목: 상추
                (값이 null이거나 0인 항목은 입력되지 않은 것으로 간주)
        
        [예시 답변2]
        ```json
        {
          "grade": "A",
          "farmType": "효율형 농장",
          "comment": "평지 기반의 구조가 좋아 인력을 효율적으로 운영하기 좋은 농장이네요! 위치 특성 정보가 추가되면 유통·접근성까지 포함해 더 정교한 분석이 가능합니다!"
        }
        ```
        """);

        UserMessage userMessageContent = new UserMessage(String.format("""
                인력: %s명, 위치: %s, 경작면적: %s㎡, 평지면적: %s㎡, 주품목: %s
                (값이 null이거나 0인 항목은 입력되지 않은 것으로 간주)
                """,
                requestDto.getManpower() != null ? requestDto.getManpower() : "없음",
                requestDto.getLocation() != null ? requestDto.getLocation() : "없음",
                requestDto.getCultivatedArea() != null ? requestDto.getCultivatedArea() : "없음",
                requestDto.getFlatArea() != null ? requestDto.getFlatArea() : "없음",
                requestDto.getMainCrop() != null ? requestDto.getMainCrop() : "없음"));

        // [로그 2] AI에게 실제로 전달되는 프롬프트 확인 (디버깅용)
        LogUtil.info(">>> [AI Request] User Prompt: \n{}", userMessageContent);
        String response = googleGenAiChatModel.call(systemMessage, userMessageContent);
        LogUtil.info(">>> [AI Raw Response]: \n{}", response);
        try {
            String cleanedResponse = response
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            FarmResponseDto.EvaluateDto responseDto = objectMapper.readValue(
                    cleanedResponse, FarmResponseDto.EvaluateDto.class);

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