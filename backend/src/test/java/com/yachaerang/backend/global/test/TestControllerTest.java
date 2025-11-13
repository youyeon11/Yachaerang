package com.yachaerang.backend.global.test;

import com.yachaerang.backend.global.response.ResponseWrappingAdvice;
import com.yachaerang.backend.global.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import org.springframework.restdocs.payload.FieldDescriptor;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    static FieldDescriptor[] envelop = new FieldDescriptor[]{
            fieldWithPath("httpStatus").type(STRING).description("HTTP 상태 코드"),
            fieldWithPath("success").type(BOOLEAN).description("응답 성공 여부"),
            fieldWithPath("code").type(STRING).description("응답 코드"),
            fieldWithPath("message").type(STRING).description("응답 메시지"),
            fieldWithPath("data").type(OBJECT).description("데이터")
    };

    @Test
    @DisplayName("데이터가 있을 때 성공의 응답인 경우")
    void success() throws Exception {
        // given
        TestDto expectedDto = new TestDto("test");
        given(testService.successWithData()).willReturn(expectedDto);

        // when & then
        mockMvc.perform(get("/test/success/data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(doc(
                        "test-success",
                        responseFields(envelop)
                                .andWithPrefix("data.",
                                        fieldWithPath("name").type(STRING).description("test")
                                )
                ));

    }
}