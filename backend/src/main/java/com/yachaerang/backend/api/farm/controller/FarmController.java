package com.yachaerang.backend.api.farm.controller;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.service.FarmService;
import com.yachaerang.backend.global.exception.GeneralException;
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
        // 컨트롤러 진입 시점 확인
        LogUtil.debug(">>> [Controller] 진입, Thread: {}", Thread.currentThread().getName());

        DeferredResult<ResponseEntity<ApiResponse<?>>> deferredResult =
                new DeferredResult<>(TIMEOUT_MILLIS);

        deferredResult.onTimeout(() -> {
            LogUtil.warn(">>> [DeferredResult] 타임아웃");
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(ApiResponse.failure(ErrorCode.REQUEST_TIMEOUT))
            );
        });

        deferredResult.onError(throwable -> {
            LogUtil.error(">>> [DeferredResult] onError: {}", throwable.getMessage());
            deferredResult.setResult(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED))
            );
        });

        farmService.saveFarmInfo(requestDto)
                .thenAccept(result -> {
                    // 비동기 응답 받는 시점
                    LogUtil.info(">>> [thenAccept] 성공, Thread: {}", Thread.currentThread().getName());
                    deferredResult.setResult(
                            ResponseEntity.status(HttpStatus.OK)
                                    .body(ApiResponse.success(SuccessCode.OK, result))
                    );
                })
                .exceptionally(ex -> {
                    // 원인 예외 추출
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof GeneralException ge) {
                        ErrorCode errorCode = ge.getErrorCode();
                        LogUtil.error("농장 정보 저장 실패: {}", errorCode.getMessage());
                        deferredResult.setResult(
                                ResponseEntity.status(errorCode.getHttpStatus())
                                        .body(ApiResponse.failure(errorCode))
                        );
                    } else {
                        // 커스텀이 아닌 것은 500
                        LogUtil.error("농장 정보 저장 실패 (Unknown): {}", cause.getMessage());
                        deferredResult.setResult(
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED))
                        );
                    }
                    return null;
                });

        LogUtil.info(">>> [Controller] DeferredResult 반환");
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
                            ResponseEntity.status(HttpStatus.OK)
                                    .body(ApiResponse.success(SuccessCode.OK, result))
                    );
                })
                .exceptionally(ex -> {
                    // 원인 예외 추출
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof GeneralException ge) {
                        ErrorCode errorCode = ge.getErrorCode();
                        LogUtil.error("농장 정보 저장 실패: {}", errorCode.getMessage());
                        deferredResult.setResult(
                                ResponseEntity.status(errorCode.getHttpStatus())
                                        .body(ApiResponse.failure(errorCode))
                        );
                    } else {
                        LogUtil.error("농장 정보 저장 실패 (Unknown): {}", cause.getMessage());
                        deferredResult.setResult(
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.failure(ErrorCode.FARM_SAVE_FAILED)));
                    }
                    return null;
                });
        return deferredResult;
    }
}
