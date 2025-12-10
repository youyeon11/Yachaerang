package com.yachaerang.backend.api.chat.controller;

import com.yachaerang.backend.api.chat.dto.request.ChatRequestDto;
import com.yachaerang.backend.api.chat.dto.response.ChatResponseDto;
import com.yachaerang.backend.api.chat.service.ChatService;
import com.yachaerang.backend.api.common.SenderRole;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.springframework.context.annotation.Import;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ResponseWrappingAdvice.class)
class ChatControllerTest extends RestDocsSupport {

    private final ChatService chatService = mock(ChatService.class);

    @Override
    protected Object initController() {
        return new ChatController(chatService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[]{ new ResponseWrappingAdvice() };
    }

    // 공통 응답 필드
    private static final FieldDescriptor[] ENVELOPE_COMMON = new FieldDescriptor[]{
            fieldWithPath("httpStatus").type(STRING).description("HTTP 상태 코드"),
            fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
            fieldWithPath("code").type(STRING).description("응답 코드"),
            fieldWithPath("message").type(STRING).description("응답 메시지")
    };

    /**
     * data 가 존재하는 경우의 필드
     */
    private static final FieldDescriptor DATA_ARRAY_DESCRIPTOR =
            fieldWithPath("data").type(ARRAY).description("응답 데이터");

    /**
     * data 가 단일객체로 존재하는 경우의 필드
     */
    private static final FieldDescriptor DATA_OBJECT_DESCRIPTOR =
            fieldWithPath("data").type(OBJECT).description("응답 데이터 (없음)");

    /**
     * data 가 존재하지 않는 경우(null) 의 필드
     */
    private static final FieldDescriptor DATA_NULL_DESCRIPTOR =
            fieldWithPath("data").type(NULL).description("응답 데이터 (없음)");

    private static final String AUTH_HEADER = "Authorization";
    private static final String TEST_TOKEN = "Bearer test-access-token";



    @Test
    @DisplayName("[POST] /api/v1/chat")
    void startNewSession_Success() throws Exception {
        // Given
        Long expectedSessionId = 1L;
        given(chatService.startNewSession()).willReturn(expectedSessionId);

        // When & Then
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TEST_TOKEN)
                ).andExpect(status().isOk())
                .andDo(doc(
                        "start-chat",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(),
                        queryParameters(),
                        responseFields(
                                Stream.concat(
                                        Arrays.stream(ENVELOPE_COMMON),
                                        Stream.of(
                                                fieldWithPath("data").type(NUMBER).description("Chat Session ID")
                                        )
                                ).toArray(FieldDescriptor[]::new)
                        )
                ));
    }

    @Test
    @DisplayName("[POST] /api/v1/chat/{chatSessionId}/messages")
    void chat_Success() throws Exception {
        // given
        Long chatSessionId = 1L;
        ChatRequestDto.MessageDto requestDto = ChatRequestDto.MessageDto.builder()
                .message("안녕하세요")
                .build();

        ChatResponseDto.MessageDto responseDto = ChatResponseDto.MessageDto.builder()
                .chatSessionId(chatSessionId)
                .response("안녕하세요! 무엇을 도와드릴까요?")
                .build();

        given(chatService.getChatResponse(any(ChatRequestDto.MessageDto.class), eq(chatSessionId)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/v1/chat/{chatSessionId}/messages", chatSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TEST_TOKEN)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(doc(
                        "call-chat",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("chatSessionId").description("Chat Session ID")
                        ),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("chatSessionId").type(NUMBER).description("Chat Session ID"),
                                        fieldWithPath("response").type(STRING).description("메시지 내용"))));

        verify(chatService, times(1))
                .getChatResponse(any(ChatRequestDto.MessageDto.class), eq(chatSessionId));
    }


    @Test
    @DisplayName("[POST] /api/v1/chat/{chatSessionId}/end")
    void endSession_Success() throws Exception {
        // given
        Long chatSessionId = 1L;
        willDoNothing().given(chatService).endSession(chatSessionId);

        // When & Then
        mockMvc.perform(post("/api/v1/chat/{chatSessionId}/end", chatSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TEST_TOKEN))
                .andExpect(status().isOk())
                .andDo(doc(
                        "end-chat",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("chatSessionId").description("Chat Session ID")
                        ),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)));
        verify(chatService, times(1)).endSession(chatSessionId);
    }

    @Test
    @DisplayName("[GET] /api/v1/chat/{chatSessionId}/messages")
    void getChatHistory_Success() throws Exception {
        // given
        Long chatSessionId = 1L;
        List<ChatResponseDto.HistoryMessageDto> historyMessages = Arrays.asList(
                ChatResponseDto.HistoryMessageDto.builder()
                        .id(1L)
                        .role(SenderRole.USER.getCode())
                        .content("안녕하세요")
                        .sentAt(LocalDateTime.now().minusMinutes(5))
                        .build(),
                ChatResponseDto.HistoryMessageDto.builder()
                        .id(2L)
                        .role(SenderRole.ASSISTANT.getCode())
                        .content("안녕하세요! 무엇을 도와드릴까요?")
                        .sentAt(LocalDateTime.now().minusMinutes(4))
                        .build(),
                ChatResponseDto.HistoryMessageDto.builder()
                        .id(3L)
                        .role(SenderRole.USER.getCode())
                        .content("날씨 알려주세요")
                        .sentAt(LocalDateTime.now().minusMinutes(3))
                        .build()
        );

        given(chatService.getChatHistory(chatSessionId)).willReturn(historyMessages);

        // When & Then
        mockMvc.perform(get("/api/v1/chat/{chatSessionId}/messages", chatSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TEST_TOKEN))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-chat",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("chatSessionId").description("Chat Session ID")
                        ),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_ARRAY_DESCRIPTOR)
                                .andWithPrefix("data[]",
                                        fieldWithPath("id").type(NUMBER).description("메시지 ID"),
                                        fieldWithPath("role").type(STRING).description("보낸 이 역할"),
                                        fieldWithPath("content").type(STRING).description("대화 내용"),
                                        fieldWithPath("sentAt").type(VARIES).description("보낸 시각"))));

        verify(chatService, times(1)).getChatHistory(chatSessionId);
    }
}