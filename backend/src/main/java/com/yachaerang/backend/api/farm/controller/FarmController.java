package com.yachaerang.backend.api.farm.controller;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.service.FarmService;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/v1/farms")
@RestController
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    private static final Long TIMEOUT_MILLIS = 30_000L;

    @PostMapping("")
    public DeferredResult<ResponseEntity<FarmResponseDto.InfoDto>> save(
            @RequestHeader("Authorization") String token,
            @RequestBody FarmRequestDto.InfoDto requestDto
    ) {
        DeferredResult<ResponseEntity<FarmResponseDto.InfoDto>> deferredResult =
                new DeferredResult<>(TIMEOUT_MILLIS);

        // timeout
        deferredResult.onTimeout(() -> {
            LogUtil.warn("농장 저장 요청 타임아웃");
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(FarmResponseDto.InfoDto.builder()
                                    .grade("N/A")
                                    .comment("요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.")
                                    .build())
            );
        });

        // error
        deferredResult.onError(throwable -> {
            LogUtil.error("농장 저장 중 에러 발생: {}", throwable.getMessage());
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(FarmResponseDto.InfoDto.builder()
                                    .grade("N/A")
                                    .comment(throwable.getMessage())
                                    .build())
            );
        });

        // Async
        farmService.saveFarmInfo(requestDto)
                .thenAccept(result -> {
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.CREATED)
                                    .body(result)
                    );
                })
                .exceptionally(ex -> {
                    LogUtil.error("농장 정보 저장 실패: {}", ex.getMessage());
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(FarmResponseDto.InfoDto.builder()
                                            .grade("N/A")
                                            .comment(ex.getMessage())
                                            .build())
                    );
                    // thenAccept 이후의 CompletableFuture<Void>에 대한 예외 처리이므로 null 반환
                    return null;
                });

        return deferredResult;
    }

    @GetMapping("")
    public FarmResponseDto .InfoDto get(@RequestHeader("Authorization") String token) {
        return farmService.getFarm();
    }

    @PatchMapping("")
    public DeferredResult<ResponseEntity<FarmResponseDto.InfoDto>> update(
            @RequestHeader("Authorization") String token,
            @RequestBody FarmRequestDto.InfoDto requestDto
    ) {
        DeferredResult<ResponseEntity<FarmResponseDto.InfoDto>> deferredResult =
                new DeferredResult<>(TIMEOUT_MILLIS);

        // timeout
        deferredResult.onTimeout(() -> {
            LogUtil.warn("농장 저장 요청 타임아웃");
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(FarmResponseDto.InfoDto.builder()
                                    .grade("N/A")
                                    .comment("요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.")
                                    .build())
            );
        });

        // error
        deferredResult.onError(throwable -> {
            LogUtil.error("농장 저장 중 에러 발생: {}", throwable.getMessage());
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(FarmResponseDto.InfoDto.builder()
                                    .grade("N/A")
                                    .comment(throwable.getMessage())
                                    .build())
            );
        });

        // 비동기 작업 시작
        farmService.updateFarmInfo(requestDto)
                .thenAccept(result -> {
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.CREATED)
                                    .body(result)
                    );
                })
                .exceptionally(ex -> {
                    LogUtil.error("농장 정보 저장 실패: {}", ex.getMessage());
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(FarmResponseDto.InfoDto.builder()
                                            .grade("N/A")
                                            .comment(ex.getMessage())
                                            .build())
                    );
                    return null;
                });

        return deferredResult;
    }
}
