package com.yachaerang.backend.api.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yachaerang.backend.api.member.dto.request.MemberRequestDto;
import com.yachaerang.backend.api.member.dto.response.MemberResponseDto;
import com.yachaerang.backend.api.member.service.MemberService;
import com.yachaerang.backend.global.auth.config.SecurityConfig;
import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({ResponseWrappingAdvice.class, SecurityConfig.class})
class MemberControllerTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);

    @Override
    protected Object initController() {
        return new MemberController(memberService);
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

    private String accessToken;
    private MemberResponseDto.MyPageDto responseDto;
    private MemberRequestDto.MyPageDto requestDto;

    @BeforeEach
    void setUp() {
        accessToken = "test-access-token";

        responseDto = MemberResponseDto.MyPageDto.builder()
                .email("test@example.com")
                .name("test")
                .nickname("test")
                .build();

        requestDto = MemberRequestDto.MyPageDto.builder()
                .name("test")
                .nickname("test")
                .build();
    }

    @Test
    @DisplayName("[GET] /api/v1/members - 나의 프로필 조회 API")
    void getMembers() throws Exception {
        // given
        given(memberService.getProfile()).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("accessToken", accessToken))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-profiles",
                        requestHeaders(
                                headerWithName("accessToken").description("Access Token")
                        ),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("email").type(STRING).description("email"),
                                        fieldWithPath("name").type(STRING).description("name"),
                                        fieldWithPath("nickname").type(STRING).description("nickname")
                )));
    }

    @Test
    @DisplayName("[PATCH] /api/v1/members - 나의 프로필 수정 API")
    void updateMembers() throws Exception {
        // given
        MemberResponseDto.MyPageDto updateDto = MemberResponseDto.MyPageDto.builder()
                .email("test@example.com")
                .name("test2")
                .nickname("test2")
                .build();

        given(memberService.updateProfile(any(MemberRequestDto.MyPageDto.class))).willReturn(updateDto);

        // when & then
        mockMvc.perform(patch("/api/v1/members")
                .header("accessToken", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(doc(
                        "update-profiles",
                        requestHeaders(
                                headerWithName("accessToken").description("Access Token")
                        ),
                        responseFields(ENVELOPE_COMMON).and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("email").type(STRING).description("email"),
                                        fieldWithPath("name").type(STRING).description("name"),
                                        fieldWithPath("nickname").type(STRING).description("nickname")
                )));
    }
}