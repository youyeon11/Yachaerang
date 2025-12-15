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
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                .imageUrl("default.png")
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
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(doc(
                        "get-profiles",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("email").type(STRING).description("email"),
                                        fieldWithPath("name").type(STRING).description("name"),
                                        fieldWithPath("nickname").type(STRING).description("nickname"),
                                        fieldWithPath("imageUrl").type(STRING).description("imageUrl")
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
                .imageUrl("update.png")
                .build();

        given(memberService.updateProfile(any(MemberRequestDto.MyPageDto.class))).willReturn(updateDto);

        // when & then
        mockMvc.perform(patch("/api/v1/members")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(doc(
                        "update-profiles",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("email").type(STRING).description("email"),
                                        fieldWithPath("name").type(STRING).description("name"),
                                        fieldWithPath("nickname").type(STRING).description("nickname"),
                                        fieldWithPath("imageUrl").type(STRING).description("imageUrl")
                )));
    }

    @Test
    @DisplayName("[POST] /api/v1/members/image")
    void uploadProfileImage() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "test-image-content".getBytes()
        );

        willDoNothing().given(memberService).uploadProfileImage(any(MultipartFile.class));

        // when & then
        mockMvc.perform(multipart("/api/v1/members/image")
                        .file(file)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(doc(
                        "upload-profile-image",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        requestParts(
                                partWithName("file").description("업로드할 프로필 이미지 파일")
                        ),
                        pathParameters(),
                        queryParameters(),
                        responseFields(ENVELOPE_COMMON).and(DATA_NULL_DESCRIPTOR)
                ));
        verify(memberService, times(1)).uploadProfileImage(any(MultipartFile.class));
    }
}