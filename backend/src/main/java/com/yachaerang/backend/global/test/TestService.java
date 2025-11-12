package com.yachaerang.backend.global.test;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    // 성공 응답
    public TestDto successWithData() {
        return TestDto.builder()
                .name("test")
                .build();
    }

    // 성공 응답
    public void successWithoutData() {
        System.out.println("successWithoutData");
    }

    // 실패 응답
    public void failure() {
        throw GeneralException.of(ErrorCode.TEST_ERROR);
    }
}
