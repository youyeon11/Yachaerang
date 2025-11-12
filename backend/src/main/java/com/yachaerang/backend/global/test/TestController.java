package com.yachaerang.backend.global.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    @GetMapping("/success/data")
    public TestDto success() {
        return testService.successWithData();
    }

    @GetMapping("/success/without")
    public void successWithout() {
        testService.successWithoutData();
    }

    @GetMapping("/failure")
    public void failure() {
        testService.failure();
    }

    @GetMapping("/failure/custom")
    public void failureCustom() {
        testService.failureWithMessage();
    }
}
