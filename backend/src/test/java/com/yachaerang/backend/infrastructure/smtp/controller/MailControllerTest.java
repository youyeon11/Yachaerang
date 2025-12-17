package com.yachaerang.backend.infrastructure.smtp.controller;

import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import com.yachaerang.backend.infrastructure.smtp.dto.request.MailRequestDto;
import com.yachaerang.backend.infrastructure.smtp.service.MailService;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ResponseWrappingAdvice.class)
class MailControllerTest extends RestDocsSupport {

    private final MailService mailService = mock(MailService.class);

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[]{
                new ResponseWrappingAdvice()
        };
    }

    @Override
    protected Object initController() {
        return new MailController(mailService);
    }

    // 공통 응답 필드
    private static final FieldDescriptor[] ENVELOPE_COMMON = new FieldDescriptor[]{
            fieldWithPath("httpStatus").type(STRING).description("HTTP 상태 코드"),
            fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
            fieldWithPath("code").type(STRING).description("응답 코드"),
            fieldWithPath("message").type(STRING).description("응답 메시지")
    };

    /**
     * data 가 존재하지 않는 경우(null) 의 필드
     */
    private static final FieldDescriptor DATA_NULL_DESCRIPTOR =
            fieldWithPath("data").type(NULL).description("응답 데이터 (없음)");

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";

    @Test
    @DisplayName("[POST] /api/v1/mails")
    void sendMail_Success() throws Exception {
        // given
        MailRequestDto.MailRequest request = MailRequestDto.MailRequest
                .builder()
                .mail(TEST_EMAIL)
                .build();
        willDoNothing().given(mailService).requestVerificationMail(any(MailRequestDto.MailRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/mails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andDo(doc(
                        "send-mail",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        requestFields(
                                fieldWithPath("mail").type(STRING).description("인증 받을 이메일")
                        ),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)
                ));
    }

    @Test
    @DisplayName("[POST] /api/v1/mails/verify-code")
    void sendMail_VerifyCode() throws Exception {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code(TEST_CODE)
                .build();
        given(mailService.isVerified(any(MailRequestDto.VerificationRequest.class)))
                .willReturn(true);

        // when & then
        mockMvc.perform(post("/api/v1/mails/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andDo(doc(
                        "verify-code",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        requestFields(
                                fieldWithPath("mail").type(STRING).description("인증 받을 이메일"),
                                fieldWithPath("code").type(STRING).description("인증 코드")
                        ),
                        responseFields(ENVELOPE_COMMON).and(
                                fieldWithPath("data").type(BOOLEAN).description("인증 성공 여부")
                        )
                ));
    }


    @Test
    @DisplayName("[POST] /api/v1/mails/password/send-code")
    void sendCodeForPassword_Success() throws Exception {
        // given
        MailRequestDto.MailRequest request = MailRequestDto.MailRequest
                .builder()
                .mail(TEST_EMAIL)
                .build();
        willDoNothing().given(mailService).requestPasswordResetVerificationMail(any(MailRequestDto.MailRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/mails/password/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andDo(doc(
                        "password-code",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        requestFields(
                                fieldWithPath("mail").type(STRING).description("인증 받을 이메일")
                        ),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)
                ));
    }

    @Test
    @DisplayName("[POST] /api/v1/mails/password/reset")
    void resetPassword_Success() throws Exception {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code(TEST_CODE)
                .build();
        willDoNothing().given(mailService).verifyAndSendTempPassword(any(MailRequestDto.VerificationRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/mails/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andDo(doc(
                        "password-reset",
                        requestHeaders(),
                        pathParameters(),
                        queryParameters(),
                        requestFields(
                                fieldWithPath("mail").type(STRING).description("인증 받을 이메일"),
                                fieldWithPath("code").type(STRING).description("인증 코드")
                        ),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)
                ));
    }
}