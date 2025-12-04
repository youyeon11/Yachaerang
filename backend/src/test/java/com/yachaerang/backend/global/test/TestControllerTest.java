package com.yachaerang.backend.global.test;

import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import(ResponseWrappingAdvice.class)
class TestControllerTest extends RestDocsSupport {

    private final TestService testService = mock(TestService.class);

    @Override
    protected Object initController() {
        return new TestController(testService); // 실제 컨트롤러 인스턴스 반환
    }

    @Override
    protected Object[] initControllerAdvices() {
        return new Object[]{new ResponseWrappingAdvice()};
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
    private static final FieldDescriptor DATA_OBJECT_DESCRIPTOR =
            fieldWithPath("data").type(OBJECT).description("응답 데이터");

    /**
     * data 가 존재하지 않는 경우(null) 의 필드
     */
    private static final FieldDescriptor DATA_NULL_DESCRIPTOR =
            fieldWithPath("data").type(NULL).description("응답 데이터 (없음)");


    @Test
    @DisplayName("데이터가 있을 때 성공의 응답인 경우")
    void 데이터가_있을때의_성공() throws Exception {
        // given
        TestDto expectedDto = new TestDto("test");
        given(testService.successWithData()).willReturn(expectedDto);

        // when & then
        mockMvc.perform(get("/test/success/data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "test-success",
                        responseFields(ENVELOPE_COMMON).and(DATA_OBJECT_DESCRIPTOR)
                                .andWithPrefix("data.",
                                        fieldWithPath("name").type(STRING).description("test")
                                )
                ));

    }

    @Test
    @DisplayName("데이터가 없을 때 성공의 응답인 경우")
    void 데이터가_없을때의_성공() throws Exception {
        // given
        willDoNothing().given(testService).successWithoutData();

        // when & then
        mockMvc.perform(get("/test/success/without")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "test-success-without-data",
                        responseFields(ENVELOPE_COMMON)
                                .and(DATA_NULL_DESCRIPTOR)
                ));
    }
}