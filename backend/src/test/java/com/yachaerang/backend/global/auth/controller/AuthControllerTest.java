package com.yachaerang.backend.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import com.yachaerang.backend.global.auth.service.AuthService;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ResponseWrappingAdvice.class)
class AuthControllerTest extends RestDocsSupport {

    private final AuthService authService = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[] {new ResponseWrappingAdvice()};
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 공통 응답 필드
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
    private static final FieldDescriptor DATA_OBJECT_DESCRIPTOR =
            fieldWithPath("data").type(OBJECT).description("응답 데이터");

    /**
     * data 가 존재하지 않는 경우(null) 의 필드
     */
    private static final FieldDescriptor DATA_NULL_DESCRIPTOR =
            fieldWithPath("data").type(NULL).description("응답 데이터 (없음)");

    @Test
    @DisplayName("[POST] /api/v1/auth/signup - 회원가입 요청 API")
    void 회원가입_요청() throws Exception {
        // given
        TokenRequestDto.SignupDto signupDto = TokenRequestDto.SignupDto.builder()
                .name("test")
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .build();
        willDoNothing().given(authService).signup(any(TokenRequestDto.SignupDto.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk())
                .andDo(doc(
                        "signup",
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)
                ));
    }

    @Test
    @DisplayName("[POST] /api/v1/auth/login - 로그인 요청 API")
    void 로그인_요청() throws Exception {
        // given
        TokenRequestDto.LoginDto loginDto = TokenRequestDto.LoginDto.builder()
                .email("test@test.com")
                .password("1234")
                .build();

        TokenResponseDto.ResultDto expectedDto = TokenResponseDto.ResultDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
        // HttpServletResponse -> any()
        given(authService.login(any(TokenRequestDto.LoginDto.class), any(HttpServletResponse.class))).willReturn(expectedDto);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andDo(doc(
                        "login",
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("accessToken").type(STRING).description("accessToken"),
                                        fieldWithPath("refreshToken").type(STRING).description("refreshToken")
                                )));
    }

    @Test
    @DisplayName("[POST] /api/v1/auth/logout - 로그아웃 요청 API")
    void 로그아웃_요청() throws Exception {
        // given
        String token = "access-token";

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("accessToken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "logout",
                        requestHeaders(
                                headerWithName("accessToken").description("Access Token")
                        ),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)
                ));
    }


    @Test
    @DisplayName("[POST] /api/v1/auth/reissue - 토큰 재발급 API")
    void 토큰재발급_요청() throws Exception {
        // given
        String token = "refresh-token";

        TokenResponseDto.ResultDto expectedDto = TokenResponseDto.ResultDto.builder()
                .accessToken("new-access-token")
                .refreshToken("refresh-token")
                .build();
        given(authService.reissue(token)).willReturn(expectedDto);

        // when & then
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .header("refreshToken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(doc(
                        "reissue",
                        requestHeaders(
                                headerWithName("refreshToken").description("Refresh Token")
                        ),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("accessToken").type(STRING).description("accessToken"),
                                        fieldWithPath("refreshToken").type(STRING).description("refreshToken")
                                )
                ));
    }
}