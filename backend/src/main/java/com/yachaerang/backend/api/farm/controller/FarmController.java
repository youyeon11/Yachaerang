package com.yachaerang.backend.api.farm.controller;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.service.FarmService;
import com.yachaerang.backend.global.response.ApiResponse;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.response.SuccessCode;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


@RequestMapping("/api/v1/farms")
@RestController
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    private static final Long TIMEOUT_MILLIS = 30_000L;

    @PostMapping("")
    public DeferredResult<ResponseEntity<ApiResponse<?>>> save(
            @RequestHeader("Authorization") String token,
            @RequestBody FarmRequestDto.InfoDto requestDto
    ) {
        DeferredResult<ResponseEntity<ApiResponse<?>>> deferredResult =
                new DeferredResult<>(TIMEOUT_MILLIS);

        // timeout
        deferredResult.onTimeout(() -> {
            LogUtil.warn("농장 저장 요청 타임아웃");
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(ApiResponse.failure(ErrorCode.REQUEST_TIMEOUT))
            );
        });

        // error
        deferredResult.onError(throwable -> {
            LogUtil.error("농장 저장 중 에러 발생: {}", throwable.getMessage());
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED))
            );
        });

        // Async
        farmService.saveFarmInfo(requestDto)
                .thenAccept(result -> {
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.CREATED)
                                    .body(ApiResponse.success(SuccessCode.OK, result))
                    );
                })
                .exceptionally(ex -> {
                    LogUtil.error("농장 정보 저장 실패: {}", ex.getMessage());
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED))
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
    public DeferredResult<ResponseEntity<ApiResponse<?>>> update(
            @RequestHeader("Authorization") String token,
            @RequestBody FarmRequestDto.InfoDto requestDto
    ) {
        DeferredResult<ResponseEntity<ApiResponse<?>>> deferredResult = new DeferredResult<>(TIMEOUT_MILLIS);

        // timeout
        deferredResult.onTimeout(() -> {
            LogUtil.warn("농장 저장 요청 타임아웃");
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(ApiResponse.failure(ErrorCode.REQUEST_TIMEOUT))
            );
        });

        // error
        deferredResult.onError(throwable -> {
            LogUtil.error("농장 저장 중 에러 발생: {}", throwable.getMessage());
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED))
            );
        });

        // 비동기 작업 시작
        farmService.updateFarmInfo(requestDto)
                .thenAccept(result -> {
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.CREATED)
                                    .body(ApiResponse.success(SuccessCode.OK, result))
                    );
                })
                .exceptionally(ex -> {
                    LogUtil.error("농장 정보 저장 실패: {}", ex.getMessage());
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED))
                    );
                    return null;
                });

        return deferredResult;
    }
}
